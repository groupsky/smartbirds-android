package org.bspb.smartbirds.pro.backend.dto

import com.google.gson.annotations.SerializedName

class BGAtlasCell {

    @SerializedName("utm_code")
    var utmCode: String? = null

    @SerializedName("spec_known")
    var specKnown: Int? = 0

    @SerializedName("spec_unknown")
    var specUnknown: Int? = 0

    @SerializedName("spec_old")
    var specOld: Int? = 0

    @SerializedName("coordinates")
    var coordinates: List<Coordinate>? = null

    override fun toString(): String {
        return "BGAtlasCell(utmCode=$utmCode, specKnown=$specKnown, specUnknown=$specUnknown, specOld=$specOld, coordinates=$coordinates)"
    }


}