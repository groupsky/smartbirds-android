package org.bspb.smartbirds.pro.forms.convert;

import android.content.Context;

import org.bspb.smartbirds.pro.R;

/**
 * Created by groupsky on 28.09.16.
 */

public class CbmConverter extends Converter {

    public CbmConverter(Context context) {
        super(context);

        addSingle(R.string.tag_transect_number, "plot");
        addSingle(R.string.tag_visit_number, "visit");
        addSingle(R.string.tag_secondary_habitat, "secondaryHabitat");
        addSingle(R.string.tag_primary_habitat, "primaryHabitat");
        addSingle(R.string.tag_distance, "distance");
        addSpecies(R.string.tag_observed_bird, "species");
        add(R.string.tag_count_subject, "count");
        add(R.string.tag_location, "location");
        add(R.string.tag_zone, "zone");
        addMulti(R.string.tag_threats, "threats");
        addBool(R.string.tag_moderator_review, "moderatorReview");
    }

}