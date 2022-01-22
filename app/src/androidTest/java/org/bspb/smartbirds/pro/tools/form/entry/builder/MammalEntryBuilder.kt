package org.bspb.smartbirds.pro.tools.form.entry.builder

import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.form.entry.FormEntryField

class MammalEntryBuilder : FormEntryBuilder() {

    fun setModeratorReview(value: Boolean): MammalEntryBuilder {
        add(FormEntryField("moderatorReview", R.string.monitoring_moderator_review, value))
        return this
    }

    fun setConfidential(value: Boolean): MammalEntryBuilder {
        add(FormEntryField("confidential", R.string.monitoring_mammal_private, value))
        return this
    }

    fun setSpecies(value: String): MammalEntryBuilder {
        add(FormEntryField("species", R.string.monitoring_herp_name, value))
        return this
    }

    fun setGender(value: String): MammalEntryBuilder {
        add(FormEntryField("sex", R.string.monitoring_herp_gender, value))
        return this
    }

    fun setAge(value: String): MammalEntryBuilder {
        add(FormEntryField("age", R.string.monitoring_herp_age, value))
        return this
    }

    fun setCount(value: String): MammalEntryBuilder {
        add(FormEntryField("count", R.string.monitoring_herp_count, value))
        return this
    }

    fun setHabitat(value: String): MammalEntryBuilder {
        add(FormEntryField("habitat", R.string.monitoring_herp_habitat, value))
        return this
    }

    fun setFindings(value: List<String>): MammalEntryBuilder {
        add(FormEntryField("findings", R.string.monitoring_mammal_danger_observation, value))
        return this
    }

    fun setMarking(value: String): MammalEntryBuilder {
        add(FormEntryField("marking", R.string.monitoring_herp_marking, value))
        return this
    }

    fun setDistanceFromAxis(value: String): MammalEntryBuilder {
        add(FormEntryField("axisDistance", R.string.monitoring_herp_axis_distance, value))
        return this
    }

    fun setThreats(value: List<String>): MammalEntryBuilder {
        add(FormEntryField("threats", R.string.monitoring_common_threats, value))
        return this
    }

    fun setNotes(value: String): MammalEntryBuilder {
        add(FormEntryField("speciesNotes", R.string.monitoring_herp_notes, value))
        return this
    }

    fun setL(value: String): MammalEntryBuilder {
        add(FormEntryField("L", R.string.monitoring_mammal_L, value))
        return this
    }

    fun setC(value: String): MammalEntryBuilder {
        add(FormEntryField("C", R.string.monitoring_mammal_C, value))
        return this
    }

    fun setA(value: String): MammalEntryBuilder {
        add(FormEntryField("A", R.string.monitoring_mammal_A, value))
        return this
    }

    fun setPl(value: String): MammalEntryBuilder {
        add(FormEntryField("Pl", R.string.monitoring_mammal_Pl, value))
        return this
    }

    fun setWeight(value: String): MammalEntryBuilder {
        add(FormEntryField("weight", R.string.monitoring_herp_weight, value))
        return this
    }

    fun setTempSubstrate(value: String): MammalEntryBuilder {
        add(FormEntryField("tempSubstrat", R.string.monitoring_herp_t_substrate, value))
        return this
    }

    fun setTempAir(value: String): MammalEntryBuilder {
        add(FormEntryField("tempAir", R.string.monitoring_herp_t_vha, value))
        return this
    }
}