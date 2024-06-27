package org.bspb.smartbirds.pro.prefs

import android.content.Context
import android.content.SharedPreferences

class MammalPrefs(context: Context) {

    companion object {
        const val KEY_MAMMAL_HABITAT = "mammalHabitat"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(
        (PrefsHelper.getLocalClassName(context) + "_MammalPrefs"),
        0
    )

    fun getMammalHabitat(): String {
        return prefs.getString(KEY_MAMMAL_HABITAT, "") ?: ""
    }

    fun setMammalHabitat(value: String) {
        prefs.edit().putString(KEY_MAMMAL_HABITAT, value).apply()
    }
}