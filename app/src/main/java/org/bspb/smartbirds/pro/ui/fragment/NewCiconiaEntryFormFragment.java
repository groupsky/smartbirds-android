package org.bspb.smartbirds.pro.ui.fragment;

import androidx.fragment.app.Fragment;

import android.os.Build;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentById;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.enums.EntryType;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by dani on 14-11-11.
 */
@EFragment(R.layout.fragment_monitoring_form_new_ciconia_entry)

public class NewCiconiaEntryFormFragment extends BaseEntryFragment {

    @FragmentById(R.id.pictures_fragment)
    NewEntryPicturesFragment picturesFragment;

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
        if (picturesFragment != null) {
            picturesFragment.doDeserialize(monitoringCode, data);
        }
    }

    @AfterViews
    protected void flushDeserialize() {
        if (picturesFragment == null) {
            picturesFragment = (NewEntryPicturesFragment) getChildFragmentManager().findFragmentById(R.id.pictures_fragment);
        }
    }

    public static class Builder implements BaseEntryFragment.Builder {

        @Override
        public Fragment build(double lat, double lon) {
            return NewCiconiaEntryFormFragment_.builder().lat(lat).lon(lon).build();
        }

        @Override
        public Fragment load(long id) {
            return NewCiconiaEntryFormFragment_.builder().entryId(id).build();
        }
    }

}
