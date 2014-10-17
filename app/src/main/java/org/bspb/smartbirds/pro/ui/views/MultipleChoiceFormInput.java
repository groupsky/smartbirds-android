package org.bspb.smartbirds.pro.ui.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;

import java.util.Arrays;

import static android.widget.AdapterView.INVALID_POSITION;

/**
 * Created by groupsky on 14-10-13.
 */
public class MultipleChoiceFormInput extends TextView {

    private static final String TAG = SmartBirdsApplication.TAG + ".MultipleChoiceFormInput";
    CharSequence[] entries;
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
            entries = a.getTextArray(R.styleable.MultipleChoiceFormInput_android_entries);
            if (entries != null) {
                mSelected = new boolean[entries.length];
            }
        } finally {
            a.recycle();

        }
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(getClass().getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(getClass().getName());
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
