package org.bspb.smartbirds.pro.forms.convert.fields;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.tools.SBGsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SpeciesMultiConverter implements FieldConverter {

    final String locale;
    final String csvField;
    final String jsonField;

    public SpeciesMultiConverter(Context context, String csvField, String jsonField) {
        this.locale = context.getString(R.string.locale);
        this.csvField = csvField;
        this.jsonField = jsonField;
    }

    @Override
    public void convert(Map<String, String> csv, JsonObject json, Set<String> usedCsvFields) throws Exception {
        String value = csv.get(csvField);
        if (!TextUtils.isEmpty(value)) {
            List<String> items = SBGsonParser.createParser().fromJson(value, List.class);
            List<String> values = new ArrayList<>();
            for (String item : items) {
                values.add(item.split(" *\n *")[0]);
            }
            json.add(jsonField, SBGsonParser.createParser().toJsonTree(values));
        } else {
            json.add(jsonField, JsonNull.INSTANCE);
        }

        usedCsvFields.add(csvField);
        usedCsvFields.add(csvField + "." + locale);
        usedCsvFields.add(csvField + ".en");
    }

    @Override
    public String toString() {
        return "SpeciesConverter{" +
                "locale='" + locale + '\'' +
                ", csvField='" + csvField + '\'' +
                ", jsonField='" + jsonField + '\'' +
                '}';
    }
}
