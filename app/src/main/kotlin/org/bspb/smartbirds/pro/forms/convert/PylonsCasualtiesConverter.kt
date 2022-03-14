package org.bspb.smartbirds.pro.forms.convert

import android.content.Context
import org.bspb.smartbirds.pro.R

class PylonsCasualtiesConverter(context: Context?) : Converter(context) {

    init {

        addSpecies(R.string.tag_species_scientific_name, "species")
        add(R.string.tag_count, "count", "0")
        addSingle(R.string.tag_age, "age")
        addSingle(R.string.tag_sex, "sex")
        addSingle(R.string.tag_casualties_cause_of_death, "causeOfDeath")
        addSingle(R.string.tag_casualties_body_condition, "bodyCondition")
        addSingle(R.string.tag_casualties_primary_habitat, "habitat100mPrime")
        addSingle(R.string.tag_casualties_secondary_habitat, "habitat100mSecond")
        addBool(R.string.tag_confidential, "confidential")
        addBool(R.string.tag_moderator_review, "moderatorReview")
        add(R.string.tag_remarks_type, "speciesNotes")

    }
}