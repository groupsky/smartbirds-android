package org.bspb.smartbirds.pro.tools.form.entry.builder

import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.form.entry.DateEntryField
import org.bspb.smartbirds.pro.tools.form.entry.FormEntryField

class CiconiaEntryBuilder : FormEntryBuilder() {

    fun setSubstrate(value: String): CiconiaEntryBuilder {
        add(FormEntryField("primarySubstrateType", R.string.monitoring_ciconia_substratum, value))
        return this
    }

    fun setPylon(value: String): CiconiaEntryBuilder {
        add(FormEntryField("electricityPole", R.string.monitoring_ciconia_column, value))
        return this
    }

    fun setNestIsOnArtificialPlatform(value: Boolean): CiconiaEntryBuilder {
        add(
            FormEntryField(
                "nestIsOnArtificialPlatform",
                R.string.monitoring_ciconia_nest_artificial,
                value
            )
        )
        return this
    }

    fun setPylonType(value: String): CiconiaEntryBuilder {
        add(FormEntryField("typeElectricityPole", R.string.monitoring_ciconia_column_type, value))
        return this
    }

    fun setTree(value: String): CiconiaEntryBuilder {
        add(FormEntryField("tree", R.string.monitoring_ciconia_tree, value))
        return this
    }

    fun setBuilding(value: String): CiconiaEntryBuilder {
        add(FormEntryField("building", R.string.monitoring_ciconia_building, value))
        return this
    }

    fun setNestIsOnHumanArtificialPlatform(value: Boolean): CiconiaEntryBuilder {
        add(
            FormEntryField(
                "nestOnArtificialHumanMadePlatform",
                R.string.monitoring_ciconia_nest_artificial_human,
                value
            )
        )
        return this
    }

    fun setNestOnAnotherSubstrate(value: String): CiconiaEntryBuilder {
        add(
            FormEntryField(
                "nestIsOnAnotherTypeOfSubstrate",
                R.string.monitoring_ciconia_nest_other,
                value
            )
        )
        return this
    }

    fun setNestThisYearNotUtilizedByWhiteStorks(value: String): CiconiaEntryBuilder {
        add(
            FormEntryField(
                "nestThisYearNotUtilizedByWhiteStorks",
                R.string.monitoring_ciconia_not_occupied,
                value
            )
        )
        return this
    }

    fun setThisYearOneTwoBirdsAppearedInNest(value: String): CiconiaEntryBuilder {
        add(
            FormEntryField(
                "thisYearOneTwoBirdsAppearedInNest",
                R.string.monitoring_ciconia_new_birds,
                value
            )
        )
        return this
    }

    fun setApproximateDateStorksAppeared(value: String): CiconiaEntryBuilder {
        add(
            DateEntryField(
                "approximateDateStorksAppeared",
                R.string.monitoring_ciconia_date_appear,
                value
            )
        )
        return this
    }

    fun setApproximateDateDisappearanceWhiteStorks(value: String): CiconiaEntryBuilder {
        add(
            DateEntryField(
                "approximateDateDisappearanceWhiteStorks",
                R.string.monitoring_ciconia_disappear,
                value
            )
        )
        return this
    }

    fun setThisYearInTheNestAppeared(value: String): CiconiaEntryBuilder {
        add(
            FormEntryField(
                "thisYearInTheNestAppeared",
                R.string.monitoring_ciconia_this_year,
                value
            )
        )
        return this
    }

    fun setCountJuvenilesInNest(value: String): CiconiaEntryBuilder {
        add(FormEntryField("countJuvenilesInNest", R.string.monitoring_ciconia_small_count, value))
        return this
    }

    fun setNestNotUsedForOverOneYear(value: String): CiconiaEntryBuilder {
        add(
            FormEntryField(
                "nestNotUsedForOverOneYear",
                R.string.monitoring_ciconia_not_occupied_more_than_year,
                value
            )
        )
        return this
    }

    fun setDataOnJuvenileMortalityFromElectrocutions(value: String): CiconiaEntryBuilder {
        add(
            FormEntryField(
                "dataOnJuvenileMortalityFromElectrocutions",
                R.string.monitoring_ciconia_small_death_electricity,
                value
            )
        )
        return this
    }

    fun setDataOnJuvenilesExpelledFromParents(value: String): CiconiaEntryBuilder {
        add(
            FormEntryField(
                "dataOnJuvenilesExpelledFromParents",
                R.string.monitoring_ciconia_small_death_from_parent,
                value
            )
        )
        return this
    }

    fun setDiedOtherReasons(value: String): CiconiaEntryBuilder {
        add(
            FormEntryField(
                "diedOtherReasons",
                R.string.monitoring_ciconia_small_death_other,
                value
            )
        )
        return this
    }

    fun setReason(value: String): CiconiaEntryBuilder {
        add(FormEntryField("reason", R.string.monitoring_ciconia_cause, value))
        return this
    }

    fun setThreats(value: List<String>): CiconiaEntryBuilder {
        add(FormEntryField("threats", R.string.monitoring_common_threats, value))
        return this
    }

    fun setNotes(value: String): CiconiaEntryBuilder {
        add(FormEntryField("speciesNotes", R.string.monitoring_ciconia_notes, value))
        return this
    }

}