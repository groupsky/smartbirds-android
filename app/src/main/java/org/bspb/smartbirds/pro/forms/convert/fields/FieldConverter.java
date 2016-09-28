package org.bspb.smartbirds.pro.forms.convert.fields;

import com.google.gson.JsonObject;

import java.util.Map;
import java.util.Set;

public interface FieldConverter {
    void convert(Map<String, String> csv, JsonObject json, Set<String> usedCsvFields) throws Exception;
}
