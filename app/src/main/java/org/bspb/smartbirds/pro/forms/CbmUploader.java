package org.bspb.smartbirds.pro.forms;

import com.google.gson.JsonObject;

import org.bspb.smartbirds.pro.backend.Converter;
import org.bspb.smartbirds.pro.backend.SmartBirdsApi;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by groupsky on 28.09.16.
 */

public class CbmUploader implements Uploader {
    @Override
    public boolean upload(SmartBirdsApi api, List<String> header, String[] row) throws Exception {
        JsonObject obj = Converter.convertCbm(header, row);
        Response<ResponseBody> res = api.createCbm(obj).execute();
        return res.isSuccessful();
    }
}
