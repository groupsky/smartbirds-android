package org.bspb.smartbirds.pro.prefs

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class CbmPrefs(context: Context) {

    companion object {
        const val KEY_CBM_ZONE = "cbmZone"
        const val KEY_SPECIES_QUICK_1 = "speciesQuick1"
        const val KEY_SPECIES_QUICK_2 = "speciesQuick2"
        const val KEY_SPECIES_QUICK_3 = "speciesQuick3"
        const val KEY_SPECIES_QUICK_4 = "speciesQuick4"
        const val KEY_SPECIES_QUICK_5 = "speciesQuick5"
        const val KEY_SPECIES_QUICK_6 = "speciesQuick6"
    }

    private var prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun getCbmZone(): String {
        return prefs?.getString(KEY_CBM_ZONE, "") ?: ""
    }

    fun setCbmZone(value: String) {
        prefs?.edit()?.putString(KEY_CBM_ZONE, value)?.apply()
    }

    fun getSpeciesQuick1(): String {
        return prefs?.getString(KEY_SPECIES_QUICK_1, "") ?: ""
    }

    fun setSpeciesQuick1(value: String?) {
        prefs?.edit()?.putString(KEY_SPECIES_QUICK_1, value)?.apply()
    }

    fun getSpeciesQuick2(): String {
        return prefs?.getString(KEY_SPECIES_QUICK_2, "") ?: ""
    }

    fun setSpeciesQuick2(value: String?) {
        prefs?.edit()?.putString(KEY_SPECIES_QUICK_2, value)?.apply()
    }

    fun getSpeciesQuick3(): String {
        return prefs?.getString(KEY_SPECIES_QUICK_3, "") ?: ""
    }

    fun setSpeciesQuick3(value: String?) {
        prefs?.edit()?.putString(KEY_SPECIES_QUICK_3, value)?.apply()
    }

    fun getSpeciesQuick4(): String {
        return prefs?.getString(KEY_SPECIES_QUICK_4, "") ?: ""
    }

    fun setSpeciesQuick4(value: String?) {
        prefs?.edit()?.putString(KEY_SPECIES_QUICK_4, value)?.apply()
    }

    fun getSpeciesQuick5(): String {
        return prefs?.getString(KEY_SPECIES_QUICK_5, "") ?: ""
    }

    fun setSpeciesQuick5(value: String?) {
        prefs?.edit()?.putString(KEY_SPECIES_QUICK_5, value)?.apply()
    }

    fun getSpeciesQuick6(): String {
        return prefs?.getString(KEY_SPECIES_QUICK_6, "") ?: ""
    }

    fun setSpeciesQuick6(value: String?) {
        prefs?.edit()?.putString(KEY_SPECIES_QUICK_6, value)?.apply()
    }

}