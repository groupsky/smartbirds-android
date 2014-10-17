package org.bspb.smartbirds.pro.ui.views;

import android.content.Context;
import android.util.AttributeSet;

import org.bspb.smartbirds.pro.R;

/**
 * Created by groupsky on 14-10-17.
 */
public class MemoFormInput extends TextFormInput {
    public MemoFormInput(Context context) {
        this(context, null);
    }

    public MemoFormInput(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.memoFormInputStyle);
    }

    public MemoFormInput(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
