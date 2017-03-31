package org.bspb.smartbirds.pro.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.content.Monitoring;
import org.bspb.smartbirds.pro.content.MonitoringManager;
import org.bspb.smartbirds.pro.content.TrackingLocation;
import org.bspb.smartbirds.pro.events.ActiveMonitoringEvent;
import org.bspb.smartbirds.pro.events.CancelMonitoringEvent;
import org.bspb.smartbirds.pro.events.CreateImageFile;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.EntrySubmitted;
import org.bspb.smartbirds.pro.events.FinishMonitoringEvent;
import org.bspb.smartbirds.pro.events.GetImageFile;
import org.bspb.smartbirds.pro.events.GetMonitoringCommonData;
import org.bspb.smartbirds.pro.events.ImageFileCreated;
import org.bspb.smartbirds.pro.events.ImageFileCreatedFailed;
import org.bspb.smartbirds.pro.events.ImageFileEvent;
import org.bspb.smartbirds.pro.events.MonitoringCommonData;
import org.bspb.smartbirds.pro.events.MonitoringFailedEvent;
import org.bspb.smartbirds.pro.events.MonitoringStartedEvent;
import org.bspb.smartbirds.pro.events.QueryActiveMonitoringEvent;
import org.bspb.smartbirds.pro.events.SetMonitoringCommonData;
import org.bspb.smartbirds.pro.events.StartMonitoringEvent;
import org.bspb.smartbirds.pro.events.UndoLastEntry;
import org.bspb.smartbirds.pro.prefs.SmartBirdsPrefs_;
import org.bspb.smartbirds.pro.tools.GpxWriter;
import org.bspb.smartbirds.pro.ui.utils.Configuration;
import org.bspb.smartbirds.pro.ui.utils.NotificationUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.text.TextUtils.isEmpty;
import static org.bspb.smartbirds.pro.content.Monitoring.Status.canceled;
import static org.bspb.smartbirds.pro.content.Monitoring.Status.finished;

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

    @Bean
    MonitoringManager monitoringManager;

    @Pref
    SmartBirdsPrefs_ globalPrefs;

    Monitoring monitoring = null;

    public boolean isMonitoring() {
        return monitoring != null;
    }

    public void setMonitoring(Monitoring monitoring) {
        this.monitoring = monitoring;
        if (monitoring != null) {
            NotificationUtils.showMonitoringNotification(getApplicationContext());
            TrackingService_.intent(this).start();
            globalPrefs.runningMonitoring().put(true);
        } else {
            NotificationUtils.hideMonitoringNotification(getApplicationContext());
            TrackingService_.intent(this).stop();
            globalPrefs.runningMonitoring().put(false);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @AfterInject
    void initBus() {
        // restore state
        setMonitoring(monitoringManager.getActiveMonitoring());

        Log.d(TAG, "bus registering...");
        bus.registerSticky(this);
        Log.d(TAG, "bus registered");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "destroying...");
        bus.unregister(this);
        if (isMonitoring()) DataService_.intent(this).start();

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand...");
        return START_STICKY;
    }

    public void onEvent(@SuppressWarnings("UnusedParameters") StartMonitoringEvent event) {
        if (isMonitoring()) {
            bus.postSticky(new MonitoringStartedEvent());
            return;
        }
        Log.d(TAG, "onStartMonitoringEvent...");
        Toast.makeText(this, "Start monitoring", Toast.LENGTH_SHORT).show();
        setMonitoring(monitoringManager.createNew());
        if (createMonitoringDir(monitoring) != null && initGpxFile()) {
            bus.postSticky(new MonitoringStartedEvent());
        } else {
            Toast.makeText(this, getString(R.string.error_message_create_monitoring_dir), Toast.LENGTH_SHORT).show();
            bus.post(new MonitoringFailedEvent());
        }
    }

    private File createMonitoringDir(Monitoring monitoring) {
        return DataOpsService.createMonitoringDir(this, monitoring);
    }

    private boolean initGpxFile() {
        File file = new File(createMonitoringDir(monitoring), "track.gpx");
        try {
            OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file, false)));
            //noinspection TryFinallyCanBeTryWithResources
            try {
                GpxWriter writer = new GpxWriter(osw);
                writer.writeHeader();
            } finally {
                //noinspection ThrowFromFinallyBlock
                osw.close();
            }
        } catch (IOException e) {
            Crashlytics.logException(e);
            return false;
        }
        return true;
    }

    public void onEvent(@SuppressWarnings("UnusedParameters") CancelMonitoringEvent event) {
        Log.d(TAG, "onCancelMonitoringEvent...");

        if (isMonitoring()) {
            monitoringManager.updateStatus(monitoring, canceled);
        }
        setMonitoring(null);
        Toast.makeText(this, getString(R.string.toast_cancel_monitoring), Toast.LENGTH_SHORT).show();
    }

    public void onEvent(SetMonitoringCommonData event) {
        Log.d(TAG, "onSetMonitoringCommonData");
        event.data.put(getResources().getString(R.string.monitoring_id), monitoring.code);
        event.data.put(getResources().getString(R.string.version), Configuration.STORAGE_VERSION_CODE);
        monitoring.commonForm.clear();
        monitoring.commonForm.putAll(event.data);
        monitoringManager.update(monitoring);
    }

    public void onEvent(@SuppressWarnings("UnusedParameters") FinishMonitoringEvent event) {
        if (monitoring.commonForm.containsKey(getResources().getString(R.string.end_time_key))) {
            if (isEmpty(monitoring.commonForm.get(getResources().getString(R.string.end_time_key)))) {
                monitoring.commonForm.put(getResources().getString(R.string.end_time_key), Configuration.STORAGE_TIME_FORMAT.format(new Date()));
                monitoringManager.update(monitoring);
            }
        }

        monitoringManager.updateStatus(monitoring, finished);

        closeGpxFile();
        DataOpsService_.intent(this).generateMonitoringFiles(monitoring.code).start();
        setMonitoring(null);
    }

    public void onEvent(EntrySubmitted event) {
        Log.d(TAG, "onEntrySubmitted");

        if (event.entryId > 0) {
            monitoringManager.updateEntry(event.monitoringCode, event.entryId, event.entryType, event.data);
            DataOpsService_.intent(this).generateMonitoringFiles(event.monitoringCode).start();
        } else {
            monitoringManager.newEntry(monitoring, event.entryType, event.data);
        }
    }

    public void onEvent(Location location) {
        Log.d(TAG, "onLocation");

        if (isMonitoring() && location != null) {
            TrackingLocation trackingLocation = monitoringManager.newTracking(monitoring, location);

            File file = new File(createMonitoringDir(monitoring), "track.gpx");
            try {
                Writer osw = new BufferedWriter(new FileWriter(file, true));
                //noinspection TryFinallyCanBeTryWithResources
                try {
                    new GpxWriter(osw).writePosition(trackingLocation);
                } finally {
                    //noinspection ThrowFromFinallyBlock
                    osw.close();
                }
            } catch (IOException e) {
                Crashlytics.logException(e);
                Toast.makeText(this, "Could not write to track.gpx!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void closeGpxFile() {
        if (isMonitoring()) {
            File file = new File(createMonitoringDir(monitoring), "track.gpx");
            try {
                Writer osw = new BufferedWriter(new FileWriter(file, true));
                //noinspection TryFinallyCanBeTryWithResources
                try {
                    new GpxWriter(osw).writeFooter();
                } finally {
                    //noinspection ThrowFromFinallyBlock
                    osw.close();
                }
            } catch (IOException e) {
                Crashlytics.logException(e);
                Toast.makeText(this, "Could not write to track.gpx!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onEvent(@SuppressWarnings("UnusedParameters") CreateImageFile event) {
        Monitoring monitoring = isEmpty(event.monitoringCode) || (this.monitoring != null && TextUtils.equals(event.monitoringCode, this.monitoring.code)) ?
                this.monitoring :
                monitoringManager.getMonitoring(event.monitoringCode);
        // Create an image file name
        int cnt = 100;
        while (monitoring != null && cnt-- > 0) {
            String index = Integer.toString(monitoring.pictureCounter++);
            monitoringManager.update(monitoring);
            while (index.length() < 4) index = '0' + index;
            String imageFileName = "Pic" + index + ".jpg";
            File image = new File(createMonitoringDir(monitoring), imageFileName);
            try {
                if (image.createNewFile()) {
                    bus.post(new ImageFileCreated(monitoring.code, imageFileName, Uri.fromFile(image), image.getAbsolutePath()));
                    return;
                }
            } catch (IOException e) {
                Log.d(TAG, "Image file create error", e);
                Crashlytics.logException(e);
            }
        }
        bus.post(new ImageFileCreatedFailed(monitoring != null ? monitoring.code : null));
    }

    public void onEvent(GetImageFile event) {
        File image = new File(DataOpsService_.getMonitoringDir(this, isEmpty(event.monitoringCode) ? monitoring.code : event.monitoringCode), event.fileName);
        bus.post(new ImageFileEvent(isEmpty(event.monitoringCode) ? monitoring.code : event.monitoringCode, event.fileName, Uri.fromFile(image), image.getAbsolutePath()));
    }

    public void onEvent(@SuppressWarnings("UnusedParameters") GetMonitoringCommonData event) {
        bus.post(new MonitoringCommonData(monitoring.commonForm));
    }

    public void onEvent(@SuppressWarnings("UnusedParameters") UndoLastEntry event) {
        boolean success = monitoringManager.deleteLastEntry(monitoring);
        if (!success) Log.e(TAG, "could not delete last monitoring entry");
    }

    public void onEvent(@SuppressWarnings("UnusedParameters") QueryActiveMonitoringEvent event) {
        bus.postSticky(new ActiveMonitoringEvent(monitoring));
    }
}
