package org.bspb.smartbirds.pro.forms.convert;

import android.content.Context;

import org.bspb.smartbirds.pro.R;

/**
 * Created by groupsky on 28.09.16.
 */

public class BirdsConverter extends Converter {

    public BirdsConverter(Context context) {
        super(context);

        // birds
        addSingle(R.string.tag_source, "source");
        addSpecies(R.string.tag_species_scientific_name, "species");
        addBool(R.string.tag_confidential, "confidential");
        addBool(R.string.tag_moderator_review, "moderatorReview");
        addSingle(R.string.tag_count_unit, "countUnit");
        addSingle(R.string.tag_count_type, "typeUnit");
        addSingle(R.string.tag_nest_type, "typeNesting");
        add(R.string.tag_count, "count", "0");
        add(R.string.tag_min, "countMin", "0");
        add(R.string.tag_max, "countMax", "0");
        addSingle(R.string.tag_sex, "sex");
        addSingle(R.string.tag_age, "age");
        addSingle(R.string.tag_marking, "marking");
        addSingle(R.string.tag_bird_status, "speciesStatus");
        addMulti(R.string.tag_behavior, "behaviour");
        addSingle(R.string.tag_dead_specimen_reason, "deadIndividualCauses");
        addSingle(R.string.tag_substrate, "substrate");
        add(R.string.tag_tree, "tree");
        add(R.string.tag_tree_height, "treeHeight");
        addSingle(R.string.tag_tree_location, "treeLocation");
        addSingle(R.string.tag_nest_height, "nestHeight");
        addSingle(R.string.tag_nest_location, "nestLocation");
        addSingle(R.string.tag_tree_location, "treeLocation");
        addBool(R.string.tag_incubation, "brooding");
        add(R.string.tag_number_of_eggs, "eggsCount");
        add(R.string.tag_number_of_pull, "countNestling");
        add(R.string.tag_number_of_fledglings, "countFledgling");
        add(R.string.tag_number_fledged_juveniles, "countSuccessfullyLeftNest");
        addBool(R.string.tag_nest_guarding, "nestProtected");
        addSingle(R.string.tag_age_female, "ageFemale");
        addSingle(R.string.tag_age_male, "ageMale");
        addSingle(R.string.tag_breeding_success, "nestingSuccess");
        add(R.string.tag_land_uses_300m, "landuse300mRadius");
        add(R.string.tag_remarks_type, "speciesNotes");
        addMulti(R.string.tag_threats, "threats");
    }

}
