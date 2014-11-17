package org.bspb.smartbirds.pro;

import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by dani on 14-11-12.
 */
@SharedPref
public interface SmartBirdsPrefs {

    String cbmPrimaryHabitat();

    String cbmSecondaryHabitat();

    String cbmVisit();

    String cbmSector();

    String birdCountUnits();

    String birdCountType();

    String herpHabitat();

    @DefaultString("1")
    String herpCount();
}
