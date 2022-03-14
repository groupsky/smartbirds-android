package org.bspb.smartbirds.pro.tools.form.entry.builder

import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.form.entry.FormEntryField

class PylonsCasualtiesEntryBuilder : FormEntryBuilder() {

    fun setModeratorReview(value: Boolean): PylonsCasualtiesEntryBuilder {
        add(FormEntryField("moderatorReview", R.string.monitoring_moderator_review, value))
        return this
    }

    fun setConfidential(value: Boolean): PylonsCasualtiesEntryBuilder {
        add(FormEntryField("confidential", R.string.monitoring_pylons_casualties_private, value))
        return this
    }

    fun setSpecies(value: String): PylonsCasualtiesEntryBuilder {
        add(FormEntryField("species", R.string.monitoring_pylons_casualties_name, value))
        return this
    }

    fun setCount(value: String): PylonsCasualtiesEntryBuilder {
        add(FormEntryField("count", R.string.monitoring_birds_count, value))
        return this
    }

    fun setAge(value: String): PylonsCasualtiesEntryBuilder {
        add(
            FormEntryField(
                "age",
                R.string.monitoring_pylons_casualties_age,
                value
            )
        )
        return this
    }

    fun setGender(value: String): PylonsCasualtiesEntryBuilder {
        add(FormEntryField("sex", R.string.monitoring_pylons_casualties_gender, value))
        return this
    }

    fun setCauseOfDeath(value: String): PylonsCasualtiesEntryBuilder {
        add(FormEntryField("causeOfDeath", R.string.monitoring_pylons_casualties_cause_of_death, value))
        return this
    }

    fun setBodyCondition(value: String): PylonsCasualtiesEntryBuilder {
        add(FormEntryField("bodyCondition", R.string.monitoring_pylons_casualties_body_condition, value))
        return this
    }

    fun setPrimaryHabitat(value: String): PylonsCasualtiesEntryBuilder {
        add(FormEntryField("habitat100mPrime", R.string.monitoring_pylons_casualties_primary_habitat, value))
        return this
    }

    fun setSecondaryHabitat(value: String): PylonsCasualtiesEntryBuilder {
        add(FormEntryField("habitat100mSecond", R.string.monitoring_pylons_casualties_secondary_habitat, value))
        return this
    }

    fun setNotes(value: String): PylonsCasualtiesEntryBuilder {
        add(FormEntryField("speciesNotes", R.string.monitoring_pylons_casualties_notes, value))
        return this
    }
}