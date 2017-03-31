package org.bspb.smartbirds.pro.enums;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;

import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.forms.convert.BirdsConverter;
import org.bspb.smartbirds.pro.forms.convert.CbmConverter;
import org.bspb.smartbirds.pro.forms.convert.CiconiaConverter;
import org.bspb.smartbirds.pro.forms.convert.Converter;
import org.bspb.smartbirds.pro.forms.convert.HerpConverter;
import org.bspb.smartbirds.pro.forms.upload.BirdsUploader;
import org.bspb.smartbirds.pro.forms.upload.CbmUploader;
import org.bspb.smartbirds.pro.forms.upload.CiconiaUploader;
import org.bspb.smartbirds.pro.forms.upload.HerpUploader;
import org.bspb.smartbirds.pro.forms.upload.Uploader;
import org.bspb.smartbirds.pro.ui.fragment.BaseEntryFragment;
import org.bspb.smartbirds.pro.ui.fragment.NewBirdsEntryFormFragment;
import org.bspb.smartbirds.pro.ui.fragment.NewCbmEntryFormFragment;
import org.bspb.smartbirds.pro.ui.fragment.NewCiconiaEntryFormFragment;
import org.bspb.smartbirds.pro.ui.fragment.NewHerpEntryFormFragment;
import org.bspb.smartbirds.pro.ui.fragment.NewHumidBirdsEntryFragment;

import static org.bspb.smartbirds.pro.tools.Reporting.logException;

/**
 * Created by dani on 14-11-11.
 */
public enum EntryType {
    BIRDS(new NewBirdsEntryFormFragment.Builder(), R.string.entry_type_birds, R.id.action_form_type_birds, "form_birds.csv", BirdsConverter.class, BirdsUploader.class),
    HUMID(new NewHumidBirdsEntryFragment.Builder(), R.string.entry_type_humid, R.id.action_form_type_humid, "form_humid.csv", BirdsConverter.class, BirdsUploader.class),
    CBM(new NewCbmEntryFormFragment.Builder(), R.string.entry_type_cbm, R.id.action_form_type_cbm, "form_cbm.csv", CbmConverter.class, CbmUploader.class),
    CICONIA(new NewCiconiaEntryFormFragment.Builder(), R.string.entry_type_ciconia, R.id.action_form_type_ciconia, "form_ciconia.csv", CiconiaConverter.class, CiconiaUploader.class),
    HERP(new NewHerpEntryFormFragment.Builder(), R.string.entry_type_herp, R.id.action_form_type_herp, "form_herp.csv", HerpConverter.class, HerpUploader.class),
    // prevent auto-formatting
    ;

    public static final int[] MENU_ACTION_IDS = {
            R.id.action_form_type_birds,
            R.id.action_form_type_cbm,
            R.id.action_form_type_ciconia,
            R.id.action_form_type_herp,
            R.id.action_form_type_humid,
    };

    private final BaseEntryFragment.Builder builder;
    @StringRes
    public final int titleId;
    @IdRes
    public final int menuActionId;
    public final String filename;
    private final Class<? extends Converter> converterClass;
    private final Class<? extends Uploader> uploaderClass;

    EntryType(BaseEntryFragment.Builder builder, @StringRes int titleId, @IdRes int menuActionId, String filename, Class<? extends Converter> converterClass, Class<? extends Uploader> uploaderClass) {
        this.builder = builder;
        this.titleId = titleId;
        this.menuActionId = menuActionId;
        this.filename = filename;
        this.converterClass = converterClass;
        this.uploaderClass = uploaderClass;
    }

    public Fragment buildFragment(double lat, double lon) {
        return builder.build(lat, lon);
    }

    public Fragment loadFragment(long id) {
        return builder.load(id);
    }

    public static String[] getTitles(Resources resources) {
        final String[] titles = new String[values().length];
        int idx = 0;
        for (EntryType formType : values())
            titles[idx++] = resources.getString(formType.titleId);
        return titles;
    }

    public Converter getConverter(Context context) {
        try {
            return converterClass.getConstructor(Context.class).newInstance(context);
        } catch (Throwable e) {
            logException(e);
            throw new IllegalStateException("Could not instantiate converter", e);
        }
    }

    public Uploader getUploader() {
        try {
            return uploaderClass.newInstance();
        } catch (Throwable e) {
            logException(e);
            throw new IllegalStateException("Could not instantiate uploader", e);
        }
    }

    public interface EntryFragment {
        boolean isDirty();
    }
}
