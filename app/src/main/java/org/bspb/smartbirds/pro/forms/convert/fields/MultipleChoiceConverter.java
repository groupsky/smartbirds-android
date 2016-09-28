package org.bspb.smartbirds.pro.forms.convert.fields;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.backend.dto.Nomenclature;

import java.util.Map;
import java.util.Set;

public class MultipleChoiceConverter implements FieldConverter {
    final String locale;
    final String csvField;
    final String jsonField;

    public MultipleChoiceConverter(Context context, String csvField, String jsonField) {
        this.locale = context.getString(R.string.locale);
        this.csvField = csvField;
        this.jsonField = jsonField;
    }

    @Override
    public void convert(Map<String, String> csv, JsonObject json, Set<String> usedCsvFields) throws Exception {
        String value = csv.get(csvField + ".json");
        if (!TextUtils.isEmpty(value)) {
            Nomenclature[] values = new Gson().fromJson(value, Nomenclature[].class);
            json.add(jsonField, new Gson().toJsonTree(values));
        } else {
            json.add(jsonField, JsonNull.INSTANCE);
        }

        usedCsvFields.add(csvField);
        usedCsvFields.add(csvField + ".json");
    }
}
