package org.bspb.smartbirds.pro.ui.fragment;

import org.androidannotations.annotations.EFragment;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.enums.EntryType;

/**
 * Created by dani on 14-11-11.
 */
@EFragment(R.layout.fragment_monitoring_form_new_herp_entry)
public class NewHerpEntryFormFragment extends BaseEntryFragment {

    @Override
    EntryType getEntryType() {
        return EntryType.HERP;
    }

}
