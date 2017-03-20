package org.bspb.smartbirds.pro.ui.fragment;

import android.app.ListFragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.adapter.ModelCursorAdapter;
import org.bspb.smartbirds.pro.adapter.ModelCursorFactory;
import org.bspb.smartbirds.pro.beans.MonitoringCursorEntries;
import org.bspb.smartbirds.pro.content.MonitoringEntry;
import org.bspb.smartbirds.pro.content.MonitoringManager;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.ui.partial.MonitoringEntryListRowPartialView;
import org.bspb.smartbirds.pro.ui.partial.MonitoringEntryListRowPartialView_;


/**
 * Created by groupsky on 08.03.17.
 */

@EFragment
public class MonitoringEntryListFragment extends ListFragment implements MonitoringCursorEntries.Listener {

    private static final String TAG = SmartBirdsApplication.TAG + ".FFormLst";

    private CursorAdapter adapter;

    @Bean
    MonitoringCursorEntries entries;

    private String monitoringCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        adapter = new ModelCursorAdapter<MonitoringEntry>(getActivity(), R.layout.partial_monitoring_entry_list_row, entries != null ? entries.getCursor() : null, new ModelCursorFactory<MonitoringEntry>() {
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
    }

    @AfterInject
    protected void setupLoader() {
        entries.setMonitoringCode(monitoringCode);
        entries.setListener(this);
        if (adapter != null) adapter.swapCursor(entries.getCursor());
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

    @FragmentArg
    public void setMonitoringCode(String monitoringCode) {
        this.monitoringCode = monitoringCode;
        if (entries != null) entries.setMonitoringCode(monitoringCode);
    }

    @Override
    public void onMonitoringEntriesChanged(MonitoringCursorEntries entries) {
        if (adapter != null) adapter.swapCursor(entries.getCursor());
    }

    public interface Listener {
        void onMonitoringEntrySelected(long id, EntryType entryType);
    }
}
