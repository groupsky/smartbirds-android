package org.bspb.smartbirds.pro.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.android.gms.maps.model.LatLng;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.events.CancelMonitoringEvent;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.FinishMonitoringEvent;
import org.bspb.smartbirds.pro.events.LocationChangedEvent;
import org.bspb.smartbirds.pro.events.MapClickedEvent;
import org.bspb.smartbirds.pro.events.MapLongClickedEvent;
import org.bspb.smartbirds.pro.events.UndoLastEntry;
import org.bspb.smartbirds.pro.prefs.MonitoringPrefs_;
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

    private static final String PREFS_MARKERS = "markers";
    private static final String PREFS_POINTS = "points";

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
    @OptionsMenuItem(R.id.action_undo_last_entry)
    MenuItem menuUndoEntry;
    @Bean
    EEventBus eventBus;
    @InstanceState
    ArrayList<LatLng> points = new ArrayList<LatLng>();
    @InstanceState
    int zoomFactor = 500;
    @InstanceState
    LatLng lastPosition;
    @OptionsMenuItem(R.id.menu_map)
    MenuItem menuMap;
    @OptionsMenuItem(R.id.action_map_google_normal)
    MenuItem menuMapNormal;
    @OptionsMenuItem(R.id.menu_zoom)
    MenuItem menuZoom;
    @OptionsMenuItem(R.id.action_form_type_birds)
    MenuItem menuFormTypeBirds;
    @OptionsMenuItem(R.id.action_form_type_cbm)
    MenuItem menuFormTypeCbm;
    @OptionsMenuItem(R.id.action_form_type_ciconia)
    MenuItem menuFormTypeCiconia;
    @OptionsMenuItem(R.id.action_form_type_herp)
    MenuItem menuFormTypeHerp;
    @InstanceState
    int lastEntryTypePosition = -1;

    @Pref
    MonitoringPrefs_ monitoringPrefs;
    private boolean canceled = false;

    @AfterInject
    public void initProviders() {
        googleMap.setFragmentManager(getSupportFragmentManager());
        osmMap.setFragmentManager(getSupportFragmentManager());
    }

    @AfterViews
    void init() {
        restoreState();
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (zoomFactor) {
            case 1000:
                menuZoom.setTitle(R.string.menu_monitoring_zoom_1000);
                break;
            case 500:
                menuZoom.setTitle(R.string.menu_monitoring_zoom_500);
                break;
            case 250:
                menuZoom.setTitle(R.string.menu_monitoring_zoom_250);
                break;
            case 100:
                menuZoom.setTitle(R.string.menu_monitoring_zoom_100);
                break;
            case -1:
                menuZoom.setTitle(R.string.menu_monitoring_zoom_free);
                break;
        }
        switch (lastEntryTypePosition) {
            case 0: menuFormTypeBirds.setChecked(true); break;
            case 1: menuFormTypeCbm.setChecked(true); break;
            case 2: menuFormTypeCiconia.setChecked(true); break;
            case 3: menuFormTypeHerp.setChecked(true); break;
        }
        return super.onPrepareOptionsMenu(menu);
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
            startNewEntryWithoutAsking(new LatLng(currentMap.getMyLocation().getLatitude(), currentMap.getMyLocation().getLongitude()));
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
        menuUndoEntry.setEnabled(true);
    }

    @OptionsItem(R.id.action_finish)
    void onFinish() {
        clearPrefs();
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
        clearPrefs();
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

    @OptionsItem(R.id.action_form_type_birds)
    void setFormTypeBirds(MenuItem sender) {
        lastEntryTypePosition = EntryType.BIRDS.ordinal();
    }

    @OptionsItem(R.id.action_form_type_cbm)
    void setFormTypeCbm(MenuItem sender) {
        lastEntryTypePosition = EntryType.CBM.ordinal();
    }

    @OptionsItem(R.id.action_form_type_ciconia)
    void setFormTypeCiconia(MenuItem sender) {
        lastEntryTypePosition = EntryType.CICONIA.ordinal();
    }

    @OptionsItem(R.id.action_form_type_herp)
    void setFormTypeHerp(MenuItem sender) {
        lastEntryTypePosition = EntryType.HERP.ordinal();
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
        startNewEntryWithoutAsking(event.position);
    }

    public void onEvent(MapLongClickedEvent event) {
        startNewEntryAsking(event.position);
    }

    void startNewEntryWithoutAsking(final LatLng position) {
        if (lastEntryTypePosition == -1) {
            startNewEntryAsking(position);
            return;
        }
        EntryType type;
        switch (lastEntryTypePosition) {
            case 0:
                type = EntryType.BIRDS;
                break;
            case 1:
                type = EntryType.CBM;
                break;
            case 2:
                type = EntryType.CICONIA;
                break;
            case 3:
                type = EntryType.HERP;
                break;
            default:
                return;
        }
        NewMonitoringEntryActivity_.IntentBuilder_ ib = NewMonitoringEntryActivity_.intent(MonitoringActivity.this);
        ib.entryType(type);
        if (position != null) {
            ib.lat(position.latitude).lon(position.longitude);
        }
        ib.startForResult(REQUEST_NEW_ENTRY);
    }

    protected void startNewEntryAsking(final LatLng position) {
        final String[] types = getResources().getStringArray(R.array.enty_types);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.menu_monitoring_new_entry)
                .setSingleChoiceItems(types, lastEntryTypePosition, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EntryType type = null;
                        lastEntryTypePosition = i;
                        startNewEntryWithoutAsking(position);
                        dialogInterface.cancel();
                    }
                })
                .setCancelable(true);

        builder.create().show();

    }

    @OptionsItem(R.id.action_undo_last_entry)
    void undoLastEntry() {
        menuUndoEntry.setEnabled(false);
        eventBus.post(new UndoLastEntry());
        markers.remove(markers.size() - 1);
        currentMap.removeLastMarker();
    }

    @Override
    protected void onDestroy() {
        persistState();
        super.onDestroy();
    }

    private void persistState() {
        if (!canceled) {

            MonitoringPrefs_.MonitoringPrefsEditor_ editor = monitoringPrefs.edit();
            editor.mapType().put(mapType.toString());
            if (markers != null) {
                editor.markersCount().put(markers.size());
            }
            if (points != null) {
                editor.pointsCount().put(points.size());
            }
            if (lastPosition != null) {
                editor.lastPositionLat().put((float) lastPosition.latitude)
                        .lastPositionLon().put((float) lastPosition.longitude);
            }
            editor.zoomFactor().put(zoomFactor);
            editor.lastEntryTypePosition().put(lastEntryTypePosition);

            editor.apply();

            persistMarkers();
            persistPoints();
        }
    }

    private void persistMarkers() {
        if (markers == null) {
            return;
        }
        SharedPreferences markersPrefs = getSharedPreferences(PREFS_MARKERS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = markersPrefs.edit();
        editor.clear();
        for (int i = 0; i < markers.size(); i++) {
            editor.putString("title_" + i, markers.get(i).getTitle());
            editor.putFloat("lat_" + i, (float) markers.get(i).getLatitude());
            editor.putFloat("lon_" + i, (float) markers.get(i).getLongitude());
        }
        editor.commit();
    }

    private void persistPoints() {
        if (points == null) {
            return;
        }
        SharedPreferences pointsPrefs = getSharedPreferences(PREFS_POINTS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pointsPrefs.edit();
        editor.clear();
        for (int i = 0; i < points.size(); i++) {
            editor.putFloat("lat_" + i, (float) points.get(i).latitude);
            editor.putFloat("lon_" + i, (float) points.get(i).longitude);
        }
        editor.commit();
    }

    private void restoreState() {
        if (monitoringPrefs.mapType().get() != null && !monitoringPrefs.mapType().get().equals("")) {
            mapType = MapProvider.ProviderType.valueOf(monitoringPrefs.mapType().get());
            lastPosition = new LatLng(monitoringPrefs.lastPositionLat().get(), monitoringPrefs.lastPositionLon().get());
            zoomFactor = monitoringPrefs.zoomFactor().get();
            lastEntryTypePosition = monitoringPrefs.lastEntryTypePosition().get();
            restoreMarkers();
            restorePoints();
        }
    }

    private void restorePoints() {
        int pointsCount = monitoringPrefs.pointsCount().get();
        if (pointsCount <= 0) {
            return;
        }

        SharedPreferences pointsPrefs = getSharedPreferences(PREFS_POINTS, Context.MODE_PRIVATE);

        points = new ArrayList<LatLng>();
        for (int i = 0; i < pointsCount; i++) {
            final double lat = pointsPrefs.getFloat("lat_" + i, 0);
            final double lon = pointsPrefs.getFloat("lon_" + i, 0);
            points.add(new LatLng(lat, lon));
        }
    }

    private void restoreMarkers() {
        int markersCount = monitoringPrefs.markersCount().get();
        if (markersCount <= 0) {
            return;
        }

        SharedPreferences markersPrefs = getSharedPreferences(PREFS_MARKERS, Context.MODE_PRIVATE);

        markers = new ArrayList<MapMarker>();
        for (int i = 0; i < markersCount; i++) {
            final String title = markersPrefs.getString("title_" + i, "");
            final double lat = markersPrefs.getFloat("lat_" + i, 0);
            final double lon = markersPrefs.getFloat("lon_" + i, 0);
            markers.add(new MapMarker(title, lat, lon));
        }
    }

    private void clearPrefs() {
        canceled = true;
        monitoringPrefs.edit().clear().apply();
        SharedPreferences markersPrefs = getSharedPreferences(PREFS_MARKERS, Context.MODE_PRIVATE);
        SharedPreferences pointsPrefs = getSharedPreferences(PREFS_POINTS, Context.MODE_PRIVATE);
        markersPrefs.edit().clear().commit();
        pointsPrefs.edit().clear().commit();
    }
}
