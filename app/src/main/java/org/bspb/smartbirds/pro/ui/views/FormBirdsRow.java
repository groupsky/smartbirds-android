package org.bspb.smartbirds.pro.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.backend.dto.Nomenclature;
import org.bspb.smartbirds.pro.prefs.BirdPrefs_;
import org.bspb.smartbirds.pro.ui.utils.FormUtils;

import java.util.Locale;

import static android.text.TextUtils.isEmpty;

/**
 * Created by groupsky on 15.12.16.
 */

@EViewGroup(R.layout.partial_form_birds_row)
public class FormBirdsRow extends LinearLayout {

    private OnDeleteListener onDeleteListener;
    private OnPopulatedListener onPopulatedListener;
    private FormUtils.FormModel model;

    @ViewById(R.id.field_name)
    protected SingleChoiceFormInput species;
    @ViewById(R.id.form_birds_count_units)
    protected SingleChoiceFormInput countUnits;
    @ViewById(R.id.form_birds_count_type)
    protected SingleChoiceFormInput countType;
    @ViewById(R.id.form_birds_count)
    protected DecimalNumberFormInput count;
    @ViewById(R.id.form_birds_count_min)
    protected DecimalNumberFormInput countMin;
    @ViewById(R.id.form_birds_count_max)
    protected DecimalNumberFormInput countMax;

    @Pref
    BirdPrefs_ prefs;

    public FormBirdsRow(Context context) {
        super(context);
    }

    public FormBirdsRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FormBirdsRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @AfterViews
    protected void init() {
        setGravity(Gravity.BOTTOM);
        setOrientation(HORIZONTAL);
        model = FormUtils.traverseForm(this);
        countUnits.setSelection(prefs.birdCountUnits().get());
        countType.setSelection(prefs.birdCountType().get());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        handleCountsLogic();
    }

    @Click(R.id.btn_delete)
    protected void doOnDelete() {
        if (onDeleteListener != null)
            onDeleteListener.onDelete(this);
    }

    @TextChange(R.id.form_birds_count_units)
    protected void doOnCountUnitsChange(CharSequence text) {
        if (!isEmpty(text)) {
            prefs.birdCountUnits().put(text.toString());
        } else {
            prefs.birdCountUnits().remove();
        }
    }

    @TextChange(R.id.form_birds_count_type)
    protected void doOnCountTypeChange(CharSequence text) {
        if (!isEmpty(text)) {
            prefs.birdCountType().put(text.toString());
        } else {
            prefs.birdCountType().remove();
        }
        handleCountsLogic();
    }

    @TextChange(R.id.field_name)
    protected  void doOnSpeciesChange() {
        if (onPopulatedListener != null && isPopulated())
            onPopulatedListener.onPopulate(this);
    }

    public boolean isPopulated() {
        return !isEmpty(species.getText());
    }

    public OnDeleteListener getOnDeleteListener() {
        return onDeleteListener;
    }

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    public OnPopulatedListener getOnPopulatedListener() {
        return onPopulatedListener;
    }

    public void setOnPopulatedListener(OnPopulatedListener onPopulatedListener) {
        this.onPopulatedListener = onPopulatedListener;
    }

    public FormUtils.FormModel getModel() {
        return model;
    }

    void handleCountsLogic() {
        Nomenclature item = countType.getSelectedItem();
        String countsType = item != null ? item.label.en : null;
        switch (countsType != null ? countsType.toLowerCase(Locale.ENGLISH) : "") {
            case "exact number": // Exact count
                count.setVisibility(VISIBLE);
                countMin.setVisibility(INVISIBLE);
                countMax.setVisibility(INVISIBLE);
                break;
            case "min.": // Min count
                count.setVisibility(INVISIBLE);
                countMin.setVisibility(VISIBLE);
                countMax.setVisibility(INVISIBLE);
                break;
            case "max.": // Max count
                count.setVisibility(INVISIBLE);
                countMin.setVisibility(INVISIBLE);
                countMax.setVisibility(VISIBLE);
                break;
            case "range": // Range count
                count.setVisibility(INVISIBLE);
                countMin.setVisibility(VISIBLE);
                countMax.setVisibility(VISIBLE);
                break;
            case "unspecified number": // Unspecified
                count.setVisibility(INVISIBLE);
                countMin.setVisibility(INVISIBLE);
                countMax.setVisibility(INVISIBLE);
                break;
            default:
                count.setVisibility(VISIBLE);
                countMin.setVisibility(VISIBLE);
                countMax.setVisibility(VISIBLE);
                break;
        }
    }

    public interface OnDeleteListener {
        void onDelete(FormBirdsRow row);
    }

    public interface OnPopulatedListener {
        void onPopulate(FormBirdsRow row);
    }
}
