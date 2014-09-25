package org.bspb.smartbirds.pro.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.bspb.smartbirds.pro.events.CancelMonitoringEvent;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.MonitoringStartedEvent;
import org.bspb.smartbirds.pro.events.StartMonitoringEvent;
import org.bspb.smartbirds.pro.ui.StartMonitoringActivity;

import de.greenrobot.event.ThreadMode;

@EService
public class DataService extends Service {

    @Bean
    EEventBus bus;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        bus.register(this);
    }

    @Override
    public void onDestroy() {
        bus.unregister(this);

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        StartMonitoringEvent startMonitoringEvent = bus.getStickyEvent(StartMonitoringEvent.class);
        if (startMonitoringEvent != null)
            onEvent(startMonitoringEvent);
        return START_NOT_STICKY;
    }

    public void onEvent(StartMonitoringEvent event) {
        Toast.makeText(this, "Start monitoring", Toast.LENGTH_SHORT).show();
        bus.post(new MonitoringStartedEvent());
    }

    public void onEvent(CancelMonitoringEvent event) {
        Toast.makeText(this, "Cancel monitoring", Toast.LENGTH_SHORT).show();
    }

}
