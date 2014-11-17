package org.bspb.smartbirds.pro.ui.map;

import com.google.android.gms.maps.SupportMapFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.MapAttachedEvent;
import org.bspb.smartbirds.pro.events.MapDetachedEvent;

/**
 * Created by dani on 14-11-17.
 */
@EFragment
public class BspbMapFragment extends SupportMapFragment {

    @Bean
    EEventBus eventBus;

    @AfterViews
    public void init() {
        eventBus.post(new MapAttachedEvent());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        eventBus.post(new MapDetachedEvent());
    }
}
