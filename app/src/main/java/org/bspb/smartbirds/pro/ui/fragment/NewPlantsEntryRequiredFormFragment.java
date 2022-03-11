package org.bspb.smartbirds.pro.ui.fragment;

import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.LocationChangedEvent;
import org.bspb.smartbirds.pro.prefs.CommonPrefs_;
import org.bspb.smartbirds.pro.ui.views.DecimalNumberFormInput;
import org.bspb.smartbirds.pro.ui.views.FloatNumberFormInput;
import org.bspb.smartbirds.pro.ui.views.SwitchFormInput;

import java.util.HashMap;

import de.greenrobot.event.EventBus;

/**
 * Created by dani on 26.02.18.
 */

@EFragment(R.layout.fragment_monitoring_form_new_plants_required_entry)
public class NewPlantsEntryRequiredFormFragment extends BaseFormFragment {

    @ViewById(R.id.form_plants_confidential)
    SwitchFormInput confidential;

    @ViewById(R.id.form_plants_elevation)
    DecimalNumberFormInput elevation;

    @Pref
    CommonPrefs_ commonPrefs;

    @Bean
    EEventBus eventBus;

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
            confidential.setChecked(commonPrefs.confidentialRecord().get());
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
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onPause() {
        super.onPause();
        commonPrefs.confidentialRecord().put(confidential.isChecked());
    }

    public void onEvent(Location location) {
        if (location != null && location.hasAltitude() && TextUtils.isEmpty(elevation.getText())) {
            elevation.setText(Integer.toString((int) location.getAltitude()));

            // unregister for the events since we need the altitude only once
            eventBus.unregister(this);
        }
    }

}
