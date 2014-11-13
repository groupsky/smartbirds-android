package org.bspb.smartbirds.pro.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.AutoCompleteTextView;

import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.ui.exception.ViewValidationException;

/**
 * Created by groupsky on 14-10-17.
 */
public class TextFormInput extends AutoCompleteTextView implements SupportRequiredView {

    boolean required;

    public TextFormInput(Context context) {
        this(context, null);
    }

    public TextFormInput(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.textFormInputStyle);
    }

    public TextFormInput(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextFormInput, defStyle, 0);
        try {
            required = a.getBoolean(R.styleable.TextFormInput_required, false);
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
                setError("This field is required");
                throw new ViewValidationException();
            }
        }
    }
}
