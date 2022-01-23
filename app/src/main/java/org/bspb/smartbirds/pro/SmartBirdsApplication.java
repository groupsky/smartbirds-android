package org.bspb.smartbirds.pro;

import android.app.Application;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;
import org.bspb.smartbirds.pro.backend.AddCookiesInterceptor;
import org.bspb.smartbirds.pro.backend.AddLanguageInterceptor;
import org.bspb.smartbirds.pro.backend.AuthenticationInterceptor;
import org.bspb.smartbirds.pro.backend.Backend;
import org.bspb.smartbirds.pro.backend.ReceivedCookiesInterceptor;
import org.bspb.smartbirds.pro.events.CancelMonitoringEvent;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.ResumeMonitoringEvent;
import org.bspb.smartbirds.pro.events.StartMonitoringEvent;
import org.bspb.smartbirds.pro.room.SmartBirdsRoomDatabase;
import org.bspb.smartbirds.pro.service.DataService_;
import org.bspb.smartbirds.pro.ui.utils.Configuration;
import org.bspb.smartbirds.pro.ui.utils.NomenclaturesBean;
import org.bspb.smartbirds.pro.utils.NomenclaturesManagerNew;

/**
 * Created by groupsky on 14-9-25.
 */
@EApplication
public class SmartBirdsApplication extends Application {

    public static final String TAG = "SBP";

    public static final String FILES_AUTHORITY = BuildConfig.APPLICATION_ID + ".SmartBirdsFileProvider";

    public static final String PREFS_MONITORING_POINTS = "points";

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

        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG);
        crashlytics.setCustomKey("git_sha", BuildConfig.GIT_SHA);
        crashlytics.setCustomKey("build_time", BuildConfig.BUILD_TIME);

        SmartBirdsRoomDatabase.Companion.init(this);
        NomenclaturesManagerNew.Companion.init(this);

        backend.addInterceptor(new AddCookiesInterceptor(this));
        backend.addInterceptor(new ReceivedCookiesInterceptor(this));
        backend.addInterceptor(authenticationInterceptor);
        backend.addInterceptor(new AddLanguageInterceptor());

        bus.register(this);

        Configuration.init(this);
    }

    public void onEvent(StartMonitoringEvent event) {
        DataService_.intent(this).start();
    }

    public void onEvent(ResumeMonitoringEvent event) {
        DataService_.intent(this).start();
    }

    public void onEvent(CancelMonitoringEvent event) {
        DataService_.intent(this).stop();
    }
}
