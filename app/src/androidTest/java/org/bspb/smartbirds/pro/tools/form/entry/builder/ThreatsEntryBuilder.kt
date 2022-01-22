package org.bspb.smartbirds.pro.tools.form.entry.builder

import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.form.entry.ConfigEntryField
import org.bspb.smartbirds.pro.tools.form.entry.FormEntryField
import org.bspb.smartbirds.pro.ui.utils.FormsConfig

class ThreatsEntryBuilder : FormEntryBuilder() {

    fun setModeratorReview(value: Boolean): ThreatsEntryBuilder {
        add(FormEntryField("moderatorReview", R.string.monitoring_moderator_review, value))
        return this
    }

    fun setConfidential(value: Boolean): ThreatsEntryBuilder {
        add(FormEntryField("confidential", R.string.monitoring_birds_private, value))
        return this
    }

    fun setPrimaryType(value: FormsConfig.NomenclatureConfig): ThreatsEntryBuilder {
        add(ConfigEntryField("primaryType", R.string.monitoring_threats_primary_type, value))
        return this
    }

    fun setCategory(value: String): ThreatsEntryBuilder {
        add(FormEntryField("category", R.string.monitoring_threats_category, value))
        return this
    }

    fun setClass(value: String): ThreatsEntryBuilder {
        add(FormEntryField("class", R.string.monitoring_threats_class, value))
        return this
    }

    fun setSpecies(value: String): ThreatsEntryBuilder {
        add(FormEntryField("species", R.string.monitoring_threats_species, value))
        return this
    }

    fun setCount(value: String): ThreatsEntryBuilder {
        add(FormEntryField("count", R.string.monitoring_threats_count, value))
        return this
    }

    fun setEstimate(value: String): ThreatsEntryBuilder {
        add(FormEntryField("estimate", R.string.monitoring_threats_estimate, value))
        return this
    }

    fun setPoisonedType(value: FormsConfig.NomenclatureConfig): ThreatsEntryBuilder {
        add(ConfigEntryField("poisonedType", R.string.monitoring_threats_poisoned_type, value))
        return this
    }

    fun setStateCarcass(value: String): ThreatsEntryBuilder {
        add(FormEntryField("stateCarcass", R.string.monitoring_threats_state_carcass, value))
        return this
    }

    fun setSampleTaken1(value: String): ThreatsEntryBuilder {
        add(FormEntryField("sampleTaken1", R.string.monitoring_threats_sample_taken_1, value))
        return this
    }

    fun setSampleCode1(value: String): ThreatsEntryBuilder {
        add(FormEntryField("sampleCode1", R.string.monitoring_threats_sample_code_1, value))
        return this
    }

    fun setSampleTaken2(value: String): ThreatsEntryBuilder {
        add(FormEntryField("sampleTaken2", R.string.monitoring_threats_sample_taken_2, value))
        return this
    }

    fun setSampleCode2(value: String): ThreatsEntryBuilder {
        add(FormEntryField("sampleCode2", R.string.monitoring_threats_sample_code_2, value))
        return this
    }

    fun setSampleTaken3(value: String): ThreatsEntryBuilder {
        add(FormEntryField("sampleTaken3", R.string.monitoring_threats_sample_taken_3, value))
        return this
    }

    fun setSampleCode3(value: String): ThreatsEntryBuilder {
        add(FormEntryField("sampleCode3", R.string.monitoring_threats_sample_code_3, value))
        return this
    }

    fun setNotes(value: String): ThreatsEntryBuilder {
        add(FormEntryField("threatsNotes", R.string.monitoring_threats_notes, value))
        return this
    }
}