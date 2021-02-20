package org.bspb.smartbirds.pro.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.textfield.TextInputLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.utils.ExtensionsKt;

/**
 * Created by dani on 26.02.18.
 */

@EViewGroup(R.layout.multiple_choice_full_screen_row)
public class MultipleChoiceFullScreenRow extends LinearLayout {

    private OnDeleteListener onDeleteListener;
    private OnPopulatedListener onPopulatedListener;

    @ViewById(R.id.field_input)
    protected SingleChoiceFormInput input;

    @ViewById(R.id.field_hint)
    protected TextInputLayout hintView;

    @ViewById(R.id.btn_delete)
    protected Button btnDelete;


    CharSequence key;
    CharSequence hint;


    public MultipleChoiceFullScreenRow(Context context, CharSequence key, CharSequence hint) {
        super(context);
        this.hint = hint;
        this.key = key;
    }

    public MultipleChoiceFullScreenRow(Context context) {
        super(context);
    }

    public MultipleChoiceFullScreenRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultipleChoiceFullScreenRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setKey(CharSequence key) {
        this.key = key;
    }

    public void setHint(CharSequence hint) {
        this.hint = hint;
    }

    @AfterViews
    protected void init() {
        setGravity(Gravity.BOTTOM);
        setOrientation(HORIZONTAL);
        input.setKey(key);
        hintView.setHint(hint);
    }

    @Click(R.id.btn_delete)
    protected void doOnDelete() {
        if (onDeleteListener != null)
            onDeleteListener.onDelete(this);
    }

    @TextChange(R.id.field_input)
    protected void doOnSpeciesChange() {
        if (onPopulatedListener != null && isPopulated())
            onPopulatedListener.onPopulate(this);
    }

    public boolean isPopulated() {
        return input.getSelectedItem() != null;
    }

    public CharSequence getText() {
        return input.getText();
    }

    public void setText(CharSequence text) {
        input.setText(text);
    }


    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    public void setOnPopulatedListener(OnPopulatedListener onPopulatedListener) {
        this.onPopulatedListener = onPopulatedListener;
    }

    public interface OnDeleteListener {
        void onDelete(MultipleChoiceFullScreenRow row);
    }

    public interface OnPopulatedListener {
        void onPopulate(MultipleChoiceFullScreenRow row);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        hintView.setEnabled(enabled);
        input.setEnabled(enabled);
        btnDelete.setEnabled(enabled);
    }
}
