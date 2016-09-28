package org.bspb.smartbirds.pro.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.googlecode.jcsv.CSVStrategy;
import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;
import com.googlecode.jcsv.reader.internal.DefaultCSVEntryParser;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;
import org.apache.commons.net.ftp.FTPClient;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.backend.Backend;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.StartingUpload;
import org.bspb.smartbirds.pro.events.UploadCompleted;
import org.bspb.smartbirds.pro.forms.BirdsUploader;
import org.bspb.smartbirds.pro.forms.CbmUploader;
import org.bspb.smartbirds.pro.forms.CiconiaUploader;
import org.bspb.smartbirds.pro.forms.HerpUploader;
import org.bspb.smartbirds.pro.forms.Uploader;
import org.bspb.smartbirds.pro.ui.utils.FTPClientUtils;
import org.bspb.smartbirds.pro.ui.utils.NomenclaturesBean;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static org.bspb.smartbirds.pro.tools.Reporting.logException;

@EIntentService
public class UploadService extends IntentService {

    private static final String TAG = SmartBirdsApplication.TAG + ".UploadService";

    @Bean
    EEventBus eventBus;
    @Bean
    Backend backend;

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
        // TODO: commented out just for debug
//        NomenclatureService_.intent(this).updateNomenclatures().start();
        eventBus.post(new UploadCompleted());
    }

    @ServiceAction()
    void upload(String monitoringPath) {
        Log.d(TAG, String.format("uploading %s", monitoringPath));
        File file = new File(monitoringPath);
        String monitoringName = file.getName().replace("-up", "");
        Log.d(TAG, String.format("uploading %s", monitoringName));

        try {
            uploadOnServer(monitoringPath, monitoringName);
            uploadOnFtp(monitoringPath, monitoringName);
            file.renameTo(new File(monitoringPath.replace("-up", "")));
        } catch (Throwable e) {
            logException(e);
        }
    }

    private void uploadOnServer(String monitoringPath, String monitoringName) throws Exception {
        File file = new File(monitoringPath);
        for (String subfile : file.list()) {
            switch (subfile) {
                case "form_bird.csv":
                    uploadForm(new File(file, subfile), new BirdsUploader());
                    break;
                case "form_herp_mam.csv":
                    uploadForm(new File(file, subfile), new HerpUploader());
                    break;
                case "form_ciconia.csv":
                    uploadForm(new File(file, subfile), new CiconiaUploader());
                    break;
                case "form_cbm.csv":
                    uploadForm(new File(file, subfile), new CbmUploader());
                    break;
            }
        }
    }

    private void uploadForm(File file, Uploader uploader) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        try {
            CSVReader<String[]> csvReader = new CSVReaderBuilder<String[]>(new InputStreamReader(new BufferedInputStream(fis))).strategy(CSVStrategy.DEFAULT).entryParser(new DefaultCSVEntryParser()).build();
            try {
                List<String> header = csvReader.readHeader();
                for (String[] row : csvReader) {
                    if (!uploader.upload(backend.api(), header, row))
                        throw new IOException("Couldn't upload form");
                }
            } finally {
                csvReader.close();
            }
        } finally {
            fis.close();
        }
    }

    private void uploadOnFtp(String monitoringPath, String monitoringName) throws IOException {
        FTPClient ftpClient = FTPClientUtils.connect();
        Log.d(TAG, String.format("mkdir %s", monitoringName));
        ftpClient.makeDirectory(monitoringName);
        File file = new File(monitoringPath);
        for (String subfile : file.list()) {
            doUpload(ftpClient, new File(file, subfile), monitoringName + '/');
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
