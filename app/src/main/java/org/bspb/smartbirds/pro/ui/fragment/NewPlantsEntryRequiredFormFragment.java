package org.bspb.smartbirds.pro.ui.fragment;

import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.prefs.CommonPrefs;
import org.bspb.smartbirds.pro.ui.views.DecimalNumberFormInput;
import org.bspb.smartbirds.pro.ui.views.SwitchFormInput;

import java.util.HashMap;

/**
 * Created by dani on 26.02.18.
 */

public class NewPlantsEntryRequiredFormFragment extends BaseFormFragment {

    SwitchFormInput confidential;
    DecimalNumberFormInput elevation;

    CommonPrefs commonPrefs;
    EEventBus eventBus = EEventBus.getInstance();

    NewEntryPicturesFragment picturesFragment;

    public static NewPlantsEntryRequiredFormFragment newInstance(boolean isNewEntry, boolean readOnly) {
        NewPlantsEntryRequiredFormFragment fragment = new NewPlantsEntryRequiredFormFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_NEW_ENTRY, isNewEntry);
        args.putBoolean(ARG_READ_ONLY, readOnly);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_monitoring_form_new_plants_required_entry, container, false);
        }
        return view;
    }

    @Override
    protected void onBeforeCreate(@Nullable Bundle savedInstanceState) {
        super.onBeforeCreate(savedInstanceState);
        commonPrefs = new CommonPrefs(requireContext());
    }

    @Override
    protected void initViews() {
        super.initViews();
        confidential = requireView().findViewById(R.id.form_plants_confidential);
        elevation = requireView().findViewById(R.id.form_plants_elevation);
    }

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
