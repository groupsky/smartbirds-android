package org.bspb.smartbirds.pro.tools.form.entry.builder

import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.form.entry.FormEntryField

class HerptileEntryBuilder : FormEntryBuilder() {

    fun setModeratorReview(value: Boolean): HerptileEntryBuilder {
        add(FormEntryField("moderatorReview", R.string.monitoring_moderator_review, value))
        return this
    }

    fun setConfidential(value: Boolean): HerptileEntryBuilder {
        add(FormEntryField("confidential", R.string.monitoring_herptiles_private, value))
        return this
    }

    fun setSpecies(value: String): HerptileEntryBuilder {
        add(FormEntryField("species", R.string.monitoring_herp_name, value))
        return this
    }

    fun setGender(value: String): HerptileEntryBuilder {
        add(FormEntryField("sex", R.string.monitoring_herp_gender, value))
        return this
    }

    fun setAge(value: String): HerptileEntryBuilder {
        add(FormEntryField("age", R.string.monitoring_herp_age, value))
        return this
    }

    fun setCount(value: String): HerptileEntryBuilder {
        add(FormEntryField("count", R.string.monitoring_herp_count, value))
        return this
    }

    fun setHabitat(value: String): HerptileEntryBuilder {
        add(FormEntryField("habitat", R.string.monitoring_herp_habitat, value))
        return this
    }

    fun setFindings(value: List<String>): HerptileEntryBuilder {
        add(FormEntryField("findings", R.string.monitoring_herptiles_danger_observation, value))
        return this
    }

    fun setMarking(value: String): HerptileEntryBuilder {
        add(FormEntryField("marking", R.string.monitoring_herp_marking, value))
        return this
    }

    fun setDistanceFromAxis(value: String): HerptileEntryBuilder {
        add(FormEntryField("axisDistance", R.string.monitoring_herp_axis_distance, value))
        return this
    }

    fun setThreats(value: List<String>): HerptileEntryBuilder {
        add(FormEntryField("threats", R.string.monitoring_common_threats, value))
        return this
    }

    fun setNotes(value: String): HerptileEntryBuilder {
        add(FormEntryField("speciesNotes", R.string.monitoring_herp_notes, value))
        return this
    }

    fun setScl(value: String): HerptileEntryBuilder {
        add(FormEntryField("sCLL", R.string.monitoring_herp_scl, value))
        return this
    }

    fun setMpl(value: String): HerptileEntryBuilder {
        add(FormEntryField("mPLLcdC", R.string.monitoring_herp_mpl, value))
        return this
    }

    fun setMcw(value: String): HerptileEntryBuilder {
        add(FormEntryField("mCWA", R.string.monitoring_herp_mcw, value))
        return this
    }

    fun setHLcap(value: String): HerptileEntryBuilder {
        add(FormEntryField("hLcapPl", R.string.monitoring_herp_h, value))
        return this
    }

    fun setWeight(value: String): HerptileEntryBuilder {
        add(FormEntryField("weight", R.string.monitoring_herp_weight, value))
        return this
    }

    fun setTempSubstrate(value: String): HerptileEntryBuilder {
        add(FormEntryField("tempSubstrat", R.string.monitoring_herp_t_substrate, value))
        return this
    }

    fun setTempAir(value: String): HerptileEntryBuilder {
        add(FormEntryField("tempAir", R.string.monitoring_herp_t_vha, value))
        return this
    }

    fun setTempCloaca(value: String): HerptileEntryBuilder {
        add(FormEntryField("tempCloaca", R.string.monitoring_herp_t_kloaka, value))
        return this
    }

    fun setSqVentr(value: String): HerptileEntryBuilder {
        add(FormEntryField("sqVentr", R.string.monitoring_herp_sq_ventr, value))
        return this
    }

    fun setSqCaud(value: String): HerptileEntryBuilder {
        add(FormEntryField("sqCaud", R.string.monitoring_herp_sq_caud, value))
        return this
    }

    fun setSqDors(value: String): HerptileEntryBuilder {
        add(FormEntryField("sqDors", R.string.monitoring_herp_sq_dors, value))
        return this
    }
}