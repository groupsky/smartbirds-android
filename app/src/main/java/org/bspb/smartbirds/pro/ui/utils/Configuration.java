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
    // Max distance to location for auto population of common form location
    public static final float MAX_DISTANCE_LOCATION_METERS = 50000;
    // Max distance to zone beyond that will display a warning
    public static final float MAX_DISTANCE_TO_ZONE_METERS = 5000;
    public static DateFormat STORAGE_DATE_TIME_FORMAT;
    public static DateFormat STORAGE_TIME_FORMAT;
    public static DateFormat STORAGE_DATE_FORMAT;

    /**
     * The minimum number of items to display a filter
     */
    public static final int ITEM_COUNT_FOR_FILTER = 20;
    /**
     * How many recently used values to display, only applicable if @ITEM_COUNT_FOR_FILTER is matched
     */
    public static final int MAX_RECENT_USED_VALUES = 10;

    public static void init(Context context) {
        STORAGE_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        STORAGE_TIME_FORMAT = new SimpleDateFormat("kk:mm:ss", Locale.ENGLISH);
        STORAGE_DATE_TIME_FORMAT = new SimpleDateFormat("dd.MM.yyyy kk:mm:ss", Locale.ENGLISH);
    }
}
