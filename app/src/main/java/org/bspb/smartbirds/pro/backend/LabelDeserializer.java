package org.bspb.smartbirds.pro.backend;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.bspb.smartbirds.pro.backend.dto.Label;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class LabelDeserializer implements JsonDeserializer<Label> {


    @Override
    public Label deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (jsonElement == null) {
            return null;
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();


        HashMap<String, String> labelValues = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {

            String key = entry.getKey();
            if (entry.getValue().isJsonNull()) {
                continue;
            }

            String value = entry.getValue().getAsString();
            if (value != null) {
                labelValues.put(key, value);
            }
        }

        Label label = new Label();
        label.setValues(labelValues);

        return label;
    }

}
