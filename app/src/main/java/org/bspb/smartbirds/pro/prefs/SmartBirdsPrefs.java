package org.bspb.smartbirds.pro.prefs;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.DefaultStringSet;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

import java.util.Set;

import static org.bspb.smartbirds.pro.ui.utils.Constants.VIEWTYPE_MAP;

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

    @DefaultString(VIEWTYPE_MAP)
    String monitoringViewType();

    @DefaultBoolean(true)
    boolean showZoneBackground();

    @DefaultBoolean(false)
    boolean pausedMonitoring();

    @DefaultBoolean(false)
    boolean isBatteryOptimizationDialogShown();

    @DefaultBoolean(false)
    boolean showLocalProjects();

    @DefaultBoolean(true)
    boolean showBgAtlasCells();

    @DefaultBoolean(false)
    boolean showSPA();

    Set<String> formsEnabled();
}
