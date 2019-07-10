package org.bspb.smartbirds.pro.forms.convert;

import android.content.Context;

import org.bspb.smartbirds.pro.R;

/**
 * Created by dani on 22.02.18.
 */

public class PlantsConverter extends Converter {

    public PlantsConverter(Context context) {
        super(context);

        // plants
        addSpecies(R.string.tag_species_scientific_name, "species");
        add(R.string.tag_elevation, "elevation");
        addSingle(R.string.tag_habitat, "habitat");
        addSpeciesMulti(R.string.tag_accompanyingSpecies, "accompanyingSpecies");
        addSingle(R.string.tag_reportingUnit, "reportingUnit");
        addSingle(R.string.tag_phenologicalPhase, "phenologicalPhase");
        add(R.string.tag_count, "count");
        add(R.string.tag_density, "density");
        add(R.string.tag_cover, "cover");
        addMulti(R.string.tag_threats_other, "threatsPlants");
        addMulti(R.string.tag_threats, "threats");
        add(R.string.tag_remarks_type, "speciesNotes");
        addBool(R.string.tag_confidential, "confidential");
    }

}
