package org.bspb.smartbirds.pro.ui.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListView;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EView;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.backend.dto.Nomenclature;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.NomenclaturesReadyEvent;
import org.bspb.smartbirds.pro.tools.SBGsonParser;
import org.bspb.smartbirds.pro.ui.utils.Configuration;
import org.bspb.smartbirds.pro.utils.NomenclaturesManagerNew;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.bspb.smartbirds.pro.tools.Reporting.logException;
import static org.bspb.smartbirds.pro.ui.utils.Configuration.MULTIPLE_CHOICE_SPLITTER;

/**
 * Created by groupsky on 14-10-13.
 */
@EView
public class MultipleChoiceFormInput extends TextViewFormInput implements SupportStorage {

    CharSequence[] entries;
    Nomenclature[] items;

    private final CharSequence key;
    NomenclaturesManagerNew nomenclatures = NomenclaturesManagerNew.Companion.getInstance();
    @Bean
    EEventBus bus;

    /**
     * The position within the adapter's data set of the currently selected item.
     */
    boolean[] mSelected = null;

    public MultipleChoiceFormInput(Context context) {
        this(context, null);
    }

    public MultipleChoiceFormInput(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.multipleChoiceFormInputStyle);
    }

    public MultipleChoiceFormInput(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MultipleChoiceFormInput, defStyle, 0);
        try {

            key = a.getText(R.styleable.MultipleChoiceFormInput_entriesType);
        } finally {
            a.recycle();

        }
    }

    public void setSelectedItems(boolean[] selected) {
        mSelected = selected;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mSelected.length; i++) {
            if (mSelected[i]) {
                if (sb.length() > 0) sb.append(Configuration.MULTIPLE_CHOICE_DELIMITER);
                sb.append(entries[i]);
            }
        }
        setText(sb.toString());
        if (sb.length() > 0) {
            setError(null);
        }
    }

    @Override
    public boolean performClick() {
        super.performClick();
        new PopupDialog().show();
        return true;
    }

    public void onEventMainThread(NomenclaturesReadyEvent e) {
        Log.d(TAG, "nomenclatures loaded, preparing selected...");
        prepareSelected();
        try {
            bus.unregister(this);
        } catch (Throwable t) {
            logException(t);
        }
    }

    void prepareSelected() {
        if (entries == null && key != null) {
            if (nomenclatures.isLoading()) {
                Log.d(TAG, "nomenclatures not loaded, waiting to load...");
                try {
                    bus.registerSticky(this);
                } catch (Throwable t) {
                    logException(t);
                }
                return;
            }
            List<Nomenclature> values = nomenclatures.getNomenclature(key.toString());
            if (values != null) {
                entries = new CharSequence[values.size()];
                items = new Nomenclature[values.size()];
                mSelected = new boolean[values.size()];
                for (int i = 0; i < values.size(); i++) {
                    items[i] = values.get(i);
                    entries[i] = values.get(i).localeLabel;
                }

                String[] selected = getText().toString().split(MULTIPLE_CHOICE_SPLITTER);
                Arrays.sort(selected);
                for (int idx = 0; idx < entries.length; idx++) {
                    mSelected[idx] = Arrays.binarySearch(selected, entries[idx]) >= 0;
                }
            }
        }
    }

    @Override
    public void serializeToStorage(Map<String, String> storage, String fieldName) {
        storage.put(fieldName, getText().toString());
        prepareSelected();
        List<Nomenclature> selectedItems = new LinkedList<>();
        for (int idx = 0; idx < mSelected.length; idx++) {
            if (mSelected[idx]) {
                selectedItems.add(items[idx]);
            }
        }
        storage.put(fieldName + ".json", SBGsonParser.createParser().toJson(selectedItems));
    }

    @Override
    public void restoreFromStorage(Map<String, String> storage, String fieldName) {
        setText(storage.get(fieldName));
    }

    private class PopupDialog implements DialogInterface.OnClickListener, DialogInterface.OnMultiChoiceClickListener {

        private AlertDialog mPopup;
        boolean[] selected;

        public void show() {
            prepareSelected();

            this.selected = mSelected.clone();
            mPopup = new AlertDialog.Builder(getContext())
                    .setTitle(getHint())
                    .setMultiChoiceItems(entries, this.selected, this)
                    .setCancelable(true)
                    .setPositiveButton(android.R.string.ok, this)
                    .setNegativeButton(android.R.string.cancel, this)
                    .create();
            final ListView listView = mPopup.getListView();
            listView.setTextDirection(getTextDirection());
            listView.setTextAlignment(getTextAlignment());
            mPopup.show();
        }

        @Override
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            this.selected[which] = isChecked;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_NEGATIVE:
                    break;
                case AlertDialog.BUTTON_POSITIVE:
                    setSelectedItems(selected);
                    break;
            }
            mPopup.dismiss();
        }
    }
}
