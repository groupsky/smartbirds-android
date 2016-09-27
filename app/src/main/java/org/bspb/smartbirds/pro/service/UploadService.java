package org.bspb.smartbirds.pro.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;
import org.apache.commons.net.ftp.FTPClient;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.StartingUpload;
import org.bspb.smartbirds.pro.events.UploadCompleted;
import org.bspb.smartbirds.pro.ui.utils.FTPClientUtils;
import org.bspb.smartbirds.pro.ui.utils.NomenclaturesBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@EIntentService
public class UploadService extends IntentService {

    private static final String TAG = SmartBirdsApplication.TAG + ".UploadService";

    @Bean
    EEventBus eventBus;

    @Bean
    NomenclaturesBean nomenclaturesBean;

    protected UploadService() {
        super("Upload Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @ServiceAction
    void uploadAll() {
        Log.d(TAG, "uploading all finished monitorings");
        eventBus.post(new StartingUpload());
        File baseDir = getExternalFilesDir(null);
        for (String monitoring : baseDir.list()) {
            if (!monitoring.endsWith("-up")) continue;
            File monitoringDir = new File(baseDir, monitoring);
            if (!monitoringDir.isDirectory()) continue;
            upload(monitoringDir.getAbsolutePath());
        }
        eventBus.post(new UploadCompleted());
    }

    @ServiceAction()
    void upload(String monitoringPath) {
        Log.d(TAG, String.format("uploading %s", monitoringPath));
        File file = new File(monitoringPath);
        String monitoringName = file.getName().replace("-up", "");
        Log.d(TAG, String.format("uploading %s", monitoringName));

        FTPClient ftpClient = FTPClientUtils.connect();
        try {
            Log.d(TAG, String.format("mkdir %s", monitoringName));
            ftpClient.makeDirectory(monitoringName);
            for (String subfile : file.list()) {
                doUpload(ftpClient, new File(file, subfile), monitoringName + '/');
            }

            file.renameTo(new File(monitoringPath.replace("-up", "")));
        } catch (IOException e) {
            Log.e(TAG, String.format("error while uploading: %s", e.getMessage()), e);
        }
    }

    private void doUpload(FTPClient ftp, File localFile, String remotePrefix) throws IOException {
        if (localFile.isDirectory()) {
            Log.d(TAG, String.format("mkdir %s", remotePrefix + localFile.getName()));
            ftp.makeDirectory(remotePrefix + localFile.getName());
            for (String subfile : localFile.list())
                doUpload(ftp, new File(localFile, subfile), remotePrefix + localFile.getName() + '/');
        } else {
            FileInputStream localStream = new FileInputStream(localFile);
            Log.d(TAG, String.format("store %s (%d)", remotePrefix + localFile.getName() + ".tmp", localStream.available()));
            ftp.storeFile(remotePrefix + localFile.getName() + ".tmp", localStream);
            localStream.close();
            Log.d(TAG, String.format("rename %s -> %s", remotePrefix + localFile.getName() + ".tmp", remotePrefix + localFile.getName()));
            ftp.rename(remotePrefix + localFile.getName() + ".tmp", remotePrefix + localFile.getName());
        }
    }

}
