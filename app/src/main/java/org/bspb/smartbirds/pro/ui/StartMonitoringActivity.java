package org.bspb.smartbirds.pro.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.bspb.smartbirds.pro.FormBirdsFragment;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.ui.fragment.FormMainFragment;

public class StartMonitoringActivity extends Activity implements FormMainFragment.Listener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_monitoring);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, FormMainFragment.newInstance())
                    .commit();
        }
    }


    @Override
    public void onSubmitMainForm() {

        startActivity(new Intent(this, MonitoringActivity.class));
        finish();

    }
}
