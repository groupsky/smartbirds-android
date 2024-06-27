package org.bspb.smartbirds.pro.ui.fragment;

import android.os.Bundle;
import android.view.View;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.ViewById;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.prefs.CommonPrefs;
import org.bspb.smartbirds.pro.prefs.HerptilePrefs;
import org.bspb.smartbirds.pro.ui.views.DecimalNumberFormInput;
import org.bspb.smartbirds.pro.ui.views.SingleChoiceFormInput;
import org.bspb.smartbirds.pro.ui.views.SwitchFormInput;

import java.util.HashMap;

/**
 * Created by dani on 04.01.18.
 */

@EFragment(R.layout.fragment_monitoring_form_new_herptile_required_entry)
public class NewHerptileEntryRequiredFormFragment extends BaseFormFragment {

    @ViewById(R.id.form_herp_habitat)
    SingleChoiceFormInput habitat;

    @ViewById(R.id.form_herp_count)
    DecimalNumberFormInput count;

    @ViewById(R.id.form_herptiles_confidential)
    SwitchFormInput confidential;

    HerptilePrefs prefs;

    CommonPrefs commonPrefs;

    @FragmentById(value = R.id.pictures_fragment, childFragment = true)
    NewEntryPicturesFragment picturesFragment;

    @Override
    public void onResume() {
        super.onResume();
        if (isNewEntry()) {
            habitat.setText(prefs.getHerptileHabitat());
            confidential.setChecked(commonPrefs.getConfidentialRecord());
        }
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
        // In some cases picturesFragment is still null. Try to find it by id
        if (picturesFragment == null) {
            picturesFragment = (NewEntryPicturesFragment) getChildFragmentManager().findFragmentById(R.id.pictures_fragment);
        }
        if (picturesFragment != null) {
            picturesFragment.doDeserialize(monitoringCode, data);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (picturesFragment == null) {
            picturesFragment = (NewEntryPicturesFragment) getChildFragmentManager().findFragmentById(R.id.pictures_fragment);
        }
        prefs = new HerptilePrefs(getContext());
        commonPrefs = new CommonPrefs(getContext());
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onPause() {
        super.onPause();
        prefs.setHerptileHabitat(habitat.getText().toString());
        commonPrefs.setConfidentialRecord(confidential.isChecked());
    }

}
