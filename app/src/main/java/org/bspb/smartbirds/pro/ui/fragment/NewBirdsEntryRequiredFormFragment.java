package org.bspb.smartbirds.pro.ui.fragment;

import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.backend.dto.Nomenclature;
import org.bspb.smartbirds.pro.prefs.BirdPrefs_;
import org.bspb.smartbirds.pro.prefs.CommonPrefs_;
import org.bspb.smartbirds.pro.ui.views.DecimalNumberFormInput;
import org.bspb.smartbirds.pro.ui.views.SingleChoiceFormInput;
import org.bspb.smartbirds.pro.ui.views.SwitchFormInput;

import java.util.HashMap;
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

    @ViewById(R.id.form_birds_confidential)
    SwitchFormInput confidential;

    @ViewById(R.id.warning_confidential_nest)
    TextView warningConfidential;

    @Pref
    BirdPrefs_ prefs;

    @Pref
    CommonPrefs_ commonPrefs;

    @FragmentById(R.id.pictures_fragment)
    NewEntryPicturesFragment picturesFragment;
    private HashMap<String, String> pendingDeserialize;

    @Override
    public void onResume() {
        super.onResume();
        if (isNewEntry()) {
            countUnits.setSelection(prefs.birdCountUnits().get());
            countType.setSelection(prefs.birdCountType().get());
            confidential.setChecked(commonPrefs.confidentialRecord().get());
        }
        handleCountsLogic();
        showConfidentialWarningIfNeeded();
    }

    @Override
    protected HashMap<String, String> serialize() {
        HashMap<String, String> data = super.serialize();
        data.putAll(picturesFragment.serialize());
        return data;
    }

    @Override
    protected void deserialize(HashMap<String, String> data) {
        super.deserialize(data);
        if (picturesFragment == null) {
            pendingDeserialize = data;
        } else {
            picturesFragment.doDeserialize(monitoringCode, data);
        }
    }

    @AfterViews
    protected void flushDeserialize() {
        if (picturesFragment == null) {
            picturesFragment = (NewEntryPicturesFragment) getChildFragmentManager().findFragmentById(R.id.pictures_fragment);
        }
        if (pendingDeserialize != null) {
            picturesFragment.doDeserialize(monitoringCode, pendingDeserialize);
            pendingDeserialize = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        prefs.birdCountUnits().put(countUnits.getSelection());
        prefs.birdCountType().put(countType.getSelection());
        commonPrefs.confidentialRecord().put(confidential.isChecked());
    }

    @TextChange(R.id.form_birds_count_units)
    @CheckedChange(R.id.form_birds_confidential)
    void showConfidentialWarningIfNeeded() {
        Nomenclature item = countUnits.getSelectedItem();
        String countUnit = item != null ? item.label.get("en") : null;
        if ("Nests".equals(countUnit)) {
            if (!confidential.isChecked()) {
                warningConfidential.setVisibility(View.VISIBLE);
            } else {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    warningConfidential.setVisibility(View.INVISIBLE);
                } else {
                    warningConfidential.setVisibility(View.GONE);
                }
            }
        } else {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                warningConfidential.setVisibility(View.INVISIBLE);
            } else {
                warningConfidential.setVisibility(View.GONE);
            }
        }
    }

    @TextChange(R.id.form_birds_count_type)
    void handleCountsLogic() {
        Nomenclature item = countType.getSelectedItem();
        String countsType = item != null ? item.label.get("en") : null;
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
