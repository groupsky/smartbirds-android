package org.bspb.smartbirds.pro.ui.map;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.view.KeyEvent;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.backend.dto.BGAtlasCell;
import org.bspb.smartbirds.pro.backend.dto.Coordinate;
import org.bspb.smartbirds.pro.backend.dto.Zone;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.EEventBus_;
import org.bspb.smartbirds.pro.events.LocationChangedEvent;
import org.bspb.smartbirds.pro.events.MapAttachedEvent;
import org.bspb.smartbirds.pro.events.MapClickedEvent;
import org.bspb.smartbirds.pro.events.MapLongClickedEvent;
import org.bspb.smartbirds.pro.tools.Reporting;
import org.bspb.smartbirds.pro.ui.fragment.OsmMapFragment_;
import org.bspb.smartbirds.pro.ui.utils.Configuration;
import org.bspb.smartbirds.pro.ui.utils.KmlUtils;
import org.osmdroid.bonuspack.kml.IconStyle;
import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.bonuspack.kml.Style;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.CustomZoomButtonsDisplay;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by dani on 14-11-6.
 */
@EBean
public class OsmMapProvider implements MapProvider, MapEventsReceiver {

    private static final String TAG = SmartBirdsApplication.TAG + ".OsmMap";
    private FragmentManager fragmentManager;
    private OsmMapFragment_ fragment;
    private MapView mMap;

    private double zoomFactor;
    private LatLng lastPosition;

    @Bean
    EEventBus eventBus;
    private final Set<MarkerHolder> markers = new HashSet<>();
    private ArrayList<LatLng> points;
    private Polyline pathOverlay;

    private MyLocationNewOverlay locationOverlay;

    private boolean positioned = false;
    private Marker lastMarker;
    private EventsOverlay eventsOverlay;
    private final ArrayList<Zone> zones = new ArrayList<>();
    private boolean showZoneBackground;

    private List<Polygon> zoneOverlays = new ArrayList<>();
    private MarkerClickListener markerClickListener;
    private boolean showLocalProjects;
    private FolderOverlay localProjectsOverlay;
    private boolean showBgAtlasCells;
    private ArrayList<BGAtlasCell> atlasCells = new ArrayList<>();
    private List<Polygon> atlasCellsPolygons = new ArrayList<>();

    @Override
    public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = fragment.getMapView();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        if (mMap == null) return;
        mMap.setMultiTouchControls(true);
        mMap.setTileSource(TileSourceFactory.MAPNIK);
        mMap.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        mMap.getZoomController().getDisplay().setPositions(
                false,
                CustomZoomButtonsDisplay.HorizontalPosition.RIGHT,
                CustomZoomButtonsDisplay.VerticalPosition.BOTTOM);
        mMap.getZoomController().getDisplay().setMarginPadding(0.5f, 0.1f);
        mMap.getController().setZoom(16f);

        locationOverlay = new LocationRetrievalOverlay(mMap);
        locationOverlay.enableMyLocation();
        locationOverlay.enableFollowLocation();

        locationOverlay.setDrawAccuracyEnabled(true);

        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mMap);
        scaleBarOverlay.setUnitsOfMeasure(ScaleBarOverlay.UnitsOfMeasure.metric);
        scaleBarOverlay.setEnableAdjustLength(true);

        pathOverlay = new Polyline();
        pathOverlay.getOutlinePaint().setColor(Color.BLUE);
        pathOverlay.getOutlinePaint().setStrokeWidth(5);
        if (points != null && !points.isEmpty()) {
            for (LatLng point : points) {
                pathOverlay.addPoint(new GeoPoint(point.latitude, point.longitude));
            }
        }

        eventsOverlay = new EventsOverlay(mMap.getContext(), this);


        mMap.getOverlayManager().clear();
        mMap.getOverlayManager().add(pathOverlay);
        mMap.getOverlayManager().add(locationOverlay);
        mMap.getOverlayManager().add(scaleBarOverlay);
        mMap.getOverlayManager().add(eventsOverlay);

        loadKmlFile();
        drawLocalProjects(showLocalProjects);
        drawBgAtlasCells();

        for (MarkerHolder markerHolder : markers) {
            if (markerHolder.marker == null || !mMap.getOverlayManager().contains(markerHolder.marker)) {
                markerHolder.marker = addMarker(markerHolder.mapMarker, true);

            }
        }
        if (zoneOverlays.isEmpty()) {
            for (Zone zone : zones) {
                Polygon overlay = addZone(zone);
                if (overlay != null) {
                    zoneOverlays.add(overlay);
                }
            }
        }
        mMap.invalidate();

        updateCamera();
    }

    @Background
    void loadKmlFile() {
        KmlDocument kml = new KmlDocument();
        File file = new File(AREA_FILE_PATH);
        if (file.exists()) {
            try {
                if (kml.parseKMLFile(file)) {
                    FolderOverlay kmlOverlay = (FolderOverlay) kml.mKmlRoot.buildOverlay(mMap, null, new OsmKmlStyler(), kml);
                    displayKml(kmlOverlay);
                }
            } catch (Throwable t) {
                Reporting.logException(t);
            }
        }

    }

    @UiThread
    void displayKml(Overlay kmlOverlay) {
        mMap.getOverlayManager().add(kmlOverlay);
    }

    @Override
    public void setZoomFactor(double zoomFactor) {
        this.zoomFactor = zoomFactor;
    }

    @Override
    public void setShowZoneBackground(boolean showBackground) {
        this.showZoneBackground = showBackground;
        drawZones();
        drawBgAtlasCells();
    }

    @Override
    public void setShowLocalProjects(boolean showKml) {
        this.showLocalProjects = showKml;
        drawLocalProjects(showKml);
    }

    private void drawLocalProjects(boolean showKml) {
        if (fragment == null || fragment.getContext() == null) {
            return;
        }

        if (showKml) {
            if (localProjectsOverlay != null && mMap.getOverlayManager().overlays().contains(localProjectsOverlay)) {
                return;
            }

            try {
                KmlDocument kml = KmlUtils.readKmlFromAssets(fragment.getContext(), Configuration.LOCAL_PROJECTS_KML_FILE);
                Style style = new Style();
                style.mIconStyle = new IconStyle();
                style.mIconStyle.mIcon = BitmapFactory.decodeResource(fragment.getContext().getApplicationContext().getResources(), R.drawable.ic_place);
                localProjectsOverlay = (FolderOverlay) kml.mKmlRoot.buildOverlay(mMap, style, null, kml);
                mMap.getOverlayManager().add(localProjectsOverlay);
                mMap.invalidate();
            } catch (Throwable t) {
                Reporting.logException(t);
            }
        } else {
            mMap.getOverlayManager().overlays().remove(localProjectsOverlay);
            localProjectsOverlay = null;
            mMap.invalidate();
        }
    }

    private void drawBgAtlasCells() {
        if (fragment == null || fragment.getContext() == null) {
            return;
        }

        if (mMap != null) {
            mMap.getOverlayManager().removeAll(atlasCellsPolygons);
        }
        zoneOverlays.clear();

        if (!showBgAtlasCells) {
            return;
        }

        for (BGAtlasCell cell : atlasCells) {
            Polygon overlay = addAtlasCell(cell);
            if (overlay != null) {
                atlasCellsPolygons.add(overlay);
            }
        }
        if (mMap != null) mMap.invalidate();
    }

    private Polygon addAtlasCell(BGAtlasCell cell) {
        if (mMap == null) return null;

        int color = getAtlasCellColor(cell);
        Polygon cellOverlay = new Polygon();
        cellOverlay.getOutlinePaint().setStrokeWidth(6f);
        cellOverlay.getOutlinePaint().setColor(color);
        cellOverlay.getFillPaint().setColor(color);
        if (showZoneBackground) {
            cellOverlay.getFillPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        } else {
            cellOverlay.getFillPaint().setStyle(Paint.Style.STROKE);
        }
        for (Coordinate point : cell.getCoordinates()) {
            cellOverlay.addPoint(new GeoPoint(point.latitude, point.longitude));
        }
        cellOverlay.addPoint(new GeoPoint(cell.getCoordinates().get(0).latitude, cell.getCoordinates().get(0).longitude));

        mMap.getOverlayManager().add(0, cellOverlay);
        return cellOverlay;
    }

    private int getAtlasCellColor(BGAtlasCell cell) {
        float percent = cell.getSpecOld() > 0 ? (float) (100.0 * cell.getSpecKnown() / cell.getSpecOld()) : 0;
        if (percent < 30) {
            return fragment.getContext().getResources().getColor(R.color.atlas_cell_color_low);
        }
        if (percent < 65) {
            return fragment.getContext().getResources().getColor(R.color.atlas_cell_color_med);
        }

        return fragment.getContext().getResources().getColor(R.color.atlas_cell_color_high);
    }

    @Override
    public void setOnMarkerClickListener(MarkerClickListener listener) {
        this.markerClickListener = listener;
    }

    private void drawZones() {
        if (mMap != null) {
            mMap.getOverlayManager().removeAll(zoneOverlays);
        }
        zoneOverlays.clear();
        for (Zone zone : zones) {
            Polygon overlay = addZone(zone);
            if (overlay != null) {
                zoneOverlays.add(overlay);
            }
        }
        if (mMap != null) mMap.invalidate();
    }

    @Override
    public void drawPath(List<LatLng> points) {

    }

    @Override
    public void showMap() {
        if (fragment == null) {
            fragment = new OsmMapFragment_();
            positioned = false;
        }

        FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
        fragTransaction.replace(R.id.map_container, fragment, "osm");
        fragTransaction.commit();

        mMap = fragment.getMapView();
    }

    @Override
    public void updateCamera() {
        if (zoomFactor > 0) {
            mMap.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
            mMap.setMultiTouchControls(false);
            locationOverlay.enableFollowLocation();
            eventsOverlay.setLocked(true);

            if (lastPosition == null) return;

            LatLng southwest = SphericalUtil.computeOffset(lastPosition, zoomFactor, 225);
            LatLng northeast = SphericalUtil.computeOffset(lastPosition, zoomFactor, 45);

            ArrayList<GeoPoint> geoPoints = new ArrayList<>();
            geoPoints.add(new GeoPoint(southwest.latitude, southwest.longitude));
            geoPoints.add(new GeoPoint(northeast.latitude, northeast.longitude));
            final BoundingBox boundingBox = BoundingBox.fromGeoPoints(geoPoints);

            mMap.post(() -> {
                mMap.getController().setCenter(boundingBox.getCenterWithDateLine());
                mMap.getController().zoomToSpan(boundingBox.getLatitudeSpan(), boundingBox.getLongitudeSpanWithDateLine());
            });
            mMap.invalidate();
            positioned = false;
        } else {
            mMap.setMultiTouchControls(true);
            mMap.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
            locationOverlay.disableFollowLocation();
            eventsOverlay.setLocked(false);
            if (!positioned && lastPosition != null) {
                // TODO investigate why animateTo is not working
                mMap.getController().setCenter(new GeoPoint(lastPosition.latitude, lastPosition.longitude));
//                mMap.getController().animateTo(new GeoPoint(lastPosition.latitude, lastPosition.longitude));
                positioned = true;
            }
        }

    }

    @Override
    public void setMapType(MapType mapType) {
        // not supported on OSM
    }

    @Override
    public Location getMyLocation() {
        Location location = new Location("myLocation");
        location.setLatitude(locationOverlay.getMyLocation().getLatitude());
        location.setLongitude(locationOverlay.getMyLocation().getLongitude());
        return location;
    }


    protected Marker addMarker(final EntryMapMarker mapMarker, boolean bulk) {
        if (mMap == null) return null;
        Marker marker = lastMarker = new Marker(mMap);
        OsmInfoWindow infoWindow = new OsmInfoWindow(R.layout.bonuspack_bubble, mMap);
        infoWindow.setClickListener(view -> {
            if (markerClickListener != null) {
                markerClickListener.onMarkerClicked(mapMarker.getId(), mapMarker.getEntryType());
            }
        });
        marker.setInfoWindow(infoWindow);
        marker.setPosition(new GeoPoint(mapMarker.getLatitude(), mapMarker.getLongitude()));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(mapMarker.getTitle());
        marker.setPanToView(false);
        mMap.getOverlays().add(marker);

        if (!bulk) mMap.invalidate();
        return marker;
    }

    protected Polygon addZone(Zone zone) {
        if (mMap == null) return null;


        Polygon zoneOverlay = new Polygon();
        zoneOverlay.getOutlinePaint().setStrokeWidth(6f);
        zoneOverlay.getOutlinePaint().setColor(mMap.getResources().getColor(R.color.zone_fill_color));
        zoneOverlay.getFillPaint().setColor(mMap.getResources().getColor(R.color.zone_fill_color));
        if (showZoneBackground) {
            zoneOverlay.getFillPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        } else {
            zoneOverlay.getFillPaint().setStyle(Paint.Style.STROKE);
        }
        for (Coordinate point : zone.coordinates) {
            zoneOverlay.addPoint(new GeoPoint(point.latitude, point.longitude));
        }
        zoneOverlay.addPoint(new GeoPoint(zone.coordinates.get(0).latitude, zone.coordinates.get(0).longitude));

        mMap.getOverlayManager().add(0, zoneOverlay);
        return zoneOverlay;
    }

    @Override
    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void setPosition(LatLng position) {
        lastPosition = position;
    }

    public void onEvent(MapAttachedEvent event) {
        mMap = fragment.getMapView();
        setUpMap();
    }

    @Override
    public void setMarkers(Iterable<EntryMapMarker> newMapMarkers) {
        boolean needInvalidate = false;
        Set<MarkerHolder> toDelete = new HashSet<>(this.markers);
        MarkerHolder testHolder = new MarkerHolder(null, null);

        for (EntryMapMarker mapMarker : newMapMarkers) {
            testHolder.mapMarker = mapMarker;
            toDelete.remove(testHolder);
//            if (this.markers.contains(testHolder)) continue;
            this.markers.add(new MarkerHolder(mapMarker, addMarker(mapMarker, true)));
            needInvalidate = true;
        }

        for (MarkerHolder markerHolder : toDelete) {
            this.markers.remove(markerHolder);
            if (mMap != null && markerHolder.marker != null) {
                markerHolder.marker.remove(mMap);
                needInvalidate = true;
            }
        }
        if (needInvalidate && mMap != null) mMap.invalidate();
    }

    @Override
    public void setPath(ArrayList<LatLng> points) {
        this.points = points;
    }

    @Override
    public void setZones(Iterable<Zone> zones) {
        this.zones.clear();
//        boolean needInvalidate = false;
        // we know that zones are not changing, so we just display all zones
        for (Zone zone : zones) {
            this.zones.add(zone);
//            needInvalidate = true;
        }
        drawZones();
//        if (needInvalidate && mMap != null) mMap.invalidate();
    }

    @Override
    public void updatePath(ArrayList<LatLng> points) {
        this.points = points;
        pathOverlay.setPoints(new ArrayList<>());
        if (points != null && !points.isEmpty()) {
            for (LatLng point : points) {
                pathOverlay.addPoint(new GeoPoint(point.latitude, point.longitude));
            }
        }
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint geoPoint) {
        InfoWindow.closeAllInfoWindowsOn(mMap);
        eventBus.post(new MapClickedEvent(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude())));
        return true;
    }

    @Override
    public boolean longPressHelper(GeoPoint geoPoint) {
        InfoWindow.closeAllInfoWindowsOn(mMap);
        eventBus.post(new MapLongClickedEvent(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude())));
        return true;
    }

    @Override
    public void setLifeCycle(Lifecycle lifecycle) {
        lifecycle.addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onResume(@NonNull LifecycleOwner owner) {
                if (mMap == null) {
                    return;
                }
                mMap.onResume();
            }

            @Override
            public void onPause(@NonNull LifecycleOwner owner) {
                if (mMap == null) {
                    return;
                }
                mMap.onPause();
            }
        });
    }

    @Override
    public void setShowBgAtlasCells(boolean showBgAtlasCells) {
        this.showBgAtlasCells = showBgAtlasCells;
        drawBgAtlasCells();
    }

    @Override
    public void setBgAtlasCells(List<BGAtlasCell> cells) {
        this.atlasCells.clear();
        if (cells != null) {
            for (BGAtlasCell cell : cells) {
                this.atlasCells.add(cell);
            }
        }
        drawBgAtlasCells();
    }

    @Override
    public void setShowSPA(boolean showSPA) {
        // TODO to be implemented
    }

    @Override
    public void setShowRandomCells(boolean showRandomCells) {
        // TODO to be implemented
    }

    @Override
    public void setShowGrid1km(boolean showGrid1km) {
        // TODO to be implemented
    }

    @Override
    public void setShowGrid10km(boolean showGrid10km) {
        // TODO to be implemented
    }

    private static class EventsOverlay extends Overlay {

        @Nullable
        private MapEventsReceiver receiver;
        private boolean locked;

        public EventsOverlay(@NonNull Context ctx, @NonNull MapEventsReceiver receiver) {
            super(ctx);
            this.receiver = receiver;
        }

        public boolean isLocked() {
            return locked;
        }

        public void setLocked(boolean locked) {
            this.locked = locked;
        }

        @Override
        public void draw(Canvas c, MapView osmv, boolean shadow) {
        }

        @Override
        public boolean onFling(MotionEvent pEvent1, MotionEvent pEvent2, float pVelocityX, float pVelocityY, MapView pMapView) {
            return locked || super.onFling(pEvent1, pEvent2, pVelocityX, pVelocityY, pMapView);
        }

        @Override
        public boolean onScroll(MotionEvent pEvent1, MotionEvent pEvent2, float pDistanceX, float pDistanceY, MapView pMapView) {
            return locked || super.onScroll(pEvent1, pEvent2, pDistanceX, pDistanceY, pMapView);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e, MapView mapView) {
            return locked || super.onDoubleTap(e, mapView);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e, MapView mapView) {
            return locked || super.onDoubleTapEvent(e, mapView);
        }

        @Override
        public boolean onDown(MotionEvent e, MapView mapView) {
            return locked || super.onDown(e, mapView);
        }

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event, MapView mapView) {
            return locked || super.onKeyDown(keyCode, event, mapView);
        }

        @Override
        public boolean onKeyUp(int keyCode, KeyEvent event, MapView mapView) {
            return locked || super.onKeyUp(keyCode, event, mapView);
        }

        @Override
        public boolean onLongPress(MotionEvent e, MapView mapView) {
            if (receiver == null) return false;
            Projection proj = mapView.getProjection();
            GeoPoint p = (GeoPoint) proj.fromPixels((int) e.getX(), (int) e.getY());
            return receiver.longPressHelper(p);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
            if (receiver == null) return false;
            Projection proj = mapView.getProjection();
            GeoPoint p = (GeoPoint) proj.fromPixels((int) e.getX(), (int) e.getY());
            return receiver.singleTapConfirmedHelper(p);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e, MapView mapView) {
            return locked;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event, MapView mapView) {
            return super.onTouchEvent(event, mapView);
        }

        @Override
        public boolean onTrackballEvent(MotionEvent event, MapView mapView) {
            return locked || super.onTrackballEvent(event, mapView);
        }

        @Override
        public void onDetach(MapView mapView) {
            super.onDetach(mapView);
            receiver = null;
        }

    }

    private static class LocationRetrievalOverlay extends MyLocationNewOverlay {

        @Nullable
        private EEventBus bus;

        public LocationRetrievalOverlay(MapView mapView) {
            super(mapView);
            bus = EEventBus_.getInstance_(mapView.getContext());
        }

        @Override
        public void onLocationChanged(Location location, IMyLocationProvider source) {
            super.onLocationChanged(location, source);
            if (bus != null) {
                bus.post(new LocationChangedEvent(location));
            }
        }

        @Override
        public void onDetach(MapView mapView) {
            super.onDetach(mapView);
            bus = null;
        }
    }

    static class MarkerHolder {
        EntryMapMarker mapMarker;
        Marker marker;

        public MarkerHolder(EntryMapMarker mapMarker, Marker marker) {
            this.mapMarker = mapMarker;
            this.marker = marker;
        }

        @Override
        public int hashCode() {
            return mapMarker.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;

            if (obj instanceof MarkerHolder) {
                return mapMarker.equals(((MarkerHolder) obj).mapMarker);
            }
            return mapMarker.equals((obj));
        }
    }

    @Override
    public void clearPositioned() {
        positioned = false;
    }
}
