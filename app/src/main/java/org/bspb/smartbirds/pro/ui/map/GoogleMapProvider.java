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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.bspb.smartbirds.pro.R;
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
import java.util.List;

/**
 * Created by dani on 14-11-6.
 */
@EBean
public class GoogleMapProvider implements MapProvider, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private FragmentManager fragmentManager;
    private BspbMapFragment_ fragment;
    private double zoomFactor;
    private LatLng lastPosition;

    @Bean
    EEventBus eventBus;
    private ArrayList<MapMarker> markers;
    private ArrayList<LatLng> points;
    private Polyline path;
    private boolean positioned = false;
    private Marker lastMarker;

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
            mMap = fragment.getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        addScaleBar();

        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
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

        if (markers != null && !markers.isEmpty()) {
            for (MapMarker marker : markers) {
                addMarker(marker);
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
    public void drawPath(List<LatLng> points) {

    }

    @Override
    public void showMap() {
        positioned = false;
        if (fragment == null) {
            fragment = new BspbMapFragment_();
        }

        FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
        fragTransaction.replace(R.id.map_container, fragment, "google");
        fragTransaction.commit();
    }

    @Override
    public void updateCamera() {
        if (zoomFactor > 0) {
            mMap.getUiSettings().setAllGesturesEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(false);

            if (lastPosition == null) return;

            LatLng southwest = SphericalUtil.computeOffset(lastPosition, zoomFactor, 225);
            LatLng northeast = SphericalUtil.computeOffset(lastPosition, zoomFactor, 45);
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(southwest, northeast), 16));
            positioned = true;
        } else {
            mMap.getUiSettings().setAllGesturesEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            if (!positioned && lastPosition != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastPosition, 16));
                positioned = true;
            }
        }
    }

    @Override
    public void setMapType(MapType mapType) {
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

    @Override
    public Location getMyLocation() {
        return mMap.getMyLocation();
    }

    @Override
    public void addMarker(MapMarker marker) {
        if (mMap == null) return;
        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(marker.getLatitude(), marker.getLongitude())).title(marker.getTitle());
        lastMarker = mMap.addMarker(markerOptions);
    }

    @Override
    public void removeLastMarker() {
        if (lastMarker != null) {
            lastMarker.remove();
            lastMarker = null;
        }
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
    public void setMarkers(ArrayList<MapMarker> markers) {
        this.markers = markers;
    }

    @Override
    public void setPath(ArrayList<LatLng> points) {
        this.points = points;
    }

    @Override
    public void updatePath(ArrayList<LatLng> points) {
        this.points = points;
        path.setPoints(points);
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

        FrameLayout scaleBarContainer = new FrameLayout(fragment.getContext());
        ScaleBar scaleBar = new ScaleBar(fragment.getActivity(), mMap);

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
}
