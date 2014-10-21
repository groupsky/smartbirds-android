package org.bspb.smartbirds.pro.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.googlecode.jcsv.CSVStrategy;
import com.googlecode.jcsv.writer.CSVWriter;
import com.googlecode.jcsv.writer.internal.CSVWriterBuilder;
import com.googlecode.jcsv.writer.internal.DefaultCSVEntryConverter;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.events.CancelMonitoringEvent;
import org.bspb.smartbirds.pro.events.CreateImageFile;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.EntrySubmitted;
import org.bspb.smartbirds.pro.events.FinishMonitoringEvent;
import org.bspb.smartbirds.pro.events.GetMonitoringCommonData;
import org.bspb.smartbirds.pro.events.ImageFileCreated;
import org.bspb.smartbirds.pro.events.MonitoringCommonData;
import org.bspb.smartbirds.pro.events.MonitoringFailedEvent;
import org.bspb.smartbirds.pro.events.MonitoringStartedEvent;
import org.bspb.smartbirds.pro.events.SetMonitoringCommonData;
import org.bspb.smartbirds.pro.events.StartMonitoringEvent;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

@EService
public class DataService extends Service {

    private static final String TAG = SmartBirdsApplication.TAG + ".DataService";
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US);
    private static final DateFormat GPX_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

    static {
        DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone("UTC"));
        GPX_DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Bean
    EEventBus bus;

    boolean monitoring = false;
    String monitoringName;
    File monitoringDir;
    HashMap<String, String> commonData;
    int pictureCounter;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @AfterInject
    void initBus() {
        Log.d(TAG, "bus registing...");
        bus.registerSticky(this);
        Log.d(TAG, "bus registered");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "destroying...");
        bus.unregister(this);

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand...");
        return START_NOT_STICKY;
    }

    public void onEvent(StartMonitoringEvent event) {
        Log.d(TAG, "onStartMonitoringEvent...");
        Toast.makeText(this, "Start monitoring", Toast.LENGTH_SHORT).show();
        monitoringName = String.format("%s-%s", DATE_FORMATTER.format(new Date()), getRandomNode());
        if ((monitoringDir = createMonitoringDir()) != null && initGpxFile()) {
            TrackingService_.intent(this).start();
            pictureCounter = 0;
            monitoring = true;
            bus.postSticky(new MonitoringStartedEvent());
        } else {
            Toast.makeText(this, "Cannot create directory for data! Check that you have enough storage available", Toast.LENGTH_SHORT).show();
            bus.post(new MonitoringFailedEvent());
        }
    }

    private File createMonitoringDir() {
        File file = new File(getExternalFilesDir(null), monitoringName + "-wip");
        if (!file.mkdirs()) {
            Log.w(TAG, String.format("Cannot create %s", file));
            file = new File(getFilesDir(), monitoringName + "-wip");
            if (!file.mkdirs()) {
                Log.e(TAG, String.format("Cannot create %s", file));
                return null;
            }
        } else {
            Log.d(TAG, String.format("Created directory %s", file));
        }
        return file;
    }

    private boolean initGpxFile() {
        File file = new File(monitoringDir, "track.gpx");
        try {
            OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file, false)));
            try {
                osw.write(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                "<gpx xmlns=\"http://www.topografix.com/GPX/1/1\"\n" +
                                "     version=\"1.1\"\n" +
                                "     creator=\"SmartBirds Pro\"\n" +
                                "     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
                                "     xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">\n" +
                                "  <metadata>\n" +
                                "    <time>" + GPX_DATE_FORMATTER.format(new Date()) + "</time>\n" +
                                "  </metadata>\n" +
                                "  <trk>\n");
            } finally {
                osw.close();
            }
        } catch (IOException e) {
            Crashlytics.logException(e);
            return false;
        }
        return true;
    }

    private String getRandomNode() {
        String uuid = UUID.randomUUID().toString();
        return uuid.substring(uuid.length() - 12);
    }

    public void onEvent(CancelMonitoringEvent event) {
        Log.d(TAG, "onCancelMonitoringEvent...");
        TrackingService_.intent(this).stop();
        monitoring = false;
        Toast.makeText(this, "Cancel monitoring", Toast.LENGTH_SHORT).show();
    }

    public void onEvent(SetMonitoringCommonData event) {
        Log.d(TAG, "onSetMonitoringCommonData");
        commonData = event.data;
    }

    public void onEvent(FinishMonitoringEvent event) {
        monitoring = false;
        TrackingService_.intent(this).stop();
        closeGpxFile();

        combineCommonWithEntires();

        File newDir = new File(monitoringDir.getAbsolutePath().replace("-wip", "-up"));
        monitoringDir.renameTo(newDir);
        monitoringDir = newDir;
        UploadService_.intent(this).upload(monitoringDir.getAbsolutePath()).start();
    }

    private void combineCommonWithEntires() {
        try {

            String[] commonLines = convertToCsvLines(commonData);

            File entriesFile = new File(monitoringDir, "entries.csv");
            BufferedReader entriesReader = new BufferedReader(new FileReader(entriesFile));
            File tempFile = new File(monitoringDir, "combined_entries.csv");
            BufferedWriter outWriter = new BufferedWriter(new FileWriter(tempFile));
            try {
                boolean firstLine = true;
                String entry;
                while ((entry = entriesReader.readLine()) != null) {
                    if (firstLine) {
                        outWriter.write(commonLines[0]);
                        firstLine = false;
                    } else {
                        outWriter.write(commonLines[1]);
                    }
                    outWriter.write(CSVStrategy.DEFAULT.getDelimiter());
                    outWriter.write(entry);
                    outWriter.newLine();
                }
            } finally {
                outWriter.close();
                entriesReader.close();
            }

            entriesFile.delete();
            tempFile.renameTo(entriesFile);
        } catch (Throwable t) {
            Crashlytics.logException(t);
        }
    }

    private String[] convertToCsvLines(HashMap<String, String> data) throws IOException {
        StringWriter memory = new StringWriter();
        try {
            CSVWriter<String[]> csvWriter = new CSVWriterBuilder<String[]>(memory).strategy(CSVStrategy.DEFAULT).entryConverter(new DefaultCSVEntryConverter()).build();
            csvWriter.write(commonData.keySet().toArray(new String[]{}));
            csvWriter.write(commonData.values().toArray(new String[]{}));
            memory.flush();
        } finally {
            memory.close();
        }
        String commonData = memory.getBuffer().toString();
        return commonData.split(System.getProperty("line.separator"));
    }

    public void onEvent(EntrySubmitted event) {
        Log.d(TAG, "onEntrySubmitted");

        HashMap<String, String> data = event.data;

        File file = new File(monitoringDir, "entries.csv");
        boolean exists = file.exists();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, true);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            CSVWriter<String[]> csvWriter = new CSVWriterBuilder<String[]>(osw).strategy(CSVStrategy.DEFAULT).entryConverter(new DefaultCSVEntryConverter()).build();

            if (!exists)
                csvWriter.write(data.keySet().toArray(new String[]{}));
            csvWriter.write(data.values().toArray(new String[]{}));

            csvWriter.flush();
            csvWriter.close();
        } catch (java.io.IOException e) {
            Crashlytics.logException(e);
        }

    }

    public void onEvent(Location location) {
        Log.d(TAG, "onLocation");

        if (monitoring && location != null) {
            File file = new File(monitoringDir, "track.gpx");
            try {
                OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file, true)));
                try {
                    osw.write(
                            "    <trkseg>\n" +
                                    "      <trkpt lat=\""+location.getLatitude()+"\" lon=\""+location.getLongitude()+"\">\n" +
                                    (location.hasAltitude()?"        <ele>"+location.getAltitude()+"</ele>\n":"") +
                                    "        <time>"+GPX_DATE_FORMATTER.format(new Date(location.getTime()))+"</time>\n" +
                                    "      </trkpt>\n" +
                                    "    </trkseg>\n");
                } finally {
                    osw.close();
                }
            } catch (IOException e) {
                Crashlytics.logException(e);
            }
        }
    }

    private void closeGpxFile() {
        if (monitoring) {
            File file = new File(monitoringDir, "track.gpx");
            try {
                OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file, true)));
                try {
                    osw.write(
                            "  </trk>\n" +
                                    "</gpx>\n");
                } finally {
                    osw.close();
                }
            } catch (IOException e) {
                Crashlytics.logException(e);
            }
        }
    }

    public void onEvent(CreateImageFile event) {
        // Create an image file name
        while (true) {
            String index = Integer.toString(pictureCounter++);
            while (index.length() < 4) index = '0' + index;
            String imageFileName = "Pic" + index + ".jpg";
            File image = new File(monitoringDir, imageFileName);
            try {
                if (image.createNewFile()) {
                    bus.post(new ImageFileCreated(imageFileName, Uri.fromFile(image), image.getAbsolutePath()));
                    break;
                }
            } catch (IOException e) {
                Crashlytics.logException(e);
            }
        }
    }

    public void onEvent(GetMonitoringCommonData event) {
        bus.post(new MonitoringCommonData(commonData));
    }
}
