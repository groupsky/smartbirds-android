package org.bspb.smartbirds.pro.prefs

import android.content.Context
import androidx.preference.PreferenceManager

class BirdsMigrationsPrefs(context: Context) {

    companion object {
        const val KEY_MIGRATION_POINT = "migrationPoint"
    }

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    fun getMigrationPoint(): String? {
        return prefs.getString(KEY_MIGRATION_POINT, "")
    }

    fun setMigrationPoint(value: String?) {
        prefs.edit().putString(KEY_MIGRATION_POINT, value).apply()
    }

}