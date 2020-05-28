package org.bspb.smartbirds.pro.backend.dto;

import com.google.gson.Gson;

import org.bspb.smartbirds.pro.ui.utils.Configuration;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Label {

    protected Map<String, String> values;


    public void setValues(Map<String, String> values) {
        this.values = values;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public boolean hasValue(String locale) {
        if (values == null) {
            return false;
        }

        return values.containsKey(locale);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Label{");
        sb.append("values=").append(values);
        sb.append('}');
        return sb.toString();
    }

    public String get(String localeString) {
        if (values == null) {
            return null;
        }

        if (localeString != null) {
            if (values.containsKey(localeString)) {
                return values.get(localeString);
            }
        }

        return values.get(Configuration.FALLBACK_LANGUAGE);
    }

    public void addValue(String lang, String value) {
        if (values == null) {
            values = new HashMap<>();
        }
        values.put(lang, value);
    }

    public String getLabelId() {
        return values.get(Configuration.NOMENCLATURE_ID_LANGUAGE);
    }
}
