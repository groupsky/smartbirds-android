package org.bspb.smartbirds.pro.forms.convert.fields;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import org.bspb.smartbirds.pro.R;

import java.util.Map;
import java.util.Set;

public class SpeciesConverter implements FieldConverter {

    final String locale;
    final String csvField;
    final String jsonField;

    public SpeciesConverter(Context context, String csvField, String jsonField) {
        this.locale = context.getString(R.string.locale);
        this.csvField = csvField;
        this.jsonField = jsonField;
    }

    @Override
    public void convert(Map<String, String> csv, JsonObject json, Set<String> usedCsvFields) throws Exception {
        String value = csv.get(csvField);
        if (!TextUtils.isEmpty(value)) {
            String[] values = value.split(" *\n *");
            json.addProperty(jsonField, values[0]);
        } else {
            json.add(jsonField, JsonNull.INSTANCE);
        }

        usedCsvFields.add(csvField);
        usedCsvFields.add(csvField + ".bg");
        usedCsvFields.add(csvField + ".en");
    }
}
