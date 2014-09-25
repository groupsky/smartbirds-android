package org.bspb.smartbirds.pro.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.bspb.smartbirds.pro.ui.fragment.NewBirdsEntryFormFragment;
import org.bspb.smartbirds.pro.R;

@EActivity(R.layout.activity_form)
public class NewMonitoringEntryActivity extends Activity implements NewBirdsEntryFormFragment.Listener {

    public static final String EXTRA_LAT = "lat";
    public static final String EXTRA_LON = "lon";

    @Extra(EXTRA_LAT)
    double lat;
    @Extra(EXTRA_LON)
    double lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getFragmentManager().findFragmentById(R.id.container) == null)
            getFragmentManager().beginTransaction()
                    .add(R.id.container, NewBirdsEntryFormFragment.newInstance(lat, lon))
                    .commit();

        setResult(RESULT_CANCELED);
    }

    @Override
    public void onSubmitMonitoringForm() {
        setResult(RESULT_OK, new Intent().putExtra(EXTRA_LAT, lat).putExtra(EXTRA_LON, lon));
        finish();
    }

}
