package org.bspb.smartbirds.pro.tools.form.entry.builder

import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.form.entry.FormEntryField

class PylonsEntryBuilder : FormEntryBuilder() {

    fun setModeratorReview(value: Boolean): PylonsEntryBuilder {
        add(FormEntryField("moderatorReview", R.string.monitoring_moderator_review, value))
        return this
    }

    fun setConfidential(value: Boolean): PylonsEntryBuilder {
        add(FormEntryField("confidential", R.string.monitoring_birds_private, value))
        return this
    }

    fun setPylonType(value: String): PylonsEntryBuilder {
        add(FormEntryField("pylonType", R.string.monitoring_pylons_pylon_type, value))
        return this
    }

    fun setSpeciesNestOnPylon(value: String): PylonsEntryBuilder {
        add(FormEntryField("speciesNestOnPylon", R.string.monitoring_pylons_species_nest_on_pylon, value))
        return this
    }

    fun setNestType(value: String): PylonsEntryBuilder {
        add(
            FormEntryField(
                "typeNest",
                R.string.monitoring_pylons_nest_type,
                value
            )
        )
        return this
    }

    fun setPylonInsulated(value: Boolean): PylonsEntryBuilder {
        add(FormEntryField("pylonInsulated", R.string.monitoring_pylons_pylon_insulated, value))
        return this
    }

    fun setPrimaryHabitat(value: String): PylonsEntryBuilder {
        add(FormEntryField("habitat100mPrime", R.string.monitoring_pylons_primary_habitat, value))
        return this
    }

    fun setSecondaryHabitat(value: String): PylonsEntryBuilder {
        add(FormEntryField("habitat100mSecond", R.string.monitoring_pylons_secondary_habitat, value))
        return this
    }

    fun setNotes(value: String): PylonsEntryBuilder {
        add(FormEntryField("speciesNotes", R.string.monitoring_pylons_notes, value))
        return this
    }
}