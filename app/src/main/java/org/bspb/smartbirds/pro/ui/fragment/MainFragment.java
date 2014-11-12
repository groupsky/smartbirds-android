package org.bspb.smartbirds.pro.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.MonitoringStartedEvent;
import org.bspb.smartbirds.pro.events.StartMonitoringEvent;
import org.bspb.smartbirds.pro.events.StartingUpload;
import org.bspb.smartbirds.pro.events.UploadCompleted;
import org.bspb.smartbirds.pro.service.UploadService_;
import org.bspb.smartbirds.pro.ui.StartMonitoringActivity_;

@EFragment(R.layout.fragment_main)
public class MainFragment extends Fragment {

    @Bean
    EEventBus bus;

    @ViewById(R.id.progress)
    View progress;

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    @Click(R.id.btn_start_birds)
    void startBirdsBtnClicked() {
        bus.postSticky(new StartMonitoringEvent(EntryType.BIRDS));
    }

    @Click(R.id.btn_start_herp)
    void startHerpBtnClicked() {
        bus.postSticky(new StartMonitoringEvent(EntryType.HERP));
    }

    @Click(R.id.btn_upload)
    void uploadBtnClicked() {
        UploadService_.intent(this).uploadAll().start();
    }

    public void onEvent(StartingUpload event) {
        progress.setVisibility(View.VISIBLE);
    }

    public void onEvent(UploadCompleted event) {
        progress.setVisibility(View.GONE);
    }
}
