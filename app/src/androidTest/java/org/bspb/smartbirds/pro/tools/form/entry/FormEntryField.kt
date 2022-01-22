package org.bspb.smartbirds.pro.tools.form.entry

open class FormEntryField(
    val jsonKey: String,
    val uiTextId: Int,
    val value: Any
) {
    open fun uiValue() = value
    open fun uploadValue() = value
}
