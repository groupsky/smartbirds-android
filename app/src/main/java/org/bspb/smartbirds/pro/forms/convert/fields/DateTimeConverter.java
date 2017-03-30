package org.bspb.smartbirds.pro.forms.convert.fields;

import android.text.TextUtils;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import static org.bspb.smartbirds.pro.ui.utils.Configuration.STORAGE_DATE_TIME_FORMAT;

public class DateTimeConverter implements FieldConverter {

    static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz", Locale.ENGLISH);
    static final TimeZone tz = TimeZone.getTimeZone("UTC");

    static {
        df.setTimeZone(tz);
    }

    final String csvDateField;
    final String csvTimeField;
    final String jsonField;

    public DateTimeConverter(String csvDateField, String csvTimeField, String jsonField) {
        this.csvDateField = csvDateField;
        this.csvTimeField = csvTimeField;
        this.jsonField = jsonField;
    }

    protected Date parse(Map<String, String> csv, Set<String> usedCsvFields) throws ParseException {
        String dateValue = csv.get(csvDateField);
        String timeValue = csv.get(csvTimeField);
        usedCsvFields.add(csvDateField);
        usedCsvFields.add(csvTimeField);

        if (TextUtils.isEmpty(dateValue) && TextUtils.isEmpty(timeValue)) {
            return null;
        } else {
            return STORAGE_DATE_TIME_FORMAT.parse(dateValue + " " + timeValue);
        }
    }

    @Override
    public void convert(Map<String, String> csv, JsonObject json, Set<String> usedCsvFields) throws Exception {
        Date value = parse(csv, usedCsvFields);
        if (value == null) {
            json.add(jsonField, JsonNull.INSTANCE);
        } else {
            String output = df.format(value);

            String result = output.replaceAll("UTC", "Z").replaceAll("\\+00:00", "Z");

            json.addProperty(jsonField, result);
        }
    }

    @Override
    public String toString() {
        return "DateTimeConverter{" +
                "csvDateField='" + csvDateField + '\'' +
                ", csvTimeField='" + csvTimeField + '\'' +
                ", jsonField='" + jsonField + '\'' +
                '}';
    }
}
