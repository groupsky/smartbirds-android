package org.bspb.smartbirds.pro.ui.fragment;

import android.support.v4.app.Fragment;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.prefs.CbmPrefs_;
import org.bspb.smartbirds.pro.ui.views.SingleChoiceFormInput;
import org.bspb.smartbirds.pro.ui.views.ZoneFormInput;

/**
 * Created by dani on 14-11-11.
 */
@EFragment(R.layout.fragment_monitoring_form_new_cbm_entry)
public class NewCbmEntryFormFragment extends BaseEntryFragment {

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

    @Pref
    CbmPrefs_ prefs;

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
    }

}
