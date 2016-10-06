package org.bspb.smartbirds.pro.prefs;

import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by dani on 14-11-18.
 */
@SharedPref
public interface CbmPrefs {

    String cbmPrimaryHabitat();

    String cbmSecondaryHabitat();

    String cbmVisit();

    String cbmSector();

    String cbmZone();

}
