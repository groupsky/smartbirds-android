package org.bspb.smartbirds.pro.prefs

import org.androidannotations.annotations.sharedpreferences.SharedPref

@SharedPref(SharedPref.Scope.APPLICATION_DEFAULT)
interface BatsPrefs {
    fun metodology(): String?

    fun tempCave(): String?

    fun humidityCave(): String?

    fun typeLoc(): String?

    fun habitat(): String?
}
