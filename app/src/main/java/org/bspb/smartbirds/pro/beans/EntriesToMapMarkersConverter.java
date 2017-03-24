package org.bspb.smartbirds.pro.beans;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.StringRes;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.collections.Converter;
import org.bspb.smartbirds.pro.content.MonitoringEntry;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.ui.map.MapMarker;

/**
 * Created by groupsky on 20.03.17.
 */
@EBean(scope = EBean.Scope.Singleton)
public class EntriesToMapMarkersConverter implements Converter<MonitoringEntry, MapMarker> {

    @StringRes(R.string.tag_lat)
    protected String tagLatitude;

    @StringRes(R.string.tag_lon)
    protected String tagLongitude;

    @StringRes(R.string.entry_type_ciconia)
    protected String entryTypeCiconia;

    @StringRes(R.string.tag_species_scientific_name)
    protected String tagSpecies1;

    @StringRes(R.string.tag_observed_bird)
    protected String tagSpecies2;

    @Override
    public MapMarker convert(MonitoringEntry item) {
        double lat = Double.valueOf(item.data.get(tagLatitude));
        double lon = Double.valueOf(item.data.get(tagLongitude));
        String name = null;

        if (EntryType.CICONIA.equals(item.type)) {
            name = entryTypeCiconia;
        } else {
            if (item.data.containsKey(tagSpecies1)) {
                name = item.data.get(tagSpecies1);
            } else if (item.data.containsKey(tagSpecies2)) {
                name = item.data.get(tagSpecies2);
            }
        }
        return new MapMarker(name, lat, lon);
    }
}