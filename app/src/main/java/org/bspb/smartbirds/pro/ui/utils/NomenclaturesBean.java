package org.bspb.smartbirds.pro.ui.utils;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.backend.dto.Nomenclature;
import org.bspb.smartbirds.pro.backend.dto.SpeciesNomenclature;
import org.bspb.smartbirds.pro.db.NomenclatureUsesCountColumns;
import org.bspb.smartbirds.pro.db.SmartBirdsProvider.NomenclatureUsesCount;
import org.bspb.smartbirds.pro.db.SmartBirdsProvider.Nomenclatures;
import org.bspb.smartbirds.pro.tools.AlphanumComparator;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.bspb.smartbirds.pro.db.NomenclatureColumns.LABEL_BG;
import static org.bspb.smartbirds.pro.db.NomenclatureColumns.LABEL_EN;
import static org.bspb.smartbirds.pro.db.NomenclatureColumns.TYPE;
import static org.bspb.smartbirds.pro.db.SmartBirdsProvider.Nomenclatures.CONTENT_URI;

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

    String localeColumn;

    final Loader.OnLoadCompleteListener<Cursor> listener = new Loader.OnLoadCompleteListener<Cursor>() {
        @Override
        public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
            loading = false;
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
    private boolean loading;

    @AfterInject
    void init() {
        loading = true;
        CursorLoader loader = new CursorLoader(context, Nomenclatures.CONTENT_URI, PROJECTION, null, null, null);
        loader.registerListener(0, listener);
        loader.startLoading();
    }

    @Background
    public void loadData() {
        try {
            data.clear();
            localeColumn = context.getString(R.string.nomenclature_locale_column);
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

            // load any missing nomenclatures from bundled - this is useful when updating, before sync with server
            Set<String> missingNomenclatures = new HashSet<>();
            for (SpeciesNomenclature species : loadBundledSpecies()) {
                fillMissingNomenclature(missingNomenclatures, Nomenclature.from(species));
            }
            missingNomenclatures.clear();
            for (Nomenclature nomenclature : loadBundledNomenclatures()) {
                fillMissingNomenclature(missingNomenclatures, nomenclature);
            }

            // sort nomenclatures
            for (List<Nomenclature> nomenclatures : data.values()) {
                Collections.sort(nomenclatures, comparator);
            }
        } catch (Throwable t) {
            Crashlytics.logException(t);
        }
    }

    private void fillMissingNomenclature(Set<String> missingNomenclatures, Nomenclature nomenclature) {
        List<Nomenclature> list;
        if (!data.containsKey(nomenclature.type)) {
            missingNomenclatures.add(nomenclature.type);
            list = new LinkedList<>();
            data.put(nomenclature.type, list);
        } else if (missingNomenclatures.contains(nomenclature.type)) {
            list = data.get(nomenclature.type);
        } else return;
        list.add(nomenclature);
    }

    public Iterable<ContentValues> prepareNomenclatureCV(Iterable<Nomenclature> nomenclatures) {
        LinkedList<ContentValues> cvs = new LinkedList<>();
        for (Nomenclature nomenclature : nomenclatures) {
            cvs.add(nomenclature.toCV());
        }
        return cvs;
    }

    public Iterable<ContentValues> prepareSpeciesCV(Iterable<SpeciesNomenclature> speciesCollection) {
        LinkedList<ContentValues> cvs = new LinkedList<>();
        for (SpeciesNomenclature species : speciesCollection) {
            cvs.add(Nomenclature.from(species).toCV());
        }
        return cvs;
    }

    private int prepareCPO(Iterable<ContentValues> cvs, Collection<ContentProviderOperation> buffer) {
        int count = 0;
        for (ContentValues cv : cvs) {
            // insert the nomenclature value
            buffer.add(ContentProviderOperation.newInsert(CONTENT_URI)
                    .withValues(cv)
                    .build());
            // increase the count
            count++;
        }
        return count;
    }

    public int prepareNomenclatureCPO(Iterable<Nomenclature> nomenclatureItems, Collection<ContentProviderOperation> buffer) {
        return prepareCPO(prepareNomenclatureCV(nomenclatureItems), buffer);
    }

    public int prepareSpeciesCPO(Iterable<SpeciesNomenclature> speciesItems, Collection<ContentProviderOperation> buffer) {
        return prepareCPO(prepareSpeciesCV(speciesItems), buffer);
    }

    public Iterable<Nomenclature> loadBundledNomenclatures() {
        return loadBundledFile("nomenclatures.json");
    }

    public Iterable<SpeciesNomenclature> loadBundledSpecies() {
        return loadBundledFile("species.json");
    }

    @SafeVarargs
    private final <T> Iterable<T> loadBundledFile(final String filename,
                                                  // this is a hack to get a concrete array class on the T and bypass the type erasue
                                                  final T... arrayType) {
        try {
            InputStream is = new BufferedInputStream(context.getAssets().open(filename));
            try {
                return Arrays.asList((T[]) new Gson().fromJson(new InputStreamReader(is), arrayType.getClass()));
            } finally {
                is.close();
            }
        } catch (IOException e) {
            Crashlytics.logException(e);
            throw new IllegalStateException("Missing bundled file " + filename, e);
        }
    }


    public List<Nomenclature> getNomenclature(String key) {
        key = key.replaceFirst("^form_", "");
        if (!data.containsKey(key)) {
            if (loading) throw new IllegalStateException("Still loading");
            throw new IllegalArgumentException("Unknown nomenclature " + key);
        }
        return data.get(key);
    }

    public List<Nomenclature> getRecentNomenclatures(String key) {
        key = key.replaceFirst("^form_", "");
        List<Nomenclature> nomenclatures = getNomenclature(key);
        Cursor cursor = context.getContentResolver().query(NomenclatureUsesCount.forType(key), new String[]{localeColumn}, null, null, null);
        if (cursor != null) try {
            final ArrayList<Nomenclature> recentItems = new ArrayList<>(cursor.getCount());
            Nomenclature temp = new Nomenclature();
            int idx;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                temp.localeLabel = cursor.getString(0);
                idx = Collections.binarySearch(nomenclatures, temp, comparator);
                if (idx < 0) continue;
                recentItems.add(nomenclatures.get(idx));
            }
            return recentItems;
        } finally {
            cursor.close();
        }
        return new ArrayList<>(0);
    }

    @Background
    public void addRecentNomenclature(Nomenclature nomenclature) {
        ContentValues cv = nomenclature.toCV();
        cv.put(NomenclatureUsesCountColumns.COUNT, 1);
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(NomenclatureUsesCount.forType(nomenclature.type),
                new String[]{NomenclatureUsesCountColumns._ID, NomenclatureUsesCountColumns.COUNT},
                localeColumn + "=?",
                new String[]{nomenclature.localeLabel},
                null);
        if (cursor != null) try {
            if (cursor.moveToFirst() && !cursor.isAfterLast()) {
                cv.put(NomenclatureUsesCountColumns.COUNT, cursor.getInt(1) + 1);
                resolver.update(NomenclatureUsesCount.forId(cursor.getLong(0)), cv, null, null);
                return;
            }
        } finally {
            cursor.close();
        }
        resolver.insert(NomenclatureUsesCount.CONTENT_URI, cv);
    }

}
