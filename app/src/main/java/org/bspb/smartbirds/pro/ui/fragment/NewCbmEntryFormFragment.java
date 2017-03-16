package org.bspb.smartbirds.pro.ui.fragment;

import android.app.Fragment;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.view.View;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.backend.dto.Zone;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.prefs.CbmPrefs_;
import org.bspb.smartbirds.pro.ui.utils.Configuration;
import org.bspb.smartbirds.pro.ui.views.SingleChoiceFormInput;
import org.bspb.smartbirds.pro.ui.views.ZoneFormInput;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by dani on 14-11-11.
 */
@EFragment(R.layout.fragment_monitoring_form_new_cbm_entry)
public class NewCbmEntryFormFragment extends BaseEntryFragment {

    private static final String TAG = SmartBirdsApplication.TAG + ".fCBM";

    @ViewById(R.id.form_cbm_primary_habitat)
    SingleChoiceFormInput primaryHabitatInput;

    @ViewById(R.id.form_cbm_secondary_habitat)
    SingleChoiceFormInput secondaryHabitatInput;

    @ViewById(R.id.form_cbm_visit_number)
    SingleChoiceFormInput visitInput;

    @ViewById(R.id.form_cbm_sector)
    SingleChoiceFormInput sectorInput;

    @ViewById(R.id.form_cbm_zone)
    ZoneFormInput zoneInput;

    @ViewById(R.id.error_cbm_too_far)
    View errorCbmTooFarView;

    @Pref
    CbmPrefs_ prefs;

    @FragmentById(R.id.pictures_fragment)
    NewEntryPicturesFragment picturesFragment;
    private HashMap<String, String> pendingDeserialize;

    @Override
    protected EntryType getEntryType() {
        return EntryType.CBM;
    }

    @Override
    public void onResume() {
        super.onResume();
        primaryHabitatInput.setText(prefs.cbmPrimaryHabitat().get());
        secondaryHabitatInput.setText(prefs.cbmSecondaryHabitat().get());
        visitInput.setText(prefs.cbmVisit().get());
        sectorInput.setText(prefs.cbmSector().get());
        zoneInput.setText(prefs.cbmZone().get());
    }

    @Override
    protected HashMap<String, String> serialize(Date entryTime) {
        HashMap<String, String> data = super.serialize(entryTime);
        data.putAll(picturesFragment.serialize());
        return data;
    }

    @Override
    protected void deserialize(HashMap<String, String> data) {
        super.deserialize(data);
        if (picturesFragment == null) {
            pendingDeserialize = data;
        } else {
            picturesFragment.doDeserialize(data);
        }
    }

    @AfterViews
    protected void flushDeserialize() {
        if (picturesFragment == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            picturesFragment = (NewEntryPicturesFragment) getChildFragmentManager().findFragmentById(R.id.pictures_fragment);
        }
        if (picturesFragment == null) {
            picturesFragment = (NewEntryPicturesFragment) getFragmentManager().findFragmentById(R.id.pictures_fragment);
        }
        if (pendingDeserialize != null) {
            picturesFragment.doDeserialize(pendingDeserialize);
            pendingDeserialize = null;
        }
    }

    @TextChange(R.id.form_cbm_zone)
    protected void onZoneChange() {
        Zone zone = zoneInput.getSelectedItem();
        if (zone != null) {
            Zone.Coordinate center = zone.getCenter();
            float[] res = new float[1];
            Location.distanceBetween(lat, lon, center.latitude, center.longitude, res);
            Log.d(TAG, String.format(Locale.ENGLISH, "distance (m): %f", res[0]));

            if (res[0] > Configuration.MAX_DISTANCE_TO_ZONE_METERS) {
                errorCbmTooFarView.setVisibility(View.VISIBLE);
                return;
            }
        }
        errorCbmTooFarView.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        prefs.cbmPrimaryHabitat().put(primaryHabitatInput.getText().toString());
        prefs.cbmSecondaryHabitat().put(secondaryHabitatInput.getText().toString());
        prefs.cbmVisit().put(visitInput.getText().toString());
        prefs.cbmSector().put(sectorInput.getText().toString());
        prefs.cbmZone().put(zoneInput.getText().toString());
    }

    public static class Builder implements BaseEntryFragment.Builder {

        @Override
        public Fragment build(double lat, double lon) {
            return NewCbmEntryFormFragment_.builder().lat(lat).lon(lon).build();
        }

        @Override
        public Fragment load(long id) {
            return NewCbmEntryFormFragment_.builder().entryId(id).build();
        }
    }

}
