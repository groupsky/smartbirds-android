package org.bspb.smartbirds.pro.prefs

import org.androidannotations.annotations.sharedpreferences.SharedPref

@SharedPref(SharedPref.Scope.APPLICATION_DEFAULT)
interface DownloadsPrefs {

    fun downloads() : String?

}