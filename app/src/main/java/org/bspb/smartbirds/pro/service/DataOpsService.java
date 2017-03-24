package org.bspb.smartbirds.pro.service;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.googlecode.jcsv.CSVStrategy;
import com.googlecode.jcsv.writer.CSVWriter;
import com.googlecode.jcsv.writer.internal.CSVWriterBuilder;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;
import org.androidannotations.api.support.app.AbstractIntentService;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.content.Monitoring;
import org.bspb.smartbirds.pro.content.MonitoringEntry;
import org.bspb.smartbirds.pro.content.MonitoringManager;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.tools.CsvPreparer;
import org.bspb.smartbirds.pro.tools.SmartBirdsCSVEntryConverter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;

import static org.bspb.smartbirds.pro.tools.CsvPreparer.prepareCsvLine;
import static org.bspb.smartbirds.pro.tools.Reporting.logException;

/**
 * Created by groupsky on 24.03.17.
 */

@EIntentService
public class DataOpsService extends AbstractIntentService {

    private static final String TAG = SmartBirdsApplication.TAG + ".DataOpsSvc";
    @Bean
    MonitoringManager monitoringManager;

    public DataOpsService() {
        super("DataOpsService");
    }

    public static File getMonitoringDir(Context context, String monitoringCode) {
        return new File(context.getExternalFilesDir(null), monitoringCode);
    }

    public static File createMonitoringDir(Context context, Monitoring monitoring) {
        File dir = getMonitoringDir(context, monitoring.code);
        if (dir.exists()) return dir;
        if (dir.mkdirs()) return dir;

        Log.w(TAG, String.format("Cannot create %s", dir));
        dir = getMonitoringDir(context, monitoring.code);
        if (dir.exists()) return dir;
        if (dir.mkdirs()) return dir;

        Log.e(TAG, String.format("Cannot create %s", dir));

        return null;
    }

    @ServiceAction
    public void generateMonitoringFiles(String monitoringCode) {
        try {
            Log.d(TAG, String.format(Locale.ENGLISH, "generateMonitoringFiles: %s", monitoringCode));
            Monitoring monitoring = monitoringManager.getMonitoring(monitoringCode);
            combineCommonWithEntries(monitoring);
        } catch (Throwable t) {
            logException(t);
        }
    }

    private void combineCommonWithEntries(Monitoring monitoring) {
        try {
            EntryType[] types = EntryType.values();
            for (EntryType entryType : types) {
                File entriesFile = getEntriesFile(monitoring, entryType);
                String[] commonLines = convertToCsvLines(monitoring.commonForm);

                Cursor cursor = monitoringManager.getEntries(monitoring, entryType);
                if (cursor != null) try {
                    if (cursor.getCount() == 0) {
                        if (entriesFile.exists())
                            entriesFile.delete();
                        continue;
                    }
                    BufferedWriter outWriter = new BufferedWriter(new FileWriter(entriesFile));
                    //noinspection TryFinallyCanBeTryWithResources
                    try {
                        boolean firstLine = true;
                        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                            MonitoringEntry entry = MonitoringManager.entryFromCursor(cursor);
                            String[] lines = convertToCsvLines(entry.data);
                            if (firstLine) {
                                outWriter.write(commonLines[0]);
                                outWriter.write(CSVStrategy.DEFAULT.getDelimiter());
                                outWriter.write(lines[0]);
                                outWriter.newLine();
                                firstLine = false;
                            }
                            outWriter.write(commonLines[1]);
                            outWriter.write(CSVStrategy.DEFAULT.getDelimiter());
                            outWriter.write(lines[1]);
                            outWriter.newLine();
                        }
                    } finally {
                        //noinspection ThrowFromFinallyBlock
                        outWriter.close();
                    }
                } finally {
                    cursor.close();
                }
            }
        } catch (Throwable t) {
            Crashlytics.logException(t);
        }
    }

    private String[] convertToCsvLines(HashMap<String, String> data) throws IOException {
        CsvPreparer.PreparedLine prepared = prepareCsvLine(data);

        StringWriter memory = new StringWriter();
        try {
            CSVWriter<String[]> csvWriter = new CSVWriterBuilder<String[]>(memory).strategy(CSVStrategy.DEFAULT).entryConverter(new SmartBirdsCSVEntryConverter()).build();
            csvWriter.write(prepared.keys);
            csvWriter.write(prepared.values);
            memory.flush();
        } finally {
            //noinspection ThrowFromFinallyBlock
            memory.close();
        }
        String commonData = memory.getBuffer().toString();
        return commonData.split(System.getProperty("line.separator"));
    }

    private File getEntriesFile(Monitoring monitoring, EntryType entryType) {
        String filename = entryType.filename;
        File file = new File(createMonitoringDir(this, monitoring), filename);
        //noinspection ResultOfMethodCallIgnored
        file.setReadable(true);
        return file;
    }


}
