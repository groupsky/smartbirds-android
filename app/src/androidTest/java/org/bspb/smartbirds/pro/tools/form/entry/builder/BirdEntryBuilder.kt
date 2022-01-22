package org.bspb.smartbirds.pro.tools.form.entry.builder

import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.form.entry.FormEntryField

class BirdEntryBuilder : FormEntryBuilder() {

    fun setModeratorReview(value: Boolean): BirdEntryBuilder {
        add(FormEntryField("moderatorReview", R.string.monitoring_moderator_review, value))
        return this
    }

    fun setConfidential(value: Boolean): BirdEntryBuilder {
        add(FormEntryField("confidential", R.string.monitoring_birds_private, value))
        return this
    }

    fun setSpecies(value: String): BirdEntryBuilder {
        add(FormEntryField("species", R.string.monitoring_birds_name, value))
        return this
    }

    fun setCountUnit(value: String): BirdEntryBuilder {
        add(FormEntryField("countUnit", R.string.monitoring_birds_count_unit, value))
        return this
    }

    fun setCountType(value: String): BirdEntryBuilder {
        add(FormEntryField("typeUnit", R.string.monitoring_birds_count_type, value))
        return this
    }

    fun setCount(value: String): BirdEntryBuilder {
        add(FormEntryField("count", R.string.monitoring_birds_count, value))
        return this
    }

    fun setCountMin(value: String): BirdEntryBuilder {
        add(FormEntryField("countMin", R.string.monitoring_birds_min, value))
        return this
    }

    fun setCountMax(value: String): BirdEntryBuilder {
        add(FormEntryField("countMax", R.string.monitoring_birds_max, value))
        return this
    }

    fun setBirdsStatus(value: String): BirdEntryBuilder {
        add(FormEntryField("speciesStatus", R.string.monitoring_birds_status, value))
        return this
    }

    fun setBehaviour(value: List<String>): BirdEntryBuilder {
        add(FormEntryField("behaviour", R.string.monitoring_birds_behaviour, value))
        return this
    }

    fun setGender(value: String): BirdEntryBuilder {
        add(FormEntryField("sex", R.string.monitoring_birds_gender, value))
        return this
    }

    fun setAge(value: String): BirdEntryBuilder {
        add(FormEntryField("age", R.string.monitoring_birds_age, value))
        return this
    }

    fun setNesting(value: String): BirdEntryBuilder {
        add(FormEntryField("typeNesting", R.string.monitoring_birds_nesting, value))
        return this
    }

    fun setDeath(value: String): BirdEntryBuilder {
        add(FormEntryField("deadIndividualCauses", R.string.monitoring_birds_death, value))
        return this
    }

    fun setMarking(value: String): BirdEntryBuilder {
        add(FormEntryField("marking", R.string.monitoring_birds_marking, value))
        return this
    }

    fun setSubstrate(value: String): BirdEntryBuilder {
        add(FormEntryField("substrate", R.string.monitoring_birds_substrate, value))
        return this
    }

    fun setTree(value: String): BirdEntryBuilder {
        add(FormEntryField("tree", R.string.monitoring_birds_tree, value))
        return this
    }

    fun setTreeHeight(value: String): BirdEntryBuilder {
        add(FormEntryField("treeHeight", R.string.monitoring_birds_tree_height, value))
        return this
    }

    fun setTreeLocation(value: String): BirdEntryBuilder {
        add(FormEntryField("treeLocation", R.string.monitoring_birds_tree_location, value))
        return this
    }

    fun setNestHeight(value: String): BirdEntryBuilder {
        add(FormEntryField("nestHeight", R.string.monitoring_birds_nest_height, value))
        return this
    }

    fun setNestLocation(value: String): BirdEntryBuilder {
        add(FormEntryField("nestLocation", R.string.monitoring_birds_nest_location, value))
        return this
    }

    fun setIncubation(value: Boolean): BirdEntryBuilder {
        add(FormEntryField("brooding", R.string.monitoring_birds_incubation, value))
        return this
    }

    fun setEggsCount(value: String): BirdEntryBuilder {
        add(FormEntryField("eggsCount", R.string.monitoring_birds_eggs_count, value))
        return this
    }

    fun setSmallDownyCount(value: String): BirdEntryBuilder {
        add(FormEntryField("countNestling", R.string.monitoring_birds_small_downy_count, value))
        return this
    }

    fun setSmallFeatheredCount(value: String): BirdEntryBuilder {
        add(
            FormEntryField(
                "countFledgling",
                R.string.monitoring_birds_small_feathered_count,
                value
            )
        )
        return this
    }

    fun setTakeoffCount(value: String): BirdEntryBuilder {
        add(
            FormEntryField(
                "countSuccessfullyLeftNest",
                R.string.monitoring_birds_takeoff_count,
                value
            )
        )
        return this
    }

    fun setNestGuard(value: Boolean): BirdEntryBuilder {
        add(FormEntryField("nestProtected", R.string.monitoring_birds_nest_guard, value))
        return this
    }

    fun setFemaleAge(value: String): BirdEntryBuilder {
        add(FormEntryField("ageFemale", R.string.monitoring_birds_female_age, value))
        return this
    }

    fun setMaleAge(value: String): BirdEntryBuilder {
        add(FormEntryField("ageMale", R.string.monitoring_birds_male_age, value))
        return this
    }

    fun setNestingSuccess(value: String): BirdEntryBuilder {
        add(FormEntryField("nestingSuccess", R.string.monitoring_birds_nest_success, value))
        return this
    }

    fun setLandUse(value: String): BirdEntryBuilder {
        add(FormEntryField("landuse300mRadius", R.string.monitoring_birds_land_use, value))
        return this
    }

    fun setThreats(value: List<String>): BirdEntryBuilder {
        add(FormEntryField("threats", R.string.monitoring_common_threats, value))
        return this
    }

    fun setNotes(value: String): BirdEntryBuilder {
        add(FormEntryField("speciesNotes", R.string.monitoring_birds_notes, value))
        return this
    }
}