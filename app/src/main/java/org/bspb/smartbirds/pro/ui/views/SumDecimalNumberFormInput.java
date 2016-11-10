package org.bspb.smartbirds.pro.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import org.bspb.smartbirds.pro.R;

/**
 * Input field for numbers that will be summed together.
 * Ime actions Go / Next are replaced with + sign. When a number is chosen then pressing the
 * + sign (action button) will the number to the sum. When the + sign is clicked twice then the sum
 * is entered in the field and the action Go / Next is executed.
 *
 * Created by Ilian Georgiev.
 */
public class SumDecimalNumberFormInput extends DecimalNumberFormInput {

    private int sum = 0;

    private boolean keyEventOccurred = false;

    public SumDecimalNumberFormInput(Context context) {
        super(context);
        init();
    }

    public SumDecimalNumberFormInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SumDecimalNumberFormInput(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        String sign = getResources().getString(R.string.sum_decimal_number_form_input_plus);
        setImeActionLabel(sign, EditorInfo.IME_ACTION_GO);
        setImeActionLabel(sign, EditorInfo.IME_ACTION_NEXT);

        setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;

                if (actionId == EditorInfo.IME_ACTION_GO ||
                        actionId == EditorInfo.IME_ACTION_NEXT) {

                    if (!keyEventOccurred) {
                        setText(String.valueOf(sum));
                    } else {
                        int currentValue = 0;
                        if (getText().length() != 0) {
                            currentValue = Integer.valueOf(getText().toString());
                        }

                        sum += currentValue;
                        getText().clear();
                    }

                    handled = keyEventOccurred;
                    keyEventOccurred = false;
                }

                return handled;
            }
        });

        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                keyEventOccurred = true;
                return false;
            }
        });
    }
}
