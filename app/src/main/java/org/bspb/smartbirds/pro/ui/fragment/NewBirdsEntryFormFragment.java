package org.bspb.smartbirds.pro.ui.fragment;

import org.androidannotations.annotations.EFragment;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.enums.EntryType;


@EFragment(R.layout.fragment_monitoring_form_birds)
public class NewBirdsEntryFormFragment extends BaseEntryFragment {

    @Override
    EntryType getEntryType() {
        return EntryType.BIRDS;
    }
}
