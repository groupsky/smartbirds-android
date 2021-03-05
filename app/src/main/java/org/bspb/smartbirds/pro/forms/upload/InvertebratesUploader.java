package org.bspb.smartbirds.pro.forms.upload;

import com.google.gson.JsonObject;

import org.bspb.smartbirds.pro.backend.SmartBirdsApi;
import org.bspb.smartbirds.pro.backend.dto.UploadFormResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class InvertebratesUploader implements Uploader {
    @Override
    public Call<UploadFormResponse> upload(SmartBirdsApi api, JsonObject data) {
        return api.createInvertebrates(data);
    }
}
