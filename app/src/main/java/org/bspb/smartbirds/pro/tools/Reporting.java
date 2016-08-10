package org.bspb.smartbirds.pro.tools;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.bspb.smartbirds.pro.SmartBirdsApplication;

/**
 * Created by dani on 10.08.16.
 */
public class Reporting {

    private static final String TAG = SmartBirdsApplication.TAG + ".Reporting";

    public static void logException(Throwable t) {
        Log.d(TAG, t.getMessage(), t);
        Crashlytics.logException(t);
    }

}
