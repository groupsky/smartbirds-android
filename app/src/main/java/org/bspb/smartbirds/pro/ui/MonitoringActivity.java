package org.bspb.smartbirds.pro.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.android.gms.maps.model.LatLng;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.events.CancelMonitoringEvent;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.FinishMonitoringEvent;
import org.bspb.smartbirds.pro.events.LocationChangedEvent;
import org.bspb.smartbirds.pro.events.MapClickedEvent;
import org.bspb.smartbirds.pro.ui.map.GoogleMapProvider;
import org.bspb.smartbirds.pro.ui.map.MapMarker;
import org.bspb.smartbirds.pro.ui.map.MapProvider;
import org.bspb.smartbirds.pro.ui.map.OsmMapProvider;

import java.util.ArrayList;

/**
 * Created by dani on 14-11-4.
 */
@EActivity(R.layout.activity_monitoring)
@OptionsMenu(R.menu.monitoring)
public class MonitoringActivity extends FragmentActivity {

    private static final int REQUEST_NEW_ENTRY = 1001;

    public static final String EXTRA_TYPE = "entryType";

    @InstanceState
    MapProvider.ProviderType mapType = MapProvider.ProviderType.GOOGLE;

    @Bean(GoogleMapProvider.class)
    MapProvider googleMap;
    @Bean(OsmMapProvider.class)
    MapProvider osmMap;
    @InstanceState
    ArrayList<MapMarker> markers = new ArrayList<MapMarker>();

    MapProvider currentMap;

    @OptionsMenuItem(R.id.action_new_entry)
    MenuItem menuNewEntry;
    @Bean
    EEventBus eventBus;
    @InstanceState
    ArrayList<LatLng> points = new ArrayList<LatLng>();
    @InstanceState
    double zoomFactor = 500;
    @InstanceState
    LatLng lastPosition;
    @OptionsMenuItem(R.id.menu_map)
    MenuItem menuMap;
    @OptionsMenuItem(R.id.action_map_google_normal)
    MenuItem menuMapNormal;
    @OptionsMenuItem(R.id.menu_zoom)
    MenuItem menuZoom;
    @Extra(EXTRA_TYPE)
    EntryType entryType;

    @AfterInject
    public void initProviders() {
        googleMap.setFragmentManager(getSupportFragmentManager());
        osmMap.setFragmentManager(getSupportFragmentManager());
    }

    @AfterViews
    void tryToSetUpMap() {
        showCurrentMap();
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentMap.setUpMapIfNeeded();
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventBus.register(this);
        eventBus.register(osmMap);
        eventBus.register(googleMap);
    }

    @Override
    protected void onStop() {
        super.onStop();
        eventBus.unregister(this);
        eventBus.unregister(osmMap);
        eventBus.unregister(googleMap);
    }

    private void showCurrentMap() {
        switch (mapType) {
            case GOOGLE:
                currentMap = googleMap;
                if (menuMap != null) {
                    menuMap.setEnabled(true);
                }
                break;
            case OSM:
                currentMap = osmMap;
                if (menuMap != null) {
                    menuMap.setEnabled(false);
                    menuMap.setTitle(R.string.menu_monitoring_map_normal);
                    menuMapNormal.setChecked(true);
                }
                break;
            default:
                currentMap = googleMap;
                if (menuMap != null) {
                    menuMap.setEnabled(true);
                }
                break;
        }
        currentMap.setPosition(lastPosition);
        currentMap.setZoomFactor(zoomFactor);
        currentMap.setMarkers(markers);
        currentMap.setPath(points);
        currentMap.showMap();
    }

    @OptionsItem(R.id.action_map_google)
    void onGoogleMapProvider() {
        mapType = MapProvider.ProviderType.GOOGLE;
        showCurrentMap();
    }

    @OptionsItem(R.id.action_map_osm)
    void onOSMMapProvider() {
        mapType = MapProvider.ProviderType.OSM;
        showCurrentMap();
    }

    @OptionsItem(R.id.action_new_entry)
    void onNewEntry() {
        LatLng position = null;
        if (currentMap.getMyLocation() != null) {
            startNewEntry(new LatLng(currentMap.getMyLocation().getLatitude(), currentMap.getMyLocation().getLongitude()));
        }
    }

    @OptionsItem(R.id.action_common_form)
    void onCommonForm() {
        EditCommonFormActivity_.intent(this).start();
    }

    @OnActivityResult(REQUEST_NEW_ENTRY)
    void onNewEntry(int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        MapMarker marker = new MapMarker(data.getExtras().getString(NewMonitoringEntryActivity.EXTRA_NAME, getString(R.string.marker_default_title)), data.getDoubleExtra(NewMonitoringEntryActivity.EXTRA_LAT, 0), data.getDoubleExtra(NewMonitoringEntryActivity.EXTRA_LON, 0));
        markers.add(marker);
        currentMap.addMarker(marker);
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

    @OptionsItem(R.id.action_zoom_1km)
    void setZoom1km(MenuItem sender) {
        zoomFactor = 1000;
        currentMap.setZoomFactor(zoomFactor);
        sender.setChecked(true);
        menuZoom.setTitle(sender.getTitle());
        currentMap.updateCamera();
    }

    @OptionsItem(R.id.action_zoom_500m)
    void setZoom500m(MenuItem sender) {
        zoomFactor = 500;
        currentMap.setZoomFactor(zoomFactor);
        sender.setChecked(true);
        menuZoom.setTitle(sender.getTitle());
        currentMap.updateCamera();
    }

    @OptionsItem(R.id.action_zoom_250m)
    void setZoom250m(MenuItem sender) {
        zoomFactor = 250;
        currentMap.setZoomFactor(zoomFactor);
        sender.setChecked(true);
        menuZoom.setTitle(sender.getTitle());
        currentMap.updateCamera();
    }

    @OptionsItem(R.id.action_zoom_100m)
    void setZoom100m(MenuItem sender) {
        zoomFactor = 100;
        currentMap.setZoomFactor(zoomFactor);
        sender.setChecked(true);
        menuZoom.setTitle(sender.getTitle());
        currentMap.updateCamera();
    }

    @OptionsItem(R.id.action_zoom_free)
    void setZoomFree(MenuItem sender) {
        zoomFactor = -1;
        currentMap.setZoomFactor(zoomFactor);
        sender.setChecked(true);
        menuZoom.setTitle(sender.getTitle());
        currentMap.updateCamera();
    }

    @OptionsItem(R.id.action_map_google_normal)
    void setGoogleMapsNormal(MenuItem sender) {
        currentMap.setMapType(MapProvider.MapType.NORMAL);
        sender.setChecked(true);
        menuMap.setTitle(sender.getTitle());
    }

    @OptionsItem(R.id.action_map_google_satellite)
    void setGoogleMapsSatellite(MenuItem sender) {
        currentMap.setMapType(MapProvider.MapType.SATELLITE);
        sender.setChecked(true);
        menuMap.setTitle(sender.getTitle());
    }

    @OptionsItem(R.id.action_map_google_hybrid)
    void setGoogleMapsHybrid(MenuItem sender) {
        currentMap.setMapType(MapProvider.MapType.HYBRID);
        sender.setChecked(true);
        menuMap.setTitle(sender.getTitle());
    }

    @OptionsItem(R.id.action_stay_awake)
    void setStayAwake(MenuItem sender) {
        if (sender.isChecked()) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        sender.setChecked(!sender.isChecked());
    }

    public void onEvent(LocationChangedEvent event) {
        Location location = event.location;
        lastPosition = new LatLng(location.getLatitude(), location.getLongitude());
        currentMap.setPosition(lastPosition);
        currentMap.updateCamera();
        if (menuNewEntry != null) {
            menuNewEntry.setEnabled(true);
        }

        points.add(new LatLng(location.getLatitude(), location.getLongitude()));
        currentMap.updatePath(points);
    }

    public void onEvent(MapClickedEvent event) {
        startNewEntry(event.position);
    }

    protected void startNewEntry(LatLng position) {
        NewMonitoringEntryActivity_.IntentBuilder_ ib = NewMonitoringEntryActivity_.intent(this);
        ib.entryType(entryType);
        if (position != null) {
            ib.lat(position.latitude).lon(position.longitude);
        }
        ib.startForResult(REQUEST_NEW_ENTRY);
    }
}
