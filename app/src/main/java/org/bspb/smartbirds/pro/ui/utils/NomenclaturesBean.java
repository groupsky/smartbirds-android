package org.bspb.smartbirds.pro.ui.utils;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;

import com.crashlytics.android.Crashlytics;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.backend.dto.Nomenclature;
import org.bspb.smartbirds.pro.db.SmartBirdsProvider;
import org.bspb.smartbirds.pro.tools.AlphanumComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bspb.smartbirds.pro.db.NomenclatureColumns.LABEL_BG;
import static org.bspb.smartbirds.pro.db.NomenclatureColumns.LABEL_EN;
import static org.bspb.smartbirds.pro.db.NomenclatureColumns.TYPE;

/**
 * Created by dani on 14-11-12.
 */
@EBean(scope = EBean.Scope.Singleton)
public class NomenclaturesBean {

    private static final String TAG = SmartBirdsApplication.TAG + ".NomenclaturesBean";
    private static final String[] PROJECTION = {
            TYPE, LABEL_BG, LABEL_EN,
    };

    private final Map<String, List<Nomenclature>> data = new HashMap<>();

    @RootContext
    Context context;

    Cursor cursor;

    final Loader.OnLoadCompleteListener<Cursor> listener = new Loader.OnLoadCompleteListener<Cursor>() {
        @Override
        public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
            cursor = data;
            if (cursor != null)
                loadData();
        }
    };
    private final Comparator<? super Nomenclature> comparator = new Comparator<Nomenclature>() {
        @Override
        public int compare(Nomenclature o1, Nomenclature o2) {
            return AlphanumComparator.compareStrings(o1.localeLabel, o2.localeLabel);
        }
    };

    @AfterInject
    void init() {
        CursorLoader loader = new CursorLoader(context, SmartBirdsProvider.Nomenclatures.CONTENT_URI, PROJECTION, null, null, null);
        loader.registerListener(0, listener);
        loader.startLoading();
    }

    @Background
    public void loadData() {
        try {
            data.clear();
            String localeColumn = context.getString(R.string.nomenclature_locale_column);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                final Nomenclature nomenclature = new Nomenclature(cursor, localeColumn);
                List<Nomenclature> list;
                if (!data.containsKey(nomenclature.type)) {
                    list = new ArrayList<>();
                    data.put(nomenclature.type, list);
                } else {
                    list = data.get(nomenclature.type);
                }
                list.add(nomenclature);
            }

            // sort nomenclatures
            for (List<Nomenclature> nomenclatures: data.values()) {
                Collections.sort(nomenclatures, comparator);
            }
        } catch (Throwable t) {
            Crashlytics.logException(t);
        }
    }


    public List<Nomenclature> getNomenclature(String key) {
        key = key.replaceFirst("^form_", "");
        if (!data.containsKey(key)) throw new IllegalArgumentException("Unknown nomenclature "+key);
        return data.get(key);
    }

}
