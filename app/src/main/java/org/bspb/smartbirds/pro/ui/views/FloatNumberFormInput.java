package org.bspb.smartbirds.pro.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.ui.exception.ViewValidationException;

/**
 * Created by groupsky on 14-10-17.
 */
public class FloatNumberFormInput extends TextFormInput implements SupportRequiredView {

    private boolean required;

    public FloatNumberFormInput(Context context) {
        this(context, null);
    }

    public FloatNumberFormInput(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.floatNumberFormInputStyle);
    }

    public FloatNumberFormInput(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FloatNumberFormInput, defStyle, 0);
        try {
            required = a.getBoolean(R.styleable.FloatNumberFormInput_required, false);
        } finally {
            a.recycle();
        }
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
