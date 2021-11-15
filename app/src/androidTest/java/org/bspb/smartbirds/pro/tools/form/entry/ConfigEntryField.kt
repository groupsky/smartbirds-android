package org.bspb.smartbirds.pro.tools.form.entry

import org.bspb.smartbirds.pro.ui.utils.FormsConfig
import java.text.SimpleDateFormat
import java.util.*

class ConfigEntryField(jsonKey: String, uiTextId: Int, value: Any) :
    FormEntryField(jsonKey, uiTextId, value) {

    override fun uploadValue(): Any {
        return (value as FormsConfig.NomenclatureConfig).id
    }
}