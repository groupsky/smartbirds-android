package org.bspb.smartbirds.pro.ui.fragment;

import android.app.Fragment;
import android.view.View;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.GetMonitoringCommonData;
import org.bspb.smartbirds.pro.events.MonitoringCommonData;
import org.bspb.smartbirds.pro.events.SetMonitoringCommonData;
import org.bspb.smartbirds.pro.prefs.CommonPrefs;
import org.bspb.smartbirds.pro.prefs.CommonPrefs_;
import org.bspb.smartbirds.pro.ui.utils.FormUtils;
import org.bspb.smartbirds.pro.ui.views.DateFormInput;
import org.bspb.smartbirds.pro.ui.views.MultipleTextFormInput;
import org.bspb.smartbirds.pro.ui.views.TimeFormInput;

import java.util.Calendar;
import java.util.HashMap;


@EFragment(R.layout.fragment_monitoring_form_common)
public class MonitoringCommonFormFragment extends Fragment {

    private static final String TAG = SmartBirdsApplication.TAG + ".CommonForm";
    @Bean
    EEventBus bus;
    FormUtils.FormModel form;
    @ViewById(R.id.form_common_start_date)
    DateFormInput startDateView;
    @ViewById(R.id.form_common_start_time)
    TimeFormInput startTimeView;
    @ViewById(R.id.form_common_end_date)
    DateFormInput endDateView;
    @FragmentArg("new_monitoring")
    boolean newMonitoring;
    @Pref
    CommonPrefs_ prefs;
    @ViewById(R.id.observers)
    View observers;

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
        if (!newMonitoring)
            bus.post(new GetMonitoringCommonData());
    }

    @Override
    public void onResume() {
        super.onResume();
        FormUtils.setViewValue(observers, prefs.commonObservers().get());
    }

    @Override
    public void onPause() {
        super.onPause();
        prefs.commonObservers().put(FormUtils.getViewValue(observers));
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    @AfterViews
    void loadSavedData() {
        form = FormUtils.traverseForm(getView());
        if (newMonitoring) {
            startDateView.setValue(Calendar.getInstance());
            startTimeView.setValue(Calendar.getInstance());
            endDateView.setValue(Calendar.getInstance());
        }
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
