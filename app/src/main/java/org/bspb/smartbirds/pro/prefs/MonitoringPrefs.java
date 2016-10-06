package org.bspb.smartbirds.pro.prefs;

import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by dani on 14-11-19.
 */
@SharedPref(SharedPref.Scope.UNIQUE)
public interface MonitoringPrefs {

    String mapType();

    int markersCount();

    int pointsCount();

    float lastPositionLat();

    float lastPositionLon();

    int zoomFactor();

    int lastEntryTypePosition();
}
