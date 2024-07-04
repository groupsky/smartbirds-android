package org.bspb.smartbirds.pro.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.enums.EntryType;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by dani on 14-11-11.
 */
public class NewCiconiaEntryFormFragment extends BaseEntryFragment {

    NewEntryPicturesFragment picturesFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_monitoring_form_new_ciconia_entry, container, false);
        }
        return view;
    }

    @Override
    protected EntryType getEntryType() {
        return EntryType.CICONIA;
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
    }


    public static class Builder implements BaseEntryFragment.Builder {

        @Override
        public Fragment build(double lat, double lon, double geolocationAccuracy) {
            Fragment fragment = new NewCiconiaEntryFormFragment();
            Bundle args = new Bundle();
            args.putDouble(ARG_LAT, lat);
            args.putDouble(ARG_LON, lon);
            args.putDouble(ARG_GEOLOCATION_ACCURACY, geolocationAccuracy);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public Fragment load(long id, boolean readOnly) {
            Fragment fragment = new NewCiconiaEntryFormFragment();
            Bundle args = new Bundle();
            args.putLong(ARG_ENTRY_ID, id);
            args.putBoolean(ARG_READ_ONLY, readOnly);
            fragment.setArguments(args);
            return fragment;
        }
    }

}
