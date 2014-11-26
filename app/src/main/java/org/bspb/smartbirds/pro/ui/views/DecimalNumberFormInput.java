package org.bspb.smartbirds.pro.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.ui.exception.ViewValidationException;

/**
 * Created by groupsky on 14-10-17.
 */
public class DecimalNumberFormInput extends TextFormInput {

    public DecimalNumberFormInput(Context context) {
        this(context, null);
    }

    public DecimalNumberFormInput(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.decimalNumberFormInputStyle);
    }

    public DecimalNumberFormInput(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

}
