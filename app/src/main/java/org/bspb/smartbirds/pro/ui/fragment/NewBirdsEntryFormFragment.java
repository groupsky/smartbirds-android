package org.bspb.smartbirds.pro.ui.fragment;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsPrefs_;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.ui.views.SingleChoiceFormInput;


@EFragment(R.layout.fragment_monitoring_form_birds)
public class NewBirdsEntryFormFragment extends BaseEntryFragment {

    @ViewById(R.id.form_birds_count_units)
    SingleChoiceFormInput countUnits;

    @ViewById(R.id.form_birds_count_type)
    SingleChoiceFormInput countType;

    @Pref
    SmartBirdsPrefs_ prefs;

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
}
