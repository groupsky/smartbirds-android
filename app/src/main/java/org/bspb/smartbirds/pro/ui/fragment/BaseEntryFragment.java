package org.bspb.smartbirds.pro.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.bspb.smartbirds.pro.BuildConfig;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.EntrySubmitted;
import org.bspb.smartbirds.pro.service.DataService_;
import org.bspb.smartbirds.pro.ui.utils.Configuration;

import java.util.Date;
import java.util.HashMap;

import de.greenrobot.event.EventBusException;

/**
 * Created by dani on 14-11-12.
 */
@EFragment
@OptionsMenu({R.menu.debug_menu, R.menu.form_entry})
public abstract class BaseEntryFragment extends BaseFormFragment {

    protected static final String ARG_LAT = "lat";
    protected static final String ARG_LON = "lon";

    @FragmentArg(ARG_LAT)
    protected double lat;
    @FragmentArg(ARG_LON)
    protected double lon;

    @Bean
    protected EEventBus eventBus;

    @OptionsMenuItem(R.id.action_crash)
    MenuItem menuCrash;

    protected abstract EntryType getEntryType();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        DataService_.intent(context).start();
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            eventBus.register(this);
        } catch (EventBusException e) {
            // silently ignore it
        }
    }

    @Override
    public void onStop() {
        eventBus.unregister(this);
        super.onStop();
    }

    @Override
    protected final HashMap<String, String> serialize() {
        return super.serialize();
    }

    protected HashMap<String, String> serialize(Date entryTime) {
        HashMap<String, String> data = super.serialize();
        data.put(getString(R.string.tag_lat), Double.toString(lat));
        data.put(getString(R.string.tag_lon), Double.toString(lon));
        data.put(getResources().getString(R.string.entry_date), Configuration.STORAGE_DATE_FORMAT.format(entryTime));
        data.put(getResources().getString(R.string.entry_time), Configuration.STORAGE_TIME_FORMAT.format(entryTime));
        return data;
    }

    protected void submitData(HashMap<String, String> data) {
        eventBus.post(new EntrySubmitted(data, getEntryType()));
    }

    protected void submitData() {
        submitData(serialize(new Date()));
    }

    @OptionsItem(R.id.action_submit)
    void onSubmitClicked(MenuItem item) {
        if (isValid()) {
            item.setEnabled(false);
            submitData();
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menuCrash != null) {
            menuCrash.setVisible(BuildConfig.DEBUG);
        }
    }

    @OptionsItem(R.id.action_crash)
    void crash() {
        Crashlytics.getInstance().crash();
    }

    public interface Builder {
        Fragment build(double lat, double lon);
    }
}
