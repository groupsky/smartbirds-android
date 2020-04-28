package org.bspb.smartbirds.pro.backend.dto;

import java.util.Locale;
import java.util.Map;

public class Label {

    private Map<String, String> values;


    public void setValues(Map<String, String> values) {
        this.values = values;
    }

    public Map<String, String> getValues() {
        return values;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Label{");
        sb.append("values=").append(values);
        sb.append('}');
        return sb.toString();
    }

    public String get(Locale locale) {
        if (values == null) {
            return null;
        }

        if (locale != null) {
            if (values.containsKey(locale.getLanguage())) {
                return values.get(locale.getLanguage());
            }
        }

        return values.get("en");
    }
}
