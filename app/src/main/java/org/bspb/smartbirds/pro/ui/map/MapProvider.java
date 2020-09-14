package org.bspb.smartbirds.pro.ui.map;

import android.location.Location;
import android.os.Environment;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import com.google.android.gms.maps.model.LatLng;

import org.bspb.smartbirds.pro.backend.dto.BGAtlasCell;
import org.bspb.smartbirds.pro.backend.dto.Zone;
import org.bspb.smartbirds.pro.enums.EntryType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dani on 14-11-4.
 */
public interface MapProvider {

    interface MarkerClickListener {
        void onMarkerClicked(long entryId, EntryType type);
    }

    public static final String AREA_FILE_PATH = Environment.getExternalStorageDirectory() + "/smartbirdspro/area.kml";

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

    void setMarkers(Iterable<EntryMapMarker> markers);

    void setPath(ArrayList<LatLng> points);

    void setZones(Iterable<Zone> zones);

    void updatePath(ArrayList<LatLng> points);

    void setShowZoneBackground(boolean showBackground);

    void setOnMarkerClickListener(MarkerClickListener listener);

    void setShowLocalProjects(boolean showLocalProjects);

    void setLifeCycle(Lifecycle lifecycle);

    void setShowBgAtlasCells(boolean showBgAtlasCells);

    void setBgAtlasCells(List<BGAtlasCell> cells);
}
