package org.bspb.smartbirds.pro.service;

import android.content.ContentProviderOperation;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;
import org.androidannotations.api.support.app.AbstractIntentService;
import org.bspb.smartbirds.pro.backend.Backend;
import org.bspb.smartbirds.pro.backend.dto.Nomenclature;
import org.bspb.smartbirds.pro.backend.dto.ResponseListEnvelope;

import java.util.ArrayList;

import retrofit2.Response;

import static org.bspb.smartbirds.pro.db.NomenclatureColumns.LABEL_BG;
import static org.bspb.smartbirds.pro.db.NomenclatureColumns.LABEL_EN;
import static org.bspb.smartbirds.pro.db.NomenclatureColumns.TYPE;
import static org.bspb.smartbirds.pro.db.SmartBirdsProvider.AUTHORITY;
import static org.bspb.smartbirds.pro.db.SmartBirdsProvider.Nomenclatures.CONTENT_URI;
import static org.bspb.smartbirds.pro.tools.Reporting.logException;

/**
 * Created by groupsky on 27.09.16.
 */

@EIntentService
public class NomenclatureService extends AbstractIntentService {

    @Bean
    Backend backend;

    public NomenclatureService() {
        super("NomenclatureService");
    }

    @ServiceAction
    void updateNomenclatures() {
        try {
            int limit = 500;
            int offset = 0;
            ArrayList<ContentProviderOperation> buffer = new ArrayList<>();
            while (true) {
                Response<ResponseListEnvelope<Nomenclature>> response = backend.api().nomenclatures(limit, offset).execute();
                if (!response.isSuccessful()) break;
                if (response.body().data.isEmpty()) break;
                for (Nomenclature nomenclature : response.body().data) {
                    // increase the offset
                    offset++;

                    // insert the nomenclature value
                    buffer.add(ContentProviderOperation.newInsert(CONTENT_URI)
                            .withValue(TYPE, nomenclature.type)
                            .withValue(LABEL_BG, nomenclature.label.bg)
                            .withValue(LABEL_EN, nomenclature.label.en)
                            .build());
                }
            }

            // if we received nomenclatures
            if (!buffer.isEmpty()) {
                buffer.add(0, ContentProviderOperation.newDelete(CONTENT_URI).build());
                getContentResolver().applyBatch(AUTHORITY, buffer);
                buffer.clear();
            }
        } catch (Throwable t) {
            logException(t);
        }
    }
}
