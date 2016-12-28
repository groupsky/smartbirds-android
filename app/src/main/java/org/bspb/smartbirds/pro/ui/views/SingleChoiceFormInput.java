package org.bspb.smartbirds.pro.ui.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EView;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.backend.dto.Nomenclature;
import org.bspb.smartbirds.pro.ui.utils.NomenclaturesBean;
import org.bspb.smartbirds.pro.ui.utils.SmartArrayAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.app.Dialog.BUTTON_NEGATIVE;
import static android.app.Dialog.BUTTON_NEUTRAL;
import static android.app.Dialog.BUTTON_POSITIVE;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;
import static android.view.inputmethod.EditorInfo.IME_FLAG_NO_EXTRACT_UI;
import static android.view.inputmethod.EditorInfo.TYPE_TEXT_VARIATION_FILTER;
import static android.widget.AdapterView.INVALID_POSITION;
import static org.bspb.smartbirds.pro.ui.utils.Configuration.MULTIPLE_CHOICE_DELIMITER;
import static org.bspb.smartbirds.pro.ui.utils.Configuration.MULTIPLE_CHOICE_SPLITTER;

/**
 * Created by groupsky on 14-10-10.
 */
@EView
public class SingleChoiceFormInput extends TextViewFormInput implements SupportStorage {

    private final CharSequence key;

    @Bean
    NomenclaturesBean nomenclatures;

    private SmartArrayAdapter<NomenclatureItem> mAdapter;
    /**
     * The currently selected item.
     */
    NomenclatureItem mSelectedItem = null;

    OnSelectionChangeListener onSelectionChangeListener;

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
            key = a.getText(R.styleable.SingleChoiceFormInput_entries);
            SmartArrayAdapter<NomenclatureItem> adapter = new SmartArrayAdapter<>(context,
                    android.R.layout.select_dialog_singlechoice, new ArrayList<NomenclatureItem>());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            setAdapter(adapter);
        } finally {
            a.recycle();
        }
    }

    @AfterInject
    void loadData() {
        if (key != null && !isInEditMode()) {
            List<Nomenclature> values = nomenclatures.getNomenclature(key.toString());
            mAdapter.clear();
            for (Nomenclature value : values) {
                mAdapter.add(new NomenclatureItem(value));
            }
            mAdapter.notifyDataSetChanged();
            if (mAdapter.getCount() == 1) {
                setSelection(mAdapter.getItem(0));
            }
        }
    }

    /**
     * The Adapter is used to provide the data which backs this Spinner.
     * It also provides methods to transform spinner items based on their position
     * relative to the selected item.
     *
     * @param adapter The SpinnerAdapter to use for this Spinner
     */
    public void setAdapter(SmartArrayAdapter<NomenclatureItem> adapter) {
        mAdapter = adapter;
        setText(getText());
    }

    public String getSelection() {
        return mSelectedItem != null ? mSelectedItem.label : null;
    }

    public Nomenclature getSelectedItem() {
        return mSelectedItem != null
                ? mSelectedItem.nomenclature
                : null;
    }

    public void setSelection(String label) {
        setSelection(new NomenclatureItem(label));
    }

    protected void setSelection(NomenclatureItem item) {
        // find our item that has nomenclature inside
        if (item != null) {
            int idx = mAdapter.getPosition(item);
            if (idx != INVALID_POSITION) {
                item = mAdapter.getItem(idx);
            } else {
                item = null;
            }
        }

        if (item != null) {
            if (item.equals(mSelectedItem)) return;
        } else if (mSelectedItem == null) return;

        mSelectedItem = item;
        if (item != null) {
            setText(item.label);
            setError(null);
        } else {
            setText("");
        }

        if (onSelectionChangeListener != null) onSelectionChangeListener.onSelectionChange(this);
    }

    @Override
    public boolean performClick() {
        super.performClick();
        new PopupDialog().show();
        return true;
    }

    @Override
    public void serializeToStorage(Map<String, String> storage, String fieldName) {
        storage.put(fieldName, getText().toString().replace("\n", MULTIPLE_CHOICE_DELIMITER));
        if (mSelectedItem != null) {
            storage.put(fieldName + ".bg", mSelectedItem.nomenclature.label.bg);
            storage.put(fieldName + ".en", mSelectedItem.nomenclature.label.en);
        } else {
            storage.put(fieldName + ".bg", "");
            storage.put(fieldName + ".en", "");
        }
    }

    @Override
    public void restoreFromStorage(Map<String, String> storage, String fieldName) {
        String value = storage.get(fieldName);
        setText(value);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (mAdapter != null) {
            setSelection(new NomenclatureItem(text.toString()));
        }
        super.setText(text, type);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return new InstanceState(super.onSaveInstanceState(), getText());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof InstanceState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        InstanceState ss = (InstanceState) state;
        super.onRestoreInstanceState(((InstanceState) state).getSuperState());

        setText(ss.text);
    }

    public OnSelectionChangeListener getOnSelectionChangeListener() {
        return onSelectionChangeListener;
    }

    public void setOnSelectionChangeListener(OnSelectionChangeListener onSelectionChangeListener) {
        this.onSelectionChangeListener = onSelectionChangeListener;
    }

    private class PopupDialog implements DialogInterface.OnClickListener, TextWatcher, DialogInterface.OnCancelListener, Filter.FilterListener {

        private AlertDialog mPopup;
        private NomenclatureItem mLastSelected = null;

        public void show() {
            loadData();

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

            mAdapter.getFilter().filter(null);

            setSelection(new NomenclatureItem(getText().toString()));

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setTitle(getHint())
                    .setSingleChoiceItems(mAdapter, mSelectedItem != null ? mAdapter.getPosition(mSelectedItem) : INVALID_POSITION, this)
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

            mPopup.getButton(BUTTON_POSITIVE).setEnabled(mSelectedItem != null);
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
                    if (mLastSelected == null) return;
                    setSelection(mLastSelected);
                    mPopup.dismiss();
                    ((Filterable) mAdapter).getFilter().filter(null, this);
                    break;
                case BUTTON_NEUTRAL:
                    setSelection((NomenclatureItem) null);
                    mPopup.dismiss();
                    ((Filterable) mAdapter).getFilter().filter(null, this);
                    break;
                default:
                    mLastSelected = mAdapter.getItem(which);
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
            setSelection(new NomenclatureItem(getText().toString()));
        }
    }

    static class NomenclatureItem {
        final Nomenclature nomenclature;
        final String label;

        NomenclatureItem(String label) {
            this.label = label;
            this.nomenclature = null;
        }

        NomenclatureItem(Nomenclature nomenclature) {
            this.nomenclature = nomenclature;
            label = TextUtils.join("\n", nomenclature.localeLabel.trim().split(MULTIPLE_CHOICE_SPLITTER));
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            NomenclatureItem that = (NomenclatureItem) o;

            return label.equals(that.label);

        }

        @Override
        public int hashCode() {
            return label.hashCode();
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public static class InstanceState extends BaseSavedState {
        private final String text;

        public InstanceState(Parcelable superState, CharSequence text) {
            this(superState, text != null ? text.toString() : null);
        }

        public InstanceState(Parcelable superState, String text) {
            super(superState);
            this.text = text;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(this.text);
        }

        protected InstanceState(Parcel source, ClassLoader loader) {
            super(source);
            this.text = source.readString();
        }

        public static final ClassLoaderCreator<InstanceState> CREATOR = new ClassLoaderCreator<InstanceState>() {

            @Override
            public InstanceState createFromParcel(Parcel source, ClassLoader loader) {
                return new InstanceState(source, loader);
            }

            @Override
            public InstanceState createFromParcel(Parcel source) {
                return createFromParcel(source, null);
            }

            @Override
            public InstanceState[] newArray(int size) {
                return new InstanceState[size];
            }
        };
    }

    public interface OnSelectionChangeListener {
        void onSelectionChange(SingleChoiceFormInput view);
    }
}
