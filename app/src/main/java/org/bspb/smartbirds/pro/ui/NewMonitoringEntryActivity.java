package org.bspb.smartbirds.pro.ui;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.EntrySubmitted;
import org.bspb.smartbirds.pro.service.DataService_;
import org.bspb.smartbirds.pro.service.TrackingServiceBuilder;

import java.util.Locale;

import static org.bspb.smartbirds.pro.tools.Reporting.logException;

@EActivity(R.layout.activity_form)
public class NewMonitoringEntryActivity extends BaseActivity implements ServiceConnection {

    public static final String EXTRA_LAT = "lat";
    public static final String EXTRA_LON = "lon";
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_TYPE = "entryType";
    private static final String TAG = SmartBirdsApplication.TAG + ".NewMonAct";

    @Extra(EXTRA_LAT)
    double lat;
    @Extra(EXTRA_LON)
    double lon;
    @Extra(EXTRA_TYPE)
    EntryType entryType;

    @Bean
    EEventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        DataService_.intent(this).start();
        try {
            bindService(new TrackingServiceBuilder(this).getIntent(), this, BIND_ABOVE_CLIENT);
        } catch (Throwable t) {
            logException(t);
        }
    }

    @AfterViews
    void createFragment() {
        setTitle(entryType.titleId);
        if (getFragmentManager().findFragmentById(R.id.container) == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, entryType.buildFragment(lat, lon))
                    .commit();
        }
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

    @Override
    protected void onDestroy() {
        try {
            unbindService(this);
        } catch (Throwable t) {
            logException(t);
        }
        super.onDestroy();
    }

    public void onEvent(EntrySubmitted event) {
        Intent intent = new Intent().putExtra(EXTRA_LAT, lat).putExtra(EXTRA_LON, lon);

        if (EntryType.CICONIA.equals(event.entryType)) {
            intent.putExtra(EXTRA_NAME, getString(R.string.entry_type_ciconia));
        } else {
            if (event.data.containsKey(getString(R.string.tag_species_scientific_name))) {
                intent.putExtra(EXTRA_NAME, event.data.get(getString(R.string.tag_species_scientific_name)));
            } else if (event.data.containsKey(getString(R.string.tag_observed_bird))) {
                intent.putExtra(EXTRA_NAME, event.data.get(getString(R.string.tag_observed_bird)));
            }
        }
        setResult(RESULT_OK, intent);
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

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, String.format(Locale.ENGLISH, "service %s connected", name));
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, String.format(Locale.ENGLISH, "service %s disconnected", name));
    }

}
