package org.bspb.smartbirds.pro.ui.fragment;

import android.util.Log;
import android.view.View;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.tools.Reporting;

/**
 * Created by groupsky on 26.01.17.
 */

@EFragment(R.layout.fragment_monitoring_form_new_threats_optional_entry)
public class NewThreatsEntryOptionalFormFragment extends BaseFormFragment implements NewThreatsEntryRequiredFormFragment.OnPrimaryTypeChangedListener {

    String primaryType;


    @AfterViews
    protected void initViews() {
        handlePrimaryType();
    }

    @Override
    public void onPrimaryTypeChange(String primaryType) {
        this.primaryType = primaryType;
        handlePrimaryType();
    }

    private void handlePrimaryType() {
        if (getView() == null) {
            return;
        }

        try {
            if (primaryType != null && "poison".equalsIgnoreCase(primaryType)) {
                getView().findViewById(R.id.sample_container_1).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.sample_container_2).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.sample_container_3).setVisibility(View.VISIBLE);
            } else {
                getView().findViewById(R.id.sample_container_1).setVisibility(View.GONE);
                getView().findViewById(R.id.sample_container_2).setVisibility(View.GONE);
                getView().findViewById(R.id.sample_container_3).setVisibility(View.GONE);
            }
        } catch (Throwable t) {
            Reporting.logException(t);
        }
    }
}
