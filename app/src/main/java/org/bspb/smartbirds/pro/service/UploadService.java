package org.bspb.smartbirds.pro.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonObject;
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
import org.bspb.smartbirds.pro.backend.Converter;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.StartingUpload;
import org.bspb.smartbirds.pro.events.UploadCompleted;
import org.bspb.smartbirds.pro.ui.utils.FTPClientUtils;
import org.bspb.smartbirds.pro.ui.utils.NomenclaturesBean;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import retrofit2.Response;

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
        NomenclatureService_.intent(this).updateNomenclatures().start();
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
        } catch (IOException e) {
            Log.e(TAG, String.format("error while uploading: %s", e.getMessage()), e);
        }
    }

    private void uploadOnServer(String monitoringPath, String monitoringName) throws IOException {
        File file = new File(monitoringPath);
        for (String subfile : file.list()) {
            switch (subfile) {
                case "form_bird.csv":
                    uploadBirds(new File(file, subfile));
                    break;
            }
        }
    }

    private void uploadBirds(File file) throws IOException {
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            logException(e);
            throw new IOException(e.getMessage(), e);
        }
        try {
            CSVReader<String[]> csvReader = new CSVReaderBuilder<String[]>(new InputStreamReader(new BufferedInputStream(fis))).strategy(CSVStrategy.DEFAULT).entryParser(new DefaultCSVEntryParser()).build();
            try {
                List<String> header = csvReader.readHeader();
                for (String[] row: csvReader) {
                    JsonObject obj = null;
                    try {
                        obj = Converter.convertBirds(header, row);
                    } catch (Exception e) {
                        logException(e);
                        throw new IOException(e.getMessage(), e);
                    }
                    Log.d(TAG, "creating "+obj);
                    Response<ResponseBody> res = backend.api().createBirds(obj).execute();
                    Log.d(TAG, "response "+res);
                    if (!res.isSuccessful()) throw new IOException("Something bad happened: "+res.code()+" - "+res.message());
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
