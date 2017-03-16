package org.bspb.smartbirds.pro.ui.views;

import android.content.Context;
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
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.ui.utils.FormUtils;
import org.bspb.smartbirds.pro.ui.utils.ViewUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by groupsky on 15.12.16.
 */

@EView
public class FormBirdsList extends LinearLayout implements FormBirdsRow.OnDeleteListener, FormBirdsRow.OnPopulatedListener {


    private static final String TAG = SmartBirdsApplication.TAG + ".FormBirdsList";
    private boolean isUpdating = false;

    public FormBirdsList(Context context) {
        super(context);
    }

    public FormBirdsList(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FormBirdsList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @AfterViews
    protected void init() {
        setOrientation(VERTICAL);
        if (isInEditMode()) {
            for (int i = 0; i < 5; i++) {
                FormBirdsRow row = addRow();
            }
        } else {
            addRow();
        }
    }

    private FormBirdsRow addRow() {
        try {
            final FormBirdsRow row = FormBirdsRow_.build(getContext());
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
        if (child instanceof FormBirdsRow) {
            FormBirdsRow row = (FormBirdsRow) child;
            row.setOnDeleteListener(this);
            row.setOnPopulatedListener(this);
        }
    }

    private void addRowIfNone() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            // if we found a row
            if (view instanceof FormBirdsRow) {
                // which is not populated
                if (!((FormBirdsRow) view).isPopulated())
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
        if (child instanceof FormBirdsRow) {
            FormBirdsRow row = (FormBirdsRow) child;
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
        for (int i=0; i<getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof FormBirdsRow) {
                SparseArray<Parcelable> state = new SparseArray<>();
                child.saveHierarchyState(state);
                childrenStates.putSparseParcelableArray("r"+i, state);
            }
        }
        return new InstanceState(parentState, childrenStates);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Log.d(TAG, "onRestoreInstanceState");
        if (!(state instanceof InstanceState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        InstanceState ss = (InstanceState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        isUpdating = true;
        try {
            clearRows();
            Bundle childrenStates = ss.childrenStates;
            for (int i = 0; i < childrenStates.getInt("count", 0); i++) {
                if (childrenStates.containsKey("r" + i)) {
                    Log.d(TAG, "restoring "+i);
                    FormBirdsRow row = addRow();
                    row.restoreHierarchyState(childrenStates.getSparseParcelableArray("r" + i));
                }
            }
//            addRowIfNone();
        } finally {
            isUpdating = false;
        }
    }

    public void deserialize(ArrayList<HashMap<String, String>> models) {
        isUpdating = true;
        try {
            clearRows();
            for (HashMap<String, String> model: models) {
                FormBirdsRow row = addRow();
                row.getModel().deserialize(model);
            }
        } finally {
            isUpdating = false;
        }
    }

    private void clearRows() {
        for (int i=getChildCount(); i-- > 0; ) {
            View child = getChildAt(i);
            if (child instanceof FormBirdsRow) {
                removeViewAt(i);
            }
        }
    }

    @Override
    public void onDelete(FormBirdsRow row) {
        removeView(row);
    }

    @Override
    public void onPopulate(FormBirdsRow row) {
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

    public ArrayList<FormUtils.FormModel> getModels() {
        ArrayList<FormUtils.FormModel> models = new ArrayList<>(getChildCount());
        for (int idx=0; idx<getChildCount(); idx++) {
            View child = getChildAt(idx);
            if (child instanceof FormBirdsRow) {
                FormBirdsRow row = (FormBirdsRow) child;
                if (row.isPopulated()) {
                    models.add(row.getModel());
                }
            }
        }
        return models;
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


        public static final ClassLoaderCreator<InstanceState> CREATOR = new ClassLoaderCreator<InstanceState>() {
            @Override
            public InstanceState createFromParcel(Parcel source, ClassLoader loader) {
                return new InstanceState(source, loader);
            }


            @Override
            public InstanceState createFromParcel(Parcel source) {
                return createFromParcel(source, FormBirdsRow.class.getClassLoader());
            }

            @Override
            public InstanceState[] newArray(int size) {
                return new InstanceState[size];
            }
        };
    }
}
