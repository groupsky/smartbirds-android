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

    public String get(Locale locale) {
        if (locale == null) {
            return null;
        }

        return get(locale.getLanguage());
    }

    public String getLabelId() {
        return get(Configuration.FALLBACK_LANGUAGE);
    }

    public String toJsonString() {
        return new Gson().toJson(this);
    }

    public void addValue(String lang, String value) {
        if (values == null) {
            values = new HashMap<>();
        }
        values.put(lang, value);
    }
}
