package org.bspb.smartbirds.pro.forms.upload;

import com.google.gson.JsonObject;

import org.bspb.smartbirds.pro.backend.SmartBirdsApi;
import org.bspb.smartbirds.pro.backend.dto.UploadFormResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by groupsky on 27.09.16.
 */

public class BirdsUploader implements Uploader {
    @Override
    public Call<UploadFormResponse> upload(SmartBirdsApi api, JsonObject data) {
        return api.createBirds(data);
    }
}
