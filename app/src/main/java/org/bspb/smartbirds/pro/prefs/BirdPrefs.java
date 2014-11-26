package org.bspb.smartbirds.pro.prefs;

import android.widget.AdapterView;

import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by dani on 14-11-18.
 */
@SharedPref
public interface BirdPrefs {

    @DefaultInt(AdapterView.INVALID_POSITION)
    int birdCountUnitsPosition();

    @DefaultInt(AdapterView.INVALID_POSITION)
    int birdCountTypePosition();

}
