package org.bspb.smartbirds.pro.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.EntrySubmitted;
import org.bspb.smartbirds.pro.events.SetMonitoringCommonData;
import org.bspb.smartbirds.pro.ui.utils.FormUtils;

import java.util.HashMap;


@EFragment(R.layout.fragment_monitoring_form_birds)
@OptionsMenu(R.menu.form_entry)
public class NewBirdsEntryFormFragment extends Fragment {

    private static final String ARG_LAT = "lat";
    private static final String ARG_LON = "lon";

    @FragmentArg(ARG_LAT)
    double lat;
    @FragmentArg(ARG_LON)
    double lon;

    @Bean
    EEventBus eventBus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @OptionsItem(R.id.action_submit)
    void onSubmitClicked(MenuItem item) {
        HashMap<String, String> data = FormUtils.traverseForm(getView()).serialize();
        data.put("latitude", Double.toString(lat));
        data.put("longitude", Double.toString(lon));
        eventBus.post(new EntrySubmitted(data));
    }

}
