package org.bspb.smartbirds.pro.prefs

import org.androidannotations.annotations.sharedpreferences.SharedPref

@SharedPref(SharedPref.Scope.APPLICATION_DEFAULT)
interface BirdsMigrationsPrefs {

    fun migrationPoint(): String?

}