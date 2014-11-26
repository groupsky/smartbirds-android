package org.bspb.smartbirds.pro.ui.utils;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.ui.exception.ViewValidationException;
import org.bspb.smartbirds.pro.ui.views.SupportRequiredView;

import java.util.HashMap;

/**
 * Created by groupsky on 14-9-29.
 */

public class FormUtils {

    static final String TAG = SmartBirdsApplication.TAG + ".FormUtils";

    public static FormModel traverseForm(View view) {
        FormModel form = new FormModel();
        initModelFromViewHierarchy(view, form);
        return form;
    }

    private static void initModelFromViewHierarchy(View view, FormModel form) {
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                initModelFromViewHierarchy(vg.getChildAt(i), form);
            }
        }

        Object tag = view.getTag();
        if (tag instanceof String) {
            String field = (String) tag;
            if (!field.isEmpty()) {
                form.fields.put(field, new FormField(view, field));
            }
        }
    }

    public static final class FormField {
        public View view;
        public String field;

        @Override
        public String toString() {
            return "FormField{" +
                    "view=" + view.getClass().getSimpleName() + "(" + view.getId() + ")" +
                    ", field='" + field + '\'' +
                    '}';
        }

        public FormField(View view, String field) {
            this.view = view;
            this.field = field;
        }

        public String getValue() {
            if (!view.isEnabled())
                return "";
            if (view instanceof EditText) {
                EditText editText = (EditText) view;
                return editText.getText().toString();
            }
            if (view instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) view;
                return checkBox.isChecked() ? "1" : "0";
            }
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                return textView.getText().toString();
            }
            Log.w(TAG, "unsupported view " + view.getClass());
            return "";
        }

        public void setValue(String value) {
            if (value == null)
                return;
            if (view instanceof EditText) {
                EditText editText = (EditText) view;
                editText.setText(value);
            }
            if (view instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) view;
                checkBox.setChecked("1".equals(value));
            }
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                textView.setText(value);
            }
        }
    }

    public static final class FormModel {
        HashMap<String, FormField> fields = new HashMap<String, FormField>();

        @Override
        public String toString() {
            return "FormModel{" +
                    "fields=" + fields +
                    '}';
        }

        public HashMap<String, String> serialize() {
            HashMap<String, String> values = new HashMap<String, String>();
            for (String key : fields.keySet()) {
                FormField field = fields.get(key);
                String value = field.getValue();
                value = value.replaceAll("\n", " | ");
                values.put(key, value);
            }
            return values;
        }

        public void deserialize(HashMap<String, String> values) {
            if (values == null) return;
            for (String key : fields.keySet()) {
                if (!values.containsKey(key)) continue;
                FormField field = fields.get(key);
                field.setValue(values.get(key));
            }
        }

        public boolean validateFields() {
            boolean res = true;
            for (FormField field : fields.values()) {
                if (field.view instanceof SupportRequiredView) {
                    try {
                        ((SupportRequiredView) field.view).checkRequired();
                    } catch (ViewValidationException e) {
                        res = false;
                    }
                }
            }


            return res;

        }
    }

}
