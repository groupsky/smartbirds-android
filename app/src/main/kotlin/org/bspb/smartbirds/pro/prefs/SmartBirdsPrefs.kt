package org.bspb.smartbirds.pro.prefs

import android.content.Context
import androidx.preference.PreferenceManager
import org.bspb.smartbirds.pro.ui.utils.Constants

class SmartBirdsPrefs(context: Context) {
    companion object {
        const val KEY_RUNNING_MONITORING = "runningMonitoring"
        const val KEY_PROVIDER_TYPE = "providerType"
        const val KEY_MAP_TYPE = "mapType"
        const val KEY_ZOOM_FACTOR = "zoomFactor"
        const val KEY_STAY_AWAKE = "stayAwake"
        const val KEY_MONITORING_VIEW_TYPE = "monitoringViewType"
        const val KEY_SHOW_ZONE_BACKGROUND = "showZoneBackground"
        const val KEY_PAUSED_MONITORING = "pausedMonitoring"
        const val KEY_BATTERY_OPTIMIZATION_DIALOG_SHOWN = "isBatteryOptimizationDialogShown"
        const val KEY_SHOW_LOCAL_PROJECTS = "showLocalProjects"
        const val KEY_SHOW_BG_ATLAS_CELLS = "showBgAtlasCells"
        const val KEY_FORMS_ENABLED = "formsEnabled"
        const val KEY_VERSION_CODE = "versionCode"
        const val KEY_KML_FILE_NAME = "kmlFileName"
        const val KEY_SHOW_USER_KML = "showUserKml"
        const val KEY_MAP_LAYERS = "mapLayers"
        const val KEY_ENABLED_MAP_LAYERS = "enabledMapLayers"
        const val KEY_SHOW_CURRENT_LOCATION_CIRCLE = "showCurrentLocationCircle"
    }

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    fun getRunningMonitoring(): Boolean {
        return prefs.getBoolean(KEY_RUNNING_MONITORING, false)
    }

    fun setRunningMonitoring(value: Boolean) {
        prefs.edit().putBoolean(KEY_RUNNING_MONITORING, value).apply()
    }

    fun getProviderType(): String {
        return prefs.getString(KEY_PROVIDER_TYPE, "") ?: ""
    }

    fun setProviderType(value: String) {
        prefs.edit().putString(KEY_PROVIDER_TYPE, value).apply()
    }

    fun getMapType(): String {
        return prefs.getString(KEY_MAP_TYPE, "") ?: ""
    }

    fun setMapType(value: String) {
        prefs.edit().putString(KEY_MAP_TYPE, value).apply()
    }

    fun getZoomFactor(defaultValue: Int = 0): Int {
        return prefs.getInt(KEY_ZOOM_FACTOR, defaultValue)
    }

    fun setZoomFactor(value: Int) {
        prefs.edit().putInt(KEY_ZOOM_FACTOR, value).apply()
    }

    fun getStayAwake(): Boolean {
        return prefs.getBoolean(KEY_STAY_AWAKE, false)
    }

    fun setStayAwake(value: Boolean) {
        prefs.edit().putBoolean(KEY_STAY_AWAKE, value).apply()
    }

    fun getMonitoringViewType(): String {
        return prefs.getString(KEY_MONITORING_VIEW_TYPE, Constants.VIEWTYPE_MAP)
            ?: Constants.VIEWTYPE_MAP
    }

    fun setMonitoringViewType(value: String) {
        prefs.edit().putString(KEY_MONITORING_VIEW_TYPE, value).apply()
    }

    fun getShowZoneBackground(): Boolean {
        return prefs.getBoolean(KEY_SHOW_ZONE_BACKGROUND, true)
    }

    fun setShowZoneBackground(value: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_ZONE_BACKGROUND, value).apply()
    }

    fun getPausedMonitoring(): Boolean {
        return prefs.getBoolean(KEY_PAUSED_MONITORING, false)
    }

    fun setPausedMonitoring(value: Boolean) {
        prefs.edit().putBoolean(KEY_PAUSED_MONITORING, value).apply()
    }

    fun getBatteryOptimizationDialogShown(): Boolean {
        return prefs.getBoolean(KEY_BATTERY_OPTIMIZATION_DIALOG_SHOWN, false)
    }

    fun setBatteryOptimizationDialogShown(value: Boolean) {
        prefs.edit().putBoolean(KEY_BATTERY_OPTIMIZATION_DIALOG_SHOWN, value).apply()
    }

    fun getShowLocalProjects(): Boolean {
        return prefs.getBoolean(KEY_SHOW_LOCAL_PROJECTS, false)
    }

    fun setShowLocalProjects(value: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_LOCAL_PROJECTS, value).apply()
    }

    fun getShowBgAtlasCells(): Boolean {
        return prefs.getBoolean(KEY_SHOW_BG_ATLAS_CELLS, true)
    }

    fun setShowBgAtlasCells(value: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_BG_ATLAS_CELLS, value).apply()
    }

    fun getFormsEnabled(defaultValue: Set<String> = emptySet()): Set<String> {
        return prefs.getStringSet(KEY_FORMS_ENABLED, defaultValue) ?: defaultValue
    }

    fun setFormsEnabled(value: Set<String>) {
        prefs.edit().putStringSet(KEY_FORMS_ENABLED, value).apply()
    }

    fun getVersionCode(): Int {
        return prefs.getInt(KEY_VERSION_CODE, 0)
    }

    fun setVersionCode(value: Int) {
        prefs.edit().putInt(KEY_VERSION_CODE, value).apply()
    }

    fun getKmlFileName(): String {
        return prefs.getString(KEY_KML_FILE_NAME, "") ?: ""
    }

    fun setKmlFileName(value: String) {
        prefs.edit().putString(KEY_KML_FILE_NAME, value).apply()
    }

    fun getShowUserKml(): Boolean {
        return prefs.getBoolean(KEY_SHOW_USER_KML, false)
    }

    fun setShowUserKml(value: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_USER_KML, value).apply()
    }

    fun getMapLayers(): String {
        return prefs.getString(KEY_MAP_LAYERS, "") ?: ""
    }

    fun setMapLayers(value: String) {
        prefs.edit().putString(KEY_MAP_LAYERS, value).apply()
    }

    fun getEnabledMapLayers(): Set<String> {
        return prefs.getStringSet(KEY_ENABLED_MAP_LAYERS, emptySet()) ?: emptySet()
    }

    fun setEnabledMapLayers(value: Set<String>) {
        prefs.edit().putStringSet(KEY_ENABLED_MAP_LAYERS, value).apply()
    }

    fun getShowCurrentLocationCircle(): Boolean {
        return prefs.getBoolean(KEY_SHOW_CURRENT_LOCATION_CIRCLE, false)
    }

    fun setShowCurrentLocationCircle(value: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_CURRENT_LOCATION_CIRCLE, value).apply()
    }
}