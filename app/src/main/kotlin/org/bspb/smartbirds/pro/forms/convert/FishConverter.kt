package org.bspb.smartbirds.pro.forms.convert

import android.content.Context
import org.bspb.smartbirds.pro.R

class FishConverter(context: Context?) : Converter(context) {


    init {
        // fish
        addBool(R.string.tag_confidential, "confidential")
        addBool(R.string.tag_moderator_review, "moderatorReview")
        addSpecies(R.string.tag_species_scientific_name, "species")
        add(R.string.tag_count, "count", "0")
        add(R.string.tag_waterbody, "waterbody")
        addSingle(R.string.tag_age, "age")
        addSingle(R.string.tag_sex, "sex")
        add(R.string.tag_size_tl, "sizeTl")
        add(R.string.tag_size_sl, "sizeSl")
        add(R.string.tag_weight, "weight")
        addSingle(R.string.tag_findings, "findings")
        addSingle(R.string.tag_monitoring_type, "monitoringType")
        add(R.string.tag_threats, "threats")

        // fish - common
        add(R.string.tag_transect_length, "transectLength")
        add(R.string.tag_transect_width, "transectWidth")
        add(R.string.tag_fishing_area, "fishingArea")
        add(R.string.tag_exposition, "exposition")
        add(R.string.tag_mesh_size, "meshSize")
        add(R.string.tag_fishing_duration, "fishingDuration")
        add(R.string.tag_count_net_trap, "countNetTrap")
        add(R.string.tag_water_temp, "waterTemp")
        add(R.string.tag_conductivity, "conductivity")
        add(R.string.tag_ph, "ph")
        add(R.string.tag_o2_mg, "o2Mg")
        add(R.string.tag_o2_percent, "o2Percent")
        add(R.string.tag_salinity, "salinity")
        add(R.string.tag_habitat, "habitat")
        add(R.string.tag_substrate_mud, "substrateMud")
        add(R.string.tag_substrate_silt, "substrateSilt")
        add(R.string.tag_substrate_sand, "substrateSand")
        add(R.string.tag_substrate_gravel, "substrateGravel")
        add(R.string.tag_substrate_small_stones, "substrateSmallStones")
        add(R.string.tag_substrate_cobble, "substrateCobble")
        add(R.string.tag_substrate_boulder, "substrateBoulder")
        add(R.string.tag_substrate_rock, "substrateRock")
        add(R.string.tag_substrate_other, "substrateOther")
        addSingle(R.string.tag_water_level, "waterLevel")
        addSingle(R.string.tag_river_current, "riverCurrent")
        add(R.string.tag_transect_av_depth, "transectAvDepth")
        add(R.string.tag_transect_max_depth, "transectMaxDepth")
        addSingle(R.string.tag_slope, "slope")
        addSingle(R.string.tag_bank_type, "bankType")
        add(R.string.tag_shading, "shading")
        add(R.string.tag_riparian_vegetation, "riparianVegetation")
        add(R.string.tag_shelters, "shelters")
        add(R.string.tag_transparency, "transparency")
        addSingle(R.string.tag_vegetation_type, "vegetationType")

    }
}