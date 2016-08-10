package org.bspb.smartbirds.pro.backend;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.androidannotations.annotations.EBean;
import org.bspb.smartbirds.pro.BuildConfig;
import org.bspb.smartbirds.pro.SmartBirdsApplication;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
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
    SmartBirdsApi api;
    private Gson gson;

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
            client = builder.build();
            // replace with okhttp instances to throw unmodifiable exception
            interceptors = client.interceptors();
            networkInterceptors = client.networkInterceptors();
        }
        return client;
    }

    public Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .create();
        }
        return gson;
    }

    public Retrofit getRetrofit() {
        if (retrofit == null) {
            Log.d(TAG, String.format("Backend base url: %s", BuildConfig.BACKEND_BASE_URL));
            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BACKEND_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(getGson()))
                    .client(getClient())
                    .build();
        }
        return retrofit;
    }


    public SmartBirdsApi api() {
        return api == null ? api = getRetrofit().create(SmartBirdsApi.class) : api;
    }
}
