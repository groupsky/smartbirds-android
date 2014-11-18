package org.bspb.smartbirds.pro.ui.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.ExportFailedEvent;
import org.bspb.smartbirds.pro.events.ExportPreparedEvent;
import org.bspb.smartbirds.pro.events.StartMonitoringEvent;
import org.bspb.smartbirds.pro.events.StartingUpload;
import org.bspb.smartbirds.pro.events.UploadCompleted;
import org.bspb.smartbirds.pro.service.ExportService_;
import org.bspb.smartbirds.pro.service.UploadService_;

import java.io.File;
import java.util.Date;

@EFragment(R.layout.fragment_main)
public class MainFragment extends Fragment {

    @Bean
    EEventBus bus;

    private AlertDialog uploadingDialog;
    private AlertDialog exportDialog;

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

    @Click(R.id.btn_export)
    void exportBtnClicked() {
        exportDialog = ProgressDialog.show(getActivity(), getString(R.string.export_dialog_title), getString(R.string.export_dialog_text), true);
        ExportService_.intent(this).prepareForExport().start();
    }

    @UiThread
    public void onEvent(StartingUpload event) {
        uploadingDialog = ProgressDialog.show(getActivity(), getString(R.string.upload_dialog_title), getString(R.string.upload_dialog_text), true);
    }

    @UiThread
    public void onEvent(UploadCompleted event) {
        if (uploadingDialog != null) {
            uploadingDialog.cancel();
        }
        showNotSyncedCount();
    }

    @UiThread
    public void onEvent(ExportPreparedEvent event) {
        if (exportDialog != null) {
            exportDialog.cancel();
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/zip");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.export_subject));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.export_text, new Date().toString()));
        intent.putExtra(Intent.EXTRA_STREAM, event.uri);
        startActivity(Intent.createChooser(intent, getString(R.string.export_app_chooser)));
    }

    @UiThread
    public void onEvent(ExportFailedEvent event) {
        if (exportDialog != null) {
            exportDialog.cancel();
        }
        Toast.makeText(getActivity(), getString(R.string.export_failed_error), Toast.LENGTH_LONG).
                show();
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
