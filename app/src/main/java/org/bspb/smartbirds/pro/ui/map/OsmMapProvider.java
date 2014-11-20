package org.bspb.smartbirds.pro.ui.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.LocationChangedEvent;
import org.bspb.smartbirds.pro.events.MapAttachedEvent;
import org.bspb.smartbirds.pro.events.MapClickedEvent;
import org.bspb.smartbirds.pro.events.MapLongClickedEvent;
import org.bspb.smartbirds.pro.ui.fragment.OsmMapFragment_;
import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.bonuspack.overlays.FolderOverlay;
import org.osmdroid.bonuspack.overlays.InfoWindow;
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dani on 14-11-6.
 */
@EBean
public class OsmMapProvider implements MapProvider, MapEventsReceiver {

    private FragmentManager fragmentManager;
    private OsmMapFragment_ fragment;
    private MapView mMap;

    private double zoomFactor;
    private LatLng lastPosition;

    @Bean
    EEventBus eventBus;
    private ItemizedIconOverlay<OverlayItem> markersOverlay;
    private ArrayList<MapMarker> markers;
    private ArrayList<LatLng> points;
    private PathOverlay pathOverlay;

    boolean locked = true;
    private MyLocationNewOverlay locationOverlay;

    private boolean positioned = false;
    private Marker lastMarker;

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
        mMap.setMultiTouchControls(true);
        mMap.setTileSource(TileSourceFactory.MAPQUESTOSM);
        mMap.setBuiltInZoomControls(true);
        mMap.getController().setZoom(16);

        locationOverlay = new MyLocationNewOverlay(fragment.getActivity(), mMap) {
            @Override
            public void onLocationChanged(Location location, IMyLocationProvider source) {
                super.onLocationChanged(location, source);
                eventBus.post(new LocationChangedEvent(location));
            }
        };
        locationOverlay.enableMyLocation();
        locationOverlay.enableFollowLocation();


        locationOverlay.setDrawAccuracyEnabled(true);

        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(fragment.getActivity());
        scaleBarOverlay.setUnitsOfMeasure(ScaleBarOverlay.UnitsOfMeasure.metric);
        scaleBarOverlay.setEnableAdjustLength(true);

        pathOverlay = new PathOverlay(Color.BLUE, mMap.getContext());
        pathOverlay.getPaint().setStrokeWidth(5);
        if (points != null && !points.isEmpty()) {
            for (LatLng point : points) {
                pathOverlay.addPoint(new GeoPoint(point.latitude, point.longitude));
            }

        }

        MapEventsOverlay eventsOverlay = new MapEventsOverlay(mMap.getContext(), this);


        mMap.getOverlayManager().clear();
        mMap.getOverlayManager().add(pathOverlay);
        mMap.getOverlayManager().add(locationOverlay);
        mMap.getOverlayManager().add(scaleBarOverlay);
        mMap.getOverlayManager().add(eventsOverlay);
        mMap.getOverlayManager().add(new TouchEventsOverlay(mMap.getContext()));

        KmlDocument kml = new KmlDocument();
        File file = new File(AREA_FILE_PATH);
        if (file.exists()) {
            kml.parseKMLFile(file);
            FolderOverlay kmlOverlay = (FolderOverlay) kml.mKmlRoot.buildOverlay(mMap, null, null, kml);
            mMap.getOverlayManager().add(kmlOverlay);
        }

        if (markers != null && !markers.isEmpty()) {
            for (MapMarker mapMarker : markers) {
                addMarker(mapMarker);
            }
        }


        updateCamera();
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
            fragment = new OsmMapFragment_();
        }

        FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
        fragTransaction.replace(R.id.map_container, fragment, "osm");
        fragTransaction.commit();

        mMap = fragment.getMapView();
    }

    @Override
    public void updateCamera() {
        if (zoomFactor > 0) {
            mMap.setBuiltInZoomControls(false);
            mMap.setMultiTouchControls(false);
            locationOverlay.enableFollowLocation();
            locked = true;

            if (lastPosition == null) return;

            LatLng southwest = SphericalUtil.computeOffset(lastPosition, zoomFactor, 225);
            LatLng northeast = SphericalUtil.computeOffset(lastPosition, zoomFactor, 45);

            ArrayList<GeoPoint> geoPoints = new ArrayList<GeoPoint>();
            geoPoints.add(new GeoPoint(southwest.latitude, southwest.longitude));
            geoPoints.add(new GeoPoint(northeast.latitude, northeast.longitude));
            final BoundingBoxE6 boundingBox = BoundingBoxE6.fromGeoPoints(geoPoints);

            mMap.post(new Runnable() {
                @Override
                public void run() {
                    mMap.getController().setCenter(boundingBox.getCenter());
                    mMap.getController().zoomToSpan(boundingBox.getLatitudeSpanE6(), boundingBox.getLongitudeSpanE6());
                }
            });
            mMap.invalidate();
            positioned = false;
        } else {
            mMap.setMultiTouchControls(true);
            mMap.setBuiltInZoomControls(true);
            locationOverlay.disableFollowLocation();
            locked = false;
            if (!positioned && lastPosition != null) {
                mMap.getController().animateTo(new GeoPoint(lastPosition.latitude, lastPosition.longitude));
                positioned = true;
            }
        }

    }

    @Override
    public void setMapType(MapType mapType) {
//        switch (mapType) {
//            case NORMAL:
//                break;
//            case SATELLITE:
//                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//                break;
//            case HYBRID:
//                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//                break;
//            default:
//                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//                break;
//        }
    }

    @Override
    public Location getMyLocation() {
        Location location = new Location("myLocation");
        location.setLatitude(locationOverlay.getMyLocation().getLatitude());
        location.setLongitude(locationOverlay.getMyLocation().getLongitude());
        return location;
    }


    @Override
    public void addMarker(MapMarker mapMarker) {
        Marker marker = lastMarker = new Marker(mMap);
        marker.setPosition(new GeoPoint(mapMarker.getLatitude(), mapMarker.getLongitude()));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(mapMarker.getTitle());
        marker.setPanToView(false);
        mMap.getOverlays().add(marker);
        mMap.invalidate();
    }

    @Override
    public void removeLastMarker() {
        if (lastMarker != null) {
            mMap.getOverlays().remove(lastMarker);
            lastMarker = null;
            mMap.invalidate();
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

    public void onEvent(MapAttachedEvent event) {
        mMap = fragment.getMapView();
        setUpMap();
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
        pathOverlay.clearPath();
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

    public class TouchEventsOverlay extends Overlay {

        public TouchEventsOverlay(Context ctx) {
            super(ctx);
        }

        @Override
        protected void draw(Canvas c, MapView osmv, boolean shadow) {
        }

        @Override
        public boolean onDoubleTap(MotionEvent e, MapView mapView) {
            return locked;
        }

        @Override
        public boolean onScroll(MotionEvent pEvent1, MotionEvent pEvent2, float pDistanceX, float pDistanceY, MapView pMapView) {
            return locked;
        }

    }
}
