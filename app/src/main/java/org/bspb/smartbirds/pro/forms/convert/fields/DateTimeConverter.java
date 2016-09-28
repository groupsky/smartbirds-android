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

import static org.bspb.smartbirds.pro.ui.utils.Configuration.STORAGE_DATE_FORMAT;
import static org.bspb.smartbirds.pro.ui.utils.Configuration.STORAGE_TIME_FORMAT;

public class DateTimeConverter implements FieldConverter {

    static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz", Locale.ENGLISH);
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
            Date date = STORAGE_DATE_FORMAT.parse(dateValue);
            Date time = STORAGE_TIME_FORMAT.parse(timeValue);

            return new Date(date.getTime() + time.getTime());
        }
    }

    @Override
    public void convert(Map<String, String> csv, JsonObject json, Set<String> usedCsvFields) throws Exception {
        Date value = parse(csv, usedCsvFields);
        if (value == null) {
            json.add(jsonField, JsonNull.INSTANCE);
        } else {
            String output = df.format(value);

            int inset0 = 9;
            int inset1 = 6;

            String s0 = output.substring(0, output.length() - inset0);
            String s1 = output.substring(output.length() - inset1, output.length());

            String result = s0 + s1;

            result = result.replaceAll("UTC", "+00:00");

            json.addProperty(jsonField, result);
        }
    }
}
