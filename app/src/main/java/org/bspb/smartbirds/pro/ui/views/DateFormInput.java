package org.bspb.smartbirds.pro.ui.views;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.DatePicker;
import android.widget.TextView;

import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.ui.exception.ViewValidationException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by groupsky on 14-10-17.
 */
public class DateFormInput extends TextView implements SupportRequiredView {

    protected Calendar mValue;
    protected DateFormat mDateFormat;

    private boolean required;

    public DateFormInput(Context context) {
        this(context, null);
    }

    public DateFormInput(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.dateFormInputStyle);
    }

    public DateFormInput(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mDateFormat = SimpleDateFormat.getDateInstance();

        setValue(Calendar.getInstance());

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DateFormInput, defStyle, 0);
        try {
            required = a.getBoolean(R.styleable.DateFormInput_required, false);
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

    private class PopupDialog implements DatePickerDialog.OnDateSetListener {

        public void show() {
            final int year = mValue.get(Calendar.YEAR);
            final int month = mValue.get(Calendar.MONTH);
            final int day = mValue.get(Calendar.DAY_OF_MONTH);

            final DatePickerDialog dialog = new DatePickerDialog(getContext(), this, year, month, day);

            dialog.setTitle(getHint());
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);

            dialog.show();
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar value = Calendar.getInstance();
            value.set(Calendar.YEAR, year);
            value.set(Calendar.MONTH, monthOfYear);
            value.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setValue(value);
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
