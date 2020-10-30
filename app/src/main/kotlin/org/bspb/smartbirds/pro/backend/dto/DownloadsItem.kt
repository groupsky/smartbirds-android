package org.bspb.smartbirds.pro.backend.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class DownloadsItem() {

    var title: Label? = null
    var url: String? = null

    @SerializedName("content_locale")
    var contentLocale: String? = null
}