package org.bspb.smartbirds.pro.prefs;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by dani on 14-11-12.
 */
@SharedPref(SharedPref.Scope.APPLICATION_DEFAULT)
public interface SmartBirdsPrefs {

    @DefaultBoolean(false)
    boolean runningMonitoring();

    String providerType();

    String mapType();

    int zoomFactor();

    @DefaultBoolean(false)
    boolean stayAwake();
}
