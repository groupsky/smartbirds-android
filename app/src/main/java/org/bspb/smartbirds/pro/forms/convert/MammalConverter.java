package org.bspb.smartbirds.pro.forms.convert;

import android.content.Context;

import org.bspb.smartbirds.pro.R;

/**
 * Created by dani on 04.01.18.
 */

public class MammalConverter extends Converter {

    public MammalConverter(Context context) {
        super(context);

        addSpecies(R.string.tag_species_scientific_name, "species");
        addSingle(R.string.tag_sex, "sex");
        addSingle(R.string.tag_age, "age");
        addSingle(R.string.tag_habitat, "habitat");
        addMulti(R.string.tag_threats_other, "findings");
        add(R.string.tag_count, "count");
        add(R.string.tag_marking, "marking");
        add(R.string.tag_distance_from_axis, "axisDistance");
        add(R.string.tag_weight_g, "weight");
        add(R.string.tag_l, "L");
        add(R.string.tag_c, "C");
        add(R.string.tag_a, "A");
        add(R.string.tag_pl, "Pl");
        add(R.string.tag_t_substrate, "tempSubstrat");
        add(R.string.tag_t_air, "tempAir");
        add(R.string.tag_remarks_type, "speciesNotes");
        addBool(R.string.tag_confidential, "confidential");
    }

}
