package org.bspb.smartbirds.pro.backend;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.bspb.smartbirds.pro.BuildConfig;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.backend.dto.LoginRequest;
import org.bspb.smartbirds.pro.backend.dto.LoginResponse;
import org.bspb.smartbirds.pro.prefs.UserPrefs_;
import org.bspb.smartbirds.pro.service.AuthenticationService_;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static android.text.TextUtils.isEmpty;
import static org.bspb.smartbirds.pro.tools.Reporting.logException;

/**
 * Created by dani on 08.08.16.
 */
@EBean(scope = EBean.Scope.Singleton)
public class AuthenticationInterceptor implements Interceptor {
    private static final String TAG = SmartBirdsApplication.TAG + ".AuthIntrcptr";

    @RootContext
    Context context;

    @Pref
    UserPrefs_ prefs;

    @Bean
    Backend backend;

    private String authorization;

    @AfterInject
    void loadAuthorization() {
        authorization = prefs.authToken().get();
    }

    public boolean isAuthorized() {
        return !TextUtils.isEmpty(this.authorization);
    }

    public void clearAuthorization() {
        this.authorization = null;
        prefs.clear();
        prefs.isAuthenticated().put(false);
    }

    public void setAuthorization(String authorization, String username, String password, boolean gdprConsent) {
        this.authorization = authorization;
        prefs.authToken().put(authorization);
        prefs.isAuthenticated().put(!TextUtils.isEmpty(authorization));
        prefs.username().put(username);
        prefs.password().put(password);
        prefs.gdprConsent().put(gdprConsent);
        Log.i(TAG, String.format("Authorization: %s", authorization));
    }

    synchronized boolean tryRelogin(String failedAuth) {
        try {
            if (!TextUtils.equals(this.authorization, failedAuth)) return !isEmpty(authorization);
            retrofit2.Response<LoginResponse> loginResponse = backend.api().login(new LoginRequest(prefs.username().get(), prefs.password().get(), prefs.gdprConsent().get())).execute();
            if (loginResponse.isSuccessful()) {
                this.authorization = loginResponse.body().token;
                prefs.authToken().put(this.authorization);
                return true;
            } else {
                this.authorization = null;
                return false;
            }
        } catch (Throwable t) {
            logException(t);
            return false;
        }
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!BuildConfig.BACKEND_BASE_URL.contains(chain.request().url().host()) || chain.request().url().encodedPath().contains("session")) {
            Log.d(TAG, "no auth required");
            return chain.proceed(chain.request());
        }
        int retries = 0;
        while (true) {
            String auth = authorization;
            if (TextUtils.isEmpty(auth)) {
                Log.d(TAG, "not authorized");
                return chain.proceed(chain.request());
            }
            Request newRequest = chain.request().newBuilder().addHeader("x-sb-csrf-token", auth).build();
            Log.d(TAG, String.format("Authorization: %s", auth));
            Response response = chain.proceed(newRequest);
            if (!response.isSuccessful() && response.code() == 401) {
                Log.w(TAG, "Invalid authorization!");
                if (retries++ > 3 || !tryRelogin(auth)) {
                    clearAuthorization();
                    AuthenticationService_.intent(context).logout().start();
                    return response;
                }
            } else {
                return response;
            }
        }
    }
}
