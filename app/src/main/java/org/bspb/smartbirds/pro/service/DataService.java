package org.bspb.smartbirds.pro.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.events.CancelMonitoringEvent;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.MonitoringStartedEvent;
import org.bspb.smartbirds.pro.events.SetMonitoringCommonData;
import org.bspb.smartbirds.pro.events.StartMonitoringEvent;
import org.bspb.smartbirds.pro.ui.StartMonitoringActivity;

import de.greenrobot.event.ThreadMode;

@EService
public class DataService extends Service {

    private static final String TAG = SmartBirdsApplication.TAG+".DataService";
    @Bean
    EEventBus bus;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @AfterInject
    void initBus() {
        Log.d(TAG, "bus registing...");
        bus.registerSticky(this);
        Log.d(TAG, "bus registered");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "destroying...");
        bus.unregister(this);

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand...");
        return START_NOT_STICKY;
    }

    public void onEvent(StartMonitoringEvent event) {
        Log.d(TAG, "onStartMonitoringEvent...");
        Toast.makeText(this, "Start monitoring", Toast.LENGTH_SHORT).show();
        bus.postSticky(new MonitoringStartedEvent());
    }

    public void onEvent(CancelMonitoringEvent event) {
        Log.d(TAG, "onCancelMonitoringEvent...");
        Toast.makeText(this, "Cancel monitoring", Toast.LENGTH_SHORT).show();
    }

    public void onEvent(SetMonitoringCommonData event) {
        Log.d(TAG, "onSetMonitoringCommonData");
    }

}
