package org.bspb.smartbirds.pro.prefs;

import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by groupsky on 22.03.16.
 */
@SharedPref(SharedPref.Scope.APPLICATION_DEFAULT)
public interface CommonPrefs {

    String commonOtherObservers();
}
