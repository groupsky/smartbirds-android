package org.bspb.smartbirds.pro.ui.map;

import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import org.bspb.smartbirds.pro.R;
import org.osmdroid.bonuspack.overlays.MarkerInfoWindow;
import org.osmdroid.views.MapView;

/**
 * Created by dani on 27/11/17.
 */

public class OsmInfoWindow extends MarkerInfoWindow {

    interface OsmInfoWindowClickListener {
        void onClick(View view);
    }

    private OsmInfoWindowClickListener clickListener;

    public OsmInfoWindow(int layoutResId, MapView mapView) {
        super(layoutResId, mapView);

        // remove the touch listener defined in BasicInfoWindow
        this.mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
    }

    @Override
    public void onOpen(Object o) {
        super.onOpen(o);
        LinearLayout layout = (LinearLayout) mView.findViewById(R.id.bubble_layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) {
                    clickListener.onClick(view);
                    close();
                }
            }
        });
    }

    @Override
    public void onClose() {
    }

    public void setClickListener(OsmInfoWindowClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
