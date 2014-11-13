package org.bspb.smartbirds.pro.ui.utils;

import android.content.Context;

import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dani on 14-11-12.
 */
@EBean(scope = EBean.Scope.Singleton)
public class NomenclaturesBean {

    private static final String FILE_NAME = "nomenclatures.csv";

    private Map<String, List<String>> data;

    @RootContext
    Context context;

    public NomenclaturesBean() {
        data = new HashMap<String, List<String>>();
    }

    @AfterInject
    @Background
    public void loadData() {
        try {
            InputStream inputStream = context.getAssets().open(FILE_NAME);
            CSVReader<String[]> csv = CSVReaderBuilder.newDefaultReader(new InputStreamReader(inputStream));
            List<String> keys = csv.readHeader();

            for (String key : keys) {
                data.put(key, new ArrayList<String>());
            }

            List<String[]> rows = csv.readAll();
            if (rows != null && !rows.isEmpty()) {
                for (String[] row : rows) {
                    for (int i = 0; i < row.length; i++) {
                        if (row[i] != null && !row[i].equals("")) {
                            data.get(keys.get(i)).add(row[i]);
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getNomenclature(String key) {
        return data.get(key);
    }

}
