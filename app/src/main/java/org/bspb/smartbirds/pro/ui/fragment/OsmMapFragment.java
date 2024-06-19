package org.bspb.smartbirds.pro.ui.fragment;

import androidx.fragment.app.Fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.MapAttachedEvent;
import org.osmdroid.views.MapView;

/**
 * Created by dani on 14-11-4.
 */
@EFragment(R.layout.fragment_map_osm)
public class OsmMapFragment extends Fragment {

    @ViewById(R.id.mapview)
    MapView mapView;

    EEventBus eventBus = EEventBus.getInstance();

    public MapView getMapView() {
        return mapView;
    }

    @AfterViews
    public void init() {
        eventBus.post(new MapAttachedEvent());
    }
}
