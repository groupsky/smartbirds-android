package org.bspb.smartbirds.pro.backend.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CheckSessionRequest() {

    @SerializedName("include")
    @Expose
    var include = arrayOf("bgatlasCells")
}