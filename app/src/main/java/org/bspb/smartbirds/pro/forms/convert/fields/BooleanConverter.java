package org.bspb.smartbirds.pro.forms.convert.fields;

import android.text.TextUtils;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.Set;

public class BooleanConverter implements FieldConverter {
    private final String csvField;
    private final String jsonField;

    public BooleanConverter(String csvField, String jsonField) {
        this.csvField = csvField;
        this.jsonField = jsonField;
    }

    @Override
    public void convert(Map<String, String> csv, JsonObject json, Set<String> usedCsvFields) throws Exception {
        String value = csv.get(csvField);
        if (!TextUtils.isEmpty(value)) {
            json.addProperty(jsonField, "1".equals(value));
        } else {
            json.add(jsonField, JsonNull.INSTANCE);
        }
        usedCsvFields.add(csvField);
    }
}
