package org.bspb.smartbirds.pro.forms.convert.fields;

import android.text.TextUtils;

import com.google.gson.JsonObject;

import java.util.Map;
import java.util.Set;

public class SimpleConverter implements FieldConverter {
    final String csvField;
    final String jsonField;
    final String defaultValue;

    public SimpleConverter(String csvField, String jsonField, String defaultValue) {
        this.csvField = csvField;
        this.jsonField = jsonField;
        this.defaultValue = defaultValue;
    }

    @Override
    public void convert(Map<String, String> csv, JsonObject json, Set<String> usedCsvFields) throws Exception {
        String value = csv.get(csvField);
        if (TextUtils.isEmpty(value)) value = defaultValue;
        json.addProperty(jsonField, value);
        usedCsvFields.add(csvField);
    }
}
