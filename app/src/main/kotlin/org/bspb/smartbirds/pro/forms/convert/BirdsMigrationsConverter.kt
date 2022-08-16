package org.bspb.smartbirds.pro.forms.convert

import android.content.Context
import org.bspb.smartbirds.pro.R

class BirdsMigrationsConverter(context: Context?) : Converter(context) {


    init {
        // birds
        addSpecies(R.string.tag_species_scientific_name, "species")
        add(R.string.tag_count, "count", "0")
        addSingle(R.string.tag_migration_point, "migrationPoint")
        addSingle(R.string.tag_location_from_migration_point, "locationFromMigrationPoint")
        addSingle(R.string.tag_sex, "sex")
        addSingle(R.string.tag_plumage, "plumage")
        addSingle(R.string.tag_age, "age")
        add(R.string.tag_visochina_polet, "visochinaPolet")
        addSingle(R.string.tag_posoka_polet_from, "posokaPoletFrom")
        addSingle(R.string.tag_posoka_polet_to, "posokaPoletTo")
        addMulti(R.string.tag_type_flight, "typeFlight")
        add(R.string.tag_remarks_type, "speciesNotes")
        addBool(R.string.tag_confidential, "confidential")
        addBool(R.string.tag_moderator_review, "moderatorReview")
    }
}