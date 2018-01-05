package org.bspb.smartbirds.pro.forms.upload;

import com.google.gson.JsonObject;

import org.bspb.smartbirds.pro.backend.SmartBirdsApi;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by dani on 04.01.18.
 */

public class MammalUploader implements Uploader {
    @Override
    public Call<ResponseBody> upload(SmartBirdsApi api, JsonObject data) {
        return api.createMammal(data);
    }
}
