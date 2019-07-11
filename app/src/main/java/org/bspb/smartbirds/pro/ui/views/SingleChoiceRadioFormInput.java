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

import retrofit2.http.POST;

@EViewGroup(R.layout.single_choice_radio)
public class SingleChoiceRadioFormInput extends FrameLayout implements SupportRequiredView, SupportStorage {

    private boolean mRequired;
    private CharSequence mEntriesKey;
    private CharSequence mHint;

    @ViewById(R.id.single_choice_radio_hint)
    protected TextView hintTextView;
    @ViewById(R.id.single_choice_radio_group)
    protected RadioGroup radioGroup;
    private FormsConfig mConfig;


    public SingleChoiceRadioFormInput(Context context) {
        this(context, null);
    }

    public SingleChoiceRadioFormInput(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.singleChoiceRadioFormInputStyle);
    }

    public SingleChoiceRadioFormInput(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SingleChoiceRadioFormInput, defStyleAttr, 0);
        try {
            mHint = a.getText(R.styleable.SingleChoiceRadioFormInput_hint);
            mEntriesKey = a.getText(R.styleable.SingleChoiceRadioFormInput_config_entries);
            mRequired = a.getBoolean(R.styleable.SingleChoiceRadioFormInput_required, false);
        } finally {
            a.recycle();
        }
    }

    @AfterViews
    protected void init() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1) {
                    ((RadioButton) group.getChildAt(group.getChildCount() - 1)).setError(null);
                }
            }
        });

        if (mHint != null && mHint.length() > 0) {
            hintTextView.setText(mHint + ":");
        }

        if (mEntriesKey == null) {
            throw new IllegalArgumentException("Entries are required for this form input.");
        }

        mConfig = FormsConfig.valueOf(mEntriesKey.toString());

        for (int i = 0; i < mConfig.getValues().length; i++) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(mConfig.getLabels()[i]);
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
            storage.put(fieldName, mConfig.getValues()[idx]);
        } else {
            storage.put(fieldName, "");
        }

    }

    @Override
    public void restoreFromStorage(Map<String, String> storage, String fieldName) {
        String value = storage.get(fieldName);
        for (int i = 0; i < mConfig.getValues().length; i++) {
            if (mConfig.getValues()[i].equals(value)) {
                ((RadioButton) radioGroup.getChildAt(i)).setChecked(true);
                break;
            }
        }
    }
}
