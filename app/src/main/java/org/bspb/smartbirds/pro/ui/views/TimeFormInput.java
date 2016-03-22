package org.bspb.smartbirds.pro.ui.views;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;
import android.widget.TimePicker;

import com.crashlytics.android.Crashlytics;

import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.ui.exception.ViewValidationException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.text.format.DateFormat.is24HourFormat;

/**
 * Created by groupsky on 14-10-17.
 */
public class TimeFormInput extends TextViewFormInput implements SupportStorage {

    private static final String TAG = SmartBirdsApplication.TAG + ".DateFormInput";

    protected Calendar mValue;
    protected DateFormat mDateFormat;
    protected DateFormat mStorageFormat;

    public TimeFormInput(Context context) {
        this(context, null);
    }

    public TimeFormInput(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.timeFormInputStyle);
    }

    public TimeFormInput(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mDateFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);
        mStorageFormat = new SimpleDateFormat("kk:mm:ss", Locale.ENGLISH);
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

    @Override
    public String valueForStorage() {
        return mStorageFormat.format(mValue.getTime());
    }

    @Override
    public void restoreFromStorage(String value) {
        try {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(mStorageFormat.parse(value));
            setValue(calendar);
        } catch (ParseException e) {
            Crashlytics.logException(e);
            Log.e(TAG, "Invalid storage format", e);
        }
    }

    private class PopupDialog implements TimePickerDialog.OnTimeSetListener {

        public void show() {
            final Calendar c = mValue!=null?mValue:Calendar.getInstance();
            final int hour = c.get(Calendar.HOUR_OF_DAY);
            final int minute = c.get(Calendar.MINUTE);

            final TimePickerDialog dialog = new TimePickerDialog(getContext(), this, hour, minute, is24HourFormat(getContext()));

            dialog.setTitle(getHint());
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);

            dialog.show();
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar value = Calendar.getInstance();
            value.set(Calendar.HOUR_OF_DAY, hourOfDay);
            value.set(Calendar.MINUTE, minute);
            setValue(value);
        }
    }

}
