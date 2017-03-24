package org.bspb.smartbirds.pro.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import org.bspb.smartbirds.pro.ui.utils.FormUtils;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by groupsky on 26.01.17.
 */

public class BaseFormFragment extends Fragment {

    protected static final String KEY_FORM_DATA = "org.bspb.smartbirds.pro.ui.fragment.BaseFormFragment.FORM_DATA";
    private static final String KEY_MONITORING_CODE = "org.bspb.smartbirds.pro.ui.fragment.BaseFormFragment.MONITORING_FORM";

    protected FormUtils.FormModel form;
    private HashMap<String, String> pendingDeserializeData;
    /**
     * Available only when loaded from storage
     */
    @Nullable
    protected String monitoringCode;

    protected boolean isValid() {
        ensureForm();
        return form.validateFields();
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
}
