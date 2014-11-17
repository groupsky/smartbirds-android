package org.bspb.smartbirds.pro.ui.fragment;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsPrefs_;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.ui.views.DecimalNumberFormInput;
import org.bspb.smartbirds.pro.ui.views.SingleChoiceFormInput;

/**
 * Created by dani on 14-11-11.
 */
@EFragment(R.layout.fragment_monitoring_form_new_herp_entry)
public class NewHerpEntryFormFragment extends BaseEntryFragment {

    @ViewById(R.id.form_herp_habitat)
    SingleChoiceFormInput habitat;

    @ViewById(R.id.form_herp_count)
    DecimalNumberFormInput count;

    @Pref
    SmartBirdsPrefs_ prefs;

    @Override
    public void onResume() {
        super.onResume();
        habitat.setText(prefs.herpHabitat().get());
        count.setText(prefs.herpCount().get());
    }

    @Override
    public void onPause() {
        super.onPause();
        prefs.herpHabitat().put(habitat.getText().toString());
        prefs.herpCount().put(count.getText().toString());
    }

    @Override
    EntryType getEntryType() {
        return EntryType.HERP;
    }

}
