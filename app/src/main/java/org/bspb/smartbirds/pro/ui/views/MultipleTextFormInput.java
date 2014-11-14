package org.bspb.smartbirds.pro.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.MultiAutoCompleteTextView;

import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.ui.exception.ViewValidationException;

/**
 * Created by groupsky on 14-10-17.
 */
public class MultipleTextFormInput extends MultiAutoCompleteTextView implements SupportRequiredView {

    private boolean required;

    public MultipleTextFormInput(Context context) {
        this(context, null);
    }

    public MultipleTextFormInput(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.multipleTextFormInputStyle);
    }

    public MultipleTextFormInput(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MultipleTextFormInput, defStyle, 0);
        try {
            required = a.getBoolean(R.styleable.MultipleTextFormInput_required, false);
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

    @Override
    public void checkRequired() throws ViewValidationException {
        if (required) {
            String value = getText().toString();
            if (value == null || value.equals("")) {
                setError(getContext().getString(R.string.required_field));
                throw new ViewValidationException();
            }
        }
    }
}
