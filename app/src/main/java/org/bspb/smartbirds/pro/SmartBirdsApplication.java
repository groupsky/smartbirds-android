package org.bspb.smartbirds.pro;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;
import org.bspb.smartbirds.pro.backend.AddCookiesInterceptor;
import org.bspb.smartbirds.pro.backend.AuthenticationInterceptor;
import org.bspb.smartbirds.pro.backend.Backend;
import org.bspb.smartbirds.pro.backend.ReceivedCookiesInterceptor;
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
    @Bean
    AuthenticationInterceptor authenticationInterceptor;
    @Bean
    Backend backend;

    @Override
    public void onCreate() {
        super.onCreate();

        Crashlytics.start(this);

        backend.addInterceptor(new AddCookiesInterceptor(this));
        backend.addInterceptor(new ReceivedCookiesInterceptor(this));
        backend.addInterceptor(authenticationInterceptor);

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
