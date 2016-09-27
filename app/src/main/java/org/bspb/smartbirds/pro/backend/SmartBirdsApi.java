package org.bspb.smartbirds.pro.backend;

import org.bspb.smartbirds.pro.backend.dto.LoginRequest;
import org.bspb.smartbirds.pro.backend.dto.LoginResponse;
import org.bspb.smartbirds.pro.backend.dto.Nomenclature;
import org.bspb.smartbirds.pro.backend.dto.ResponseListEnvelope;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by dani on 08.08.16.
 */
public interface SmartBirdsApi {

    @POST("session")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("nomenclature")
    Call<ResponseListEnvelope<Nomenclature>> nomenclatures(@Query("limit") int limit, @Query("offset") int offset);
}
