package org.bspb.smartbirds.pro.service;

import android.content.ContentProviderOperation;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.MainThread;
import androidx.annotation.UiThread;
import android.widget.Toast;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;
import org.androidannotations.api.support.app.AbstractIntentService;
import org.bspb.smartbirds.pro.backend.Backend;
import org.bspb.smartbirds.pro.backend.dto.ResponseListEnvelope;
import org.bspb.smartbirds.pro.backend.dto.Zone;
import org.bspb.smartbirds.pro.events.DownloadCompleted;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.StartingDownload;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Response;

import static org.bspb.smartbirds.pro.db.SmartBirdsProvider.AUTHORITY;
import static org.bspb.smartbirds.pro.db.SmartBirdsProvider.Zones.CONTENT_URI;
import static org.bspb.smartbirds.pro.tools.Reporting.logException;

/**
 * Created by groupsky on 06.10.16.
 */

@EIntentService
public class ZoneService extends AbstractIntentService {

    public static boolean isDownloading;

    @Bean
    Backend backend;
    @Bean
    EEventBus bus;

    public ZoneService() {
        super("ZoneService");
    }

    @ServiceAction
    public void downloadZones() {
        isDownloading = true;
        try {
            bus.post(new StartingDownload());
            try {
                ArrayList<ContentProviderOperation> buffer = new ArrayList<>();
                buffer.add(ContentProviderOperation.newDelete(CONTENT_URI).build());
                Response<ResponseListEnvelope<Zone>> response = backend.api().listZones().execute();
                if (!response.isSuccessful())
                    throw new IOException("Server error: " + response.code() + " - " + response.message());
                for (Zone zone : response.body().data) {
                    buffer.add(ContentProviderOperation
                            .newInsert(CONTENT_URI)
                            .withValues(zone.toCV())
                            .build());
                }
                getContentResolver().applyBatch(AUTHORITY, buffer);
            } catch (Throwable t) {
                logException(t);
                showToast("Could not download zones. Try again.");
            }
            bus.post(new DownloadCompleted());
        } finally {
            isDownloading = false;
        }
    }

    private void showToast(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
