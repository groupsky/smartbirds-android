package org.bspb.smartbirds.pro.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.EntrySubmitted;
import org.bspb.smartbirds.pro.ui.fragment.MainFragment_;
import org.bspb.smartbirds.pro.ui.fragment.NewBirdsEntryFormFragment;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.ui.fragment.NewBirdsEntryFormFragment_;

@EActivity(R.layout.activity_form)
@OptionsMenu(R.menu.form_entry)
public class NewMonitoringEntryActivity extends Activity {

    public static final String EXTRA_LAT = "lat";
    public static final String EXTRA_LON = "lon";

    @Extra(EXTRA_LAT)
    double lat;
    @Extra(EXTRA_LON)
    double lon;

    @Bean
    EEventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
    }

    @AfterViews
    void createFragment() {
        if (getFragmentManager().findFragmentById(R.id.container) == null)
            getFragmentManager().beginTransaction()
                    .add(R.id.container, NewBirdsEntryFormFragment_.builder().lat(lat).lon(lon).build())
                    .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventBus.register(this);
    }

    @Override
    protected void onStop() {
        eventBus.unregister(this);
        super.onStop();
    }

    public void onEvent(EntrySubmitted event) {
        setResult(RESULT_OK, new Intent().putExtra(EXTRA_LAT, lat).putExtra(EXTRA_LON, lon));
        finish();
    }

    @OptionsItem(android.R.id.home)
    void onUp() {
        confirmCancel();
    }

    @Override
    public void onBackPressed() {
        confirmCancel();
    }

    private void confirmCancel() {
        //Ask the user if they want to quit
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.cancel_entry)
                .setMessage(R.string.really_cancel_entry)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

}
