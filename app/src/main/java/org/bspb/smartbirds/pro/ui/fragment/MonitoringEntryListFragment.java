package org.bspb.smartbirds.pro.ui.fragment;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.adapter.ModelCursorAdapter;
import org.bspb.smartbirds.pro.adapter.ModelCursorFactory;
import org.bspb.smartbirds.pro.content.MonitoringEntry;
import org.bspb.smartbirds.pro.content.MonitoringManager;
import org.bspb.smartbirds.pro.db.FormColumns;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.ui.partial.MonitoringEntryListRowPartialView;
import org.bspb.smartbirds.pro.ui.partial.MonitoringEntryListRowPartialView_;

import java.util.Locale;

import static android.text.TextUtils.isEmpty;
import static org.bspb.smartbirds.pro.db.SmartBirdsProvider.Forms;


/**
 * Created by groupsky on 08.03.17.
 */

@EFragment
public class MonitoringEntryListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] PROJECTION = {
            FormColumns._ID,
            FormColumns.DATA,
            FormColumns.TYPE,
            FormColumns.CODE,
            FormColumns.LATITUDE,
            FormColumns.LONGITUDE,
    };
    private static final String TAG = SmartBirdsApplication.TAG + ".FFormLst";

    private CursorAdapter adapter;

    @InstanceState
    protected String monitoringCode;

    private String lastMonitoringCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        adapter = new ModelCursorAdapter<MonitoringEntry>(getActivity(), R.layout.partial_monitoring_entry_list_row, null, new ModelCursorFactory<MonitoringEntry>() {
            @Override
            public MonitoringEntry createModelFromCursor(Cursor cursor) {
                return MonitoringManager.entryFromCursor(cursor);
            }
        }) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return MonitoringEntryListRowPartialView_.build(context);
            }

            @Override
            public void bindView(View view, Context context, MonitoringEntry model) {
                if (!(view instanceof MonitoringEntryListRowPartialView))
                    throw new IllegalArgumentException("Must use " + MonitoringEntryListRowPartialView.class.getSimpleName());
                ((MonitoringEntryListRowPartialView) view).bind(model);
            }
        };
        setListAdapter(adapter);
        if (!TextUtils.equals(lastMonitoringCode, monitoringCode))
            getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        if (!TextUtils.equals(lastMonitoringCode, monitoringCode))
            getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        MonitoringEntry entry = (MonitoringEntry) getListAdapter().getItem(position);
        Log.d(TAG, "entry = " + entry);
        if (entry != null && getActivity() instanceof Listener) {
            ((Listener) getActivity()).onMonitoringEntrySelected(entry.id, entry.type);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, String.format(Locale.ENGLISH, "onCreateLoader: %s", monitoringCode));
        lastMonitoringCode = monitoringCode;
        return new CursorLoader(getActivity(),
                isEmpty(monitoringCode) ? Forms.CONTENT_URI : Forms.withMonitoringCode(this.monitoringCode),
                PROJECTION, null, null, FormColumns._ID + " desc");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(TAG, String.format("onLoadFinished: %d", cursor.getCount()));
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset");
        adapter.swapCursor(null);
    }

    @FragmentArg
    public void setMonitoringCode(String monitoringCode) {
        if (TextUtils.equals(this.monitoringCode, monitoringCode)) return;
        this.monitoringCode = monitoringCode;
        if (!TextUtils.equals(lastMonitoringCode, monitoringCode))
            getLoaderManager().restartLoader(0, null, this);
    }

    public interface Listener {
        void onMonitoringEntrySelected(long id, EntryType entryType);
    }
}
