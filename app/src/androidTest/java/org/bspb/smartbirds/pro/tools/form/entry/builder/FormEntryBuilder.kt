package org.bspb.smartbirds.pro.tools.form.entry.builder

import org.bspb.smartbirds.pro.tools.form.entry.FormEntry
import org.bspb.smartbirds.pro.tools.form.entry.FormEntryField

open class FormEntryBuilder {
    val fields = mutableListOf<FormEntryField>()

    fun add(item: FormEntryField) {
        fields.add(item)
    }

    fun build(): FormEntry {
        val res = FormEntry()
        res.add(fields)
        return res
    }
}