package org.bspb.smartbirds.pro.ui.views;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;
import android.widget.TimePicker;

import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.ui.exception.ViewValidationException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.text.format.DateFormat.is24HourFormat;

/**
 * Created by groupsky on 14-10-17.
 */
public class TimeFormInput extends TextView implements SupportRequiredView {

    protected Calendar mValue;
    protected DateFormat mDateFormat;

    private boolean required;

    public TimeFormInput(Context context) {
        this(context, null);
    }

    public TimeFormInput(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.timeFormInputStyle);
    }

    public TimeFormInput(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mDateFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);

        setValue(Calendar.getInstance());

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimeFormInput, defStyle, 0);
        try {
            required = a.getBoolean(R.styleable.TimeFormInput_required, false);
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

    public void setValue(Calendar calendar) {
        mValue = calendar;

        setText(mDateFormat.format(calendar.getTime()));
    }

    @Override
    public boolean performClick() {
        super.performClick();
        new PopupDialog().show();
        return true;
    }

    private class PopupDialog implements TimePickerDialog.OnTimeSetListener {

        public void show() {
            final int hour = mValue.get(Calendar.HOUR);
            final int minute = mValue.get(Calendar.MINUTE);

            final TimePickerDialog dialog = new TimePickerDialog(getContext(), this, hour, minute, is24HourFormat(getContext()));

            dialog.setTitle(getHint());
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);

            dialog.show();
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar value = Calendar.getInstance();
            value.set(Calendar.HOUR, hourOfDay);
            value.set(Calendar.MINUTE, minute);
            setValue(value);
        }
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
