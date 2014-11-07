package org.bspb.smartbirds.pro.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.MapAttachedEvent;
import org.osmdroid.views.MapView;

import de.greenrobot.event.EventBus;

/**
 * Created by dani on 14-11-4.
 */
@EFragment(R.layout.fragment_map_osm)
public class OsmMapFragment extends Fragment {

    @ViewById(R.id.mapview)
    MapView mapView;

    @Bean
    EEventBus eventBus;

    public MapView getMapView() {
        return mapView;
    }

    @AfterViews
    public void init() {
        eventBus.post(new MapAttachedEvent());
    }
}
