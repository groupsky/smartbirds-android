package org.bspb.smartbirds.pro.ui.views;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TimePicker;

import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.tools.Reporting;
import org.bspb.smartbirds.pro.ui.utils.Configuration;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

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
        mStorageFormat = Configuration.STORAGE_TIME_FORMAT;
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
            Reporting.logException(e);
            Log.e(TAG, "Invalid storage format", e);
        }
    }

    private class PopupDialog implements TimePickerDialog.OnTimeSetListener {

        public void show() {
            final Calendar c = mValue != null ? mValue : Calendar.getInstance();
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

    @Override
    public Parcelable onSaveInstanceState() {
        return new InstanceState(super.onSaveInstanceState(), mValue != null ? mValue.getTimeInMillis() : 0);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof InstanceState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        InstanceState ss = (InstanceState) state;
        super.onRestoreInstanceState(((InstanceState) state).getSuperState());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ss.timestamp);
        setValue(calendar);
    }

    public static class InstanceState extends BaseSavedState {
        private long timestamp;

        public InstanceState(Parcelable superState, long timestamp) {
            super(superState);
            this.timestamp = timestamp;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeLong(this.timestamp);
        }

        protected InstanceState(Parcel source, ClassLoader loader) {
            super(source);
            this.timestamp = source.readLong();
        }

        public static final ClassLoaderCreator<InstanceState> CREATOR = new ClassLoaderCreator<InstanceState>() {

            @Override
            public InstanceState createFromParcel(Parcel source, ClassLoader loader) {
                return new InstanceState(source, loader);
            }

            @Override
            public InstanceState createFromParcel(Parcel source) {
                return createFromParcel(source, null);
            }

            @Override
            public InstanceState[] newArray(int size) {
                return new TimeFormInput.InstanceState[size];
            }
        };
    }
}
