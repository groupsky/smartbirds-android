package org.bspb.smartbirds.pro.backend;

import com.google.gson.JsonObject;

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

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by dani on 08.08.16.
 */
public interface SmartBirdsApi {

    @POST("session")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @PUT("session")
    Call<LoginResponse> checkSession(@Body CheckSessionRequest checkSessionRequest);

    @GET("nomenclature")
    Call<ResponseListEnvelope<Nomenclature>> nomenclatures(@Query("limit") int limit, @Query("offset") int offset);

    @GET("species")
    Call<ResponseListEnvelope<Nomenclature>> species(@Query("limit") int limit, @Query("offset") int offset);

    @POST("birds")
    Call<UploadFormResponse> createBirds(@Body JsonObject request);

    @POST("herp")
    Call<UploadFormResponse> createHerp(@Body JsonObject request);

    @POST("herptiles")
    Call<UploadFormResponse> createHerptile(@Body JsonObject request);

    @POST("mammals")
    Call<UploadFormResponse> createMammal(@Body JsonObject request);

    @POST("invertebrates")
    Call<UploadFormResponse> createInvertebrates(@Body JsonObject request);

    @POST("ciconia")
    Call<UploadFormResponse> createCiconia(@Body JsonObject request);

    @POST("cbm")
    Call<UploadFormResponse> createCbm(@Body JsonObject request);

    @POST("plants")
    Call<UploadFormResponse> createPlants(@Body JsonObject request);

    @POST("threats")
    Call<UploadFormResponse> createThreats(@Body JsonObject request);

    @Multipart
    @POST("storage")
    Call<ResponseEnvelope<FileId>> upload(@Part MultipartBody.Part file);

    @GET("zone?limit=-1&status=owned")
    Call<ResponseListEnvelope<Zone>> listZones();

    @GET("locations?limit=-1")
    Call<ResponseListEnvelope<Location>> listLocations();

    @GET
    Call<DownloadsResponse> getDownloads(@Url String url);
}
