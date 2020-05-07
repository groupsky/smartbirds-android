package org.bspb.smartbirds.pro.forms.convert.fields;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import org.bspb.smartbirds.pro.R;

import java.util.Map;
import java.util.Set;

public class SingleChoiceConverter implements FieldConverter {

    final String locale;
    final String csvField;
    final String jsonField;

    public SingleChoiceConverter(Context context, String csvField, String jsonField) {
        this.locale = context.getString(R.string.locale);
        this.csvField = csvField;
        this.jsonField = jsonField;
    }

    @Override
    public void convert(Map<String, String> csv, JsonObject json, Set<String> usedCsvFields) throws Exception {
        String value = csv.get(csvField);
        if (TextUtils.isEmpty(value)) {
            json.add(jsonField, JsonNull.INSTANCE);
        } else {
            JsonObject label = new JsonObject();
            label.addProperty(locale, value);
            {
                String en = csv.get(csvField + ".en");
                if (!TextUtils.isEmpty(en))
                    label.addProperty("en", en);
            }
            JsonObject field = new JsonObject();
            field.add("label", label);
            json.add(jsonField, field);
        }
        usedCsvFields.add(csvField);
        usedCsvFields.add(csvField + "." + locale);
        usedCsvFields.add(csvField + ".en");
    }

    @Override
    public String toString() {
        return "SingleChoiceConverter{" +
                "locale='" + locale + '\'' +
                ", csvField='" + csvField + '\'' +
                ", jsonField='" + jsonField + '\'' +
                '}';
    }
}
