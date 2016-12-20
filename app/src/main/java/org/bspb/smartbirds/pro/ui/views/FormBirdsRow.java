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
import org.bspb.smartbirds.pro.prefs.BirdPrefs_;
import org.bspb.smartbirds.pro.ui.utils.FormUtils;

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
    }

    @Click(R.id.btn_delete)
    protected void doOnDelete() {
        if (onDeleteListener != null)
            onDeleteListener.onDelete(this);
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

    public interface OnDeleteListener {
        void onDelete(FormBirdsRow row);
    }

    public interface OnPopulatedListener {
        void onPopulate(FormBirdsRow row);
    }
}
