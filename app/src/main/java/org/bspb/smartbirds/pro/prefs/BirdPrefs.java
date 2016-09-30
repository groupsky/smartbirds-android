package org.bspb.smartbirds.pro.prefs;

import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by dani on 14-11-18.
 */
@SharedPref
public interface BirdPrefs {

    String birdCountUnits();

    String birdCountType();

}
