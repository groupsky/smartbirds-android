package org.bspb.smartbirds.pro.tools.form.entry.builder

import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.form.entry.FormEntryField

class CbmEntryBuilder : FormEntryBuilder() {

    fun setModeratorReview(value: Boolean): CbmEntryBuilder {
        add(FormEntryField("moderatorReview", R.string.monitoring_moderator_review, value))
        return this
    }

    fun setConfidential(value: Boolean): CbmEntryBuilder {
        add(FormEntryField("confidential", R.string.monitoring_birds_private, value))
        return this
    }

    fun setSpecies(value: String): CbmEntryBuilder {
        add(FormEntryField("species", R.string.monitoring_cbm_name, value))
        return this
    }

    fun setDistance(value: String): CbmEntryBuilder {
        add(FormEntryField("distance", R.string.monitoring_cbm_distance, value))
        return this
    }

    fun setCount(value: String): CbmEntryBuilder {
        add(FormEntryField("count", R.string.monitoring_cbm_count, value))
        return this
    }

    fun setZone(value: String): CbmEntryBuilder {
        add(FormEntryField("zone", R.string.monitoring_cbm_zone, value))
        return this
    }

    fun setThreats(value: List<String>): CbmEntryBuilder {
        add(FormEntryField("threats", R.string.monitoring_common_threats, value))
        return this
    }
}