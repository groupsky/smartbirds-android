package org.bspb.smartbirds.pro.ui.map

import android.os.Bundle
import android.view.View
import com.google.android.gms.maps.SupportMapFragment
import org.bspb.smartbirds.pro.events.EEventBus
import org.bspb.smartbirds.pro.events.MapAttachedEvent
import org.bspb.smartbirds.pro.events.MapDetachedEvent

class BspbMapFragment : SupportMapFragment() {

    private val eventBus: EEventBus = EEventBus.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventBus.post(MapAttachedEvent())
    }

    override fun onDetach() {
        super.onDetach()
        eventBus.post(MapDetachedEvent())
    }
}