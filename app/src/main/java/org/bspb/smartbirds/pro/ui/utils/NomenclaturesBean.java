package org.bspb.smartbirds.pro.ui.utils;

import static org.bspb.smartbirds.pro.db.NomenclatureColumns.DATA;
import static org.bspb.smartbirds.pro.db.NomenclatureColumns.LABEL_BG;
import static org.bspb.smartbirds.pro.db.NomenclatureColumns.LABEL_EN;
import static org.bspb.smartbirds.pro.db.NomenclatureColumns.TYPE;
import static org.bspb.smartbirds.pro.db.SmartBirdsProvider.Nomenclatures.CONTENT_URI;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.util.Log;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.backend.dto.Nomenclature;
import org.bspb.smartbirds.pro.db.NomenclatureUsesCountColumns;
import org.bspb.smartbirds.pro.db.SmartBirdsProvider.NomenclatureUsesCount;
import org.bspb.smartbirds.pro.db.SmartBirdsProvider.Nomenclatures;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.NomenclaturesReadyEvent;
import org.bspb.smartbirds.pro.tools.AlphanumComparator;
import org.bspb.smartbirds.pro.tools.Reporting;
import org.bspb.smartbirds.pro.tools.SBGsonParser;
import org.bspb.smartbirds.pro.utils.ExtensionsKt;

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

/**
 * Created by dani on 14-11-12.
 */
@EBean(scope = EBean.Scope.Singleton)
public class NomenclaturesBean {

    private static final String TAG = SmartBirdsApplication.TAG + ".NomenclaturesBean";
    private static final String[] PROJECTION = {
            TYPE, LABEL_BG, LABEL_EN, DATA
    };

    private static final String[] USES_COUNT_PROJECTION = {
            NomenclatureUsesCountColumns.TYPE, NomenclatureUsesCountColumns.LABEL_BG, NomenclatureUsesCountColumns.LABEL_EN, NomenclatureUsesCountColumns.LABEL_ID, NomenclatureUsesCountColumns.DATA
    };

    private final Map<String, List<Nomenclature>> data = new HashMap<>();

    @RootContext
    Context context;

    @Bean
    EEventBus bus;

    Cursor cursor;

    final Loader.OnLoadCompleteListener<Cursor> listener = new Loader.OnLoadCompleteListener<Cursor>() {
        @Override
        public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
            cursor = data;
            if (cursor != null) {
                loadData();
            }
        }
    };
    private final Comparator<? super Nomenclature> comparator =
            (Comparator<Nomenclature>) (o1, o2) -> {
                if (o1.label.getLabelId().equalsIgnoreCase(o2.label.getLabelId())) {
                    return 0;
                }
                return AlphanumComparator.compareStrings(o1.localeLabel, o2.localeLabel);
            };
    private boolean loading;

    public boolean isLoading() {
        return loading;
    }

    @AfterInject
    void init() {
        loading = true;
        CursorLoader loader = new CursorLoader(context, Nomenclatures.CONTENT_URI, PROJECTION, null, null, null);
        loader.registerListener(0, listener);
        loader.startLoading();
    }

    // moved in new Nomenclatures manager
    @Deprecated
    @Background
    public void loadData() {
        try {
            Log.d(TAG, "loading data");
            String locale = context.getString(R.string.locale);
            data.clear();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                final Nomenclature nomenclature = Nomenclature.fromCursor(cursor, locale);
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
            for (Nomenclature species : loadBundledSpecies()) {
                fillMissingNomenclature(missingNomenclatures, localizeNomenclature(Nomenclature.fromSpecies(species, locale), locale));
            }
            missingNomenclatures.clear();
            for (Nomenclature nomenclature : loadBundledNomenclatures()) {
                fillMissingNomenclature(missingNomenclatures, localizeNomenclature(nomenclature, locale));
            }

            // sort nomenclatures
            for (List<Nomenclature> nomenclatures : data.values()) {
                Collections.sort(nomenclatures, comparator);
            }

            loading = false;
            bus.postSticky(new NomenclaturesReadyEvent());

            Log.d(TAG, "data loaded");
        } catch (Throwable t) {
            Reporting.logException(t);
        }
    }

    // moved in new Nomenclatures manager
    @Deprecated
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

    // TODO remove when finish with Room integration
    @Deprecated
    public Iterable<ContentValues> prepareNomenclatureCV(Iterable<Nomenclature> nomenclatures) {
        LinkedList<ContentValues> cvs = new LinkedList<>();
        for (Nomenclature nomenclature : nomenclatures) {
            cvs.add(nomenclature.toCV());
        }
        return cvs;
    }

    // TODO remove when finish with Room integration
    @Deprecated
    public Iterable<ContentValues> prepareSpeciesCV(Iterable<Nomenclature> speciesCollection) {
        LinkedList<ContentValues> cvs = new LinkedList<>();
        for (Nomenclature species : speciesCollection) {
            cvs.add(Nomenclature.fromSpecies(species, context.getString(R.string.locale)).toCV());
        }
        return cvs;
    }

    // TODO remove when finish with Room integration
    @Deprecated
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

    // TODO remove when finish with Room integration
    @Deprecated
    public int prepareNomenclatureCPO(Iterable<Nomenclature> nomenclatureItems, Collection<ContentProviderOperation> buffer) {
        return prepareCPO(prepareNomenclatureCV(nomenclatureItems), buffer);
    }

    // TODO remove when finish with Room integration
    @Deprecated
    public int prepareSpeciesCPO(Iterable<Nomenclature> speciesItems, Collection<ContentProviderOperation> buffer) {
        return prepareCPO(prepareSpeciesCV(speciesItems), buffer);
    }

    // moved in new Nomenclatures manager
    @Deprecated
    public Iterable<Nomenclature> loadBundledNomenclatures() {
        return loadBundledFile("nomenclatures.json");
    }

    // moved in new Nomenclatures manager
    @Deprecated
    public Iterable<Nomenclature> loadBundledSpecies() {
        return loadBundledFile("species.json");
    }

    // moved in new Nomenclatures manager
    @Deprecated
    private Nomenclature localizeNomenclature(Nomenclature nomenclature, String locale) {
        nomenclature.localeLabel = nomenclature.label.get(locale);
        return nomenclature;
    }

    // moved in new Nomenclatures manager
    @Deprecated
    private final Iterable<Nomenclature> loadBundledFile(final String filename) {
        try {
            InputStream is = new BufferedInputStream(context.getAssets().open(filename));
            try {
                return Arrays.asList(SBGsonParser.createParser().fromJson(new InputStreamReader(is), Nomenclature[].class));
            } finally {
                is.close();
            }
        } catch (IOException e) {
            Reporting.logException(e);
            throw new IllegalStateException("Missing bundled file " + filename, e);
        }
    }

    // moved in new Nomenclatures manager
    @Deprecated
    public List<Nomenclature> getNomenclatureOld(String key) {
        key = key.replaceFirst("^form_", "");
        if (!data.containsKey(key)) {
            if (loading) throw new IllegalStateException("Still loading");
            throw new IllegalArgumentException("Unknown nomenclature " + key);
        }
        return data.get(key);
    }

    // moved in new Nomenclatures manager
    @Deprecated
    public List<Nomenclature> getRecentNomenclatures(String key) {
        key = key.replaceFirst("^form_", "");
        List<Nomenclature> nomenclatures = getNomenclatureOld(key);
        Cursor cursor = context.getContentResolver().query(NomenclatureUsesCount.forType(key), USES_COUNT_PROJECTION, null, null, null);
        if (cursor != null) try {
            final ArrayList<Nomenclature> recentItems = new ArrayList<>(cursor.getCount());
            int idx;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                Nomenclature temp = Nomenclature.fromCursor(cursor, context.getString(R.string.locale));
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

    // moved in new Nomenclatures manager
    @Deprecated
    public void addRecentNomenclature(Nomenclature nomenclature) {
        ContentValues cv = nomenclature.toCV();
        cv.put(NomenclatureUsesCountColumns.COUNT, 1);
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(NomenclatureUsesCount.forType(nomenclature.type),
                new String[]{NomenclatureUsesCountColumns._ID, NomenclatureUsesCountColumns.COUNT},
                NomenclatureUsesCountColumns.LABEL_ID + "=?",
                new String[]{nomenclature.label.getLabelId()},
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

        cv.put(NomenclatureUsesCountColumns.LABEL_ID, nomenclature.label.getLabelId());
        resolver.insert(NomenclatureUsesCount.CONTENT_URI, cv);
    }

}
