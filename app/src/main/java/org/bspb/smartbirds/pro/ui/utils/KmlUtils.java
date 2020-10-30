package org.bspb.smartbirds.pro.ui.utils;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import org.bspb.smartbirds.pro.tools.Reporting;
import org.bspb.smartbirds.pro.ui.map.SimpleMapMarker;
import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.bonuspack.kml.KmlFeature;
import org.osmdroid.bonuspack.kml.KmlFolder;
import org.osmdroid.bonuspack.kml.KmlPlacemark;
import org.osmdroid.bonuspack.kml.KmlPoint;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class KmlUtils {

    public static List<SimpleMapMarker> readLocalProjectsPointsFromKml(@NonNull Context context) {
        List<SimpleMapMarker> points = new ArrayList<>();

        try {
            KmlDocument kml = readKmlFromAssets(context, Configuration.LOCAL_PROJECTS_KML_FILE);
            if (kml.mKmlRoot != null && kml.mKmlRoot.mItems != null && !kml.mKmlRoot.mItems.isEmpty()) {
                KmlFolder root = (KmlFolder) kml.mKmlRoot.mItems.get(0);
                for (KmlFeature item : root.mItems) {
                    KmlPlacemark placemark = (KmlPlacemark) item;
                    String name = placemark.mName;
                    KmlPoint point = (KmlPoint) placemark.mGeometry;
                    GeoPoint coordinates = point.mCoordinates.get(0);
                    points.add(new SimpleMapMarker(name, coordinates.getLatitude(), coordinates.getLongitude()));
                }
            }
        } catch (Throwable t) {
            Reporting.logException(t);
        }

        return points;

    }

    public static KmlDocument readKmlFromAssets(@NonNull Context context, @NonNull String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }

        KmlDocument kml = new KmlDocument();
        try {
            kml.parseKMLStream(context.getAssets().open(Configuration.LOCAL_PROJECTS_KML_FILE), null);
        } catch (Throwable t) {
            Reporting.logException(t);
        }
        return kml;
    }

}
