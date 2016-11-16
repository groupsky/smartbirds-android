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
 * + sign (action button) will append a plus at the end in the edit field. When the + sign is
 * clicked twice then the sum calculated then entered in the field and
 * the action Go / Next is executed.
 *
 * Created by Ilian Georgiev.
 */
public class SumDecimalNumberFormInput extends DecimalNumberFormInput {

    private static final String SPLIT_CHARACTER = "+";

    /**
     * Tracks key event to make a sum after second time action button next is pressed and
     * there was no key event before.
     */
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
        setImeOptions(EditorInfo.IME_ACTION_NEXT);
        setImeActionLabel(sign, EditorInfo.IME_ACTION_NEXT);

        setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;

                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    String currentValue = getText().toString();

                    if (!keyEventOccurred) {
                        int sum = 0;
                        if (currentValue.length() != 0) {
                            String[] split = currentValue.split("\\" + SPLIT_CHARACTER);

                            for (String number : split) {
                                sum += Integer.valueOf(number);
                            }
                        }

                        String sumStr = String.valueOf(sum);
                        setText(sumStr);
                        setSelection(sumStr.length());
                    } else {

                        if (currentValue.length() != 0) {
                            currentValue = getText().toString();
                            currentValue += "+";

                            setText(currentValue);
                            setSelection(currentValue.length());
                        }
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

        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    keyEventOccurred = true;
                }
            }
        });
    }
}
