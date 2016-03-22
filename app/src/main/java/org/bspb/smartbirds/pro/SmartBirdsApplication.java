package org.bspb.smartbirds.pro;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;
import org.bspb.smartbirds.pro.events.CancelMonitoringEvent;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.StartMonitoringEvent;
import org.bspb.smartbirds.pro.service.DataService_;
import org.bspb.smartbirds.pro.ui.utils.Configuration;
import org.bspb.smartbirds.pro.ui.utils.NomenclaturesBean;

/**
 * Created by groupsky on 14-9-25.
 */
@EApplication
public class SmartBirdsApplication extends Application {

    public static final String TAG = "SBP";
    @Bean
    EEventBus bus;
    @Bean
    NomenclaturesBean nomenclaturesBean;

    @Override
    public void onCreate() {
        super.onCreate();

        Crashlytics.start(this);

        bus.register(this);

        Configuration.init(this);
    }

    public void onEvent(StartMonitoringEvent event) {
        DataService_.intent(this).start();
    }

    public void onEvent(CancelMonitoringEvent event) {
        DataService_.intent(this).stop();
    }

}
