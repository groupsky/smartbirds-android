package org.bspb.smartbirds.pro.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.RootContext;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.MonitoringStartedEvent;
import org.bspb.smartbirds.pro.events.StartMonitoringEvent;
import org.bspb.smartbirds.pro.ui.MonitoringActivity;
import org.bspb.smartbirds.pro.ui.MonitoringActivity_;


@EFragment(R.layout.fragment_monitoring_form_common)
@OptionsMenu(R.menu.monitoring_common_form)
public class MonitoringCommonFormFragment extends Fragment {

    @Bean
    EEventBus bus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @OptionsItem(R.id.action_submit)
    void onSubmit() {
        MonitoringActivity_.intent(this).start();
        getActivity().finish();
    }

//    @AfterInject
//    void registerBus() {
//        bus.register(this);
//    }
//
//    public void onEvent(MonitoringStartedEvent event) {
//        bus.removeStickyEvent(StartMonitoringEvent.class);
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        bus.unregister(this);
//    }

}
