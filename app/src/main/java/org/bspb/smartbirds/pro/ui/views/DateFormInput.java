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
public class DateFormInput extends TextViewFormInput {

    protected Calendar mValue;
    protected DateFormat mDateFormat;

    public DateFormInput(Context context) {
        this(context, null);
    }

    public DateFormInput(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.dateFormInputStyle);
    }

    public DateFormInput(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mDateFormat = SimpleDateFormat.getDateInstance();
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

}
