package org.bspb.smartbirds.pro.ui.fragment;

import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.backend.dto.Nomenclature;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.LocationChangedEvent;
import org.bspb.smartbirds.pro.prefs.BirdPrefs;
import org.bspb.smartbirds.pro.prefs.CommonPrefs_;
import org.bspb.smartbirds.pro.ui.views.DecimalNumberFormInput;
import org.bspb.smartbirds.pro.ui.views.SingleChoiceFormInput;
import org.bspb.smartbirds.pro.ui.views.SwitchFormInput;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by groupsky on 26.01.17.
 */

@EFragment(R.layout.fragment_monitoring_form_new_birds_required_entry)
public class NewBirdsEntryRequiredFormFragment extends BaseFormFragment {

    protected static final String ARG_LAT = "lat";
    protected static final String ARG_LON = "lon";

    @FragmentArg(ARG_LAT)
    protected double lat;

    @FragmentArg(ARG_LON)
    protected double lon;

    @ViewById(R.id.form_birds_count_units)
    SingleChoiceFormInput countUnits;

    @ViewById(R.id.form_birds_count_type)
    SingleChoiceFormInput countType;

    @ViewById(R.id.form_birds_count)
    DecimalNumberFormInput count;
    @ViewById(R.id.form_birds_count_min)
    DecimalNumberFormInput countMin;
    @ViewById(R.id.form_birds_count_max)
    DecimalNumberFormInput countMax;

    @ViewById(R.id.form_birds_confidential)
    SwitchFormInput confidential;

    @ViewById(R.id.warning_confidential_nest)
    TextView warningConfidential;

    @ViewById(R.id.form_birds_distance)
    EditText distanceView;

    BirdPrefs prefs;

    @Pref
    CommonPrefs_ commonPrefs;

    EEventBus eventBus = EEventBus.getInstance();

    @FragmentById(value = R.id.pictures_fragment, childFragment = true)
    NewEntryPicturesFragment picturesFragment;

    @AfterInject
    void initPrefs() {
        prefs = new BirdPrefs(getContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (lat != 0 && lon != 0 && isNewEntry()) {
            eventBus.registerSticky(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        eventBus.unregister(this);
    }

    public void onEvent(LocationChangedEvent e) {
        if (e == null || e.location == null) {
            return;
        }

        eventBus.unregister(this);

        Location entryLocation = new Location("entry");
        entryLocation.setLatitude(lat);
        entryLocation.setLongitude(lon);

        double distance = e.location.distanceTo(entryLocation);

        if (distance < 10) {
            return;
        }

        distanceView.setVisibility(View.VISIBLE);
        distanceView.setText(getString(R.string.form_birds_distance_value, distance));

    }

    @Override
    public void onResume() {
        super.onResume();
        if (isNewEntry()) {
            countUnits.setSelection(prefs.getCountUnits());
            countType.setSelection(prefs.getCountType());
            confidential.setChecked(commonPrefs.confidentialRecord().get());
        }
        handleCountsLogic();
        showConfidentialWarningIfNeeded();
    }

    @Override
    protected HashMap<String, String> serialize() {
        HashMap<String, String> data = super.serialize();
        data.putAll(picturesFragment.serialize());
        return data;
    }

    @Override
    protected void deserialize(HashMap<String, String> data) {
        super.deserialize(data);
        // In some cases picturesFragment is still null. Try to find it by id
        if (picturesFragment == null) {
            picturesFragment = (NewEntryPicturesFragment) getChildFragmentManager().findFragmentById(R.id.pictures_fragment);
        }
        if (picturesFragment != null) {
            picturesFragment.doDeserialize(monitoringCode, data);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (picturesFragment == null) {
            picturesFragment = (NewEntryPicturesFragment) getChildFragmentManager().findFragmentById(R.id.pictures_fragment);
        }
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        prefs.setCountUnits(countUnits.getSelection());
        prefs.setCountType(countType.getSelection());
        commonPrefs.confidentialRecord().put(confidential.isChecked());
    }

    @TextChange(R.id.form_birds_count_units)
    @CheckedChange(R.id.form_birds_confidential)
    void showConfidentialWarningIfNeeded() {
        Nomenclature item = countUnits.getSelectedItem();
        String countUnit = item != null ? item.label.get("en") : null;
        if ("Nests".equals(countUnit)) {
            if (!confidential.isChecked()) {
                warningConfidential.setVisibility(View.VISIBLE);
            } else {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    warningConfidential.setVisibility(View.INVISIBLE);
                } else {
                    warningConfidential.setVisibility(View.GONE);
                }
            }
        } else {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                warningConfidential.setVisibility(View.INVISIBLE);
            } else {
                warningConfidential.setVisibility(View.GONE);
            }
        }
    }

    @TextChange(R.id.form_birds_count_type)
    void handleCountsLogic() {
        if (readOnly) {
            count.setEnabled(false);
            countMin.setEnabled(false);
            countMax.setEnabled(false);
            return;
        }
        Nomenclature item = countType.getSelectedItem();
        String countsType = item != null ? item.label.get("en") : null;
        switch (countsType != null ? countsType.toLowerCase(Locale.ENGLISH) : "") {
            case "exact number": // Exact count
                count.setEnabled(true);
                countMin.setEnabled(false);
                countMax.setEnabled(false);
                break;
            case "min.": // Min count
                count.setEnabled(false);
                countMin.setEnabled(true);
                countMax.setEnabled(false);
                break;
            case "max.": // Max count
                count.setEnabled(false);
                countMin.setEnabled(false);
                countMax.setEnabled(true);
                break;
            case "range": // Range count
                count.setEnabled(false);
                countMin.setEnabled(true);
                countMax.setEnabled(true);
                break;
            case "unspecified number": // Unspecified
                count.setEnabled(false);
                countMin.setEnabled(false);
                countMax.setEnabled(false);
                break;
            default:
                count.setEnabled(true);
                countMin.setEnabled(true);
                countMax.setEnabled(true);
                break;
        }
    }
}
