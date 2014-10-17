package org.bspb.smartbirds.pro.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.MultiAutoCompleteTextView;

import org.bspb.smartbirds.pro.R;

/**
 * Created by groupsky on 14-10-17.
 */
public class MultipleTextFormInput extends MultiAutoCompleteTextView {
    public MultipleTextFormInput(Context context) {
        this(context, null);
    }

    public MultipleTextFormInput(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.multipleTextFormInputStyle);
    }

    public MultipleTextFormInput(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
}
