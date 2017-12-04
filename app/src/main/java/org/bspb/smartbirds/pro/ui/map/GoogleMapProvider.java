package org.bspb.smartbirds.pro.ui.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.backend.dto.Zone;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.LocationChangedEvent;
import org.bspb.smartbirds.pro.events.MapAttachedEvent;
import org.bspb.smartbirds.pro.events.MapClickedEvent;
import org.bspb.smartbirds.pro.events.MapDetachedEvent;
import org.bspb.smartbirds.pro.events.MapLongClickedEvent;
import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.bonuspack.kml.KmlFeature;
import org.osmdroid.bonuspack.kml.KmlFolder;
import org.osmdroid.bonuspack.kml.KmlGeometry;
import org.osmdroid.bonuspack.kml.KmlLineString;
import org.osmdroid.bonuspack.kml.KmlMultiGeometry;
import org.osmdroid.bonuspack.kml.KmlPlacemark;
import org.osmdroid.bonuspack.kml.KmlPolygon;
import org.osmdroid.bonuspack.kml.Style;
import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.bspb.smartbirds.pro.tools.Reporting.logException;

/**
 * Created by dani on 14-11-6.
 */
@EBean
public class GoogleMapProvider implements MapProvider, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private static final String TAG = SmartBirdsApplication.TAG + ".GMap";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private FragmentManager fragmentManager;
    private BspbMapFragment fragment;
    private double zoomFactor;
    private LatLng lastPosition;

    @Bean
    EEventBus eventBus;
    private final Map<Long, MarkerHolder> markers = new HashMap<>();
    private ArrayList<LatLng> points;
    private Polyline path;
    private boolean positioned = false;
    private Marker lastMarker;
    private MapType mapType;
    private ArrayList<Zone> zones = new ArrayList<>();
    private boolean showZoneBackground;
    private List<Polygon> zonePolygons = new ArrayList<>();
    private MarkerClickListener markerClickListener;

    @Override
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
     * stopped or paused), {@link #onCreate(android.os.Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            fragment.getMapAsync(this);
        }
    }

    private void setUpMap() {
        addScaleBar();

        setMapType(mapType);
        mMap.setOnMapClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        if (ActivityCompat.checkSelfPermission(fragment.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        mMap.setBuildingsEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setTrafficEnabled(false);
        path = mMap.addPolyline(new PolylineOptions()
                .addAll(points)
                .width(5)
                .color(Color.BLUE)
                .geodesic(true)
        );
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                eventBus.post(new LocationChangedEvent(location));
            }
        });

        for (MarkerHolder markerHolder : markers.values()) {
            if (markerHolder.marker == null) {
                markerHolder.marker = addMarker(markerHolder.mapMarker);
            }
        }
        if (zonePolygons.isEmpty()) {
            for (Zone zone : zones) {
                Polygon p = addZone(zone);
                if (p != null) {
                    zonePolygons.add(p);
                }
            }
        }

        drawArea();
        fragment.getView().post(new Runnable() {
            @Override
            public void run() {
                updateCamera();
            }
        });

    }

    @Override
    public void setZoomFactor(double zoomFactor) {
        this.zoomFactor = zoomFactor;
    }

    @Override
    public void setShowZoneBackground(boolean showBackground) {
        this.showZoneBackground = showBackground;
        drawZones();
    }

    private void drawZones() {
        for (Polygon p : zonePolygons) {
            if (p.isVisible()) {
                p.remove();
            }
        }
        zonePolygons.clear();
        for (Zone zone : zones) {
            Polygon p = addZone(zone);
            if (p != null) {
                zonePolygons.add(p);
            }
        }
    }

    @Override
    public void drawPath(List<LatLng> points) {

    }

    @Override
    public void showMap() {
        positioned = false;
        if (fragment == null) {
            fragment = BspbMapFragment_.builder().build();
        }

        FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
        fragTransaction.replace(R.id.map_container, fragment, "google");
        fragTransaction.commit();
    }

    @Override
    public void updateCamera() {
        if (mMap == null) return;
        try {
            if (zoomFactor > 0) {
                mMap.getUiSettings().setAllGesturesEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.getUiSettings().setZoomControlsEnabled(false);

                if (lastPosition == null) return;

                LatLng southwest = SphericalUtil.computeOffset(lastPosition, zoomFactor, 225);
                LatLng northeast = SphericalUtil.computeOffset(lastPosition, zoomFactor, 45);
                CameraUpdate cameraUpdate;
                if (southwest.equals(northeast)) {
                    cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lastPosition.latitude, lastPosition.longitude), 16);
                } else {
                    cameraUpdate = CameraUpdateFactory.newLatLngBounds(new LatLngBounds(southwest, northeast), 16);
                }
                if (!positioned) {
                    positioned = true;
                    mMap.animateCamera(cameraUpdate);
                } else {
                    mMap.moveCamera(cameraUpdate);
                }
            } else {
                mMap.getUiSettings().setAllGesturesEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                if (!positioned && lastPosition != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastPosition, 16));
                    positioned = true;
                }
            }
        } catch (Throwable t) {
            logException(t);
        }
    }

    @Override
    public void setMapType(MapType mapType) {
        this.mapType = mapType;
        if (mMap != null) {
            switch (mapType) {
                case NORMAL:
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    break;
                case SATELLITE:
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    break;
                case HYBRID:
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    break;
                default:
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    break;
            }
        }
    }

    @Override
    public Location getMyLocation() {
        return mMap.getMyLocation();
    }

    private Marker addMarker(MapMarker newMapMarker) {
        if (mMap == null) return null;
        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(newMapMarker.getLatitude(), newMapMarker.getLongitude())).title(newMapMarker.getTitle());
        lastMarker = mMap.addMarker(markerOptions);
        lastMarker.setTag(newMapMarker.getId());
        return lastMarker;
    }

    private Polygon addZone(Zone zone) {
        if (mMap == null) return null;

        PolygonOptions polygonOptions = new PolygonOptions();
        for (Zone.Coordinate coord : zone.coordinates) {
            polygonOptions.add(new LatLng(coord.latitude, coord.longitude));
        }

        polygonOptions.strokeColor(fragment.getContext().getResources().getColor(R.color.zone_stroke_color));
        if (showZoneBackground) {
            polygonOptions.fillColor(fragment.getContext().getResources().getColor(R.color.zone_fill_color));
        }

        polygonOptions.strokeWidth(6f);
        return mMap.addPolygon(polygonOptions);
    }

    @Override
    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void setPosition(LatLng position) {
        lastPosition = position;
    }

    @Override
    public void setMarkers(Iterable<MapMarker> newMapMarkers) {
        Set<MarkerHolder> currentMarkers = new HashSet<>(this.markers.values());
        Set<MarkerHolder> markerHoldersToDelete = new HashSet<>(this.markers.values());
        Set<Long> idsToDelete = new HashSet<>(this.markers.keySet());

        MarkerHolder testHolder = new MarkerHolder(null, null);
        int cnt = 0;
        for (MapMarker mapMarker : newMapMarkers) {
            cnt++;

            testHolder.mapMarker = mapMarker;
            markerHoldersToDelete.remove(testHolder);
            idsToDelete.remove(mapMarker.getId());
            if (currentMarkers.contains(testHolder)) continue;
            this.markers.put(mapMarker.getId(), new MarkerHolder(mapMarker, addMarker(mapMarker)));
        }

        for (Long id : idsToDelete) {
            this.markers.remove(id);
        }

        for (MarkerHolder markerHolder : markerHoldersToDelete) {
            if (markerHolder.marker != null) {
                markerHolder.marker.remove();
            }
        }
    }

    @Override
    public void setPath(ArrayList<LatLng> points) {
        this.points = points;
    }

    @Override
    public void setZones(Iterable<Zone> zones) {
        this.zones.clear();
        for (Zone zone : zones) {
            this.zones.add(zone);
        }
        drawZones();
    }

    @Override
    public void updatePath(ArrayList<LatLng> points) {
        this.points = points;
        path.setPoints(points);
    }

    @Override
    public void setOnMarkerClickListener(MarkerClickListener listener) {
        this.markerClickListener = listener;
    }

    @Background
    void drawArea() {
        KmlDocument kml = new KmlDocument();
        File file = new File(AREA_FILE_PATH);
        if (!file.exists()) {
            return;
        }
        try {
            kml.parseKMLFile(file);
            if (kml.mKmlRoot != null && kml.mKmlRoot.mItems != null && !kml.mKmlRoot.mItems.isEmpty()) {
                displayArea(kml);
            }
        } catch (Throwable t) {
            Crashlytics.logException(t);
        }
    }

    @UiThread
    void displayArea(KmlDocument kml) {
        // sometimes map is null
        if (mMap == null) {
            return;
        }
        displayItem(kml, kml.mKmlRoot);
    }

    void displayItem(KmlDocument kml, KmlFeature item) {
        if (item instanceof KmlFolder) {
            KmlFolder folder = (KmlFolder) item;
            for (KmlFeature subitem : folder.mItems) {
                displayItem(kml, subitem);
            }
        } else if (item instanceof KmlPlacemark) {
            KmlPlacemark placemark = (KmlPlacemark) item;
            Style style = kml.getStyle(placemark.mStyle);

            displayGeometry(placemark.mGeometry, style);
        }
    }

    private void displayGeometry(KmlGeometry geometry, Style style) {
        if (geometry instanceof KmlPolygon || geometry instanceof KmlLineString) {
            ArrayList<GeoPoint> geopoints = geometry.mCoordinates;
            ArrayList<LatLng> points = new ArrayList<LatLng>();
            for (GeoPoint point : geopoints) {
                points.add(new LatLng(point.getLatitude(), point.getLongitude()));
            }
            PolygonOptions polygonOptions = new PolygonOptions();
            polygonOptions.addAll(points);

            if (style != null) {
                Paint paint = style.getOutlinePaint();
                polygonOptions.strokeColor(paint.getColor());
                polygonOptions.strokeWidth(Math.max(1f, paint.getStrokeWidth()));
                if (style.mPolyStyle != null) {
                    polygonOptions.fillColor(style.mPolyStyle.getFinalColor());
                }
            }

            mMap.addPolygon(polygonOptions);

        } else if (geometry instanceof KmlMultiGeometry) {
            KmlMultiGeometry multiGeometry = (KmlMultiGeometry) geometry;
            for (KmlGeometry subGeometry : multiGeometry.mItems) {
                displayGeometry(subGeometry, style);
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        eventBus.post(new MapClickedEvent(latLng));
    }

    public void onEvent(MapAttachedEvent event) {
        setUpMapIfNeeded();
    }

    public void onEvent(MapDetachedEvent event) {
        mMap = null;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        eventBus.post(new MapLongClickedEvent(latLng));
    }

    private void addScaleBar() {
        View mapContainer = fragment.getView();
        ScaleBar scaleBar = (ScaleBar) mapContainer.findViewById(R.id.scale_bar);
        if (scaleBar != null) return;

        FrameLayout scaleBarContainer = new FrameLayout(fragment.getContext());
        scaleBar = new ScaleBar(fragment.getActivity(), mMap);
        scaleBar.setId(R.id.scale_bar);

        FrameLayout.LayoutParams scaleBarContainerParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) scaleBar.getViewHeight());
        scaleBarContainerParams.gravity = Gravity.TOP;

        scaleBarContainer.setLayoutParams(scaleBarContainerParams);

        FrameLayout.LayoutParams scaleBarParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        scaleBarParams.gravity = Gravity.CENTER;

        scaleBar.setLayoutParams(scaleBarParams);
        scaleBarContainer.addView(scaleBar);

        if (mapContainer instanceof FrameLayout) {
            ((FrameLayout) mapContainer).addView(scaleBarContainer);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Check if we were successful in obtaining the map.
        if (mMap != null) {
            setUpMap();
        } else {
            fragment.getMapAsync(this);
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (markerClickListener != null) {
            markerClickListener.onMarkerClicked(markers.get(marker.getTag()).mapMarker.getId(), markers.get(marker.getTag()).mapMarker.getEntryType());
        }
    }

    static class MarkerHolder {
        MapMarker mapMarker;
        Marker marker;

        public MarkerHolder(MapMarker mapMarker, Marker marker) {
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
}
