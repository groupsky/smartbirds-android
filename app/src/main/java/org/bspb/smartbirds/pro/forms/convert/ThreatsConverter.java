package org.bspb.smartbirds.pro.forms.convert;

import android.content.Context;

import org.bspb.smartbirds.pro.R;

public class ThreatsConverter extends Converter {

    public ThreatsConverter(Context context) {
        super(context);

        // threats
        addSingle(R.string.tag_category, "category");
        addSingle(R.string.tag_class, "class");
        addSpecies(R.string.tag_species_scientific_name, "species");
        addSingle(R.string.tag_estimate, "estimate");
        add(R.string.tag_poisoned_type, "poisonedType");
        addSingle(R.string.tag_state_carcass, "stateCarcass");
        addSingle(R.string.tag_sample_taken_1, "sampleTaken1");
        addSingle(R.string.tag_sample_taken_2, "sampleTaken2");
        addSingle(R.string.tag_sample_taken_3, "sampleTaken3");
        add(R.string.tag_sample_code_1, "sampleCode1");
        add(R.string.tag_sample_code_2, "sampleCode2");
        add(R.string.tag_sample_code_3, "sampleCode3");
        add(R.string.tag_count, "count", "0");
        add(R.string.tag_threats_notes, "threatsNotes");
        add(R.string.tag_primary_type, "primaryType");
        addBool(R.string.tag_confidential, "confidential");
    }
}
