package org.bspb.smartbirds.pro.tools.form.entry

class FormEntry {

    private val fields: MutableList<FormEntryField> = mutableListOf()

    fun add(field: FormEntryField) {
        fields.add(field)
    }

    fun add(fieldsToAdd: List<FormEntryField>) {
        fields.addAll(fieldsToAdd)
    }

    fun toUploadMap(): Map<String, Any> {
        val uploadMap = mutableMapOf<String, Any>()
        fields.forEach { field ->
            uploadMap[field.jsonKey] = field.uploadValue()
        }
        return uploadMap
    }

    fun toUiMap(): Map<Int, Any> {
        val uiMap = mutableMapOf<Int, Any>()
        fields.forEach { field ->
            uiMap[field.uiTextId] = field.uiValue()
        }
        return uiMap
    }
}