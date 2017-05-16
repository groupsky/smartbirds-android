package org.bspb.smartbirds.pro.ui.fragment;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
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
import org.bspb.smartbirds.pro.service.DataOpsService_;
import org.bspb.smartbirds.pro.ui.partial.MonitoringEntryListRowPartialView;
import org.bspb.smartbirds.pro.ui.partial.MonitoringEntryListRowPartialView_;

import java.util.Locale;


/**
 * Created by groupsky on 08.03.17.
 */

@EFragment
public class MonitoringEntryListFragment extends ListFragment implements MonitoringCursorEntries.Listener {

    private static final String TAG = SmartBirdsApplication.TAG + ".FFormLst";

    private CursorAdapter adapter;

    @Bean
    MonitoringCursorEntries entries;

    @Bean
    MonitoringManager monitoringManager;

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

    @AfterViews
    protected void setupListview() {
        final ListView lv = getListView();
        setEmptyText(getText(R.string.emptyList));
        lv.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                Log.d(TAG, String.format(Locale.ENGLISH, "onItemCheckedStateChanged: %d %s", position, checked));
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                Log.d(TAG, String.format(Locale.ENGLISH, "onCreateActionMode: %s", mode));
                getActivity().getMenuInflater().inflate(R.menu.listselection, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                Log.d(TAG, String.format(Locale.ENGLISH, "onActionItemClicked: %s", item));
                switch (item.getItemId()) {
                    case R.id.action_select_all:
                        for (int i = 0; i < lv.getCount(); i++)
                            lv.setItemChecked(i, true);
                        return true;
                    case R.id.action_delete:
                        final long[] selectedItems = lv.getCheckedItemIds();

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                        builder.setMessage(getString(R.string.confirm_delete_n, selectedItems.length));
                        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mode.finish();
                                monitoringManager.deleteEntries(selectedItems);
                                DataOpsService_.intent(getActivity()).generateMonitoringFiles(monitoringCode).start();
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, null);

                        AlertDialog dialog = builder.create();
                        dialog.show();
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                Log.d(TAG, String.format(Locale.ENGLISH, "onDestroyActionMode: %s", mode));
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        MonitoringEntry entry = (MonitoringEntry) getListAdapter().getItem(position);
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
