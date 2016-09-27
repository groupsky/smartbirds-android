package org.bspb.smartbirds.pro.ui.views;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.DatePicker;

import com.crashlytics.android.Crashlytics;

import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.ui.utils.Configuration;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by groupsky on 14-10-17.
 */
public class DateFormInput extends TextViewFormInput implements SupportStorage {

    private static final String TAG = SmartBirdsApplication.TAG + ".DateFormInput";

    protected Calendar mValue;
    protected DateFormat mDateFormat;
    protected DateFormat mStorageFormat;

    public DateFormInput(Context context) {
        this(context, null);
    }

    public DateFormInput(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.dateFormInputStyle);
    }

    public DateFormInput(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mDateFormat = SimpleDateFormat.getDateInstance();
        mStorageFormat = Configuration.STORAGE_DATE_FORMAT;
    }

    public Calendar getValue() {
        return mValue;
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
    public void serializeToStorage(Map<String, String> storage, String fieldName) {
        storage.put(fieldName, mValue != null ? mStorageFormat.format(mValue.getTime()) : "");
    }

    @Override
    public void restoreFromStorage(Map<String, String> storage, String fieldName) {
        try {
            final String value = storage.get(fieldName);
            if (TextUtils.isEmpty(value)) return;
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(mStorageFormat.parse(value));
            setValue(calendar);
        } catch (ParseException e) {
            Crashlytics.logException(e);
            Log.e(TAG, "Invalid storage format", e);
        }
    }

    private class PopupDialog implements DatePickerDialog.OnDateSetListener {

        public void show() {
            final Calendar c = mValue != null ? mValue : Calendar.getInstance();
            final int year = c.get(Calendar.YEAR);
            final int month = c.get(Calendar.MONTH);
            final int day = c.get(Calendar.DAY_OF_MONTH);

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
