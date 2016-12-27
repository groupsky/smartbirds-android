package org.bspb.smartbirds.pro.ui.fragment;

import android.app.Fragment;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.ui.utils.FormUtils;
import org.bspb.smartbirds.pro.ui.views.FormBirdsList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by groupsky on 19.12.16.
 */

@EFragment(R.layout.fragment_monitoring_form_humid_birds)
public class NewHumidBirdsEntryFragment extends BaseEntryFragment {

    @ViewById(R.id.form_birds_list)
    FormBirdsList birdsList;

    @Override
    protected EntryType getEntryType() {
        return EntryType.HUMID;
    }

    @Override
    protected void ensureForm() {
        throw new IllegalStateException("Should not be called!");
    }

    @Override
    protected boolean isValid() {
        for (FormUtils.FormModel model: birdsList.getModels()) {
            if (!model.validateFields())
                return false;
        }
        return true;
    }

    @Override
    protected void submitData() {
        ArrayList<FormUtils.FormModel> models = birdsList.getModels();
        Calendar entryTime = new GregorianCalendar();
        entryTime.add(Calendar.SECOND, -models.size());
        for (FormUtils.FormModel model: models) {
            form = model;
            submitData(serialize(entryTime.getTime()));
            entryTime.add(Calendar.SECOND, 1);
        }
    }

    public static class Builder implements BaseEntryFragment.Builder {

        @Override
        public Fragment build(double lat, double lon) {
            return NewHumidBirdsEntryFragment_.builder().lat(lat).lon(lon).build();
        }
    }
}
