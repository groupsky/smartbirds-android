package org.bspb.smartbirds.pro.ui.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.db.LocationColumns;
import org.bspb.smartbirds.pro.db.SmartBirdsProvider;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.GetMonitoringCommonData;
import org.bspb.smartbirds.pro.events.MonitoringCommonData;
import org.bspb.smartbirds.pro.events.SetMonitoringCommonData;
import org.bspb.smartbirds.pro.prefs.CommonPrefs_;
import org.bspb.smartbirds.pro.prefs.UserPrefs_;
import org.bspb.smartbirds.pro.service.DataService_;
import org.bspb.smartbirds.pro.ui.utils.Configuration;
import org.bspb.smartbirds.pro.ui.utils.FormUtils;
import org.bspb.smartbirds.pro.ui.views.DateFormInput;
import org.bspb.smartbirds.pro.ui.views.MultipleTextFormInput;
import org.bspb.smartbirds.pro.ui.views.TimeFormInput;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static org.bspb.smartbirds.pro.tools.ArrayUtils.join;


@EFragment(R.layout.fragment_monitoring_form_common)
public class MonitoringCommonFormFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = SmartBirdsApplication.TAG + ".CommonForm";
    private static final int LOADER_NEAERST_PLACES = 1;
    @Bean
    EEventBus bus;
    FormUtils.FormModel form;
    @ViewById(R.id.form_common_start_date)
    DateFormInput startDateView;
    @ViewById(R.id.form_common_start_time)
    TimeFormInput startTimeView;
    @ViewById(R.id.form_common_end_date)
    DateFormInput endDateView;
    @Pref
    CommonPrefs_ prefs;
    @Pref
    UserPrefs_ userPrefs;
    @ViewById(R.id.observers)
    MultipleTextFormInput observers;
    @ViewById(R.id.location)
    TextView locationView;
    @ViewById(R.id.button_refresh_location)
    View buttonRefreshLocation;
    @ViewById(R.id.progress_location_refreshing)
    View progressLocationRefreshing;
    @InstanceState
    Location lastLocation;
    @InstanceState
    boolean manualLocation;
    @InstanceState
    boolean reloadLocationLoader;
    private transient boolean updatingLocationFromGPS;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        DataService_.intent(context).start();
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
        bus.postSticky(new GetMonitoringCommonData());
    }

    @Override
    public void onResume() {
        super.onResume();
        observers.setText(prefs.commonOtherObservers().get());
    }

    @Override
    public void onPause() {
        super.onPause();
        prefs.commonOtherObservers().put(observers.getText().toString());
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    @AfterViews
    void loadSavedData() {
        form = FormUtils.traverseForm(getView());
        startDateView.setValue(Calendar.getInstance());
        startTimeView.setValue(Calendar.getInstance());
        endDateView.setValue(Calendar.getInstance());
    }

    @OptionsItem(R.id.action_submit)
    public void save() {
        HashMap<String, String> data = form.serialize();
        data.put(getString(R.string.tag_user_id), userPrefs.userId().get());
        data.put(getString(R.string.tag_user_first_name), userPrefs.firstName().get());
        data.put(getString(R.string.tag_user_last_name), userPrefs.lastName().get());
        data.put(getString(R.string.tag_user_email), userPrefs.email().get());
        bus.post(new SetMonitoringCommonData(data));
    }

    public void onEventMainThread(MonitoringCommonData event) {
        if (event.data != null && !event.data.isEmpty()) {
            manualLocation = true;
            form.deserialize(event.data);
        }
    }

    public void onEvent(Location location) {
        if (location == null) return;
        this.lastLocation = location;
        if (!reloadLocationLoader) {
            Loader<Object> loader = getLoaderManager().getLoader(LOADER_NEAERST_PLACES);
            if (loader != null && loader.isStarted()) return;
        } else {
            reloadLocationLoader = false;
        }
        getLoaderManager().restartLoader(LOADER_NEAERST_PLACES, null, this);
    }

    public boolean validate() {
        return form.validateFields();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_NEAERST_PLACES:
                Location location = this.lastLocation;
                if (manualLocation) {
                    buttonRefreshLocation.setVisibility(View.VISIBLE);
                    progressLocationRefreshing.setVisibility(View.INVISIBLE);
                }
                if (location == null) {
                    reloadLocationLoader = true;
                }

                buttonRefreshLocation.setVisibility(View.INVISIBLE);
                progressLocationRefreshing.setVisibility(View.VISIBLE);

                return new CursorLoader(getActivity(), SmartBirdsProvider.Locations.CONTENT_URI_LIMIT_1,
                        join(org.bspb.smartbirds.pro.backend.dto.Location.DEFAULT_PROJECTION,
                                SmartBirdsProvider.Locations.distance(location != null ? location.getLatitude() : 0, location != null ? location.getLongitude() : 0)),
                        // in case we don't have location or we don't want to modify it, force a false where clause
                        location == null || manualLocation ? "1=0" : null, null,
                        // order by disance in ascending order
                        LocationColumns.DISTANCE);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (reloadLocationLoader) return;

        buttonRefreshLocation.setVisibility(View.VISIBLE);
        progressLocationRefreshing.setVisibility(View.INVISIBLE);

        Log.d(TAG, String.format(Locale.ENGLISH, "found places: %d", cursor.getCount()));
        if (cursor.moveToFirst() && !cursor.isAfterLast()) {
            org.bspb.smartbirds.pro.backend.dto.Location location = org.bspb.smartbirds.pro.backend.dto.Location.fromCursor(cursor);
            Log.d(TAG, String.format(Locale.ENGLISH, "place: %s", location));
            Log.d(TAG, String.format(Locale.ENGLISH, "distance: %s", cursor.getDouble(cursor.getColumnIndexOrThrow(LocationColumns.DISTANCE))));

            Location location1 = lastLocation;
            if (location1 == null) return;
            float[] res = new float[1];
            Location.distanceBetween(location1.getLatitude(), location1.getLongitude(), location.latitude, location.longitude, res);
            Log.d(TAG, String.format(Locale.ENGLISH, "distance (m): %f", res[0]));

            if (res[0] > Configuration.MAX_DISTANCE_LOCATION_METERS) {
                Toast.makeText(getActivity(), R.string.no_place_nearby, Toast.LENGTH_SHORT).show();
                return;
            }

            if (!manualLocation) {
                updatingLocationFromGPS = true;
                try {
                    locationView.setText(location.name.get(getString(R.string.locale)));
                } finally {
                    updatingLocationFromGPS = false;
                }
            }
        } else {
            Toast.makeText(getActivity(), R.string.no_place_nearby, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Click(R.id.button_refresh_location)
    protected void refreshLocation() {
        buttonRefreshLocation.setVisibility(View.INVISIBLE);
        progressLocationRefreshing.setVisibility(View.VISIBLE);
        manualLocation = false;
        getLoaderManager().restartLoader(LOADER_NEAERST_PLACES, null, this);
    }

    @TextChange(R.id.location)
    protected void textChange() {
        if (updatingLocationFromGPS) return;
        Log.d(TAG, "location changed manually");
        manualLocation = true;
        buttonRefreshLocation.setVisibility(View.VISIBLE);
        progressLocationRefreshing.setVisibility(View.INVISIBLE);
    }
}
