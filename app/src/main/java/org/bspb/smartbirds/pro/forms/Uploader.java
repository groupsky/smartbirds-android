package org.bspb.smartbirds.pro.forms;

import org.bspb.smartbirds.pro.backend.SmartBirdsApi;

import java.util.List;

/**
 * Created by groupsky on 27.09.16.
 */

public interface Uploader {

    boolean upload(SmartBirdsApi api, List<String> header, String[] row) throws Exception;
}
