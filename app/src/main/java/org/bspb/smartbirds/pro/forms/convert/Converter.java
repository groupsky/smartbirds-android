package org.bspb.smartbirds.pro.forms.convert;

import android.content.Context;
import androidx.annotation.StringRes;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonObject;

import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.forms.convert.fields.BooleanConverter;
import org.bspb.smartbirds.pro.forms.convert.fields.DateConverter;
import org.bspb.smartbirds.pro.forms.convert.fields.DateTimeConverter;
import org.bspb.smartbirds.pro.forms.convert.fields.FieldConverter;
import org.bspb.smartbirds.pro.forms.convert.fields.MultipleChoiceConverter;
import org.bspb.smartbirds.pro.forms.convert.fields.SimpleConverter;
import org.bspb.smartbirds.pro.forms.convert.fields.SingleChoiceConverter;
import org.bspb.smartbirds.pro.forms.convert.fields.SpeciesConverter;
import org.bspb.smartbirds.pro.forms.convert.fields.SpeciesMultiConverter;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by groupsky on 28.09.16.
 */

public abstract class Converter {

    private static final String TAG = SmartBirdsApplication.TAG + ".Converter";

    private final List<FieldConverter> fieldConverters = new LinkedList<>();
    private final Context context;

    protected Converter(Context context) {
        this.context = context.getApplicationContext();

        // common form
        add(R.string.tag_location, "location");
        add(R.string.tag_lat, "latitude");
        add(R.string.tag_lon, "longitude");
        addDateTime(R.string.entry_date, R.string.entry_time, "observationDateTime");
        add(R.string.monitoring_id, "monitoringCode");
        addDateTime(R.string.tag_begin_date, R.string.tag_begin_time, "startDateTime");
        addDateTime(R.string.tag_end_date, R.string.tag_end_time, "endDateTime");
        add(R.string.tag_other_observers, "observers");
        addSingle(R.string.tag_rain, "rain");
        add(R.string.tag_temperature, "temperature");
        addSingle(R.string.tag_wind_direction, "windDirection");
        addSingle(R.string.tag_wind_strength, "windSpeed");
        addSingle(R.string.tag_cloudiness, "cloudiness");
        add(R.string.tag_clouds_type, "cloudsType");
        add(R.string.tag_visibility_km, "visibility");
        add(R.string.tag_weather_other, "mto");
        add(R.string.tag_notes, "notes");
        addMulti(R.string.tag_threats, "threats");
    }

    protected void add(FieldConverter fieldConverter) {
        fieldConverters.add(fieldConverter);
    }

    protected void add(@StringRes int csvFieldName, String jsonFieldName) {
        add(csvFieldName, jsonFieldName, null);
    }

    protected void add(@StringRes int csvFieldName, String jsonFieldName, String defaultValue) {
        add(new SimpleConverter(context.getString(csvFieldName), jsonFieldName, defaultValue));
    }

    protected void addSingle(@StringRes int csvFieldName, String jsonFieldName) {
        add(new SingleChoiceConverter(context, context.getString(csvFieldName), jsonFieldName));
    }

    protected void addMulti(@StringRes int csvFieldName, String jsonFieldName) {
        add(new MultipleChoiceConverter(context, context.getString(csvFieldName), jsonFieldName));
    }

    protected void addBool(@StringRes int csvFieldName, String jsonFieldName) {
        add(new BooleanConverter(context.getString(csvFieldName), jsonFieldName));
    }

    protected void addSpecies(@StringRes int csvFieldName, String jsonFieldName) {
        add(new SpeciesConverter(context, context.getString(csvFieldName), jsonFieldName));
    }

    protected void addSpeciesMulti(@StringRes int csvFieldName, String jsonFieldName) {
        add(new SpeciesMultiConverter(context, context.getString(csvFieldName), jsonFieldName));
    }

    protected void addDateTime(@StringRes int csvDateFieldName, @StringRes int csvTimeFieldName, String jsonFieldName) {
        add(new DateTimeConverter(context.getString(csvDateFieldName), context.getString(csvTimeFieldName), jsonFieldName));
    }

    protected void addDate(@StringRes int csvDateFieldName, String jsonFieldName) {
        add(new DateConverter(context.getString(csvDateFieldName), jsonFieldName));
    }

    public JsonObject convert(Map<String, String> csv) throws Exception {
        JsonObject result = new JsonObject();
        HashSet<String> usedCsvColumns = new HashSet<>();
        for (FieldConverter converter : fieldConverters) {
            try {
                converter.convert(csv, result, usedCsvColumns);
            } catch (Throwable t) {
                throw new RuntimeException("Converter " + converter + " failed: " + t.getMessage(), t);
            }
        }

        Set<String> unusedColumns = new HashSet<>(csv.keySet());
        unusedColumns.removeAll(usedCsvColumns);
        if (!unusedColumns.isEmpty()) {
            Log.w(TAG, "Unused csv columns: " + TextUtils.join(", ", unusedColumns));
        }

        return result;
    }
}
