package org.bspb.smartbirds.pro.backend;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddLanguageInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

        builder.addHeader("Language", Locale.getDefault().getLanguage());

        return chain.proceed(builder.build());
    }
}
