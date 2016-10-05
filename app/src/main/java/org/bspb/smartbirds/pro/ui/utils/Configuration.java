package org.bspb.smartbirds.pro.ui.utils;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by groupsky on 22.03.16.
 */
public class Configuration {

    public static final String STORAGE_VERSION_CODE = "3";

    public static final String MULTIPLE_CHOICE_DELIMITER = " | ";
    public static final String MULTIPLE_CHOICE_SPLITTER = " *\\| *";
    public static DateFormat STORAGE_DATE_TIME_FORMAT;
    public static DateFormat STORAGE_TIME_FORMAT;
    public static DateFormat STORAGE_DATE_FORMAT;

    public static void init(Context context) {
        STORAGE_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        STORAGE_TIME_FORMAT = new SimpleDateFormat("kk:mm:ss", Locale.ENGLISH);
        STORAGE_DATE_TIME_FORMAT = new SimpleDateFormat("dd.MM.yyyy kk:mm:ss", Locale.ENGLISH);
    }
}
