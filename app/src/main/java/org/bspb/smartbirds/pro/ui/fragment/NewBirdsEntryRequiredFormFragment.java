package org.bspb.smartbirds.pro.ui.fragment;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.backend.dto.Nomenclature;
import org.bspb.smartbirds.pro.prefs.BirdPrefs_;
import org.bspb.smartbirds.pro.ui.views.DecimalNumberFormInput;
import org.bspb.smartbirds.pro.ui.views.SingleChoiceFormInput;

import java.util.Locale;

/**
 * Created by groupsky on 26.01.17.
 */

@EFragment(R.layout.fragment_monitoring_form_new_birds_required_entry)
public class NewBirdsEntryRequiredFormFragment extends BaseFormFragment {

    @ViewById(R.id.form_birds_count_units)
    SingleChoiceFormInput countUnits;

    @ViewById(R.id.form_birds_count_type)
    SingleChoiceFormInput countType;

    @ViewById(R.id.form_birds_count)
    DecimalNumberFormInput count;
    @ViewById(R.id.form_birds_count_min)
    DecimalNumberFormInput countMin;
    @ViewById(R.id.form_birds_count_max)
    DecimalNumberFormInput countMax;

    @Pref
    BirdPrefs_ prefs;

    @Override
    public void onResume() {
        super.onResume();
        countUnits.setSelection(prefs.birdCountUnits().get());
        countType.setSelection(prefs.birdCountType().get());
        handleCountsLogic();
    }

    @Override
    public void onPause() {
        super.onPause();
        prefs.birdCountUnits().put(countUnits.getSelection());
        prefs.birdCountType().put(countType.getSelection());
    }

    @TextChange(R.id.form_birds_count_type)
    void handleCountsLogic() {
        Nomenclature item = countType.getSelectedItem();
        String countsType = item != null ? item.label.en : null;
        switch (countsType != null ? countsType.toLowerCase(Locale.ENGLISH) : "") {
            case "exact number": // Exact count
                count.setEnabled(true);
                countMin.setEnabled(false);
                countMax.setEnabled(false);
                break;
            case "min.": // Min count
                count.setEnabled(false);
                countMin.setEnabled(true);
                countMax.setEnabled(false);
                break;
            case "max.": // Max count
                count.setEnabled(false);
                countMin.setEnabled(false);
                countMax.setEnabled(true);
                break;
            case "range": // Range count
                count.setEnabled(false);
                countMin.setEnabled(true);
                countMax.setEnabled(true);
                break;
            case "unspecified number": // Unspecified
                count.setEnabled(false);
                countMin.setEnabled(false);
                countMax.setEnabled(false);
                break;
            default:
                count.setEnabled(true);
                countMin.setEnabled(true);
                countMax.setEnabled(true);
                break;
        }
    }

}
