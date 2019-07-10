package org.bspb.smartbirds.pro.forms.convert;

import android.content.Context;

import org.bspb.smartbirds.pro.R;

/**
 * Created by groupsky on 28.09.16.
 */

public class CiconiaConverter extends Converter {

    public CiconiaConverter(Context context) {
        super(context);

        addSingle(R.string.tag_substrate_type, "primarySubstrateType");
        addSingle(R.string.tag_pylon, "electricityPole");
        addBool(R.string.tag_nest_artificial_platform, "nestIsOnArtificialPlatform");
        addSingle(R.string.tag_pylon_type, "typeElectricityPole");
        addSingle(R.string.tag_ciconia_tree, "tree");
        addSingle(R.string.tag_building, "building");
        addBool(R.string.tag_nest_artificial_platform_human, "nestOnArtificialHumanMadePlatform");
        add(R.string.tag_nest_another_substrate, "nestIsOnAnotherTypeOfSubstrate");
        addSingle(R.string.tag_nest_not_occupied_this_year, "nestThisYearNotUtilizedByWhiteStorks");
        addSingle(R.string.tag_birds_come_to_nest_this_year, "thisYearOneTwoBirdsAppearedInNest");
        addDate(R.string.tag_approximate_date_stork_arrival, "approximateDateStorksAppeared");
        addDate(R.string.tag_approximate_date_stork_disappear, "approximateDateDisappearanceWhiteStorks");
        addSingle(R.string.tag_this_year_in_the_nest_appeared, "thisYearInTheNestAppeared");
        add(R.string.tag_number_juveniles_in_nest, "countJuvenilesInNest");
        add(R.string.tag_nest_not_inhabited_more_than_year, "nestNotUsedForOverOneYear");
        add(R.string.tag_info_for_juveniles_electrocuted, "dataOnJuvenileMortalityFromElectrocutions");
        add(R.string.tag_info_for_juveniles_rejected_by_parents, "dataOnJuvenilesExpelledFromParents");
        add(R.string.tag_died_from_other_causes, "diedOtherReasons");
        add(R.string.tag_cause, "reason");
        add(R.string.tag_ciconia_remarks_type, "speciesNotes");
        addMulti(R.string.tag_threats, "threats");
    }

}
