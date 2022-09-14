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
        add(R.string.tag_waterbody, "nameWaterBody")
        addSingle(R.string.tag_sex, "sex")
        addSingle(R.string.tag_age, "age")
        add(R.string.tag_size_tl, "sizeTL_mm")
        add(R.string.tag_size_sl, "sizeSL_mm")
        add(R.string.tag_masa_gr, "masa_gr")
        addSingle(R.string.tag_findings, "findings")
        addSingle(R.string.tag_monitoring_type, "monitoringType")
        add(R.string.tag_threats, "threats")
        add(R.string.tag_remarks_type, "speciesNotes")

        // fish - common
        add(R.string.tag_transect_length_m, "transectLength_M")
        add(R.string.tag_transect_width_m, "transectWidth_M")
        add(R.string.tag_fishing_area_m, "fishingArea_M")
        add(R.string.tag_exposition, "exposition")
        add(R.string.tag_mesh_size, "meshSize")
        add(R.string.tag_count_net_trap, "countNetTrap")
        add(R.string.tag_water_temp, "waterTemp")
        add(R.string.tag_conductivity, "conductivity")
        add(R.string.tag_ph, "pH")
        add(R.string.tag_o2_mg, "o2mgL")
        add(R.string.tag_o2_percent, "o2percent")
        add(R.string.tag_salinity, "salinity")
        addSingle(R.string.tag_habitat_description_type, "habitatDescriptionType")
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
        addMulti(R.string.tag_shelters, "shelters")
        add(R.string.tag_transparency, "transparency")
        addMulti(R.string.tag_vegetation_type, "vegetationType")

    }
}