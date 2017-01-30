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

    protected boolean isValid() {
        ensureForm();
        return form.validateFields();
    }

    protected void ensureForm() {
        if (form == null)
            form = FormUtils.traverseForm(getView());
    }

    protected HashMap<String, String> serialize() {
        ensureForm();
        return form.serialize();
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
        if (savedInstanceState != null) onRestoreInstanceState(savedInstanceState);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(KEY_FORM_DATA)) {
            Serializable data = savedInstanceState.getSerializable(KEY_FORM_DATA);
            if (data instanceof HashMap) {
                deserialize((HashMap<String, String>) data);
            }
        }
    }
}
