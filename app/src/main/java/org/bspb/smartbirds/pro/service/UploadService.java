package org.bspb.smartbirds.pro.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.UploadCompleted;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@EIntentService
public class UploadService extends IntentService {

    private static final String TAG = SmartBirdsApplication.TAG + ".UploadService";

    @Bean
    EEventBus eventBus;

    protected UploadService() {
        super("Upload Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @ServiceAction()
    void upload(String monitoringPath) {
        Log.d(TAG, String.format("uploading %s", monitoringPath));
        File file = new File(monitoringPath);
        String monitoringName = file.getName().replace("-up", "");
        Log.d(TAG, String.format("uploading %s", monitoringName));
        FTPClient ftpClient = new FTPClient();
        try {
            Log.d(TAG, "connecting");
            ftpClient.connect("87.252.173.51");
//            ftpClient.connect("192.168.1.130");
            Log.d(TAG, "authorizing");
            ftpClient.login("gis", "Pomarina1");
//            ftpClient.login("dani", "alabala");

            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            Log.d(TAG, String.format("mkdir %s", monitoringName));
            ftpClient.makeDirectory(monitoringName);
            for (String subfile: file.list()) {
                doUpload(ftpClient, new File(file, subfile), monitoringName+'/');
            }

            file.renameTo(new File(monitoringPath.replace("-up", "")));
            eventBus.post(new UploadCompleted(monitoringPath.replace("-up", "")));
        } catch (IOException e) {
            Log.e(TAG, String.format("error while uploading: %s", e.getMessage()), e);
        }
    }

    private void doUpload(FTPClient ftp, File localFile, String remotePrefix) throws IOException {
        if (localFile.isDirectory()) {
            Log.d(TAG, String.format("mkdir %s", remotePrefix+localFile.getName()));
            ftp.makeDirectory(remotePrefix+localFile.getName());
            for (String subfile: localFile.list())
                doUpload(ftp, new File(localFile, subfile), remotePrefix+localFile.getName()+'/');
        } else {
            FileInputStream localStream = new FileInputStream(localFile);
            Log.d(TAG, String.format("store %s (%d)", remotePrefix+localFile.getName()+".tmp", localStream.available()));
            ftp.storeFile(remotePrefix+localFile.getName() + ".tmp", localStream);
            localStream.close();
            Log.d(TAG, String.format("rename %s -> %s", remotePrefix + localFile.getName() + ".tmp", remotePrefix + localFile.getName()));
            ftp.rename(remotePrefix+localFile.getName()+".tmp", remotePrefix+localFile.getName());
        }
    }

}
