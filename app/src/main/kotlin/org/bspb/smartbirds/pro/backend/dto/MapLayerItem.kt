package org.bspb.smartbirds.pro.backend.dto

import androidx.annotation.Keep

@Keep
class MapLayerItem() {

    var enabled: Boolean? = null
    var id: Int? = null
    var label: Label? = null
    var summary: Label? = null
    var tileHeight: Int? = null
    var tileWidth: Int? = null
    var type: String? = null
    var url: Label? = null
}