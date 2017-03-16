package org.bspb.smartbirds.pro.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.ui.utils.FormUtils;
import org.bspb.smartbirds.pro.ui.views.FormBirdsList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * Created by groupsky on 19.12.16.
 */

@EFragment(R.layout.fragment_monitoring_form_humid_birds)
public class NewHumidBirdsEntryFragment extends BaseEntryFragment {

    @ViewById(R.id.form_birds_list)
    FormBirdsList birdsList;

    FormUtils.FormModel fakeForm = new FormUtils.FormModel();

    @Override
    protected EntryType getEntryType() {
        return EntryType.HUMID;
    }

    @Override
    protected boolean ensureForm() {
        if (form == null) {
            throw new IllegalStateException("Should not be called with null form!");
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        form = fakeForm;
        try {
            super.onSaveInstanceState(outState);
        } finally {
            form = null;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        form = fakeForm;
        try {
            super.onRestoreInstanceState(savedInstanceState);
        } finally {
            form = null;
        }
    }

    @Override
    protected boolean isValid() {
        for (FormUtils.FormModel model : birdsList.getModels()) {
            if (!model.validateFields())
                return false;
        }
        return true;
    }

    @Override
    protected void doDeserialize(HashMap<String, String> data) {
        form = fakeForm;
        try {
            super.doDeserialize(data);
        } finally {
            form = null;
        }
    }

    @Override
    protected void deserialize(HashMap<String, String> data) {
        form = fakeForm;
        try {
            super.deserialize(data);
            ArrayList<HashMap<String, String>> models = new ArrayList<>();
            models.add(data);
            birdsList.deserialize(models);
        } finally {
            form = null;
        }
    }

    @Override
    protected void submitData() {
        ArrayList<FormUtils.FormModel> models = birdsList.getModels();
        Calendar entryTime = new GregorianCalendar();
        if (entryTimestamp != null) {
            entryTime.setTime(entryTimestamp);
        }
        entryTime.add(Calendar.SECOND, -models.size());
        for (FormUtils.FormModel model : models) {
            form = model;
            entryTime.add(Calendar.SECOND, 1);
            submitData(serialize(entryTime.getTime()));
        }
    }

    public static class Builder implements BaseEntryFragment.Builder {

        @Override
        public Fragment build(double lat, double lon) {
            return NewHumidBirdsEntryFragment_.builder().lat(lat).lon(lon).build();
        }

        @Override
        public Fragment load(long id) {
            return NewHumidBirdsEntryFragment_.builder().entryId(id).build();
        }
    }
}
