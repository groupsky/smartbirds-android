package org.bspb.smartbirds.pro.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.ui.exception.ViewValidationException;
import org.bspb.smartbirds.pro.ui.utils.FormsConfig;

import java.util.Map;

@EViewGroup(R.layout.single_choice_radio)
public class SingleChoiceConfigRadioFormInput extends FrameLayout implements SupportRequiredView, SupportStorage {

    public interface OnValueChangeListener {
        void onValueChanged(String value);
    }

    private boolean mRequired;
    private int mEntriesKey;
    private CharSequence mHint;
    private boolean mIsVertical;


    @ViewById(R.id.single_choice_radio_hint)
    protected TextView hintTextView;
    @ViewById(R.id.single_choice_radio_group)
    protected RadioGroup radioGroup;
    private FormsConfig.NomenclatureConfig[] mConfig;
    private OnValueChangeListener mOnValueChangeListener;
    private String mSelectedItem;

    public SingleChoiceConfigRadioFormInput(Context context) {
        this(context, null);
    }

    public SingleChoiceConfigRadioFormInput(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.singleChoiceConfigRadioFormInputStyle);
    }

    public SingleChoiceConfigRadioFormInput(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SingleChoiceConfigRadioFormInput, defStyleAttr, 0);
        try {
            mHint = a.getText(R.styleable.SingleChoiceConfigRadioFormInput_hint);
            mEntriesKey = a.getInteger(R.styleable.SingleChoiceConfigRadioFormInput_config_entries, -1);
            mRequired = a.getBoolean(R.styleable.SingleChoiceConfigRadioFormInput_required, false);
            mIsVertical = a.getInteger(R.styleable.SingleChoiceConfigRadioFormInput_orientation, 0) == 1;
        } finally {
            a.recycle();
        }
    }

    @AfterViews
    protected void init() {
        if (mIsVertical) {
            radioGroup.setOrientation(LinearLayout.VERTICAL);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1) {
                    ((RadioButton) group.getChildAt(group.getChildCount() - 1)).setError(null);
                }

                if (mOnValueChangeListener != null) {
                    if (checkedId == -1) {
                        mSelectedItem = null;
                        mOnValueChangeListener.onValueChanged(null);
                    } else {
                        View radioButton = radioGroup.findViewById(checkedId);
                        int idx = radioGroup.indexOfChild(radioButton);
                        mSelectedItem = mConfig[idx].getId();
                        mOnValueChangeListener.onValueChanged(mSelectedItem);


                    }

                }
            }
        });

        if (mHint != null && mHint.length() > 0) {
            hintTextView.setText(mHint + ":");
        }

        if (mEntriesKey == -1) {
            throw new IllegalArgumentException("Entries are required for this form input.");
        }

        mConfig = FormsConfig.configs.get(mEntriesKey);
        for (int i = 0; i < mConfig.length; i++) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(mConfig[i].getLabelId());
            radioGroup.addView(radioButton, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    @Override
    public void checkRequired() throws ViewValidationException {
        if (mRequired && isEnabled()) {
            if (radioGroup.getCheckedRadioButtonId() == -1) {
                ((RadioButton) radioGroup.getChildAt(radioGroup.getChildCount() - 1)).setError(getContext().getString(R.string.required_field));
                throw new ViewValidationException();
            } else {
                ((RadioButton) radioGroup.getChildAt(radioGroup.getChildCount() - 1)).setError(null);
            }
        } else {
            ((RadioButton) radioGroup.getChildAt(radioGroup.getChildCount() - 1)).setError(null);
        }
    }

    public boolean isRequired() {
        return mRequired;
    }

    public void setRequired(boolean required) {
        this.mRequired = required;
    }

    @Override
    public void serializeToStorage(Map<String, String> storage, String fieldName) {
        int radioButtonID = radioGroup.getCheckedRadioButtonId();
        if (radioButtonID != -1) {
            View radioButton = radioGroup.findViewById(radioButtonID);
            int idx = radioGroup.indexOfChild(radioButton);
            storage.put(fieldName, mConfig[idx].getId());
        } else {
            storage.put(fieldName, "");
        }

    }

    @Override
    public void restoreFromStorage(Map<String, String> storage, String fieldName) {
        String value = storage.get(fieldName);
        for (int i = 0; i < mConfig.length; i++) {
            if (mConfig[i].getId().equals(value)) {
                ((RadioButton) radioGroup.getChildAt(i)).setChecked(true);
                mSelectedItem = value;
                break;
            }
        }
    }

    public void setOnValueChangeListener(OnValueChangeListener onValueChangeListener) {
        this.mOnValueChangeListener = onValueChangeListener;
    }

    public String getSelectedItem() {
        return mSelectedItem;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (radioGroup == null) {
            return;
        }

        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(enabled);
        }
        radioGroup.setEnabled(enabled);
    }
}
