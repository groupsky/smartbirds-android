package org.bspb.smartbirds.pro.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.ui.utils.FormUtils;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by groupsky on 26.01.17.
 */

@EFragment
public class BaseFormFragment extends Fragment {

    protected static final String KEY_FORM_DATA = "org.bspb.smartbirds.pro.ui.fragment.BaseFormFragment.FORM_DATA";
    private static final String KEY_MONITORING_CODE = "org.bspb.smartbirds.pro.ui.fragment.BaseFormFragment.MONITORING_FORM";

    protected static final String ARG_IS_NEW_ENTRY = "isNewEntry";

    protected FormUtils.FormModel form;
    private HashMap<String, String> pendingDeserializeData;
    /**
     * Available only when loaded from storage
     */
    @Nullable
    protected String monitoringCode;


    private boolean newEntry;
    private ModeratorReviewFragment moderatorReviewFragment;

    public boolean isNewEntry() {
        return newEntry;
    }

    @FragmentArg(ARG_IS_NEW_ENTRY)
    public void setNewEntry(boolean isNewEntry) {
        this.newEntry = isNewEntry;
    }

    protected boolean isValid() {
        ensureForm();

        boolean valid = true;
        if (moderatorReviewFragment != null) {
            valid = moderatorReviewFragment.isValid();
        }

        return form.validateFields() && valid;
    }

    protected boolean ensureForm() {
        if (form != null) return true;
        View view = getView();
        if (view == null) return false;
        form = FormUtils.traverseForm(view);
        return true;
    }

    protected HashMap<String, String> serialize() {
        ensureForm();
        return form.serialize();
    }

    protected void doDeserialize(String monitoringCode, HashMap<String, String> data) {
        this.monitoringCode = monitoringCode;
        if (ensureForm()) {
            deserialize(data);
            return;
        }
        pendingDeserializeData = data;
    }

    protected void deserialize(HashMap<String, String> data) {
        ensureForm();
        form.deserialize(data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ensureForm();
        outState.putString(KEY_MONITORING_CODE, monitoringCode);
        outState.putSerializable(KEY_FORM_DATA, serialize());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        } else if (pendingDeserializeData != null) {
            doDeserialize(monitoringCode, pendingDeserializeData);
            pendingDeserializeData = null;
        }
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        String monitoringCode = null;
        if (savedInstanceState.containsKey(KEY_MONITORING_CODE)) {
            monitoringCode = savedInstanceState.getString(KEY_MONITORING_CODE);
        }
        if (savedInstanceState.containsKey(KEY_FORM_DATA)) {
            Serializable data = savedInstanceState.getSerializable(KEY_FORM_DATA);
            if (data instanceof HashMap) {
                //noinspection unchecked
                doDeserialize(monitoringCode, (HashMap<String, String>) data);
            }
        }
    }

    @AfterViews
    public void initModeratorReviewFragment() {
        moderatorReviewFragment = (ModeratorReviewFragment) getChildFragmentManager().findFragmentById(R.id.moderator_review_fragment);
        NewEntryPicturesFragment picturesFragment = (NewEntryPicturesFragment) getChildFragmentManager().findFragmentById(R.id.pictures_fragment);
        if (moderatorReviewFragment != null) {
            if (picturesFragment != null) {
                moderatorReviewFragment.setPicturesFragment(picturesFragment);
            }
        }
    }
}
