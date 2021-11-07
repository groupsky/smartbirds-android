package org.bspb.smartbirds.pro.tools.form.entry

import java.text.SimpleDateFormat
import java.util.*

class DateEntryField(jsonKey: String, uiTextId: Int, value: Any) :
    FormEntryField(jsonKey, uiTextId, value) {

    override fun uploadValue(): Any {
        // Parse input value to date object
        val date = SimpleDateFormat("yyyy-MM-dd").parse(value as String)

        // Format date according to upload date format
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz", Locale.ENGLISH)
        val tz = TimeZone.getTimeZone("UTC")
        df.timeZone = tz
        val output = df.format(date)
        return output.replace("UTC".toRegex(), "Z").replace("\\+00:00".toRegex(), "Z")
    }
}