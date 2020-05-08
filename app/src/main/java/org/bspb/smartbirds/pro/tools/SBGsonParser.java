package org.bspb.smartbirds.pro.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.bspb.smartbirds.pro.backend.LabelDeserializer;
import org.bspb.smartbirds.pro.backend.LabelSerializer;
import org.bspb.smartbirds.pro.backend.dto.Label;

public class SBGsonParser {


    public static Gson createParser() {
        return new GsonBuilder()
                .registerTypeAdapter(Label.class, new LabelDeserializer())
                .registerTypeAdapter(Label.class, new LabelSerializer())
                .create();
    }

}
