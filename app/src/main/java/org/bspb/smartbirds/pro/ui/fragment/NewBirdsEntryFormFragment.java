package org.bspb.smartbirds.pro.ui.fragment;

import android.widget.TextView;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.prefs.BirdPrefs_;
import org.bspb.smartbirds.pro.ui.views.FloatNumberFormInput;
import org.bspb.smartbirds.pro.ui.views.SingleChoiceFormInput;


@EFragment(R.layout.fragment_monitoring_form_birds)
public class NewBirdsEntryFormFragment extends BaseEntryFragment {

    @ViewById(R.id.form_birds_count_units)
    SingleChoiceFormInput countUnits;

    @ViewById(R.id.form_birds_count_type)
    SingleChoiceFormInput countType;

    @ViewById(R.id.form_birds_count)
    FloatNumberFormInput count;
    @ViewById(R.id.form_birds_count_min)
    FloatNumberFormInput countMin;
    @ViewById(R.id.form_birds_count_max)
    FloatNumberFormInput countMax;

    @Pref
    BirdPrefs_ prefs;

    @Override
    public void onResume() {
        super.onResume();
        countUnits.setText(prefs.birdCountUnits().get());
        countType.setText(prefs.birdCountType().get());
    }

    @Override
    public void onPause() {
        super.onPause();
        prefs.birdCountUnits().put(countUnits.getText().toString());
        prefs.birdCountType().put(countType.getText().toString());
    }

    @Override
    EntryType getEntryType() {
        return EntryType.BIRDS;
    }

    @TextChange(R.id.form_birds_count_type)
    void handleCountsLogic() {
        switch (countType.getSelection()) {
            case 0: // Exact count
                count.setEnabled(true);
                countMin.setEnabled(false);
                countMax.setEnabled(false);
                break;
            case 1: // Min count
                count.setEnabled(false);
                countMin.setEnabled(true);
                countMax.setEnabled(false);
                break;
            case 2: // Max count
                count.setEnabled(false);
                countMin.setEnabled(false);
                countMax.setEnabled(true);
                break;
            case 3: // Range count
                count.setEnabled(false);
                countMin.setEnabled(true);
                countMax.setEnabled(true);
                break;
            case 4: // Unspecified
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
