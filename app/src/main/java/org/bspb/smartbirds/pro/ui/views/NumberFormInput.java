package org.bspb.smartbirds.pro.ui.views;

import android.content.Context;
import android.util.AttributeSet;

import org.bspb.smartbirds.pro.R;

/**
 * Created by groupsky on 14-10-17.
 */
public class NumberFormInput extends TextFormInput {
    public NumberFormInput(Context context) {
        this(context, null);
    }

    public NumberFormInput(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.numberFormInputStyle);
    }

    public NumberFormInput(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
