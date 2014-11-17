package org.bspb.smartbirds.pro.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.EntrySubmitted;
import org.bspb.smartbirds.pro.ui.fragment.NewBirdsEntryFormFragment_;
import org.bspb.smartbirds.pro.ui.fragment.NewCbmEntryFormFragment_;
import org.bspb.smartbirds.pro.ui.fragment.NewCiconiaEntryFormFragment_;
import org.bspb.smartbirds.pro.ui.fragment.NewHerpEntryFormFragment_;

@EActivity(R.layout.activity_form)
@OptionsMenu(R.menu.form_entry)
public class NewMonitoringEntryActivity extends Activity {

    public static final String EXTRA_LAT = "lat";
    public static final String EXTRA_LON = "lon";
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_TYPE = "entryType";

    @Extra(EXTRA_LAT)
    double lat;
    @Extra(EXTRA_LON)
    double lon;
    @Extra(EXTRA_TYPE)
    EntryType entryType;

    @Bean
    EEventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
    }

    @AfterViews
    void createFragment() {
        Fragment fragment = null;
        switch (entryType) {
            case BIRDS:
                fragment = NewBirdsEntryFormFragment_.builder().lat(lat).lon(lon).build();
                break;
            case HERP:
                fragment = NewHerpEntryFormFragment_.builder().lat(lat).lon(lon).build();
                break;
            case CBM:
                fragment = NewCbmEntryFormFragment_.builder().lat(lat).lon(lon).build();
                break;
            case CICONIA:
                fragment = NewCiconiaEntryFormFragment_.builder().lat(lat).lon(lon).build();
                break;
            default:
                throw new IllegalArgumentException("Unsupported entry type");
        }
        if (getFragmentManager().findFragmentById(R.id.container) == null)
            getFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventBus.register(this);
    }

    @Override
    protected void onStop() {
        eventBus.unregister(this);
        super.onStop();
    }

    public void onEvent(EntrySubmitted event) {
        Intent intent = new Intent().putExtra(EXTRA_LAT, lat).putExtra(EXTRA_LON, lon);

        if (EntryType.CICONIA.equals(event.entryType)) {
            intent.putExtra(EXTRA_NAME, getString(R.string.entry_type_ciconia));
        } else {
            if (event.data.containsKey("Вид_научно_име")) {
                intent.putExtra(EXTRA_NAME, event.data.get("Вид_научно_име"));
            }
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    @OptionsItem(android.R.id.home)
    void onUp() {
        confirmCancel();
    }

    @Override
    public void onBackPressed() {
        confirmCancel();
    }

    private void confirmCancel() {
        //Ask the user if they want to quit
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.cancel_entry)
                .setMessage(R.string.really_cancel_entry)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

}
