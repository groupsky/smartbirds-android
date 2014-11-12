package org.bspb.smartbirds.pro.ui.views;

import static android.app.Dialog.BUTTON_NEGATIVE;
import static android.app.Dialog.BUTTON_POSITIVE;
import static android.app.Dialog.BUTTON_NEUTRAL;
import static android.text.InputType.TYPE_MASK_VARIATION;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;
import static android.view.inputmethod.EditorInfo.IME_FLAG_NO_EXTRACT_UI;
import static android.view.inputmethod.EditorInfo.IME_ACTION_NONE;
import static android.view.inputmethod.EditorInfo.TYPE_TEXT_VARIATION_FILTER;
import static android.widget.AdapterView.INVALID_POSITION;
import static android.view.ViewGroup.LayoutParams.*;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.bspb.smartbirds.pro.R;

/**
 * Created by groupsky on 14-10-10.
 */
public class SingleChoiceFormInput extends TextView {

    private ArrayAdapter<CharSequence> mAdapter;
    private DataSetObserver mDataSetObserver;
    /**
     * The position within the adapter's data set of the currently selected item.
     */
    int mSelectedPosition = INVALID_POSITION;

    public SingleChoiceFormInput(Context context) {
        this(context, null);
    }

    public SingleChoiceFormInput(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.singleChoiceFormInputStyle);
    }

    public SingleChoiceFormInput(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SingleChoiceFormInput, defStyle, 0);
        try {
            CharSequence[] entries = a.getTextArray(R.styleable.SingleChoiceFormInput_android_entries);
            if (entries != null) {
                ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(context,
                        android.R.layout.select_dialog_singlechoice, entries);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                setAdapter(adapter);
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

    /**
     * The Adapter is used to provide the data which backs this Spinner.
     * It also provides methods to transform spinner items based on their position
     * relative to the selected item.
     *
     * @param adapter The SpinnerAdapter to use for this Spinner
     */
    public void setAdapter(ArrayAdapter<CharSequence> adapter) {
        mAdapter = adapter;
        setSelection(INVALID_POSITION);
    }

    public void setSelection(int position) {
        if (position != mSelectedPosition) {
            mSelectedPosition = position;
            if (position != INVALID_POSITION) {
                setText(String.valueOf(mAdapter.getItem(position)));
            } else {
                setText("");
            }
        }
    }

    @Override
    public boolean performClick() {
        super.performClick();
        new PopupDialog().show();
        return true;
    }

    private class PopupDialog implements DialogInterface.OnClickListener, TextWatcher, DialogInterface.OnCancelListener, Filter.FilterListener {

        private AlertDialog mPopup;
        private int mLastSelected = INVALID_POSITION;

        public void show() {
            boolean needFilter = mAdapter.getCount() > 20;

            EditText view = null;
            if (needFilter) {
                view = new EditText(getContext());
                view.setImeOptions(view.getImeOptions() | IME_FLAG_NO_EXTRACT_UI | IME_ACTION_DONE);
                view.setInputType(TYPE_TEXT_VARIATION_FILTER);
                view.setHint(getHint());
                view.setSingleLine();
                view.addTextChangedListener(this);
            }

            ((Filterable) mAdapter).getFilter().filter(null);

            setSelection(mAdapter.getPosition(getText()));

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setTitle(getHint())
                    .setSingleChoiceItems(mAdapter, mSelectedPosition, this)
                    .setCancelable(true)
                    .setOnCancelListener(this)
                    .setNegativeButton(android.R.string.cancel, this)
                    .setNeutralButton(R.string.clear, this);
            if (needFilter) {
                builder.setCustomTitle(view);
            }
            mPopup = builder.create();

            final ListView listView = mPopup.getListView();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                listView.setTextDirection(getTextDirection());
                listView.setTextAlignment(getTextAlignment());
            }

            mPopup.show();

            mPopup.getButton(BUTTON_POSITIVE).setEnabled(mSelectedPosition != INVALID_POSITION);
            if (needFilter) {
                mPopup.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                view.requestFocus();
            }
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case BUTTON_NEGATIVE:
                    mPopup.dismiss();
                    ((Filterable) mAdapter).getFilter().filter(null, this);
                    break;
                case BUTTON_POSITIVE:
                    if (mLastSelected == INVALID_POSITION) return;
                    setSelection(mLastSelected);
                    mPopup.dismiss();
                    ((Filterable) mAdapter).getFilter().filter(null, this);
                    break;
                case BUTTON_NEUTRAL:
                    setSelection(INVALID_POSITION);
                    mPopup.dismiss();
                    ((Filterable) mAdapter).getFilter().filter(null, this);
                    break;
                default:
                    mLastSelected = which;
                    onClick(dialog, BUTTON_POSITIVE);
                    break;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            ((Filterable) mAdapter).getFilter().filter(s);
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            ((Filterable) mAdapter).getFilter().filter(null);
        }

        @Override
        public void onFilterComplete(int count) {
            setSelection(mAdapter.getPosition(getText()));
        }
    }
}
