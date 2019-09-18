package org.bspb.smartbirds.pro.ui.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.ListView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EView;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.ui.utils.FormsConfig;
import org.bspb.smartbirds.pro.ui.utils.SmartArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static android.app.Dialog.BUTTON_NEGATIVE;
import static android.app.Dialog.BUTTON_NEUTRAL;
import static android.app.Dialog.BUTTON_POSITIVE;
import static android.widget.AdapterView.INVALID_POSITION;
import static org.bspb.smartbirds.pro.ui.utils.Configuration.MULTIPLE_CHOICE_DELIMITER;

@EView
public class SingleChoiceConfigFormInput extends TextViewFormInput implements SupportStorage {

    private int key;

    private SmartArrayAdapter<FormsConfig.NomenclatureConfig> mAdapter;
    /**
     * The currently selected item.
     */
    FormsConfig.NomenclatureConfig mSelectedItem = null;

    OnSelectionChangeListener onSelectionChangeListener;
    FormsConfig.NomenclatureConfig[] mConfig;

    public SingleChoiceConfigFormInput(Context context) {
        this(context, null);
    }

    public SingleChoiceConfigFormInput(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.singleChoiceFormInputStyle);
    }

    public SingleChoiceConfigFormInput(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SingleChoiceConfigFormInput, defStyle, 0);
        try {
            key = a.getInteger(R.styleable.SingleChoiceConfigFormInput_config_entries, -1);
            SmartArrayAdapter<FormsConfig.NomenclatureConfig> adapter = new SmartArrayAdapter<>(context,
                    android.R.layout.select_dialog_singlechoice, new ArrayList<FormsConfig.NomenclatureConfig>());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            setAdapter(adapter);
        } finally {
            a.recycle();
        }
    }

    public void setKey(int key) {
        this.key = key;
        loadData();
    }

    @AfterInject
    void loadData() {
        if (key != -1 && !isInEditMode()) {
            mConfig = FormsConfig.configs.get(key);
            mAdapter.clear();
            for (int i = 0; i < mConfig.length; i++) {
                mAdapter.add(mConfig[i]);
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
    public void setAdapter(SmartArrayAdapter<FormsConfig.NomenclatureConfig> adapter) {
        mAdapter = adapter;
        setText(getText());
    }

    public String getSelectedItem() {
        return mSelectedItem != null
                ? mSelectedItem.getId()
                : null;
    }

    public void setSelection(String value) {
        for (FormsConfig.NomenclatureConfig config : mConfig) {
            if (config.getId().equals(value)) {
                setSelection(config);
                break;
            }
        }
    }

    protected void setSelection(FormsConfig.NomenclatureConfig item) {
        if (mAdapter == null || mAdapter.getCount() == 0) {
            return;
        }

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
            setText(item.getLabelId());
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
            storage.put(fieldName, mSelectedItem.getId());
        } else {
            storage.put(fieldName, "");
        }
    }

    @Override
    public void restoreFromStorage(Map<String, String> storage, String fieldName) {
        String value = storage.get(fieldName);
        setSelection(value);
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

    private class PopupDialog implements DialogInterface.OnClickListener {

        private AlertDialog mPopup;
        private FormsConfig.NomenclatureConfig mLastSelected = null;

        private final DataSetObserver datasetObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                if (mPopup == null) return;
                if (mLastSelected != null) return;
                if (mSelectedItem == null) return;
                mPopup.getListView().setSelection(mAdapter.getPosition(mSelectedItem));
            }
        };

        public void show() {
            loadData();


            EditText view = null;
//            setSelection(new ConfigItem(getText().toString()));

            mAdapter.registerDataSetObserver(datasetObserver);

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setTitle(getHint())
                    .setSingleChoiceItems(mAdapter, mSelectedItem != null ? mAdapter.getPosition(mSelectedItem) : INVALID_POSITION, this)
                    .setCancelable(true)
                    .setNegativeButton(android.R.string.cancel, this)
                    .setNeutralButton(R.string.clear, this);
            mPopup = builder.create();

            final ListView listView = mPopup.getListView();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                listView.setTextDirection(getTextDirection());
                listView.setTextAlignment(getTextAlignment());
            }

            mPopup.show();

            mPopup.getButton(BUTTON_POSITIVE).setEnabled(mSelectedItem != null);
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case BUTTON_NEGATIVE:
                    mPopup.dismiss();
                    break;
                case BUTTON_POSITIVE:
                    if (mLastSelected == null) return;
                    setSelection(mLastSelected);
                    mPopup.dismiss();
                    break;
                case BUTTON_NEUTRAL:
                    setSelection((String) null);
                    mPopup.dismiss();
                    break;
                default:
                    mLastSelected = mAdapter.getItem(which);
                    onClick(dialog, BUTTON_POSITIVE);
                    break;
            }
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
        void onSelectionChange(SingleChoiceConfigFormInput view);
    }
}
