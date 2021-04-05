package org.bspb.smartbirds.pro.service;

import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.api.support.app.AbstractIntentService;
import org.bspb.smartbirds.pro.prefs.UserPrefs_;

/**
 * Created by groupsky on 06.10.16.
 */

@EIntentService
public class SyncService extends AbstractIntentService {
    public SyncService() {
        super("SyncService");
    }

    @Pref
    UserPrefs_ prefs;

    @ServiceAction
    public void sync(Long tag) {
        UploadService_.intent(this).uploadAll(tag).start();
        initialSync();
    }

    @ServiceAction
    public void initialSync() {
        AuthenticationService_.intent(this).checkSession().start();
        NomenclatureService_.intent(this).updateNomenclatures().start();
        NomenclatureService_.intent(this).downloadLocations().start();
        ZoneService_.intent(this).downloadZones().start();
    }
}
