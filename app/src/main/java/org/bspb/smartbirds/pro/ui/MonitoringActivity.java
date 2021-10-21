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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.reflect.TypeToken;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
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
import org.bspb.smartbirds.pro.backend.dto.BGAtlasCell;
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
import org.bspb.smartbirds.pro.prefs.UserPrefs_;
import org.bspb.smartbirds.pro.service.DataService_;
import org.bspb.smartbirds.pro.service.TrackingServiceBuilder;
import org.bspb.smartbirds.pro.tools.SBGsonParser;
import org.bspb.smartbirds.pro.ui.fragment.MonitoringEntryListFragment;
import org.bspb.smartbirds.pro.ui.fragment.MonitoringEntryListFragment_;
import org.bspb.smartbirds.pro.ui.map.EntryMapMarker;
import org.bspb.smartbirds.pro.ui.map.GoogleMapProvider;
import org.bspb.smartbirds.pro.ui.map.MapProvider;
import org.bspb.smartbirds.pro.ui.map.OsmMapProvider;
import org.osmdroid.config.Configuration;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static android.text.TextUtils.isEmpty;
import static org.bspb.smartbirds.pro.tools.Reporting.logException;
import static org.bspb.smartbirds.pro.ui.utils.Constants.VIEWTYPE_LIST;
import static org.bspb.smartbirds.pro.ui.utils.Constants.VIEWTYPE_MAP;

/**
 * Created by dani on 14-11-4.
 */
@EActivity(R.layout.activity_monitoring)
@OptionsMenu({R.menu.monitoring, R.menu.debug_menu})
public class MonitoringActivity extends BaseActivity implements MonitoringEntryListFragment.Listener, MonitoringModelEntries.Listener, ZonesModelEntries.Listener, MapProvider.MarkerClickListener {

    private static final String TAG = SmartBirdsApplication.TAG + ".MonitoringActivity";

    private static final int REQUEST_NEW_ENTRY = 1001;

    private static final int REQUEST_FINISH_MONITORING = 1002;

    @NonNull
    MapProvider.ProviderType providerType = MapProvider.ProviderType.GOOGLE;
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

    @NonNull
    ArrayList<LatLng> points = new ArrayList<LatLng>();
    @InstanceState
    int zoomFactor = 500;
    @InstanceState
    LatLng lastPosition;


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
    @OptionsMenuItem(R.id.action_crash)
    MenuItem menuCrash;

    @InstanceState
    @Nullable
    EntryType entryType = null;

    @Pref
    MonitoringPrefs_ monitoringPrefs;
    @Pref
    SmartBirdsPrefs_ prefs;
    @Pref
    UserPrefs_ userPrefs;

    private boolean canceled = false;
    boolean stayAwake;
    boolean showZoneBackground;
    boolean showLocalProjects;
    boolean showBgAtlasCells;
    boolean showSPA;
    boolean showRandomCells;
    boolean showGrid1km;
    boolean showGrid10km;

    @FragmentById(R.id.list_container)
    MonitoringEntryListFragment listFragment;

    @InstanceState
    String monitoringCode;

    @ViewById(R.id.root_map_container)
    View mapContainer;
    @ViewById(R.id.list_container)
    View listContainer;

    @Bean
    MonitoringModelEntries entries;
    @Bean
    EntriesToMapMarkersConverter mapMarkerConverter;
    @Bean
    ZonesModelEntries zones;

    private Set<String> formsEnabled;
    private Menu menu;

    private ServiceConnection trackingServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, String.format(Locale.ENGLISH, "service %s connected", name));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, String.format(Locale.ENGLISH, "service %s disconnected", name));
        }
    };

    private ServiceConnection dataServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, String.format(Locale.ENGLISH, "service %s connected", name));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, String.format(Locale.ENGLISH, "service %s disconnected", name));
        }
    };

    @AfterInject
    protected void initProviders() {
        googleMap.setFragmentManager(getSupportFragmentManager());
        osmMap.setFragmentManager(getSupportFragmentManager());
        osmMap.setLifeCycle(getLifecycle());
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
        setupList();
    }

    private void setupList() {
        if (isEmpty(monitoringCode)) return;
        if (listFragment == null) {
            listFragment = MonitoringEntryListFragment_.builder().setMonitoringCode(monitoringCode).build();
            getSupportFragmentManager().beginTransaction().replace(R.id.list_container, listFragment).commit();
        } else {
            listFragment.setMonitoringCode(monitoringCode);
        }
        if (entries != null) entries.setMonitoringCode(monitoringCode);
    }

    private void updateViewType() {
        String viewType = prefs.monitoringViewType().get();
        mapContainer.setVisibility(!VIEWTYPE_LIST.equals(viewType) ? View.VISIBLE : View.GONE);
        if (getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT && !VIEWTYPE_LIST.equals(viewType)) {
            listContainer.setVisibility(View.GONE);
        } else {
            listContainer.setVisibility(!VIEWTYPE_MAP.equals(viewType) ? View.VISIBLE : View.GONE);
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        super.onCreate(savedInstanceState);
        try {
            bindService(DataService_.intent(this).get(), trackingServiceConnection, BIND_AUTO_CREATE);
            bindService(new TrackingServiceBuilder(this).getIntent(), dataServiceConnection, BIND_ABOVE_CLIENT);
        } catch (Throwable t) {
            logException(t);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        restoreState();
        showCurrentMap();
        updateViewType();
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
            unbindService(trackingServiceConnection);
            unbindService(dataServiceConnection);
        } catch (Throwable t) {
            logException(t);
        }
        super.onDestroy();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
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

        updateTypeOfObservationMenus(menu);

        return super.onPrepareOptionsMenu(menu);
    }

    private void updateTypeOfObservationMenus(Menu menu) {
        if (menu == null) {
            return;
        }

        // hide all types
        for (EntryType form : EntryType.values()) {
            MenuItem menuItem = menu.findItem(form.menuActionId);
            if (menuItem != null) {
                menuItem.setVisible(false);
            }
        }

        // show enabled types
        if (formsEnabled != null) {
            for (String formName : formsEnabled) {
                try {
                    EntryType form = EntryType.valueOf(formName);
                    MenuItem item = menu.findItem(form.menuActionId);
                    if (item != null) {
                        item.setVisible(true);
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

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
                if (currentMap != null && currentMap != googleMap) {
                    currentMap.clearPositioned();
                }
                currentMap = googleMap;
                break;
            case OSM:
                if (currentMap != null && currentMap != osmMap) {
                    currentMap.clearPositioned();
                }
                currentMap = osmMap;
                break;
        }
        currentMap.setPosition(lastPosition);
        currentMap.setZoomFactor(zoomFactor);
        currentMap.setMarkers(getMarkers());
        currentMap.setPath(points);
        currentMap.setMapType(mapType);
        currentMap.setZones(getZones());
        currentMap.setShowZoneBackground(showZoneBackground);
        currentMap.setShowLocalProjects(showLocalProjects);
        currentMap.setOnMarkerClickListener(this);
        currentMap.showMap();
        currentMap.setBgAtlasCells(readAtlasCells());
        currentMap.setShowBgAtlasCells(showBgAtlasCells);
        currentMap.setShowSPA(showSPA);
        currentMap.setShowRandomCells(showRandomCells);
        currentMap.setShowGrid1km(showGrid1km);
        currentMap.setShowGrid10km(showGrid10km);
    }

    private List<BGAtlasCell> readAtlasCells() {
        Type listType = new TypeToken<List<BGAtlasCell>>() {
        }.getType();
        return SBGsonParser.createParser().fromJson(userPrefs.bgAtlasCells().get(), listType);
    }

    @OptionsItem(R.id.action_new_entry)
    @Click(R.id.fab)
    void onNewEntry() {
        if (currentMap.getMyLocation() != null) {
            startNewEntryWithoutAsking(new LatLng(currentMap.getMyLocation().getLatitude(), currentMap.getMyLocation().getLongitude()));
        }
    }

    @OptionsItem(R.id.action_common_form)
    void onCommonForm() {
        EditCurrentCommonFormActivity_.intent(this).start();
    }

    @OptionsItem(R.id.menu_settings)
    void openSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
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
        EditCurrentCommonFormActivity_.intent(this).isFinishing(true).startForResult(REQUEST_FINISH_MONITORING);
    }

    @OptionsItem(android.R.id.home)
    void onUp() {
        pauseMonitoring();
    }

    @Override
    public void onBackPressed() {
        pauseMonitoring();
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

    private void setShowZoneBackground(boolean showBackground) {
        this.showZoneBackground = showBackground;
        if (currentMap != null) {
            currentMap.setShowZoneBackground(showBackground);
            currentMap.updateCamera();
        }
    }

    private void setShowLocalProjects(boolean showLocalProjects) {
        this.showLocalProjects = showLocalProjects;
        if (currentMap != null) {
            currentMap.setShowLocalProjects(showLocalProjects);
            currentMap.updateCamera();
        }
    }

    private void setShowBgAtlasCells(boolean showBgAtlasCells) {
        this.showBgAtlasCells = showBgAtlasCells;
        if (currentMap != null) {
            currentMap.setShowBgAtlasCells(showBgAtlasCells);
            currentMap.updateCamera();
        }
    }

    private void setShowSPA(boolean showSPA) {
        this.showSPA = showSPA;
        if (currentMap != null) {
            currentMap.setShowSPA(showSPA);
            currentMap.updateCamera();
        }
    }

    private void setShowRandomCells(boolean showRandomCells) {
        this.showRandomCells = showRandomCells;
        if (currentMap != null) {
            currentMap.setShowRandomCells(showRandomCells);
            currentMap.updateCamera();
        }
    }

    private void setShowGrid1km(boolean showGrid1km) {
        this.showGrid1km = showGrid1km;
        if (currentMap != null) {
            currentMap.setShowGrid1km(showGrid1km);
            currentMap.updateCamera();
        }
    }

    private void setShowGrid10km(boolean showGrid10km) {
        this.showGrid10km = showGrid10km;
        if (currentMap != null) {
            currentMap.setShowGrid10km(showGrid10km);
            currentMap.updateCamera();
        }
    }

    private void setStayAwake(boolean stayAwake) {
        this.stayAwake = stayAwake;
        if (stayAwake) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @OptionsItem({
            R.id.action_form_type_birds,
            R.id.action_form_type_cbm,
            R.id.action_form_type_ciconia,
            R.id.action_form_type_humid,
            R.id.action_form_type_herptile,
            R.id.action_form_type_mammal,
            R.id.action_form_type_invertebrates,
            R.id.action_form_type_plants,
            R.id.action_form_type_threats
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
        if (event.monitoring == null) {
            logException(new IllegalStateException("Receive null monitoring as active monitoring."));
            finish();
            return;
        }
        this.monitoringCode = event.monitoring.code;
        setupList();
    }

    public void onEvent(MonitoringResumedEvent event) {
        eventBus.removeStickyEvent(ResumeMonitoringEvent.class);
        eventBus.removeStickyEvent(MonitoringResumedEvent.class);
    }

    public void onEvent(CancelMonitoringEvent event) {
        logException(new IllegalStateException("Receive cancelMonitoring event while in monitoring"));
        finish();
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
        final List<EntryType> enabledEntryTypes = new ArrayList();
        final List<String> typeTitles = new ArrayList<>();

        if (formsEnabled != null) {
            EntryType[] allEntries = EntryType.values();
            for (EntryType form : allEntries) {
                if (formsEnabled.contains(form.name())) {
                    enabledEntryTypes.add(form);
                }
            }
        }

        for (EntryType form : enabledEntryTypes) {
            typeTitles.add(getString(form.titleId));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.menu_monitoring_new_entry)
                .setSingleChoiceItems(typeTitles.toArray(new String[]{}), entryType != null ? entryType.ordinal() : -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setEntryType(enabledEntryTypes.get(i));
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
        throw new RuntimeException("Test Crash");
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
        SharedPreferences pointsPrefs = getSharedPreferences(SmartBirdsApplication.PREFS_MONITORING_POINTS, Context.MODE_PRIVATE);
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

        formsEnabled = prefs.formsEnabled().getOr(new HashSet(Arrays.asList(getResources().getStringArray(R.array.monitoring_form_values))));

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
            if (!isEmpty(entryTypeName) && formsEnabled.contains(entryTypeName))
                entryType = EntryType.valueOf(entryTypeName);
        } catch (IllegalArgumentException ignored) {
        }

        setStayAwake(prefs.stayAwake().get());
        setShowZoneBackground(prefs.showZoneBackground().get());
        setShowLocalProjects(prefs.showLocalProjects().get());
        setShowBgAtlasCells(prefs.showBgAtlasCells().get());
        setShowSPA(prefs.showSPA().get());
        setShowRandomCells(prefs.showRandomCells().get());
        setShowGrid1km(prefs.showGrid1km().get());
        setShowGrid10km(prefs.showGrid10km().get());

        restorePoints();

        updateTypeOfObservationMenus(menu);
    }

    private void restorePoints() {
        int pointsCount = monitoringPrefs.pointsCount().get();
        if (pointsCount <= 0) {
            return;
        }

        SharedPreferences pointsPrefs = getSharedPreferences(SmartBirdsApplication.PREFS_MONITORING_POINTS, Context.MODE_PRIVATE);

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
        getSharedPreferences(SmartBirdsApplication.PREFS_MONITORING_POINTS, Context.MODE_PRIVATE).edit().clear().apply();
    }

    @Override
    public void onMonitoringEntrySelected(long id, EntryType entryType) {
        EditMonitoringEntryActivity_.intent(this).entryId(id).entryType(entryType).start();
    }

    private Iterable<EntryMapMarker> getMarkers() {
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
