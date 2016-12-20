package org.bspb.smartbirds.pro.ui.fragment;

import android.support.v4.app.Fragment;

import org.androidannotations.annotations.EFragment;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.enums.EntryType;

/**
 * Created by dani on 14-11-11.
 */
@EFragment(R.layout.fragment_monitoring_form_new_ciconia_entry)
public class NewCiconiaEntryFormFragment extends BaseEntryFragment {

    @Override
    protected EntryType getEntryType() {
        return EntryType.CICONIA;
    }

    public static class Builder implements BaseEntryFragment.Builder {

        @Override
        public Fragment build(double lat, double lon) {
            return NewCiconiaEntryFormFragment_.builder().lat(lat).lon(lon).build();
        }
    }

}
