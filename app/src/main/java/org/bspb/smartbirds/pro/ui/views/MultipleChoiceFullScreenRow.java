package org.bspb.smartbirds.pro.ui.views;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.textfield.TextInputLayout;

import org.bspb.smartbirds.pro.R;

/**
 * Created by dani on 26.02.18.
 */

public class MultipleChoiceFullScreenRow extends LinearLayout {

    private OnDeleteListener onDeleteListener;
    private OnPopulatedListener onPopulatedListener;

    private SingleChoiceFormInput input;
    private TextInputLayout hintView;
    private Button btnDelete;


    CharSequence key;
    CharSequence hint;
    private boolean alreadyInflated = false;

    public static MultipleChoiceFullScreenRow build(Context context, CharSequence key, CharSequence hint) {
        MultipleChoiceFullScreenRow instance = new MultipleChoiceFullScreenRow(context, key, hint);
        instance.onFinishInflate();
        return instance;
    }

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

    @Override
    protected void onFinishInflate() {
        if (!alreadyInflated) {
            alreadyInflated = true;
            inflate(getContext(), R.layout.multiple_choice_full_screen_row, this);
            init();
        }
        super.onFinishInflate();
    }

    public void setKey(CharSequence key) {
        this.key = key;
    }

    public void setHint(CharSequence hint) {
        this.hint = hint;
    }

    protected void init() {
        input = findViewById(R.id.field_input);
        hintView = findViewById(R.id.field_hint);
        btnDelete = findViewById(R.id.btn_delete);
        if (btnDelete != null) {
            btnDelete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(android.view.View view) {
                    doOnDelete();
                }
            });
        }
        if (input != null) {
            input.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    doOnSpeciesChange();
                }

                @Override
                public void afterTextChanged(android.text.Editable s) {
                }
            });
        }

        setGravity(Gravity.BOTTOM);
        setOrientation(HORIZONTAL);
        input.setKey(key);
        hintView.setHint(hint);
    }

    protected void doOnDelete() {
        if (onDeleteListener != null)
            onDeleteListener.onDelete(this);
    }

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
