package org.bspb.smartbirds.pro.ui.fragment;

import android.app.Fragment;
import android.os.Build;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.PreferenceScreen;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.prefs.CommonPrefs_;
import org.bspb.smartbirds.pro.ui.views.SwitchFormInput;

import java.util.Date;
import java.util.HashMap;

@EFragment(R.layout.fragment_monitoring_form_new_invertebrates_entry)

public class NewInvertebratesEntryFormFragment extends BaseEntryFragment {

    @FragmentById(R.id.pictures_fragment)
    NewEntryPicturesFragment picturesFragment;
    private HashMap<String, String> pendingDeserialize;


    @ViewById(R.id.form_invertebrates_confidential)
    SwitchFormInput confidential;

    @Pref
    CommonPrefs_ commonPrefs;

    @Override
    public void onResume() {
        super.onResume();
        if (isNewEntry()) {
            confidential.setChecked(commonPrefs.confidentialRecord().get());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        commonPrefs.confidentialRecord().put(confidential.isChecked());
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
        if (picturesFragment == null) {
            pendingDeserialize = data;
        } else {
            picturesFragment.doDeserialize(monitoringCode, data);
        }
    }

    @AfterViews
    protected void flushDeserialize() {
        if (picturesFragment == null) {
            picturesFragment = (NewEntryPicturesFragment) getChildFragmentManager().findFragmentById(R.id.pictures_fragment);
        }
        if (pendingDeserialize != null) {
            picturesFragment.doDeserialize(monitoringCode, pendingDeserialize);
            pendingDeserialize = null;
        }
    }

    public static class Builder implements BaseEntryFragment.Builder {

        @Override
        public Fragment build(double lat, double lon) {
            return NewInvertebratesEntryFormFragment_.builder().lat(lat).lon(lon).build();
        }

        @Override
        public Fragment load(long id) {
            return NewInvertebratesEntryFormFragment_.builder().entryId(id).build();
        }
    }

}
