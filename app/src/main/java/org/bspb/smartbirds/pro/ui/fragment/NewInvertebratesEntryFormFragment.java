package org.bspb.smartbirds.pro.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.ViewById;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.prefs.CommonPrefs;
import org.bspb.smartbirds.pro.ui.views.SwitchFormInput;

import java.util.Date;
import java.util.HashMap;

@EFragment(R.layout.fragment_monitoring_form_new_invertebrates_entry)

public class NewInvertebratesEntryFormFragment extends BaseEntryFragment {

    @FragmentById(value = R.id.pictures_fragment, childFragment = true)
    NewEntryPicturesFragment picturesFragment;


    @ViewById(R.id.form_invertebrates_confidential)
    SwitchFormInput confidential;

    CommonPrefs commonPrefs;

    @Override
    public void onResume() {
        super.onResume();
        if (isNewEntry()) {
            confidential.setChecked(commonPrefs.getConfidentialRecord());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        commonPrefs.setConfidentialRecord(confidential.isChecked());
    }

    @Override
    protected EntryType getEntryType() {
        return EntryType.INVERTEBRATES;
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
        commonPrefs = new CommonPrefs(getContext());
        super.onViewCreated(view, savedInstanceState);
    }


    public static class Builder implements BaseEntryFragment.Builder {

        @Override
        public Fragment build(double lat, double lon, double geolocationAccuracy) {
            return NewInvertebratesEntryFormFragment_.builder().lat(lat).lon(lon).geolocationAccuracy(geolocationAccuracy).build();
        }

        @Override
        public Fragment load(long id, boolean readOnly) {
            return NewInvertebratesEntryFormFragment_.builder().entryId(id).readOnly(readOnly).build();
        }
    }

}
