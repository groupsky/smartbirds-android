package org.bspb.smartbirds.pro.backend;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.bspb.smartbirds.pro.backend.dto.Label;

import java.lang.reflect.Type;
import java.util.Map;

public class LabelSerializer implements JsonSerializer<Label> {
    @Override
    public JsonElement serialize(Label src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null || src.getValues() == null) {
            return JsonNull.INSTANCE;
        }

        JsonObject json = new JsonObject();
        for (Map.Entry<String, String> entry : src.getValues().entrySet()) {
            json.addProperty(entry.getKey(), entry.getValue());
        }
        return json;
    }
}
