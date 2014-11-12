package org.bspb.smartbirds.pro.ui.views;

import android.content.Context;
import android.util.AttributeSet;

import org.bspb.smartbirds.pro.R;

/**
 * Created by groupsky on 14-10-17.
 */
public class FloatNumberFormInput extends TextFormInput {
    public FloatNumberFormInput(Context context) {
        this(context, null);
    }

    public FloatNumberFormInput(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.floatNumberFormInputStyle);
    }

    public FloatNumberFormInput(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
