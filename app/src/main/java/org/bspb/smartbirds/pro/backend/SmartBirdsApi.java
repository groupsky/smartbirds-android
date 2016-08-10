package org.bspb.smartbirds.pro.backend;

import org.bspb.smartbirds.pro.backend.dto.LoginRequest;
import org.bspb.smartbirds.pro.backend.dto.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by dani on 08.08.16.
 */
public interface SmartBirdsApi {

    @POST("session")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

}
