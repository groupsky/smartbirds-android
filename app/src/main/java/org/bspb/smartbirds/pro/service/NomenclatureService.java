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
import org.bspb.smartbirds.pro.backend.dto.Location;
import org.bspb.smartbirds.pro.backend.dto.Nomenclature;
import org.bspb.smartbirds.pro.backend.dto.ResponseListEnvelope;
import org.bspb.smartbirds.pro.backend.dto.SpeciesNomenclature;
import org.bspb.smartbirds.pro.db.SmartBirdsProvider.Locations;
import org.bspb.smartbirds.pro.db.SmartBirdsProvider.Nomenclatures;
import org.bspb.smartbirds.pro.events.DownloadCompleted;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.StartingDownload;
import org.bspb.smartbirds.pro.ui.utils.NomenclaturesBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Response;

import static org.bspb.smartbirds.pro.db.SmartBirdsProvider.AUTHORITY;
import static org.bspb.smartbirds.pro.service.NomenclatureService.Downloading.LOCATIONS;
import static org.bspb.smartbirds.pro.service.NomenclatureService.Downloading.NOMENCLATURES;
import static org.bspb.smartbirds.pro.tools.Reporting.logException;

/**
 * Created by groupsky on 27.09.16.
 */

@EIntentService
public class NomenclatureService extends AbstractIntentService {

    enum Downloading {
        LOCATIONS,
        NOMENCLATURES,
    }

    public static final Set<Downloading> isDownloading = new HashSet<>();

    @Bean
    Backend backend;
    @Bean
    EEventBus bus;
    @Bean
    NomenclaturesBean nomenclaturesBean;

    public NomenclatureService() {
        super("NomenclatureService");
    }


    @ServiceAction
    public void downloadLocations() {
        isDownloading.add(LOCATIONS);
        try {
            bus.post(new StartingDownload());
            try {
                ArrayList<ContentProviderOperation> buffer = new ArrayList<>();
                buffer.add(ContentProviderOperation.newDelete(Locations.CONTENT_URI).build());
                Response<ResponseListEnvelope<Location>> response = backend.api().listLocations().execute();
                if (!response.isSuccessful())
                    throw new IOException("Server error: " + response.code() + " - " + response.message());
                for (Location location : response.body().data) {
                    buffer.add(ContentProviderOperation
                            .newInsert(Locations.CONTENT_URI)
                            .withValues(location.toCV())
                            .build());
                }
                getContentResolver().applyBatch(AUTHORITY, buffer);
            } catch (Throwable t) {
                logException(t);
                showToast("Could not download locations. Try again.");
            }
        } finally {
            isDownloading.remove(LOCATIONS);
            if (isDownloading.isEmpty()) {
                bus.post(new DownloadCompleted());
            }
        }
    }

    @ServiceAction
    void updateNomenclatures() {
        isDownloading.add(NOMENCLATURES);
        try {
            bus.post(new StartingDownload());
            try {
                int limit = 500;
                int offset = 0;
                ArrayList<ContentProviderOperation> buffer = new ArrayList<>();
                while (true) {
                    Response<ResponseListEnvelope<Nomenclature>> response = backend.api().nomenclatures(limit, offset).execute();
                    if (!response.isSuccessful())
                        throw new IOException("Server error: " + response.code() + " - " + response.message());
                    if (response.body().data.isEmpty()) break;
                    offset += nomenclaturesBean.prepareNomenclatureCPO(response.body().data, buffer);
                }

                // if we received nomenclatures
                if (!buffer.isEmpty()) {
                    buffer.add(0, ContentProviderOperation.newDelete(Nomenclatures.CONTENT_URI).build());
                    getContentResolver().applyBatch(AUTHORITY, buffer);
                    buffer.clear();

                    limit = 500;
                    offset = 0;
                    while (true) {
                        Response<ResponseListEnvelope<SpeciesNomenclature>> response = backend.api().species(limit, offset).execute();
                        if (!response.isSuccessful())
                            throw new IOException("Server error: " + response.code() + " - " + response.message());
                        if (response.body().data.isEmpty()) break;
                        offset += nomenclaturesBean.prepareSpeciesCPO(response.body().data, buffer);
                    }

                    // if we received nomenclatures
                    if (!buffer.isEmpty()) {
                        getContentResolver().applyBatch(AUTHORITY, buffer);
                        buffer.clear();
                    }
                }

            } catch (Throwable t) {
                logException(t);
                showToast("Could not download nomenclatures. Try again.");
            }
        } finally {
            isDownloading.remove(NOMENCLATURES);
            if (isDownloading.isEmpty()) {
                bus.post(new DownloadCompleted());
            }
        }
    }

    protected void showToast(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
