package org.bspb.smartbirds.pro.androidTest;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.google.gson.JsonObject;

import org.bspb.smartbirds.pro.backend.Backend;
import org.bspb.smartbirds.pro.backend.SmartBirdsApi;
import org.bspb.smartbirds.pro.backend.dto.CheckSessionRequest;
import org.bspb.smartbirds.pro.backend.dto.DownloadsResponse;
import org.bspb.smartbirds.pro.backend.dto.FileId;
import org.bspb.smartbirds.pro.backend.dto.Location;
import org.bspb.smartbirds.pro.backend.dto.LoginRequest;
import org.bspb.smartbirds.pro.backend.dto.LoginResponse;
import org.bspb.smartbirds.pro.backend.dto.Nomenclature;
import org.bspb.smartbirds.pro.backend.dto.ResponseEnvelope;
import org.bspb.smartbirds.pro.backend.dto.ResponseListEnvelope;
import org.bspb.smartbirds.pro.backend.dto.UploadFormResponse;
import org.bspb.smartbirds.pro.backend.dto.Zone;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.Mockito;
import org.mockito.internal.junit.JUnitRule;

import okhttp3.MultipartBody;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Retrofit;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class MockBackendTestRule implements TestRule {

    SmartBirdsApi api;
    MockWebServer server;

    public MockBackendTestRule () {
    }

    public SmartBirdsApi getApi() {
        return api;
    }

    public MockWebServer getServer() {
        return server;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return statement(base);
    }

    private Statement statement(final Statement base) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                server = new MockWebServer();
                server.start();

                // replace retrofit proxy with our mock
                Backend.backendBaseUrl = "http://"+server.getHostName()+":"+server.getPort();
//                api = Mockito.mock(SmartBirdsApi.class);
//                when(api.login(any())).then(invocation -> {
//                    return new Call<LoginResponse>() {}
//                })
//                        .spy(Backend_.getInstance_(getApplicationContext()).api());

                base.evaluate();
            }
        };
    }
}
