package org.bspb.smartbirds.pro.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.LinearLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EView;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.tools.SBGsonParser;
import org.bspb.smartbirds.pro.ui.utils.ViewUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by dani on 26.02.18.
 */

@EView
public class MultipleChoiceFullScreenFormInput extends LinearLayout implements MultipleChoiceFullScreenRow.OnDeleteListener, MultipleChoiceFullScreenRow.OnPopulatedListener, SupportStorage {


    private static final String TAG = MultipleChoiceFullScreenFormInput.class.getSimpleName();
    private final CharSequence key;
    private final CharSequence hint;

    private boolean isUpdating = false;

    public MultipleChoiceFullScreenFormInput(Context context) {
        this(context, null);
    }

    public MultipleChoiceFullScreenFormInput(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.multipleChoiceFullScreenFormInputStyle);
    }

    public MultipleChoiceFullScreenFormInput(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MultipleChoiceFullScreenFormInput, defStyle, 0);
        try {
            hint = a.getText(R.styleable.MultipleChoiceFullScreenFormInput_hint);
            key = a.getText(R.styleable.MultipleChoiceFullScreenFormInput_entries);
        } finally {
            a.recycle();

        }
    }

    @Override
    public void serializeToStorage(Map<String, String> storage, String fieldName) {
        List<String> selectedItems = new LinkedList<>();

        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof MultipleChoiceFullScreenRow) {
                if (((MultipleChoiceFullScreenRow) view).isPopulated())
                    selectedItems.add(((MultipleChoiceFullScreenRow) view).getText() + "");
            }
        }

        storage.put(fieldName, SBGsonParser.createParser().toJson(selectedItems, List.class));
    }

    @Override
    public void restoreFromStorage(Map<String, String> storage, String fieldName) {
        final String jsonString = storage.get(fieldName);
        List<String> items = SBGsonParser.createParser().fromJson(jsonString, List.class);
        deserialize(items);
    }

    @AfterViews
    protected void init() {
        setOrientation(VERTICAL);
        addRow();
    }

    private MultipleChoiceFullScreenRow addRow() {
        try {
            final MultipleChoiceFullScreenRow row = MultipleChoiceFullScreenRow_.build(getContext(), key, hint);
            addView(row);
            return row;
        } catch (Throwable t) {
            Log.e(TAG, "Something bad happened", t);
            throw t;
        }
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        if (child instanceof MultipleChoiceFullScreenRow) {
            MultipleChoiceFullScreenRow row = (MultipleChoiceFullScreenRow) child;
            row.setOnDeleteListener(this);
            row.setOnPopulatedListener(this);
        }
    }

    private void addRowIfNone() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            // if we found a row
            if (view instanceof MultipleChoiceFullScreenRow) {
                // which is not populated
                if (!((MultipleChoiceFullScreenRow) view).isPopulated())
                    // we can exit
                    return;
            }
        }
        // there are no empty rows, so we need to add one
        addRow();
    }

    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        if (child instanceof MultipleChoiceFullScreenRow) {
            MultipleChoiceFullScreenRow row = (MultipleChoiceFullScreenRow) child;
            row.setOnPopulatedListener(null);
            row.setOnDeleteListener(null);

            if (!isUpdating) {
                addRowIfNone();
            }
        }
    }

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        Log.d(TAG, "dispatchSaveInstanceState");
        dispatchFreezeSelfOnly(container);
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        Log.d(TAG, "dispatchRestoreInstanceState");
        dispatchThawSelfOnly(container);
    }

    @Override
    public void restoreHierarchyState(SparseArray<Parcelable> container) {
        Log.d(TAG, "restoreHierarchyState");
        super.restoreHierarchyState(container);
    }

    @Override
    public void saveHierarchyState(SparseArray<Parcelable> container) {
        Log.d(TAG, "saveHierarchyState");
        super.saveHierarchyState(container);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Log.d(TAG, "onSaveInstanceState");
        Parcelable parentState = super.onSaveInstanceState();
        Bundle childrenStates = new Bundle();
        childrenStates.putInt("count", getChildCount());
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof MultipleChoiceFullScreenRow) {
                SparseArray<Parcelable> state = new SparseArray<>();
                child.saveHierarchyState(state);
                childrenStates.putSparseParcelableArray("r" + i, state);
            }
        }
        return new MultipleChoiceFullScreenFormInput.InstanceState(parentState, childrenStates);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Log.d(TAG, "onRestoreInstanceState");
        if (!(state instanceof MultipleChoiceFullScreenFormInput.InstanceState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        MultipleChoiceFullScreenFormInput.InstanceState ss = (MultipleChoiceFullScreenFormInput.InstanceState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        isUpdating = true;
        try {
            clearRows();
            Bundle childrenStates = ss.childrenStates;
            for (int i = 0; i < childrenStates.getInt("count", 0); i++) {
                if (childrenStates.containsKey("r" + i)) {
                    Log.d(TAG, "restoring " + i);
                    MultipleChoiceFullScreenRow row = addRow();
                    row.restoreHierarchyState(childrenStates.getSparseParcelableArray("r" + i));
                }
            }
        } finally {
            isUpdating = false;
        }
    }

    public void deserialize(List<String> items) {
        isUpdating = true;
        try {
            clearRows();
            if (items != null) {
                for (String item : items) {
                    MultipleChoiceFullScreenRow row = addRow();
                    row.setText(item);
                }
            }
        } finally {
            isUpdating = false;
        }
        addRowIfNone();
    }

    private void clearRows() {
        for (int i = getChildCount(); i-- > 0; ) {
            View child = getChildAt(i);
            if (child instanceof MultipleChoiceFullScreenRow) {
                removeViewAt(i);
            }
        }
    }

    @Override
    public void onDelete(MultipleChoiceFullScreenRow row) {
        removeView(row);
    }

    @Override
    public void onPopulate(MultipleChoiceFullScreenRow row) {
        if (row.getId() == View.NO_ID) {
            // set id to the row, so instansce state is preserved
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                row.setId(View.generateViewId());
            } else {
                row.setId(ViewUtils.generateViewId());
            }
        }
        // the row is used, so we need to add an empty one
        if (!isUpdating) {
            addRow();
        }
        // we don't need to listen anymore for this event, as it fires only once
        row.setOnPopulatedListener(null);
    }

    public static class InstanceState extends BaseSavedState {

        private final Bundle childrenStates;

        public InstanceState(Parcelable parentState, Bundle childrenStates) {
            super(parentState);
            this.childrenStates = childrenStates;
        }

        protected InstanceState(Parcel in, ClassLoader loader) {
            super(in);
            this.childrenStates = in.readBundle(loader);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeBundle(this.childrenStates);
        }


        public static final ClassLoaderCreator<MultipleChoiceFullScreenFormInput.InstanceState> CREATOR = new ClassLoaderCreator<MultipleChoiceFullScreenFormInput.InstanceState>() {
            @Override
            public MultipleChoiceFullScreenFormInput.InstanceState createFromParcel(Parcel source, ClassLoader loader) {
                return new MultipleChoiceFullScreenFormInput.InstanceState(source, loader);
            }


            @Override
            public MultipleChoiceFullScreenFormInput.InstanceState createFromParcel(Parcel source) {
                return createFromParcel(source, MultipleChoiceFullScreenFormInput.class.getClassLoader());
            }

            @Override
            public MultipleChoiceFullScreenFormInput.InstanceState[] newArray(int size) {
                return new MultipleChoiceFullScreenFormInput.InstanceState[size];
            }
        };
    }
}
