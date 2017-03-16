package org.bspb.smartbirds.pro.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

import org.bspb.smartbirds.pro.ui.utils.FormUtils;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by groupsky on 26.01.17.
 */

public class BaseFormFragment extends Fragment {

    protected static final String KEY_FORM_DATA = "org.bspb.smartbirds.pro.ui.fragment.BaseFormFragment.FORM_DATA";

    protected FormUtils.FormModel form;
    private HashMap<String, String> pendingDeserializeData;

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

    protected void doDeserialize(HashMap<String, String> data) {
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
        outState.putSerializable(KEY_FORM_DATA, serialize());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        } else if (pendingDeserializeData != null) {
            doDeserialize(pendingDeserializeData);
            pendingDeserializeData = null;
        }
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(KEY_FORM_DATA)) {
            Serializable data = savedInstanceState.getSerializable(KEY_FORM_DATA);
            if (data instanceof HashMap) {
                //noinspection unchecked
                doDeserialize((HashMap<String, String>) data);
            }
        }
    }
}
