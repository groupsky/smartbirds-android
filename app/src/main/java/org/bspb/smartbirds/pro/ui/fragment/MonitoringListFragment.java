package org.bspb.smartbirds.pro.ui.fragment;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;

import org.androidannotations.annotations.EFragment;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.adapter.ModelCursorAdapter;
import org.bspb.smartbirds.pro.adapter.ModelCursorFactory;
import org.bspb.smartbirds.pro.content.Monitoring;
import org.bspb.smartbirds.pro.content.MonitoringManager;
import org.bspb.smartbirds.pro.db.MonitoringColumns;
import org.bspb.smartbirds.pro.ui.partial.MonitoringListRowPartialView;
import org.bspb.smartbirds.pro.ui.partial.MonitoringListRowPartialView_;

import static org.bspb.smartbirds.pro.db.SmartBirdsProvider.Monitorings;


/**
 * Created by groupsky on 08.03.17.
 */

@EFragment
public class MonitoringListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] PROJECTION = {
            MonitoringColumns._ID,
            MonitoringColumns.CODE,
            MonitoringColumns.STATUS,
            MonitoringColumns.DATA,
    };
    private static final String TAG = SmartBirdsApplication.TAG + ".FMonLst";

    private CursorAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        adapter = new ModelCursorAdapter<Monitoring>(getActivity(), R.layout.partial_monitoring_list_row, null, new ModelCursorFactory<Monitoring>() {
            @Override
            public Monitoring createModelFromCursor(Cursor cursor) {
                return MonitoringManager.monitoringFromCursor(cursor);
            }
        }) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return MonitoringListRowPartialView_.build(context);
            }

            @Override
            public void bindView(View view, Context context, Monitoring model) {
                if (!(view instanceof MonitoringListRowPartialView))
                    throw new IllegalArgumentException("Must use " + MonitoringListRowPartialView.class.getSimpleName());
                ((MonitoringListRowPartialView) view).bind(model);
            }
        };
        setListAdapter(adapter);
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Monitoring monitoring = (Monitoring) getListAdapter().getItem(position);
        if (monitoring != null && getActivity() instanceof Listener) {
            ((Listener)getActivity()).onMonitoringSelected(monitoring.code);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader");
        return new CursorLoader(getActivity(),
                Monitorings.CONTENT_URI,
                PROJECTION,
                MonitoringColumns.STATUS + "=?",
                new String[]{Monitoring.Status.finished.name()},
                MonitoringColumns._ID + " DESC");
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

    public interface Listener {
        void onMonitoringSelected(String monitoringCode);
    }
}
