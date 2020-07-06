package org.bspb.smartbirds.pro.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsItem;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.EntrySubmitted;
import org.bspb.smartbirds.pro.service.DataService_;

/**
 * Created by groupsky on 16.03.17.
 */

@EActivity(R.layout.activity_form)
public class EditMonitoringEntryActivity extends BaseActivity {

    @Extra
    long entryId;
    @Extra
    EntryType entryType;
    @FragmentById(R.id.container)
    Fragment fragment;
    @Bean
    EEventBus eventBus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataService_.intent(this).start();
    }

    @AfterViews
    protected void setupFragment() {
        if (entryId <= 0 || entryType == null) {
            this.finish();
            return;
        }
        if (fragment == null) {
            fragment = entryType.loadFragment(entryId);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventBus.register(this);
    }

    @OptionsItem(android.R.id.home)
    boolean onUp() {
        return confirmCancel();
    }

    @Override
    public void onBackPressed() {
        if (!confirmCancel()) super.onBackPressed();
    }

    private boolean confirmCancel() {
        if (fragment == null) return false;
        if (!(fragment instanceof EntryType.EntryFragment)) return false;
        if (!((EntryType.EntryFragment) fragment).isDirty()) return false;

        //Ask the user if they want to quit
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_alert)
                .setTitle(R.string.cancel_edit_entry)
                .setMessage(R.string.really_cancel_edit_entry)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
        return true;
    }

    @Override
    protected void onStop() {
        eventBus.unregister(this);
        super.onStop();
    }

    public void onEvent(EntrySubmitted event) {
        setResult(RESULT_OK);
        finish();
    }
}
