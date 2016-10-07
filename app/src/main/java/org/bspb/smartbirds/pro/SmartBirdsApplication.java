package org.bspb.smartbirds.pro;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.core.CrashlyticsCore;

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

import io.fabric.sdk.android.Fabric;

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

        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();

        // Initialize Fabric with the debug-disabled crashlytics.
        Fabric.with(this, crashlyticsKit, new Answers());

        Crashlytics.setString("git_sha", BuildConfig.GIT_SHA);
        Crashlytics.setString("build_time", BuildConfig.BUILD_TIME);

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
