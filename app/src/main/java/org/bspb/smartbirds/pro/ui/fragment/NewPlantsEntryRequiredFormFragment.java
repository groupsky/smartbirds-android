package org.bspb.smartbirds.pro.ui.fragment;

import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.ViewById;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.prefs.CommonPrefs;
import org.bspb.smartbirds.pro.ui.views.DecimalNumberFormInput;
import org.bspb.smartbirds.pro.ui.views.SwitchFormInput;

import java.util.HashMap;

/**
 * Created by dani on 26.02.18.
 */

@EFragment(R.layout.fragment_monitoring_form_new_plants_required_entry)
public class NewPlantsEntryRequiredFormFragment extends BaseFormFragment {

    @ViewById(R.id.form_plants_confidential)
    SwitchFormInput confidential;

    @ViewById(R.id.form_plants_elevation)
    DecimalNumberFormInput elevation;

    CommonPrefs commonPrefs;

    EEventBus eventBus = EEventBus.getInstance();

    @FragmentById(value = R.id.pictures_fragment, childFragment = true)
    NewEntryPicturesFragment picturesFragment;


    @Override
    public void onStart() {
        super.onStart();
        eventBus.registerSticky(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        eventBus.unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isNewEntry()) {
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
        commonPrefs = new CommonPrefs(getContext());
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onPause() {
        super.onPause();
        commonPrefs.setConfidentialRecord(confidential.isChecked());
    }

    public void onEvent(Location location) {
        if (location != null && location.hasAltitude() && TextUtils.isEmpty(elevation.getText())) {
            elevation.setText(Integer.toString((int) location.getAltitude()));

            // unregister for the events since we need the altitude only once
            eventBus.unregister(this);
        }
    }

}
