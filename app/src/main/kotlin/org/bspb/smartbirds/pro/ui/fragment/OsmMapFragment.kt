package org.bspb.smartbirds.pro.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.events.EEventBus
import org.bspb.smartbirds.pro.events.MapAttachedEvent
import org.osmdroid.views.MapView

class OsmMapFragment : Fragment() {

    var mapView: MapView? = null
        private set
    private var eventBus: EEventBus = EEventBus.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState) ?: inflater.inflate(
            R.layout.fragment_map_osm,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = view.findViewById(R.id.mapview)
        eventBus.post(MapAttachedEvent())
    }
}