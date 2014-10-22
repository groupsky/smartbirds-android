package org.bspb.smartbirds.pro.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.events.CancelMonitoringEvent;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.FinishMonitoringEvent;

@EActivity(R.layout.activity_monitoring)
@OptionsMenu(R.menu.monitoring)
public class MonitoringActivity extends FragmentActivity {

    private static final int REQUEST_NEW_ENTRY = 1001;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @OptionsMenuItem(R.id.action_new_entry)
    MenuItem menuNewEntry;
    @Bean
    EEventBus eventBus;


    @AfterViews
    void tryToSetUpMap() {
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
                menuNewEntry.setEnabled(true);
            }
        });
    }

    @OptionsItem(R.id.action_new_entry)
    void onNewEntry() {
        NewMonitoringEntryActivity_.IntentBuilder_ ib = NewMonitoringEntryActivity_.intent(this);

        Location loc = mMap.getMyLocation();
        if (loc != null) {
            ib.lat(loc.getLatitude()).lon(loc.getLongitude());
        }
        ib.startForResult(REQUEST_NEW_ENTRY);
    }

    @OptionsItem(R.id.action_common_form)
    void onCommonForm() {
        EditCommonFormActivity_.intent(this).start();
    }

    @OnActivityResult(REQUEST_NEW_ENTRY)
    void onNewEntry(int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        mMap.addMarker(new MarkerOptions().position(new LatLng(data.getDoubleExtra(NewMonitoringEntryActivity.EXTRA_LAT, 0), data.getDoubleExtra(NewMonitoringEntryActivity.EXTRA_LON, 0))).title("Отчитане"));
    }

    @OptionsItem(R.id.action_finish)
    void onFinish() {
        eventBus.post(new FinishMonitoringEvent());
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
                .setTitle(R.string.cancel_monitoring)
                .setMessage(R.string.really_cancel_monitoring)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doCancel();
                        finish();
                    }

                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    void doCancel() {
        eventBus.post(new CancelMonitoringEvent());
    }
}
