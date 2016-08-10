package org.bspb.smartbirds.pro.backend;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.bspb.smartbirds.pro.BuildConfig;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.prefs.SmartBirdsPrefs_;
import org.bspb.smartbirds.pro.service.AuthenticationService_;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by dani on 08.08.16.
 */
@EBean(scope = EBean.Scope.Singleton)
public class AuthenticationInterceptor  implements Interceptor {
    private static final String TAG = SmartBirdsApplication.TAG + ".AuthIntrcptr";

    @RootContext
    Context context;

    @Pref
    SmartBirdsPrefs_ prefs;

    private String authorization;

    @AfterInject
    void loadAuthorization() {
        authorization = prefs.authToken().get();
    }

    public void clearAuthorization() {
        this.authorization = null;
        prefs.authToken().remove();
        prefs.isAuthenticated().put(false);
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
        prefs.authToken().put(authorization);
        prefs.isAuthenticated().put(!TextUtils.isEmpty(authorization));
        Log.i(TAG, String.format("Authorization: %s", authorization));
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        String auth = authorization;
        if (TextUtils.isEmpty(auth)) {
            Log.d(TAG, "not authorized");
            return chain.proceed(chain.request());
        }
        if (!BuildConfig.BACKEND_BASE_URL.contains(chain.request().url().host())) {
            Log.d(TAG, "no auth required");
            return chain.proceed(chain.request());
        }
        Request newRequest = chain.request().newBuilder().addHeader("Authorization", auth).build();
        Log.d(TAG, String.format("Authorization: %s", auth));
        Response response = chain.proceed(newRequest);
        if (!response.isSuccessful() && response.code() == 401) {
            Log.w(TAG, "Invalid authorization!");
            AuthenticationService_.intent(context).logout().start();
        }
        return response;
    }
}
