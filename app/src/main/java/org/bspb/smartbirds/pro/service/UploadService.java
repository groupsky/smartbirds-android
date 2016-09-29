package org.bspb.smartbirds.pro.service;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.googlecode.jcsv.CSVStrategy;
import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;
import org.apache.commons.net.ftp.FTPClient;
import org.bspb.smartbirds.pro.BuildConfig;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.backend.Backend;
import org.bspb.smartbirds.pro.backend.dto.FileId;
import org.bspb.smartbirds.pro.backend.dto.ResponseEnvelope;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.StartingUpload;
import org.bspb.smartbirds.pro.events.UploadCompleted;
import org.bspb.smartbirds.pro.forms.convert.BirdsConverter;
import org.bspb.smartbirds.pro.forms.convert.CbmConverter;
import org.bspb.smartbirds.pro.forms.convert.CiconiaConverter;
import org.bspb.smartbirds.pro.forms.convert.Converter;
import org.bspb.smartbirds.pro.forms.convert.HerpConverter;
import org.bspb.smartbirds.pro.forms.upload.BirdsUploader;
import org.bspb.smartbirds.pro.forms.upload.CbmUploader;
import org.bspb.smartbirds.pro.forms.upload.CiconiaUploader;
import org.bspb.smartbirds.pro.forms.upload.HerpUploader;
import org.bspb.smartbirds.pro.forms.upload.Uploader;
import org.bspb.smartbirds.pro.tools.SmartBirdsCSVEntryParser;
import org.bspb.smartbirds.pro.ui.utils.FTPClientUtils;
import org.bspb.smartbirds.pro.ui.utils.NomenclaturesBean;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
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
            uploadOnFtp(monitoringPath, monitoringName);
            uploadOnServer(monitoringPath, monitoringName);
            file.renameTo(new File(monitoringPath.replace("-up", "")));
        } catch (Throwable e) {
            logException(e);
        }
    }

    private void uploadOnServer(String monitoringPath, String monitoringName) throws Exception {
        File file = new File(monitoringPath);

        // map between filenames and their ids
        Map<String, JsonObject> fileObjs = new HashMap<>();

        // first upload images
        for (String subfile : file.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.matches("Pic\\d+\\.jpg") || "track.gpx".equals(name);
            }
        })) {
            fileObjs.put(subfile, uploadFile(new File(file, subfile)));
        }

        // then upload forms
        for (String subfile : file.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.matches(".*\\.csv");
            }
        })) {
            Converter converter;
            Uploader uploader;
            switch (subfile) {
                case "form_bird.csv":
                    converter = new BirdsConverter(this);
                    uploader = new BirdsUploader();
                    break;
                case "form_herp_mam.csv":
                    converter = new HerpConverter(this);
                    uploader = new HerpUploader();
                    break;
                case "form_ciconia.csv":
                    converter = new CiconiaConverter(this);
                    uploader = new CiconiaUploader();
                    break;
                case "form_cbm.csv":
                    converter = new CbmConverter(this);
                    uploader = new CbmUploader();
                    break;
                default:
                    Log.w(TAG, "Unhandled form file: " + subfile);
                    continue;
            }
            uploadForm(new File(file, subfile), converter, uploader, fileObjs);
        }
    }

    private JsonObject uploadFile(File file) throws IOException {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        Call<ResponseEnvelope<FileId>> call = backend.api().upload(body);
        Response<ResponseEnvelope<FileId>> response = call.execute();
        if (!response.isSuccessful()) {
            throw new IOException("Server error: " + response.code() + " - " + response.message());
        }

        JsonObject fileObj = new JsonObject();
        fileObj.addProperty("url", String.format("%sstorage/%s", BuildConfig.BACKEND_BASE_URL, response.body().data.id));
        return fileObj;
    }

    private void uploadForm(File file, Converter converter, Uploader uploader, Map<String, JsonObject> fileObjs) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        try {
            CSVReader<String[]> csvReader = new CSVReaderBuilder<String[]>(new InputStreamReader(new BufferedInputStream(fis))).strategy(CSVStrategy.DEFAULT).entryParser(new SmartBirdsCSVEntryParser()).build();
            try {
                List<String> header = csvReader.readHeader();
                for (String[] row : csvReader) {
                    HashMap<String, String> csv = new HashMap<>();
                    Iterator<String> it = header.iterator();
                    String columnName;
                    for (int idx = 0; it.hasNext() && idx < row.length; idx++) {
                        columnName = it.next();
                        csv.put(columnName, row[idx]);
                    }

                    JsonObject data = converter.convert(csv);

                    // convert pictures
                    JsonArray pictures = new JsonArray();
                    int idx = 0;
                    while (true) {
                        String fieldName = "Picture" + idx;
                        idx++;
                        if (!csv.containsKey(fieldName)) break;
                        String filename = csv.get(fieldName);
                        if (TextUtils.isEmpty(filename)) continue;
                        JsonObject fileObj = fileObjs.get(filename);
                        if (fileObj == null)
                            throw new IllegalStateException("Missing file: " + filename);
                        pictures.add(fileObj);
                    }
                    data.add("pictures", pictures);

                    // convert gpx
                    data.add("track", fileObjs.get("track.gpx").get("url"));

                    Call<ResponseBody> call = uploader.upload(backend.api(), data);
                    Response<ResponseBody> response = call.execute();
                    if (!response.isSuccessful())
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
