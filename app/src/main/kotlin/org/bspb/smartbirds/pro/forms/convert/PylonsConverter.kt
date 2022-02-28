package org.bspb.smartbirds.pro.forms.convert

import android.content.Context
import org.bspb.smartbirds.pro.R

class PylonsConverter(context: Context?) : Converter(context) {

    init {

        // birds
        addSingle(R.string.tag_pylons_pylon_type, "pylonType")
        addSpecies(R.string.tag_pylons_species_nest_on_pylon, "speciesNestOnPylon")
        addSingle(R.string.tag_pylons_nest_type, "typeNest")
        addBool(R.string.tag_pylons_pylon_insulated, "pylonInsulated")
        addSingle(R.string.tag_pylons_primary_habitat, "habitat100mPrime")
        addSingle(R.string.tag_pylons_secondary_habitat, "habitat100mSecond")
        addBool(R.string.tag_confidential, "confidential")
        addBool(R.string.tag_moderator_review, "moderatorReview")

    }
}