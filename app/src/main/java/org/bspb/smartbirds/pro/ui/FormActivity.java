package org.bspb.smartbirds.pro.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.bspb.smartbirds.pro.FormBirdsFragment;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.ui.fragment.FormMainFragment;

public class FormActivity extends Activity implements FormBirdsFragment.Listener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, FormBirdsFragment.newInstance())
                    .commit();
        }
    }


    @Override
    public void onSubmitMonitoringForm() {
        finish();
    }
}
