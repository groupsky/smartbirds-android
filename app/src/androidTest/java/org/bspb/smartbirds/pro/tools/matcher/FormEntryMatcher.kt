package org.bspb.smartbirds.pro.tools.matcher

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.bspb.smartbirds.pro.tools.SBGsonParser
import org.bspb.smartbirds.pro.tools.form.entry.FormEntry
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matchers

class FormEntryMatcher(private val entry: FormEntry) : BaseMatcher<String>() {

    private var errorKey: String? = null
    private var errorValue: String? = null

    override fun matches(item: Any?): Boolean {
        checkNotNull(item)
        if (item !is String) return false

        val expectedMap = entry.toUploadMap()
        val actualMap = mapActualData(item)

        var res = false

        expectedMap.forEach { (key, value) ->
            res = Matchers.hasEntry(key, value).matches(actualMap)
            if (!res) {
                errorKey = key
                errorValue = value.toString()
                return false
            }
        }
        return res
    }

    override fun describeTo(description: Description) {
        description.appendText("entry with field: $errorKey and value $errorValue")
    }

    private fun mapActualData(data: String): Map<String, Any> {
        val resultMap = mutableMapOf<String, Any>()
        val json = SBGsonParser.createParser().fromJson(data, JsonObject::class.java)
        json.entrySet().forEach { (key, value) ->
            parseJsonElement(value)?.let { resultMap[key] = it }
        }
        return resultMap
    }

    private fun parseJsonElement(el: JsonElement): Any? {
        if (el.isJsonNull) return null
        if (el.isJsonPrimitive) {
            // map primitive item
            el.asJsonPrimitive.apply {
                val primitiveValue: Any = when {
                    isBoolean -> {
                        asBoolean
                    }
                    isNumber -> {
                        asNumber
                    }
                    else -> {
                        asString
                    }
                }
                return primitiveValue
            }
        } else if (el.isJsonObject) {
            // Map nomenclature item
            if (el.asJsonObject.has("label")) {
                return el.asJsonObject["label"].asJsonObject["en"].asString
            }
        } else if (el.isJsonArray) {
            val resValues = mutableListOf<Any?>()
            el.asJsonArray.forEach {
                parseJsonElement(it)?.apply { resValues.add(this) }
            }
            return resValues
        }

        return null
    }
}