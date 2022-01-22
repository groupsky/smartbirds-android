package org.bspb.smartbirds.pro.tools.form.entry.builder

import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.form.entry.FormEntryField

class InvertebratesEntryBuilder : FormEntryBuilder() {

    fun setModeratorReview(value: Boolean): InvertebratesEntryBuilder {
        add(FormEntryField("moderatorReview", R.string.monitoring_moderator_review, value))
        return this
    }

    fun setConfidential(value: Boolean): InvertebratesEntryBuilder {
        add(FormEntryField("confidential", R.string.monitoring_invertebrates_private, value))
        return this
    }

    fun setSpecies(value: String): InvertebratesEntryBuilder {
        add(FormEntryField("species", R.string.monitoring_herp_name, value))
        return this
    }

    fun setGender(value: String): InvertebratesEntryBuilder {
        add(FormEntryField("sex", R.string.monitoring_herp_gender, value))
        return this
    }

    fun setAge(value: String): InvertebratesEntryBuilder {
        add(FormEntryField("age", R.string.monitoring_herp_age, value))
        return this
    }

    fun setCount(value: String): InvertebratesEntryBuilder {
        add(FormEntryField("count", R.string.monitoring_herp_count, value))
        return this
    }

    fun setHabitat(value: String): InvertebratesEntryBuilder {
        add(FormEntryField("habitat", R.string.monitoring_herp_habitat, value))
        return this
    }

    fun setFindings(value: List<String>): InvertebratesEntryBuilder {
        add(FormEntryField("findings", R.string.monitoring_invertebrates_danger_observation, value))
        return this
    }

    fun setMarking(value: String): InvertebratesEntryBuilder {
        add(FormEntryField("marking", R.string.monitoring_herp_marking, value))
        return this
    }

    fun setThreats(value: List<String>): InvertebratesEntryBuilder {
        add(FormEntryField("threats", R.string.monitoring_common_threats, value))
        return this
    }

    fun setNotes(value: String): InvertebratesEntryBuilder {
        add(FormEntryField("speciesNotes", R.string.monitoring_herp_notes, value))
        return this
    }
}