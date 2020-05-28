package org.bspb.smartbirds.pro.service;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.api.support.app.AbstractIntentService;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.backend.AuthenticationInterceptor;
import org.bspb.smartbirds.pro.backend.Backend;
import org.bspb.smartbirds.pro.backend.LoginResultEvent;
import org.bspb.smartbirds.pro.backend.dto.LoginRequest;
import org.bspb.smartbirds.pro.backend.dto.LoginResponse;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.LoginStateEvent;
import org.bspb.smartbirds.pro.events.LogoutEvent;
import org.bspb.smartbirds.pro.prefs.SmartBirdsPrefs_;
import org.bspb.smartbirds.pro.tools.SBGsonParser;

import java.io.IOException;

import retrofit2.Response;

import static org.bspb.smartbirds.pro.tools.Reporting.logException;

/**
 * Created by dani on 08.08.16.
 */
@EIntentService
public class AuthenticationService extends AbstractIntentService {

    private static final String TAG = SmartBirdsApplication.TAG + ".AuthenticationSvc";
    @Pref
    SmartBirdsPrefs_ prefs;
    @Bean
    Backend backend;
    @Bean
    AuthenticationInterceptor authenticationInterceptor;
    @Bean
    EEventBus bus;

    public AuthenticationService() {
        super("AuthenticationService");
    }

    @ServiceAction
    void logout() {
        authenticationInterceptor.clearAuthorization();
        bus.removeStickyEvent(LoginResultEvent.class);
        bus.postSticky(new LogoutEvent());
    }

    @ServiceAction
    void login(String email, String password, Boolean gdprConsent) {
        Log.d(TAG, String.format("login: %s %s", email, password));
        bus.postSticky(new LoginStateEvent(true));
        try {
            LoginResultEvent result = doLogin(email, password, gdprConsent);
            Log.d(TAG, String.format("login: %s %s => %s", email, password, result));
            bus.postSticky(result);
        } finally {
            bus.postSticky(new LoginStateEvent(false));
        }
    }

    private LoginResultEvent doLogin(String email, String password, Boolean gdprConsent) {

        Response<LoginResponse> response = null;
        try {
            response = backend.api().login(new LoginRequest(email, password, gdprConsent)).execute();
        } catch (IOException e) {
            logException(e);
            return new LoginResultEvent(LoginResultEvent.Status.CONNECTIVITY);
        }

        if (!response.isSuccessful()) {
            String errorResponse = "";
            try {
                errorResponse = response.errorBody().string();
                Log.i(TAG, String.format("Login error %d: %s", response.code(), errorResponse));
            } catch (IOException e) {
                logException(e);
            }

            LoginResponse loginResponse = SBGsonParser.createParser().fromJson(errorResponse, LoginResponse.class);
            String errorMessage = null;
            if(loginResponse != null) {
                errorMessage = loginResponse.error;
            }
            switch (response.code()) {
                case Backend.HTTP_STATUS_BAD_REQUEST:
                    return new LoginResultEvent(LoginResultEvent.Status.ERROR, errorMessage);
                case Backend.HTTP_STATUS_UNAUTHORIZED:
                    if (loginResponse != null && loginResponse.token == null) {
                        if (LoginResponse.REQUIRE_GDPR.equals(loginResponse.require)) {
                            return new LoginResultEvent(LoginResultEvent.Status.MISSING_GDPR, loginResponse.error);
                        }
                    }
                    return new LoginResultEvent(LoginResultEvent.Status.BAD_PASSWORD);
                default:
                    return new LoginResultEvent(LoginResultEvent.Status.CONNECTIVITY);
            }
        }

        authenticationInterceptor.setAuthorization(response.body().token, email, password);

        SyncService_.intent(this).initialSync().start();

        return new LoginResultEvent(response.body().user);
    }
}
