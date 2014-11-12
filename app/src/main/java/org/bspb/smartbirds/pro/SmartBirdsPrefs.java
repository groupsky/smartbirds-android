package org.bspb.smartbirds.pro;

import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by dani on 14-11-12.
 */
@SharedPref
public interface SmartBirdsPrefs {

    String cbmName();

    String cbmDistance();

    String cbmPrimaryHabitat();

    String cbmSecondaryHabitat();

    String cbmVisit();

    String cbmSector();
}
