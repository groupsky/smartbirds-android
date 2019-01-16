package org.bspb.smartbirds.pro.ui;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.model.LatLng;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.bspb.smartbirds.pro.BuildConfig;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.backend.dto.Zone;
import org.bspb.smartbirds.pro.beans.EntriesToMapMarkersConverter;
import org.bspb.smartbirds.pro.beans.MonitoringModelEntries;
import org.bspb.smartbirds.pro.beans.ZonesModelEntries;
import org.bspb.smartbirds.pro.collections.IterableConverter;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.events.ActiveMonitoringEvent;
import org.bspb.smartbirds.pro.events.CancelMonitoringEvent;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.FinishMonitoringEvent;
import org.bspb.smartbirds.pro.events.LocationChangedEvent;
import org.bspb.smartbirds.pro.events.MapClickedEvent;
import org.bspb.smartbirds.pro.events.MapLongClickedEvent;
import org.bspb.smartbirds.pro.events.MonitoringResumedEvent;
import org.bspb.smartbirds.pro.events.PauseMonitoringEvent;
import org.bspb.smartbirds.pro.events.QueryActiveMonitoringEvent;
import org.bspb.smartbirds.pro.events.ResumeMonitoringEvent;
import org.bspb.smartbirds.pro.events.UndoLastEntry;
import org.bspb.smartbirds.pro.prefs.MonitoringPrefs_;
import org.bspb.smartbirds.pro.prefs.SmartBirdsPrefs_;
import org.bspb.smartbirds.pro.service.DataService_;
import org.bspb.smartbirds.pro.service.TrackingServiceBuilder;
import org.bspb.smartbirds.pro.ui.fragment.MonitoringEntryListFragment;
import org.bspb.smartbirds.pro.ui.fragment.MonitoringEntryListFragment_;
import org.bspb.smartbirds.pro.ui.map.GoogleMapProvider;
import org.bspb.smartbirds.pro.ui.map.MapMarker;
import org.bspb.smartbirds.pro.ui.map.MapProvider;
import org.bspb.smartbirds.pro.ui.map.OsmMapProvider;

import java.util.ArrayList;
import java.util.Locale;

import static android.text.TextUtils.isEmpty;
import static org.bspb.smartbirds.pro.tools.Reporting.logException;
import static org.bspb.smartbirds.pro.ui.utils.Constants.VIEWTYPE_COMBINED;
import static org.bspb.smartbirds.pro.ui.utils.Constants.VIEWTYPE_LIST;
import static org.bspb.smartbirds.pro.ui.utils.Constants.VIEWTYPE_MAP;

/**
 * Created by dani on 14-11-4.
 */
@EActivity(R.layout.activity_monitoring)
@OptionsMenu({R.menu.monitoring, R.menu.debug_menu})
public class MonitoringActivity extends BaseActivity implements ServiceConnection, MonitoringEntryListFragment.Listener, MonitoringModelEntries.Listener, ZonesModelEntries.Listener, MapProvider.MarkerClickListener {

    private static final String TAG = SmartBirdsApplication.TAG + ".MonitoringActivity";

    private static final int REQUEST_NEW_ENTRY = 1001;

    private static final int REQUEST_FINISH_MONITORING = 1002;

    private static final String PREFS_POINTS = "points";

    @InstanceState
    @NonNull
    MapProvider.ProviderType providerType = MapProvider.ProviderType.GOOGLE;
    @InstanceState
    @NonNull
    MapProvider.MapType mapType = MapProvider.MapType.NORMAL;

    @Bean(GoogleMapProvider.class)
    MapProvider googleMap;
    @Bean(OsmMapProvider.class)
    MapProvider osmMap;

    MapProvider currentMap;

    @OptionsMenuItem(R.id.action_new_entry)
    MenuItem menuNewEntry;
    @OptionsMenuItem(R.id.action_undo_last_entry)
    MenuItem menuUndoEntry;
    @Bean
    EEventBus eventBus;

    @InstanceState
    @NonNull
    ArrayList<LatLng> points = new ArrayList<LatLng>();
    @InstanceState
    int zoomFactor = 500;
    @InstanceState
    LatLng lastPosition;
    @OptionsMenuItem(R.id.menu_map)
    MenuItem menuMap;
    @OptionsMenuItem(R.id.action_map_google_normal)
    MenuItem menuMapNormal;
    @OptionsMenuItem(R.id.action_map_google_hybrid)
    MenuItem menuMapHybrid;
    @OptionsMenuItem(R.id.action_map_google_satellite)
    MenuItem menuMapSatellite;
    @OptionsMenuItem(R.id.menu_zoom)
    MenuItem menuZoom;
    @OptionsMenuItem(R.id.action_zoom_free)
    MenuItem menuZoomFree;
    @OptionsMenuItem(R.id.action_zoom_1km)
    MenuItem menuZoom1km;
    @OptionsMenuItem(R.id.action_zoom_500m)
    MenuItem menuZoom500m;
    @OptionsMenuItem(R.id.action_zoom_250m)
    MenuItem menuZoom250m;
    @OptionsMenuItem(R.id.action_zoom_100m)
    MenuItem menuZoom100m;

    @OptionsMenuItem(R.id.menu_map_provider)
    MenuItem menuMapProvider;
    @OptionsMenuItem(R.id.action_map_google)
    MenuItem menuMapGoogle;
    @OptionsMenuItem(R.id.action_map_osm)
    MenuItem menuMapOSM;
    @OptionsMenuItem(R.id.action_crash)
    MenuItem menuCrash;
    @OptionsMenuItem(R.id.action_stay_awake)
    MenuItem menuStayAwake;
    @OptionsMenuItem(R.id.action_show_zone_background)
    MenuItem menuShowZoneBackground;
    @OptionsMenuItem(R.id.view_type_map)
    MenuItem menuViewTypeMap;
    @OptionsMenuItem(R.id.view_type_list)
    MenuItem menuViewTypeList;
    @OptionsMenuItem(R.id.view_type_combined)
    MenuItem menuViewTypeCombined;
    @InstanceState
    @Nullable
    EntryType entryType = null;

    @Pref
    MonitoringPrefs_ monitoringPrefs;
    @Pref
    SmartBirdsPrefs_ prefs;
    private boolean canceled = false;
    @InstanceState
    boolean stayAwake;
    @InstanceState
    boolean showZoneBackground;

    @FragmentById(R.id.list_container)
    MonitoringEntryListFragment listFragment;

    @InstanceState
    String monitoringCode;

    @ViewById(R.id.map_container)
    View mapContainer;
    @ViewById(R.id.list_container)
    View listContainer;

    @Bean
    MonitoringModelEntries entries;
    @Bean
    EntriesToMapMarkersConverter mapMarkerConverter;
    @Bean
    ZonesModelEntries zones;

    @AfterInject
    protected void initProviders() {
        googleMap.setFragmentManager(getSupportFragmentManager());
        osmMap.setFragmentManager(getSupportFragmentManager());
    }

    @AfterInject
    protected void fetchMonitoringCode() {
        eventBus.postSticky(new QueryActiveMonitoringEvent());
    }

    @AfterInject
    protected void setupEntries() {
        entries.setListener(this);
        if (!isEmpty(monitoringCode)) entries.setMonitoringCode(monitoringCode);
        zones.setListener(this);
    }

    @AfterViews
    void init() {
        restoreState();
        showCurrentMap();
        setupList();
        updateViewType();
    }

    private void setupList() {
        if (isEmpty(monitoringCode)) return;
        if (listFragment == null) {
            listFragment = MonitoringEntryListFragment_.builder().setMonitoringCode(monitoringCode).build();
            getFragmentManager().beginTransaction().replace(R.id.list_container, listFragment).commit();
        } else {
            listFragment.setMonitoringCode(monitoringCode);
        }
        if (entries != null) entries.setMonitoringCode(monitoringCode);
    }

    private void updateViewType() {
        int viewType = prefs.viewType().get();
        mapContainer.setVisibility((viewType & VIEWTYPE_MAP) != 0 ? View.VISIBLE : View.GONE);
        listContainer.setVisibility((viewType & VIEWTYPE_LIST) != 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataService_.intent(this).start();
        try {
            bindService(new TrackingServiceBuilder(this).getIntent(), this, BIND_ABOVE_CLIENT);
        } catch (Throwable t) {
            logException(t);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentMap.setUpMapIfNeeded();
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventBus.registerSticky(this);
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
    protected void onDestroy() {
        try {
            unbindService(this);
        } catch (Throwable t) {
            logException(t);
        }
        super.onDestroy();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuCrash.setVisible(BuildConfig.DEBUG);
        switch (zoomFactor) {
            case 1000:
                menuZoom.setTitle(R.string.menu_monitoring_zoom_1000);
                menuZoom1km.setChecked(true);
                break;
            case 500:
                menuZoom.setTitle(R.string.menu_monitoring_zoom_500);
                menuZoom500m.setChecked(true);
                break;
            case 250:
                menuZoom.setTitle(R.string.menu_monitoring_zoom_250);
                menuZoom250m.setChecked(true);
                break;
            case 100:
                menuZoom.setTitle(R.string.menu_monitoring_zoom_100);
                menuZoom100m.setChecked(true);
                break;
            case -1:
                menuZoom.setTitle(R.string.menu_monitoring_zoom_free);
                menuZoomFree.setChecked(true);
                break;
        }
        switch (providerType) {
            case GOOGLE:
                menuMapGoogle.setChecked(true);
                menuMapProvider.setTitle(menuMapGoogle.getTitle());
                menuMap.setEnabled(true);
                switch (mapType) {
                    case HYBRID:
                        menuMapHybrid.setChecked(true);
                        menuMap.setTitle(menuMapHybrid.getTitle());
                        break;
                    case SATELLITE:
                        menuMapSatellite.setChecked(true);
                        menuMap.setTitle(menuMapSatellite.getTitle());
                        break;
                    case NORMAL:
                        menuMapNormal.setChecked(true);
                        menuMap.setTitle(menuMapNormal.getTitle());
                        break;
                    default:
                        throw new IllegalStateException("Unhandled map type: " + mapType);
                }
                break;
            case OSM:
                menuMapOSM.setChecked(true);
                menuMapProvider.setTitle(menuMapOSM.getTitle());
                menuMap.setEnabled(false);
                menuMap.setTitle(menuMapNormal.getTitle());
                break;
            default:
                throw new IllegalStateException("Unhandled provider type: " + providerType);
        }
        int viewType = prefs.viewType().get();
        switch (viewType) {
            case VIEWTYPE_MAP:
                menuViewTypeMap.setChecked(true);
                break;
            case VIEWTYPE_LIST:
                menuViewTypeList.setChecked(true);
                break;
            case VIEWTYPE_COMBINED:
                menuViewTypeCombined.setChecked(true);
                break;
        }

        updateCheckedEntryType(menu);
        menuStayAwake.setChecked(stayAwake);
        menuShowZoneBackground.setChecked(showZoneBackground);

        return super.onPrepareOptionsMenu(menu);
    }

    private void updateCheckedEntryType(Menu menu) {
        if (entryType == null) return;
        MenuItem item = menu.findItem(entryType.menuActionId);
        if (item == null) {
            throw new IllegalStateException("Unhandled entry type: " + entryType);
        }
        item.setChecked(true);
    }

    private void showCurrentMap() {
        switch (providerType) {
            case GOOGLE:
            default:
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
        }
        currentMap.setPosition(lastPosition);
        currentMap.setZoomFactor(zoomFactor);
        currentMap.setMarkers(getMarkers());
        currentMap.setPath(points);
        currentMap.setMapType(mapType);
        currentMap.setZones(getZones());
        currentMap.setShowZoneBackground(showZoneBackground);
        currentMap.setOnMarkerClickListener(this);
        currentMap.showMap();
    }

    @OptionsItem(R.id.action_map_google)
    void onGoogleMapProvider() {
        setProviderType(MapProvider.ProviderType.GOOGLE);
    }

    @OptionsItem(R.id.action_map_osm)
    void onOSMMapProvider() {
        setProviderType(MapProvider.ProviderType.OSM);
    }

    @OptionsItem(R.id.view_type_map)
    void onViewTypeMap(MenuItem item) {
        item.setChecked(true);
        setViewType(VIEWTYPE_MAP);
    }

    @OptionsItem(R.id.view_type_list)
    void onViewTypeList(MenuItem item) {
        item.setChecked(true);
        setViewType(VIEWTYPE_LIST);
    }

    @OptionsItem(R.id.view_type_combined)
    void onViewTypeCombined(MenuItem item) {
        item.setChecked(true);
        setViewType(VIEWTYPE_COMBINED);
    }

    public void setProviderType(@NonNull MapProvider.ProviderType providerType) {
        this.providerType = providerType;
        showCurrentMap();
        prefs.providerType().put(providerType.toString());
    }

    public void setViewType(int viewType) {
        prefs.viewType().put(viewType);
        updateViewType();
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
        if (menuUndoEntry != null) {
            menuUndoEntry.setEnabled(true);
        }
    }

    @OnActivityResult(REQUEST_FINISH_MONITORING)
    void onFinishConfirm(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        clearPrefs();
        eventBus.post(new FinishMonitoringEvent());
        finish();
    }

    @OptionsItem(R.id.action_finish)
    void onFinish() {
        EditCommonFormActivity_.intent(this).isFinishing(true).startForResult(REQUEST_FINISH_MONITORING);
    }

    @OptionsItem(android.R.id.home)
    void onUp() {
        pauseMonitoring();
    }

    @Override
    public void onBackPressed() {
        pauseMonitoring();
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

    void pauseMonitoring() {
        eventBus.post(new PauseMonitoringEvent());
        finish();
    }

    @OptionsItem(R.id.action_zoom_1km)
    void setZoom1km(MenuItem sender) {
        setZoomFactor(1000);
        sender.setChecked(true);
        menuZoom.setTitle(sender.getTitle());
    }

    @OptionsItem(R.id.action_zoom_500m)
    void setZoom500m(MenuItem sender) {
        setZoomFactor(500);
        sender.setChecked(true);
        menuZoom.setTitle(sender.getTitle());
    }

    @OptionsItem(R.id.action_zoom_250m)
    void setZoom250m(MenuItem sender) {
        setZoomFactor(250);
        sender.setChecked(true);
        menuZoom.setTitle(sender.getTitle());
    }

    @OptionsItem(R.id.action_zoom_100m)
    void setZoom100m(MenuItem sender) {
        setZoomFactor(100);
        sender.setChecked(true);
        menuZoom.setTitle(sender.getTitle());
    }

    @OptionsItem(R.id.action_zoom_free)
    void setZoomFree(MenuItem sender) {
        setZoomFactor(-1);
        sender.setChecked(true);
        menuZoom.setTitle(sender.getTitle());
    }

    public void setZoomFactor(int zoomFactor) {
        this.zoomFactor = zoomFactor;
        currentMap.setZoomFactor(zoomFactor);
        currentMap.updateCamera();
        prefs.zoomFactor().put(zoomFactor);
    }

    @OptionsItem(R.id.action_map_google_normal)
    void setGoogleMapsNormal(MenuItem sender) {
        setMapType(MapProvider.MapType.NORMAL);
        sender.setChecked(true);
        menuMap.setTitle(sender.getTitle());
    }

    @OptionsItem(R.id.action_map_google_satellite)
    void setGoogleMapsSatellite(MenuItem sender) {
        setMapType(MapProvider.MapType.SATELLITE);
        sender.setChecked(true);
        menuMap.setTitle(sender.getTitle());
    }

    @OptionsItem(R.id.action_map_google_hybrid)
    void setGoogleMapsHybrid(MenuItem sender) {
        setMapType(MapProvider.MapType.HYBRID);
        sender.setChecked(true);
        menuMap.setTitle(sender.getTitle());
    }

    public void setMapType(@NonNull MapProvider.MapType mapType) {
        this.mapType = mapType;
        currentMap.setMapType(mapType);
        prefs.mapType().put(mapType.toString());
    }

    @OptionsItem(R.id.action_show_zone_background)
    void setShowZoneBackground(MenuItem sender) {
        sender.setChecked(!sender.isChecked());
        setShowZoneBackground(sender.isChecked());
    }

    private void setShowZoneBackground(boolean showBackground) {
        this.showZoneBackground = showBackground;
        if (currentMap != null) {
            currentMap.setShowZoneBackground(showBackground);
            currentMap.updateCamera();
        }
        prefs.showZoneBackground().put(showBackground);
    }

    @OptionsItem(R.id.action_stay_awake)
    void setStayAwake(MenuItem sender) {
        sender.setChecked(!sender.isChecked());
        setStayAwake(sender.isChecked());
    }

    private void setStayAwake(boolean stayAwake) {
        this.stayAwake = stayAwake;
        if (stayAwake) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        prefs.stayAwake().put(stayAwake);
    }

    @OptionsItem({
            R.id.action_form_type_birds,
            R.id.action_form_type_cbm,
            R.id.action_form_type_ciconia,
            R.id.action_form_type_herp,
            R.id.action_form_type_humid,
            R.id.action_form_type_herptile,
            R.id.action_form_type_mammal,
            R.id.action_form_type_invertebrates,
            R.id.action_form_type_plants
    })
    void setFormType(MenuItem sender) {
        final int senderId = sender.getItemId();
        for (EntryType entryType : EntryType.values())
            if (senderId == entryType.menuActionId) {
                setEntryType(entryType);
                return;
            }
    }

    public void setEntryType(@Nullable EntryType entryType) {
        this.entryType = entryType;
        if (entryType != null)
            monitoringPrefs.entryType().put(entryType.name());
        else
            monitoringPrefs.entryType().remove();
    }

    public void onEvent(LocationChangedEvent event) {
        Location location = event.location;
        lastPosition = new LatLng(location.getLatitude(), location.getLongitude());
        currentMap.setPosition(lastPosition);
        currentMap.updateCamera();
        if (menuNewEntry != null) {
            menuNewEntry.setEnabled(true);
        }
        monitoringPrefs.lastPositionLat().put((float) lastPosition.latitude);
        monitoringPrefs.lastPositionLon().put((float) lastPosition.longitude);

        addPoint(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    private void addPoint(LatLng point) {
        points.add(point);
        currentMap.updatePath(points);
        persistPoints();
    }

    public void onEvent(MapClickedEvent event) {
        startNewEntryWithoutAsking(event.position);
    }

    public void onEvent(MapLongClickedEvent event) {
        startNewEntryAsking(event.position);
    }

    public void onEventMainThread(ActiveMonitoringEvent event) {
        this.monitoringCode = event.monitoring != null ? event.monitoring.code : null;
        setupList();
    }

    public void onEvent(MonitoringResumedEvent event) {
        eventBus.removeStickyEvent(ResumeMonitoringEvent.class);
        eventBus.removeStickyEvent(MonitoringResumedEvent.class);
    }

    void startNewEntryWithoutAsking(final LatLng position) {
        if (entryType == null) {
            startNewEntryAsking(position);
            return;
        }
        NewMonitoringEntryActivity_.IntentBuilder_ ib = NewMonitoringEntryActivity_.intent(MonitoringActivity.this);
        ib.entryType(entryType);
        if (position != null) {
            ib.lat(position.latitude).lon(position.longitude);
        }
        ib.startForResult(REQUEST_NEW_ENTRY);
    }

    protected void startNewEntryAsking(final LatLng position) {
        final String[] types = EntryType.getTitles(getResources());
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.menu_monitoring_new_entry)
                .setSingleChoiceItems(types, entryType != null ? entryType.ordinal() : -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setEntryType(EntryType.values()[i]);
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
    }

    @OptionsItem(R.id.action_crash)
    void crash() {
        Crashlytics.getInstance().crash();
    }

    @Override
    protected void onPause() {
        persistState();
        super.onPause();
    }

    private void persistState() {
        Log.d(TAG, "persisting state");
        if (!canceled) {

            MonitoringPrefs_.MonitoringPrefsEditor_ editor = monitoringPrefs.edit();
            prefs.providerType().put(providerType.toString());
            prefs.mapType().put(mapType.toString());
            prefs.stayAwake().put(stayAwake);
            prefs.showZoneBackground().put(showZoneBackground);
            if (lastPosition != null) {
                editor.lastPositionLat().put((float) lastPosition.latitude);
                editor.lastPositionLon().put((float) lastPosition.longitude);
            } else {
                editor.lastPositionLat().remove();
                editor.lastPositionLon().remove();
            }
            prefs.zoomFactor().put(zoomFactor);
            if (entryType != null) {
                editor.entryType().put(entryType.name());
            } else {
                editor.entryType().remove();
            }

            editor.apply();

            persistPoints();
        }
    }

    private void persistPoints() {
        SharedPreferences pointsPrefs = getSharedPreferences(PREFS_POINTS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pointsPrefs.edit();
        editor.clear();
        try {
            if (points.isEmpty()) {
                monitoringPrefs.pointsCount().remove();
                return;
            }
            monitoringPrefs.pointsCount().put(points.size());
            for (int i = 0; i < points.size(); i++) {
                editor.putFloat("lat_" + i, (float) points.get(i).latitude);
                editor.putFloat("lon_" + i, (float) points.get(i).longitude);
            }
        } finally {
            editor.apply();
        }
    }

    private void restoreState() {
        Log.d(TAG, "restoring state");
        providerType = MapProvider.ProviderType.GOOGLE;
        try {
            final String providerTypeName = prefs.providerType().get();
            if (!isEmpty(providerTypeName))
                providerType = MapProvider.ProviderType.valueOf(providerTypeName);
        } catch (IllegalArgumentException ignored) {
        }
        mapType = MapProvider.MapType.NORMAL;
        try {
            final String mapTypeName = prefs.mapType().get();
            if (!isEmpty(mapTypeName))
                mapType = MapProvider.MapType.valueOf(mapTypeName);
        } catch (IllegalArgumentException ignored) {
        }

        if (monitoringPrefs.lastPositionLat().exists() && monitoringPrefs.lastPositionLon().exists()) {
            lastPosition = new LatLng(monitoringPrefs.lastPositionLat().get(), monitoringPrefs.lastPositionLon().get());
        } else {
            lastPosition = null;
        }

        zoomFactor = prefs.zoomFactor().getOr(500);
        entryType = null;
        try {
            final String entryTypeName = monitoringPrefs.entryType().get();
            if (!isEmpty(entryTypeName))
                entryType = EntryType.valueOf(entryTypeName);
        } catch (IllegalArgumentException ignored) {
        }

        setStayAwake(prefs.stayAwake().get());
        setShowZoneBackground(prefs.showZoneBackground().get());
        restorePoints();
    }

    private void restorePoints() {
        int pointsCount = monitoringPrefs.pointsCount().get();
        if (pointsCount <= 0) {
            return;
        }

        SharedPreferences pointsPrefs = getSharedPreferences(PREFS_POINTS, Context.MODE_PRIVATE);

        points.clear();
        for (int i = 0; i < pointsCount; i++) {
            final double lat = pointsPrefs.getFloat("lat_" + i, 0);
            final double lon = pointsPrefs.getFloat("lon_" + i, 0);
            points.add(new LatLng(lat, lon));
        }
    }

    private void clearPrefs() {
        Log.d(TAG, "clearing monitoring prefs");
        canceled = true;
        monitoringPrefs.edit().clear().apply();
        getSharedPreferences(PREFS_POINTS, Context.MODE_PRIVATE).edit().clear().apply();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, String.format(Locale.ENGLISH, "service %s connected", name));
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, String.format(Locale.ENGLISH, "service %s disconnected", name));
    }

    @Override
    public void onMonitoringEntrySelected(long id, EntryType entryType) {
        EditMonitoringEntryActivity_.intent(this).entryId(id).entryType(entryType).start();
    }

    private Iterable<MapMarker> getMarkers() {
        return new IterableConverter<>(entries.iterable(), mapMarkerConverter);
    }

    private Iterable<Zone> getZones() {
        return zones.iterable();
    }

    @Override
    public void onMonitoringEntriesChanged(MonitoringModelEntries entries) {
        if (currentMap != null) currentMap.setMarkers(getMarkers());
    }

    @Override
    public void onZoneEntriesChanged(ZonesModelEntries entries) {
        if (currentMap != null) currentMap.setZones(getZones());
    }

    @Override
    public void onMarkerClicked(long entryId, EntryType type) {
        onMonitoringEntrySelected(entryId, type);
    }
}
