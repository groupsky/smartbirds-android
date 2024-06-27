package org.bspb.smartbirds.pro.prefs

import android.content.Context
import android.content.SharedPreferences

class MonitoringPrefs(context: Context) {

    companion object {
        const val KEY_MARKERS_COUNT = "markersCount"
        const val KEY_POINTS_COUNT = "pointsCount"
        const val KEY_LAST_POSITION_LAT = "lastPositionLat"
        const val KEY_LAST_POSITION_LON = "lastPositionLon"
        const val KEY_ENTRY_TYPE = "entryType"
    }

    private val prefs = context.getSharedPreferences("MonitoringPrefs", 0)

    fun edit(): SharedPreferences.Editor = prefs.edit()

    fun contains(key: String): Boolean = prefs.contains(key)
    
    fun getMarkersCount(): Int {
        return prefs.getInt(KEY_MARKERS_COUNT, 0)
    }

    fun setMarkersCount(value: Int) {
        prefs.edit().putInt(KEY_MARKERS_COUNT, value).apply()
    }

    fun getPointsCount(): Int {
        return prefs.getInt(KEY_POINTS_COUNT, 0)
    }

    fun setPointsCount(value: Int) {
        prefs.edit().putInt(KEY_POINTS_COUNT, value).apply()
    }

    fun getLastPositionLat(): Double {
        return prefs.getFloat(KEY_LAST_POSITION_LAT, 0f).toDouble()
    }

    fun setLastPositionLat(value: Double) {
        prefs.edit().putFloat(KEY_LAST_POSITION_LAT, value.toFloat()).apply()
    }

    fun getLastPositionLon(): Double {
        return prefs.getFloat(KEY_LAST_POSITION_LON, 0f).toDouble()
    }

    fun setLastPositionLon(value: Double) {
        prefs.edit().putFloat(KEY_LAST_POSITION_LON, value.toFloat()).apply()
    }

    fun getEntryType(): String {
        return prefs.getString(KEY_ENTRY_TYPE, "") ?: ""
    }

    fun setEntryType(value: String) {
        prefs.edit().putString(KEY_ENTRY_TYPE, value).apply()
    }
}