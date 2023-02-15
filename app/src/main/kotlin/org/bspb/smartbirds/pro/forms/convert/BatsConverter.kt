package org.bspb.smartbirds.pro.forms.convert

import android.content.Context
import org.bspb.smartbirds.pro.R

class BatsConverter(context: Context?) : Converter(context) {


    init {
        // Bats main form
        addBool(R.string.tag_confidential, "confidential")
        addBool(R.string.tag_moderator_review, "moderatorReview")
        addSpecies(R.string.tag_species_scientific_name, "species")
        add(R.string.tag_count, "count", "0")
        addSingle(R.string.tag_metodoly, "metodology")
        add(R.string.tag_temp_cave, "tCave", "0")
        add(R.string.tag_humidity_cave, "hCave", "0")
        addSingle(R.string.tag_type_locality, "typloc")
        add(R.string.tag_sublocality, "sublocality")
        addBool(R.string.tag_swarming, "swarming")
        addSingle(R.string.tag_sex, "sex")
        addSingle(R.string.tag_age, "age")
        add(R.string.tag_remarks_type, "speciesNotes")
        addSingle(R.string.tag_habitats, "habitats")
        addSingle(R.string.tag_condition, "condition")
        addSingle(R.string.tag_bat_type_condition, "typeCond")
        addMulti(R.string.tag_threats, "threats")

        // Additional fields
        addSingle(R.string.tag_reproductive_status, "reproductiveStatus")
        addSingle(R.string.tag_ring, "ring")
        add(R.string.tag_ring_number, "ringN")
        add(R.string.tag_body_length, "bodyLength")
        add(R.string.tag_tail_length, "tailLength")
        add(R.string.tag_ear_length, "earLength")
        add(R.string.tag_forearm_length, "forearmLength")
        add(R.string.tag_length_third_digit, "lengthThirdDigit")
        add(R.string.tag_length_fifth_digit, "lengthFifthDigit")
        add(R.string.tag_length, "lengthWS")
        add(R.string.tag_weight, "weight")

    }
}