package org.bspb.smartbirds.pro.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.core.content.ContextCompat.checkSelfPermission;

public class DBExporter {

    public static void exportDB(Context context) {
        checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);
        boolean success = false;

        try {
            File dbFile = context.getDatabasePath("smartBirdsDatabase.db");
            File dbDir = dbFile.getParentFile();
            if (dbDir.isDirectory() && dbDir.exists()) {
                File[] dbFiles = dbDir.listFiles();
                if (dbFiles != null) {
                    File baseOutputDir = new File(Environment.getExternalStorageDirectory(), "smartbirdspro");
                    if (!baseOutputDir.exists() && baseOutputDir.isDirectory()) {
                        baseOutputDir.mkdir();
                    }

                    File dbOutputDir = new File(baseOutputDir.getAbsolutePath() + File.separator + "db");
                    if (!dbOutputDir.exists()) {
                        dbOutputDir.mkdir();
                    }

                    File currentDbDir = new File(dbOutputDir, System.currentTimeMillis() + "");
                    if (!currentDbDir.exists()) {
                        currentDbDir.mkdir();
                    }

                    for (File file : dbFiles) {
                        File out = new File(currentDbDir, file.getName());
                        out.createNewFile();
                        copy(file, out);
                    }

                    success = true;
                }

            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("DB Export");
        alert.setMessage("DB export: " + (success ? "SUCCESS" : "FAIL"));
        alert.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        alert.show();
    }

    private static void copy(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }
}
