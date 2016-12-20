package org.bspb.smartbirds.pro.enums;

import android.app.Fragment;
import android.content.res.Resources;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;

import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.ui.fragment.BaseEntryFragment.Builder;
import org.bspb.smartbirds.pro.ui.fragment.NewBirdsEntryFormFragment;
import org.bspb.smartbirds.pro.ui.fragment.NewCbmEntryFormFragment;
import org.bspb.smartbirds.pro.ui.fragment.NewCiconiaEntryFormFragment;
import org.bspb.smartbirds.pro.ui.fragment.NewHerpEntryFormFragment;
import org.bspb.smartbirds.pro.ui.fragment.NewHumidBirdsEntryFragment;

/**
 * Created by groupsky on 20.12.16.
 */

public enum FormType {
    BIRDS(new NewBirdsEntryFormFragment.Builder(), R.string.entry_type_birds, R.id.action_form_type_birds),
    HUMID(new NewHumidBirdsEntryFragment.Builder(), R.string.entry_type_humid, R.id.action_form_type_humid),
    CBM(new NewCbmEntryFormFragment.Builder(), R.string.entry_type_cbm, R.id.action_form_type_cbm),
    CICONIA(new NewCiconiaEntryFormFragment.Builder(), R.string.entry_type_ciconia, R.id.action_form_type_ciconia),
    HERP(new NewHerpEntryFormFragment.Builder(), R.string.entry_type_herp, R.id.action_form_type_herp),
    // prevent auto-formatting
    ;

    public static final int[] MENU_ACTION_IDS = {
            R.id.action_form_type_birds,
            R.id.action_form_type_cbm,
            R.id.action_form_type_ciconia,
            R.id.action_form_type_herp,
            R.id.action_form_type_humid,
    };
    private final Builder builder;
    @StringRes
    public final int titleId;
    @IdRes
    public final int menuActionId;

    FormType(Builder builder, @StringRes int titleId, @IdRes int menuActionId) {
        this.builder = builder;
        this.titleId = titleId;
        this.menuActionId = menuActionId;
    }

    public Fragment buildFragment(double lat, double lon) {
        return builder.build(lat, lon);
    }

    public static String[] getTitles(Resources resources) {
        final String[] titles = new String[values().length];
        int idx = 0;
        for (FormType formType : values())
            titles[idx++] = resources.getString(formType.titleId);
        return titles;
    }
}
