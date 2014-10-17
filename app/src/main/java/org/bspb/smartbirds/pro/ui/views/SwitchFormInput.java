package org.bspb.smartbirds.pro.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CheckBox;

import org.bspb.smartbirds.pro.R;

/**
 * Created by groupsky on 14-10-17.
 */
public class SwitchFormInput extends CheckBox {
    public SwitchFormInput(Context context) {
        this(context, null);
    }

    public SwitchFormInput(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.switchFormInputStyle);
    }

    public SwitchFormInput(Context context, AttributeSet attrs, int defStyle) {
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
