package org.bspb.smartbirds.pro.backend;

import android.util.Log;

import androidx.annotation.VisibleForTesting;

import com.google.gson.Gson;

import org.androidannotations.annotations.EBean;
import org.bspb.smartbirds.pro.BuildConfig;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.tools.SBGsonParser;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dani on 08.08.16.
 */
@EBean(scope = EBean.Scope.Singleton)
public class Backend {

    private static final String TAG = SmartBirdsApplication.TAG + ".Backend";

    public static final int HTTP_STATUS_BAD_REQUEST = 400;
    public static final int HTTP_STATUS_UNAUTHORIZED = 401;
    public static final int HTTP_STATUS_FORBIDDEN = 403;
    public static final int HTTP_STATUS_NOT_FOUND = 404;

    private List<Interceptor> interceptors = new LinkedList<>();
    private List<Interceptor> networkInterceptors = new LinkedList<>();
    private OkHttpClient client;
    private Retrofit retrofit;
    private SmartBirdsApi api;
    private Gson gson;
    public static String backendBaseUrl = BuildConfig.BACKEND_BASE_URL;

    public Backend() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.BASIC);
        addNetworkInterceptor(logging);
    }

    public void addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
    }

    public void addNetworkInterceptor(Interceptor interceptor) {
        networkInterceptors.add(interceptor);
    }

    public OkHttpClient getClient() {
        if (client == null) {
            // Add the interceptor to OkHttpClient
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.interceptors().addAll(interceptors);
            builder.networkInterceptors().addAll(networkInterceptors);
            builder.readTimeout(5, TimeUnit.MINUTES);
            builder.cookieJar(new JavaNetCookieJar(CookieHandler.getDefault() != null ? CookieHandler.getDefault() : new CookieManager()));
            client = builder.build();
            // replace with okhttp instances to throw unmodifiable exception
            interceptors = client.interceptors();
            networkInterceptors = client.networkInterceptors();
        }
        return client;
    }

    public Retrofit getRetrofit() {
        gson = SBGsonParser.createParser();

        if (retrofit == null) {
            Log.d(TAG, String.format("Backend base url: %s", backendBaseUrl));
            retrofit = new Retrofit.Builder()
                    .baseUrl(backendBaseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(getClient())
                    .build();
        }
        return retrofit;
    }


    public SmartBirdsApi api() {
        return api == null ? api = getRetrofit().create(SmartBirdsApi.class) : api;
    }
}
