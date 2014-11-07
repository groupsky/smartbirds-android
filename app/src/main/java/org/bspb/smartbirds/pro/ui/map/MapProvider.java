package org.bspb.smartbirds.pro.ui.map;

import android.location.Location;
import android.support.v4.app.FragmentManager;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dani on 14-11-4.
 */
public interface MapProvider {

    public enum ProviderType {
        GOOGLE, OSM;
    }

    public enum MapType {
        NORMAL, SATELLITE, HYBRID;
    }

    void setUpMapIfNeeded();

    void setZoomFactor(double zoomFactor);

    void drawPath(List<LatLng> points);

    void showMap();

    void updateCamera();

    void setMapType(MapType mapType);

    Location getMyLocation();

    void setFragmentManager(FragmentManager fragmentManager);

    void setPosition(LatLng position);

    void addMarker(MapMarker marker);

    void setMarkers(ArrayList<MapMarker> markers);

    void setPath(ArrayList<LatLng> points);

    void updatePath(ArrayList<LatLng> points);
}
