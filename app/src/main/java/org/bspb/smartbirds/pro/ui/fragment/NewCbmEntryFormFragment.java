package org.bspb.smartbirds.pro.ui.fragment;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsPrefs_;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.ui.views.SingleChoiceFormInput;
import org.bspb.smartbirds.pro.ui.views.SingleChoiceFormInput_;

/**
 * Created by dani on 14-11-11.
 */
@EFragment(R.layout.fragment_monitoring_form_new_cbm_entry)
public class NewCbmEntryFormFragment extends BaseEntryFragment {

    @ViewById(R.id.form_cbm_name)
    SingleChoiceFormInput nameInput;

    @ViewById(R.id.form_cbm_distance)
    SingleChoiceFormInput distanceInput;

    @ViewById(R.id.form_cbm_primary_habitat)
    SingleChoiceFormInput primaryHabitatInput;

    @ViewById(R.id.form_cbm_secondary_habitat)
    SingleChoiceFormInput secondaryHabitatInput;

    @ViewById(R.id.form_cbm_visit_number)
    SingleChoiceFormInput visitInput;

    @ViewById(R.id.form_cbm_sector)
    SingleChoiceFormInput sectorInput;

    @Pref
    SmartBirdsPrefs_ prefs;

    @Override
    EntryType getEntryType() {
        return EntryType.CBM;
    }

    @Override
    public void onResume() {
        super.onResume();
        nameInput.setText(prefs.cbmName().get());
        distanceInput.setText(prefs.cbmDistance().get());
        primaryHabitatInput.setText(prefs.cbmPrimaryHabitat().get());
        secondaryHabitatInput.setText(prefs.cbmSecondaryHabitat().get());
        visitInput.setText(prefs.cbmVisit().get());
        sectorInput.setText(prefs.cbmSector().get());
    }

    @Override
    public void onPause() {
        super.onPause();
        prefs.cbmName().put(nameInput.getText().toString());
        prefs.cbmDistance().put(distanceInput.getText().toString());
        prefs.cbmPrimaryHabitat().put(primaryHabitatInput.getText().toString());
        prefs.cbmSecondaryHabitat().put(secondaryHabitatInput.getText().toString());
        prefs.cbmVisit().put(visitInput.getText().toString());
        prefs.cbmSector().put(sectorInput.getText().toString());
    }
}
