package org.bspb.smartbirds.pro.prefs;

import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by dani on 14-11-18.
 */
@SharedPref(SharedPref.Scope.UNIQUE)
public interface DataServicePrefs {

    String monitoringDir();

    String monitoringName();

    int pictureCounter();


}
