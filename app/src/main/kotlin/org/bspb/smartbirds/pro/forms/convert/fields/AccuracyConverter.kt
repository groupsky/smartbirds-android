package org.bspb.smartbirds.pro.forms.convert.fields

import android.text.TextUtils
import com.google.gson.JsonObject

class AccuracyConverter(
    private val csvField: String?,
    private val jsonField: String?,
    private val defaultValue: String?
) :
    FieldConverter {

    @Throws(Exception::class)
    override fun convert(csv: Map<String?, String?>, json: JsonObject, usedCsvFields: MutableSet<String?>) {
        var value = csv[csvField]
        if (TextUtils.isEmpty(value)) value = defaultValue
        value?.let {
            if (it.toDoubleOrNull() != null && it.toDouble() < 0) {
                value = null
            }
        }
        json.addProperty(jsonField, value)
        usedCsvFields.add(csvField)
    }

    override fun toString(): String {
        return "AccuracyConverter{" +
                "csvField='" + csvField + '\'' +
                ", jsonField='" + jsonField + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                '}'
    }
}