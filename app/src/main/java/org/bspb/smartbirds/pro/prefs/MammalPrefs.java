package org.bspb.smartbirds.pro.prefs;

import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by dani on 14-11-18.
 */
@SharedPref
public interface MammalPrefs {

    String mammalHabitat();

    @DefaultString("1")
    String mammalCount();


}
