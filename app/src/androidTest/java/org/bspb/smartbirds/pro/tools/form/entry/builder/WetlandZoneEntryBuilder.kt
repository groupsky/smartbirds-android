package org.bspb.smartbirds.pro.tools.form.entry.builder

import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.form.entry.FormEntryField

class WetlandZoneEntryBuilder : FormEntryBuilder() {

    fun setSpecies(value: String): WetlandZoneEntryBuilder {
        add(FormEntryField("species", R.string.monitoring_birds_name, value))
        return this
    }

    fun setCount(value: String): WetlandZoneEntryBuilder {
        add(FormEntryField("count", R.string.monitoring_birds_count, value))
        return this
    }

    fun setGender(value: String): WetlandZoneEntryBuilder {
        add(FormEntryField("sex", R.string.monitoring_birds_gender, value))
        return this
    }

    fun setAge(value: String): WetlandZoneEntryBuilder {
        add(FormEntryField("age", R.string.monitoring_birds_age, value))
        return this
    }
}