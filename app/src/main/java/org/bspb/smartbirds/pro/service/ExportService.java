package org.bspb.smartbirds.pro.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import androidx.core.content.FileProvider;
import android.util.Log;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.content.MonitoringManager;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.ExportFailedEvent;
import org.bspb.smartbirds.pro.events.ExportPreparedEvent;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.bspb.smartbirds.pro.content.Monitoring.Status.finished;

/**
 * Created by dani on 14-11-18.
 */
@EIntentService
public class ExportService extends IntentService {

    private static final String TAG = SmartBirdsApplication.TAG + ".ExportService";

    private static final int BUFFER = 2048;

    @Bean
    EEventBus eventBus;

    @Bean
    MonitoringManager monitoringManager;

    public ExportService() {
        super("Export Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @ServiceAction
    public void prepareForExport() {
        Log.d(TAG, "Prepare for export all finished monitorings");
        File exportFile = new File(getExternalFilesDir(null), "export.zip");
        try {
            FileOutputStream exportOutStream = new FileOutputStream(exportFile);
            ZipOutputStream zipOut = new ZipOutputStream(exportOutStream);
            File baseDir = getExternalFilesDir(null);

            byte data[] = new byte[BUFFER];

            for (String monitoringCode: monitoringManager.monitoringCodesForStatus(finished)) {
                File monitoring = new File(baseDir, monitoringCode);
                if (!monitoring.exists()) continue;
                if (!monitoring.isDirectory()) continue;
                for (File fileToZip : monitoring.listFiles()) {
                    ZipEntry entry = new ZipEntry(monitoring.getName() + "/" + fileToZip.getName());
                    zipOut.putNextEntry(entry);
                    FileInputStream inputStream = new FileInputStream(fileToZip);
                    BufferedInputStream in = new BufferedInputStream(inputStream);
                    int count;
                    while ((count = in.read(data, 0, BUFFER)) != -1) {
                        zipOut.write(data, 0, count);
                    }
                    zipOut.closeEntry();
                    in.close();
                }
            }

            zipOut.close();
        } catch (Exception e) {
            Log.e(TAG, "Error while preparing zip for export", e);
            eventBus.post(new ExportFailedEvent());
            return;
        }

        Uri uri = FileProvider.getUriForFile(getApplicationContext(), SmartBirdsApplication.FILES_AUTHORITY, exportFile);
        eventBus.post(new ExportPreparedEvent(uri));
    }
}
