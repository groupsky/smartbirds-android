package org.bspb.smartbirds.pro.prefs

import android.content.Context

object PrefsHelper {

    fun getLocalClassName(context: Context): String {
        val packageName = context.packageName
        val className = context.javaClass.name
        val packageLen = packageName.length
        if (((!className.startsWith(packageName)) || (className.length <= packageLen)) || (className[packageLen] != '.')) {
            return className
        }
        return className.substring((packageLen + 1))
    }
}