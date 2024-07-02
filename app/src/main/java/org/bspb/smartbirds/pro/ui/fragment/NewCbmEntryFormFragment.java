package org.bspb.smartbirds.pro.ui.fragment;

import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.backend.dto.Coordinate;
import org.bspb.smartbirds.pro.backend.dto.Zone;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.prefs.CbmPrefs;
import org.bspb.smartbirds.pro.prefs.CommonPrefs;
import org.bspb.smartbirds.pro.ui.utils.Configuration;
import org.bspb.smartbirds.pro.ui.views.CbmQuickChoiceFormInput;
import org.bspb.smartbirds.pro.ui.views.SingleChoiceFormInput;
import org.bspb.smartbirds.pro.ui.views.SwitchFormInput;
import org.bspb.smartbirds.pro.ui.views.ZoneFormInput;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by dani on 14-11-11.
 */
public class NewCbmEntryFormFragment extends BaseEntryFragment {

    private static final String TAG = SmartBirdsApplication.TAG + ".fCBM";

    ZoneFormInput zoneInput;
    View errorCbmTooFarView;
    SwitchFormInput confidential;
    CbmQuickChoiceFormInput speciesQuickChoice;
    SingleChoiceFormInput speciesInput;

    CbmPrefs prefs;

    CommonPrefs commonPrefs;

    NewEntryPicturesFragment picturesFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_monitoring_form_new_cbm_entry, container, false);
        }
        return view;
    }

    @Override
    protected void onBeforeCreate(@Nullable Bundle savedInstanceState) {
        super.onBeforeCreate(savedInstanceState);
        prefs = new CbmPrefs(requireContext());
        commonPrefs = new CommonPrefs(requireContext());
    }

    @Override
    protected EntryType getEntryType() {
        return EntryType.CBM;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isNewEntry()) {
            zoneInput.setText(prefs.getCbmZone());
            confidential.setChecked(commonPrefs.getConfidentialRecord());
        }

    }

    @Override
    protected HashMap<String, String> serialize(Date entryTime) {
        HashMap<String, String> data = super.serialize(entryTime);
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
        flushDeserialize();
    }

    @Override
    protected void initViews() {
        super.initViews();
        zoneInput = requireView().findViewById(R.id.form_cbm_zone);
        errorCbmTooFarView = requireView().findViewById(R.id.error_cbm_too_far);
        confidential = requireView().findViewById(R.id.form_cbm_confidential);
        speciesQuickChoice = requireView().findViewById(R.id.form_cbm_species_quick);
        speciesInput = requireView().findViewById(R.id.form_cbm_name);

        if (zoneInput != null) {
            zoneInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    onZoneChange();
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }

    protected void flushDeserialize() {
        speciesQuickChoice.setOnItemSelected(nomenclatureItem -> {
            speciesInput.setSelection(nomenclatureItem);
            return null;
        });
    }

    private void onZoneChange() {
        Zone zone = zoneInput.getSelectedItem();
        if (zone != null) {
            Coordinate center = zone.getCenter();
            float[] res = new float[1];
            Location.distanceBetween(lat, lon, center.latitude, center.longitude, res);
            Log.d(TAG, String.format(Locale.ENGLISH, "distance (m): %f", res[0]));

            if (res[0] > Configuration.MAX_DISTANCE_TO_ZONE_METERS) {
                errorCbmTooFarView.setVisibility(View.VISIBLE);
                return;
            }
        }
        errorCbmTooFarView.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        prefs.setCbmZone(zoneInput.getText().toString());
        commonPrefs.setConfidentialRecord(confidential.isChecked());
    }

    public static class Builder implements BaseEntryFragment.Builder {

        @Override
        public Fragment build(double lat, double lon, double geolocationAccuracy) {
            Fragment fragment = new NewCbmEntryFormFragment();
            Bundle args = new Bundle();
            args.putDouble(ARG_LAT, lat);
            args.putDouble(ARG_LON, lon);
            args.putDouble(ARG_GEOLOCATION_ACCURACY, geolocationAccuracy);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public Fragment load(long id, boolean readOnly) {
            Fragment fragment = new NewCbmEntryFormFragment();
            Bundle args = new Bundle();
            args.putLong(ARG_ENTRY_ID, id);
            args.putBoolean(ARG_READ_ONLY, readOnly);
            fragment.setArguments(args);
            return fragment;
        }
    }

}
