package org.bspb.smartbirds.pro.backend.dto

import androidx.annotation.Keep
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

@Keep
class UploadFormResponse {

    @SerializedName("data")
    var data: JsonObject? = null

    @SerializedName("error")
    var error: String? = null

}