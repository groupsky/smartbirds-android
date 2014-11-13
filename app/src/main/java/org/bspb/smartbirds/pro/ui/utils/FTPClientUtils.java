package org.bspb.smartbirds.pro.ui.utils;

import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.bspb.smartbirds.pro.SmartBirdsApplication;

import java.io.IOException;

/**
 * Created by dani on 14-11-13.
 */
public class FTPClientUtils {

    private static final String TAG = SmartBirdsApplication.TAG + ".FTPClientUtils";

    public static FTPClient connect() {
        FTPClient ftpClient = new FTPClient();
        try {
            Log.d(TAG, "connecting");
            ftpClient.connect("87.252.173.51");
            Log.d(TAG, "authorizing");
            ftpClient.login("gis", "Pomarina1");

            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        } catch (IOException e) {
            Log.e(TAG, String.format("error while connecting to ftp: %s", e.getMessage()), e);
        }

        return ftpClient;
    }
}
