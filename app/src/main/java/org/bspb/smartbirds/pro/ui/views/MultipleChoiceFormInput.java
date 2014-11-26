package org.bspb.smartbirds.pro.ui.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ListView;
import android.widget.TextView;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EView;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.ui.utils.NomenclaturesBean;

import java.util.List;

/**
 * Created by groupsky on 14-10-13.
 */
@EView
public class MultipleChoiceFormInput extends TextViewFormInput {

    CharSequence[] entries;

    private final CharSequence key;
    @Bean
    NomenclaturesBean nomenclatures;
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

            key = a.getText(R.styleable.MultipleChoiceFormInput_entries);
        } finally {
            a.recycle();

        }
    }

    public void setSelectedItems(boolean[] selected) {
        mSelected = selected;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mSelected.length; i++) {
            if (mSelected[i]) {
                if (sb.length() > 0) sb.append(", ");
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

    private class PopupDialog implements DialogInterface.OnClickListener, DialogInterface.OnMultiChoiceClickListener {

        private AlertDialog mPopup;
        boolean[] selected;

        public void show() {
            if (entries == null && key != null) {
                List<String> values = nomenclatures.getNomenclature(key.toString());
                if (values != null) {
                    entries = new CharSequence[values.size()];
                    mSelected = new boolean[values.size()];
                    for (int i = 0; i < values.size(); i++) {
                        entries[i] = values.get(i);
                    }

                    String[] items = getText().toString().split(", ");
                    if (items != null) {
                        for (String item : items) {
                            if (values.contains(item)) {
                                mSelected[values.indexOf(item)] = true;
                            }
                        }
                    }
                }
            }


            this.selected = mSelected.clone();
            mPopup = new AlertDialog.Builder(getContext())
                    .setTitle(getHint())
                    .setMultiChoiceItems(entries, this.selected, this)
                    .setCancelable(true)
                    .setPositiveButton(android.R.string.ok, this)
                    .setNegativeButton(android.R.string.cancel, this)
                    .create();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                final ListView listView = mPopup.getListView();
                listView.setTextDirection(getTextDirection());
                listView.setTextAlignment(getTextAlignment());
            }
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
