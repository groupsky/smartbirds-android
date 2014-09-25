package org.bspb.smartbirds.pro.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import org.bspb.smartbirds.pro.ui.fragment.NewBirdsEntryFormFragment;
import org.bspb.smartbirds.pro.R;

public class NewMonitoringEntryActivity extends Activity implements NewBirdsEntryFormFragment.Listener {

    public static final String EXTRA_LAT = "lat";
    public static final String EXTRA_LON = "lon";
    private double lat;
    private double lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        Intent intent = getIntent();
        if (intent != null) {
            onRestoreInstanceState(intent.getExtras());
        }

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, NewBirdsEntryFormFragment.newInstance(lat, lon))
                    .commit();
        } else {
            onRestoreInstanceState(savedInstanceState);
        }

        setResult(RESULT_CANCELED);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble(EXTRA_LAT, lat);
        outState.putDouble(EXTRA_LON, lon);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        lat = savedInstanceState.getDouble(EXTRA_LAT, lat);
        lon = savedInstanceState.getDouble(EXTRA_LON, lon);
    }

    @Override
    public void onSubmitMonitoringForm() {
        setResult(RESULT_OK, new Intent().putExtra(EXTRA_LAT, lat).putExtra(EXTRA_LON, lon));
        finish();
    }

    public static Intent newIntent(Context context, double latitude, double longitude) {
        return new Intent(context, NewMonitoringEntryActivity.class)
                .putExtra(EXTRA_LAT, latitude)
                .putExtra(EXTRA_LON, longitude);
    }
}
