package org.bspb.smartbirds.pro.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;

import org.bspb.smartbirds.pro.service.DataService_;

/**
 * This is the parent for all activities.
 * Each new activity should extend this class instead of API Activity class.
 * <p>
 * Created by Ilian Georgiev.
 */
public abstract class BaseActivity extends FragmentActivity {

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        bindService(DataService_.intent(this).get(), mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
    }
}
