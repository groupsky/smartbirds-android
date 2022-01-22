package org.bspb.smartbirds.pro.tools.form.entry.builder

import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.form.entry.FormEntryField

class PlantsEntryBuilder : FormEntryBuilder() {

    fun setModeratorReview(value: Boolean): PlantsEntryBuilder {
        add(FormEntryField("moderatorReview", R.string.monitoring_moderator_review, value))
        return this
    }

    fun setConfidential(value: Boolean): PlantsEntryBuilder {
        add(FormEntryField("confidential", R.string.monitoring_plants_private, value))
        return this
    }

    fun setSpecies(value: String): PlantsEntryBuilder {
        add(FormEntryField("species", R.string.monitoring_plants_name, value))
        return this
    }

    fun setReportingUnit(value: String): PlantsEntryBuilder {
        add(FormEntryField("reportingUnit", R.string.monitoring_plants_reporting_unit, value))
        return this
    }

    fun setPhenologicalPhase(value: String): PlantsEntryBuilder {
        add(
            FormEntryField(
                "phenologicalPhase",
                R.string.monitoring_plants_phenological_phase,
                value
            )
        )
        return this
    }

    fun setCount(value: String): PlantsEntryBuilder {
        add(FormEntryField("count", R.string.monitoring_plants_count, value))
        return this
    }

    fun setDensity(value: String): PlantsEntryBuilder {
        add(FormEntryField("density", R.string.monitoring_plants_density, value))
        return this
    }

    fun setCover(value: String): PlantsEntryBuilder {
        add(FormEntryField("cover", R.string.monitoring_plants_cover, value))
        return this
    }

    fun setHabitat(value: String): PlantsEntryBuilder {
        add(FormEntryField("habitat", R.string.monitoring_plants_habitat, value))
        return this
    }

    fun setElevation(value: String): PlantsEntryBuilder {
        add(FormEntryField("elevation", R.string.monitoring_plants_elevation, value))
        return this
    }

    fun setThreatsPlants(value: List<String>): PlantsEntryBuilder {
        add(FormEntryField("threatsPlants", R.string.monitoring_plants_threats, value))
        return this
    }

    fun setThreats(value: List<String>): PlantsEntryBuilder {
        add(FormEntryField("threats", R.string.monitoring_common_threats, value))
        return this
    }

    fun setNotes(value: String): PlantsEntryBuilder {
        add(FormEntryField("speciesNotes", R.string.monitoring_plants_notes, value))
        return this
    }

    fun setAccompanyingSpecies(value: List<String>): PlantsEntryBuilder {
        add(
            FormEntryField(
                "accompanyingSpecies",
                R.string.monitoring_plants_accompanying_species,
                value
            )
        )
        return this
    }
}