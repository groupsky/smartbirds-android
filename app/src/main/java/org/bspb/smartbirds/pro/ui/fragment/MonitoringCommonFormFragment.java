package org.bspb.smartbirds.pro.ui.fragment;

import android.app.Fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.GetMonitoringCommonData;
import org.bspb.smartbirds.pro.events.MonitoringCommonData;
import org.bspb.smartbirds.pro.events.SetMonitoringCommonData;
import org.bspb.smartbirds.pro.ui.utils.FormUtils;

import java.util.HashMap;


@EFragment(R.layout.fragment_monitoring_form_common)
public class MonitoringCommonFormFragment extends Fragment {

    private static final String TAG = SmartBirdsApplication.TAG + ".CommonForm";
    @Bean
    EEventBus bus;
    FormUtils.FormModel form;

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
        bus.post(new GetMonitoringCommonData());
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    @AfterViews
    void loadSavedData() {
        form = FormUtils.traverseForm(getView());
    }

    @OptionsItem(R.id.action_submit)
    public void save() {
        HashMap<String, String> data = form.serialize();
        bus.post(new SetMonitoringCommonData(data));
    }

    public void onEventMainThread(MonitoringCommonData event) {
        form.deserialize(event.data);
    }

    public boolean validate() {
        return form.validateFields();
    }
}
