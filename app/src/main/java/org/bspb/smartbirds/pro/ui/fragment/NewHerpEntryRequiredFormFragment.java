package org.bspb.smartbirds.pro.ui.fragment;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.prefs.HerpPrefs_;
import org.bspb.smartbirds.pro.ui.views.DecimalNumberFormInput;
import org.bspb.smartbirds.pro.ui.views.SingleChoiceFormInput;

/**
 * Created by groupsky on 26.01.17.
 */

@EFragment(R.layout.fragment_monitoring_form_new_herp_required_entry)
public class NewHerpEntryRequiredFormFragment extends BaseFormFragment {

    @ViewById(R.id.form_herp_habitat)
    SingleChoiceFormInput habitat;

    @ViewById(R.id.form_herp_count)
    DecimalNumberFormInput count;

    @Pref
    HerpPrefs_ prefs;

    @Override
    public void onResume() {
        super.onResume();
        habitat.setText(prefs.herpHabitat().get());
    }

    @Override
    public void onPause() {
        super.onPause();
        prefs.herpHabitat().put(habitat.getText().toString());
    }

}
