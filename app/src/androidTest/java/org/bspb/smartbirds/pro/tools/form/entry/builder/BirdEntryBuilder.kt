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

//    var birdsEntry: Map<Int, Any> = mapOf(
//        R.string.monitoring_birds_min to "2",
//        R.string.monitoring_birds_max to "3",
//        R.string.monitoring_birds_status to "Singing male",
//        R.string.monitoring_birds_behaviour to arrayOf("Feeding"),
//        R.string.monitoring_birds_gender to "Male",
//        R.string.monitoring_birds_age to "Pull",
//        R.string.monitoring_birds_nesting to "Nests",
//        R.string.monitoring_birds_death to "Poison",
//        R.string.monitoring_birds_marking to "Color ring",
//        R.string.monitoring_birds_substrate to "On bushes",
//        R.string.monitoring_birds_tree to "Tree",
//        R.string.monitoring_birds_tree_height to "50",
//        R.string.monitoring_birds_tree_location to "Single tree",
//        R.string.monitoring_birds_nest_height to "1-3 m.",
//        R.string.monitoring_birds_nest_location to "Next to trunk",
//        R.string.monitoring_birds_incubation to true,
//        R.string.monitoring_birds_eggs_count to "3",
//        R.string.monitoring_birds_small_downy_count to "1",
//        R.string.monitoring_birds_small_feathered_count to "5",
//        R.string.monitoring_birds_takeoff_count to "7",
//        R.string.monitoring_birds_nest_guard to true,
//        R.string.monitoring_birds_female_age to "6 years",
//        R.string.monitoring_birds_male_age to "5 years",
//        R.string.monitoring_birds_nest_success to "Occupied nest",
//        R.string.monitoring_birds_land_use to "garden",
//        R.string.monitoring_common_threats to arrayOf("Solar park"),
//        R.string.monitoring_birds_notes to "some notes",
//    )


//    addSingle(R.string.tag_source, "source");
//    addSingle(R.string.tag_nest_type, "typeNesting");
//    add(R.string.tag_min, "countMin", "0");
//    add(R.string.tag_max, "countMax", "0");
//    addSingle(R.string.tag_sex, "sex");
//    addSingle(R.string.tag_age, "age");
//    addSingle(R.string.tag_marking, "marking");
//    addSingle(R.string.tag_bird_status, "speciesStatus");
//    addMulti(R.string.tag_behavior, "behaviour");
//    addSingle(R.string.tag_dead_specimen_reason, "deadIndividualCauses");
//    addSingle(R.string.tag_substrate, "substrate");
//    add(R.string.tag_tree, "tree");
//    add(R.string.tag_tree_height, "treeHeight");
//    addSingle(R.string.tag_tree_location, "treeLocation");
//    addSingle(R.string.tag_nest_height, "nestHeight");
//    addSingle(R.string.tag_nest_location, "nestLocation");
//    addSingle(R.string.tag_tree_location, "treeLocation");
//    addBool(R.string.tag_incubation, "brooding");
//    add(R.string.tag_number_of_eggs, "eggsCount");
//    add(R.string.tag_number_of_pull, "countNestling");
//    add(R.string.tag_number_of_fledglings, "countFledgling");
//    add(R.string.tag_number_fledged_juveniles, "countSuccessfullyLeftNest");
//    addBool(R.string.tag_nest_guarding, "nestProtected");
//    addSingle(R.string.tag_age_female, "ageFemale");
//    addSingle(R.string.tag_age_male, "ageMale");
//    addSingle(R.string.tag_breeding_success, "nestingSuccess");
//    add(R.string.tag_land_uses_300m, "landuse300mRadius");
//    add(R.string.tag_remarks_type, "speciesNotes");
//    addMulti(R.string.tag_threats, "threats");
//


}