package org.bspb.smartbirds.pro.ui.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.bspb.smartbirds.pro.BuildConfig;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.content.MonitoringEntry;
import org.bspb.smartbirds.pro.content.MonitoringManager;
import org.bspb.smartbirds.pro.db.FormColumns;
import org.bspb.smartbirds.pro.db.SmartBirdsProvider;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.EntrySubmitted;
import org.bspb.smartbirds.pro.service.DataService_;
import org.bspb.smartbirds.pro.ui.utils.Configuration;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import de.greenrobot.event.EventBusException;

import static android.text.TextUtils.isEmpty;
import static org.bspb.smartbirds.pro.tools.Reporting.logException;

/**
 * Created by dani on 14-11-12.
 */
@EFragment
@OptionsMenu({R.menu.debug_menu, R.menu.form_entry})
public abstract class BaseEntryFragment extends BaseFormFragment implements LoaderManager.LoaderCallbacks<Cursor>, EntryType.EntryFragment {

    protected static final String[] PROJECTION = {
            FormColumns._ID,
            FormColumns.DATA,
            FormColumns.TYPE,
            FormColumns.CODE,
            FormColumns.LATITUDE,
            FormColumns.LONGITUDE,
    };

    protected static final String ARG_LAT = "lat";
    protected static final String ARG_LON = "lon";

    protected String TAG = SmartBirdsApplication.TAG + "." + getClass().getSimpleName();

    @FragmentArg(ARG_LAT)
    protected double lat;
    @FragmentArg(ARG_LON)
    protected double lon;

    /**
     * Available only when loaded from storage, otherwise = 0
     */
    @Nullable
    @FragmentArg
    protected long entryId;

    @Bean
    protected EEventBus eventBus;

    @OptionsMenuItem(R.id.action_crash)
    MenuItem menuCrash;

    /**
     * Available only when loaded from storage
     */
    @Nullable
    protected Date entryTimestamp;
    private boolean haveDeserialized;

    @InstanceState
    protected MonitoringEntry storedEntry;

    protected abstract EntryType getEntryType();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (entryId > 0) {
            LoaderManager.getInstance(getActivity()).restartLoader(this.hashCode(), null, this);
        }
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), SmartBirdsProvider.Forms.withId(entryId), PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (haveDeserialized) return;
        if (!cursor.moveToFirst()) {
            getActivity().finish();
            logException(new IllegalStateException("Loader found no data for id: " + entryId));
            return;
        }
        MonitoringEntry entry = MonitoringManager.entryFromCursor(cursor);
        if (entry == null) {
            getActivity().finish();
            logException(new IllegalStateException("Entry couldn't be loaded for id: " + entryId));
            return;
        }
        this.storedEntry = entry;
        doDeserialize(entry.monitoringCode, entry.data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "loader reset");
    }


    @Override
    public void onStop() {
        eventBus.unregister(this);
        super.onStop();
    }

    @Override
    protected void doDeserialize(String monitoringCode, HashMap<String, String> data) {
        haveDeserialized = true;
        LoaderManager.getInstance(getActivity()).destroyLoader(this.hashCode());
        super.doDeserialize(monitoringCode, data);
    }

    @Override
    protected void deserialize(HashMap<String, String> data) {
        super.deserialize(data);
        String latVal = data.get(getString(R.string.tag_lat));
        if (!isEmpty(latVal)) lat = Double.parseDouble(latVal);
        String lonVal = data.get(getString(R.string.tag_lon));
        if (!isEmpty(lonVal)) lon = Double.parseDouble(lonVal);

        checkCoordinates();

        String dateVal = data.get(getString(R.string.entry_date));
        String timeVal = data.get(getString(R.string.entry_time));
        if (!isEmpty(dateVal) && !isEmpty(timeVal)) try {
            entryTimestamp = Configuration.parseDateTime(dateVal, timeVal);
        } catch (ParseException e) {
            logException(e);
        }
    }

    @Override
    protected final HashMap<String, String> serialize() {
        return super.serialize();
    }

    @AfterInject
    protected void checkArguments() {
        if (entryId == 0) {
            checkCoordinates();
        }
    }

    protected void checkCoordinates() {
        if (lat == 0 || lon == 0) {
            logException(new IllegalStateException("Creating entry fragment with zero coordinates"));
        }
    }

    protected HashMap<String, String> serialize(Date entryTime) {
        HashMap<String, String> data = super.serialize();
        data.put(getString(R.string.tag_lat), Double.toString(lat));
        data.put(getString(R.string.tag_lon), Double.toString(lon));
        data.put(getString(R.string.entry_date), Configuration.STORAGE_DATE_FORMAT.format(entryTime));
        data.put(getString(R.string.entry_time), Configuration.STORAGE_TIME_FORMAT.format(entryTime));
        return data;
    }

    protected void submitData(HashMap<String, String> data) {
        eventBus.post(new EntrySubmitted(monitoringCode, entryId, data, getEntryType()));
    }

    protected void submitData() {
        submitData(serialize(entryTimestamp != null ? entryTimestamp : new Date()));
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

    @Override
    public boolean isDirty() {
        if (storedEntry == null) return false;
        if (entryTimestamp == null) return false;
        HashMap<String, String> data = serialize(entryTimestamp);
        return !data.equals(storedEntry.data);
    }

    public interface Builder {
        Fragment build(double lat, double lon);

        Fragment load(long id);
    }

    @Override
    public boolean isNewEntry() {
        return entryId == 0;
    }
}
