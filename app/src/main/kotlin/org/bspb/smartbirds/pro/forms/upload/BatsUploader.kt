package org.bspb.smartbirds.pro.forms.upload

import com.google.gson.JsonObject
import org.bspb.smartbirds.pro.backend.SmartBirdsApi
import org.bspb.smartbirds.pro.backend.dto.UploadFormResponse
import retrofit2.Call

class BatsUploader : Uploader {
    override fun upload(api: SmartBirdsApi, data: JsonObject): Call<UploadFormResponse> {
        return api.createBat(data)
    }
}