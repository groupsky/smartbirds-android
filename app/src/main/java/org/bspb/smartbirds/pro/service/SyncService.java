package org.bspb.smartbirds.pro.service;

import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;
import org.androidannotations.api.support.app.AbstractIntentService;

/**
 * Created by groupsky on 06.10.16.
 */

@EIntentService
public class SyncService extends AbstractIntentService {
    public SyncService() {
        super("SyncService");
    }

    @ServiceAction
    public void sync() {
        UploadService_.intent(this).uploadAll().start();
        initialSync();
    }

    @ServiceAction
    public void initialSync() {
        NomenclatureService_.intent(this).updateNomenclatures().start();
        NomenclatureService_.intent(this).downloadLocations().start();
        ZoneService_.intent(this).downloadZones().start();
    }
}
