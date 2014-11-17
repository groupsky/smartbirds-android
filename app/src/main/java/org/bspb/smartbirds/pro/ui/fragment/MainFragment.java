package org.bspb.smartbirds.pro.ui.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.widget.TextView;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.StartMonitoringEvent;
import org.bspb.smartbirds.pro.events.StartingUpload;
import org.bspb.smartbirds.pro.events.UploadCompleted;
import org.bspb.smartbirds.pro.service.UploadService_;

import java.io.File;

@EFragment(R.layout.fragment_main)
public class MainFragment extends Fragment {

    @Bean
    EEventBus bus;

    private AlertDialog dialog;

    @ViewById(R.id.not_synced_count)
    TextView notSyncedCountView;

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

    @Override
    public void onResume() {
        super.onResume();
        showNotSyncedCount();
    }

    @Click(R.id.btn_start_birds)
    void startBirdsClicked() {
        bus.postSticky(new StartMonitoringEvent());
    }

    @Click(R.id.btn_upload)
    void uploadBtnClicked() {
        UploadService_.intent(this).uploadAll().start();
    }

    @UiThread
    public void onEvent(StartingUpload event) {
        dialog = ProgressDialog.show(getActivity(), "Upload", "Uploading items...", true);
    }

    @UiThread
    public void onEvent(UploadCompleted event) {
        if (dialog != null) {
            dialog.cancel();
        }
        showNotSyncedCount();
    }

    protected void showNotSyncedCount() {
        File baseDir = getActivity().getExternalFilesDir(null);
        int notSyncedCount = 0;
        for (String monitoring : baseDir.list()) {
            if (monitoring.endsWith("-up")) {
                notSyncedCount++;
            }
        }
        notSyncedCountView.setText(getString(R.string.not_synced_count, notSyncedCount));
    }
}
