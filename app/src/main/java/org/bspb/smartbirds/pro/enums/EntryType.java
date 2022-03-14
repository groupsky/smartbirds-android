package org.bspb.smartbirds.pro.enums;

import static org.bspb.smartbirds.pro.tools.Reporting.logException;

import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.forms.convert.BirdsConverter;
import org.bspb.smartbirds.pro.forms.convert.CbmConverter;
import org.bspb.smartbirds.pro.forms.convert.CiconiaConverter;
import org.bspb.smartbirds.pro.forms.convert.Converter;
import org.bspb.smartbirds.pro.forms.convert.HerptileConverter;
import org.bspb.smartbirds.pro.forms.convert.InvertebratesConverter;
import org.bspb.smartbirds.pro.forms.convert.MammalConverter;
import org.bspb.smartbirds.pro.forms.convert.PlantsConverter;
import org.bspb.smartbirds.pro.forms.convert.PylonsCasualtiesConverter;
import org.bspb.smartbirds.pro.forms.convert.PylonsConverter;
import org.bspb.smartbirds.pro.forms.convert.ThreatsConverter;
import org.bspb.smartbirds.pro.forms.upload.BirdsUploader;
import org.bspb.smartbirds.pro.forms.upload.CbmUploader;
import org.bspb.smartbirds.pro.forms.upload.CiconiaUploader;
import org.bspb.smartbirds.pro.forms.upload.HerptileUploader;
import org.bspb.smartbirds.pro.forms.upload.InvertebratesUploader;
import org.bspb.smartbirds.pro.forms.upload.MammalUploader;
import org.bspb.smartbirds.pro.forms.upload.PlantsUploader;
import org.bspb.smartbirds.pro.forms.upload.PylonsCasualtiesUploader;
import org.bspb.smartbirds.pro.forms.upload.PylonsUploader;
import org.bspb.smartbirds.pro.forms.upload.ThreatsUploader;
import org.bspb.smartbirds.pro.forms.upload.Uploader;
import org.bspb.smartbirds.pro.ui.fragment.BaseEntryFragment;
import org.bspb.smartbirds.pro.ui.fragment.NewBirdsEntryFormFragment;
import org.bspb.smartbirds.pro.ui.fragment.NewCbmEntryFormFragment;
import org.bspb.smartbirds.pro.ui.fragment.NewCiconiaEntryFormFragment;
import org.bspb.smartbirds.pro.ui.fragment.NewHerptileEntryFormFragment;
import org.bspb.smartbirds.pro.ui.fragment.NewHumidBirdsEntryFragment;
import org.bspb.smartbirds.pro.ui.fragment.NewInvertebratesEntryFormFragment;
import org.bspb.smartbirds.pro.ui.fragment.NewMammalEntryFormFragment;
import org.bspb.smartbirds.pro.ui.fragment.NewPlantsEntryFormFragment;
import org.bspb.smartbirds.pro.ui.fragment.NewPylonsCasualtiesEntryFormFragment;
import org.bspb.smartbirds.pro.ui.fragment.NewPylonsEntryFormFragment;
import org.bspb.smartbirds.pro.ui.fragment.NewThreatsEntryFormFragment;

import java.util.ArrayList;

/**
 * Created by dani on 14-11-11.
 */
public enum EntryType {
    BIRDS(new NewBirdsEntryFormFragment.Builder(), R.string.entry_type_birds, R.id.action_form_type_birds, "form_birds.csv", BirdsConverter.class, BirdsUploader.class, true),
    HUMID(new NewHumidBirdsEntryFragment.Builder(), R.string.entry_type_humid, R.id.action_form_type_humid, "form_humid.csv", BirdsConverter.class, BirdsUploader.class, true),
    CBM(new NewCbmEntryFormFragment.Builder(), R.string.entry_type_cbm, R.id.action_form_type_cbm, "form_cbm.csv", CbmConverter.class, CbmUploader.class, true),
    CICONIA(new NewCiconiaEntryFormFragment.Builder(), R.string.entry_type_ciconia, R.id.action_form_type_ciconia, "form_ciconia.csv", CiconiaConverter.class, CiconiaUploader.class, true),
    HERPTILE(new NewHerptileEntryFormFragment.Builder(), R.string.entry_type_herptile, R.id.action_form_type_herptile, "form_herptile.csv", HerptileConverter.class, HerptileUploader.class, true),
    MAMMAL(new NewMammalEntryFormFragment.Builder(), R.string.entry_type_mammal, R.id.action_form_type_mammal, "form_mammal.csv", MammalConverter.class, MammalUploader.class, true),
    INVERTEBRATES(new NewInvertebratesEntryFormFragment.Builder(), R.string.entry_type_invertebrates, R.id.action_form_type_invertebrates, "form_invertebrates.csv", InvertebratesConverter.class, InvertebratesUploader.class, true),
    PLANTS(new NewPlantsEntryFormFragment.Builder(), R.string.entry_type_plants, R.id.action_form_type_plants, "form_plants.csv", PlantsConverter.class, PlantsUploader.class, true),
    THREATS(new NewThreatsEntryFormFragment.Builder(), R.string.entry_type_threats, R.id.action_form_type_threats, "form_threats.csv", ThreatsConverter.class, ThreatsUploader.class, true),
    PYLONS(new NewPylonsEntryFormFragment.Builder(), R.string.entry_type_pylons, R.id.action_form_type_pylons, "form_pylons.csv", PylonsConverter.class, PylonsUploader.class, true),
    PYLONS_CASUALTIES(new NewPylonsCasualtiesEntryFormFragment.Builder(), R.string.entry_type_pylons_casualties, R.id.action_form_type_pylons_casualties, "form_pylons_casualties.csv", PylonsCasualtiesConverter.class, PylonsCasualtiesUploader.class, true),
    // prevent auto-formatting
    ;

    public static final int[] MENU_ACTION_IDS = {
            R.id.action_form_type_birds,
            R.id.action_form_type_cbm,
            R.id.action_form_type_ciconia,
            R.id.action_form_type_humid,
            R.id.action_form_type_herptile,
            R.id.action_form_type_mammal,
            R.id.action_form_type_invertebrates,
            R.id.action_form_type_plants,
            R.id.action_form_type_threats,
            R.id.action_form_type_pylons,
            R.id.action_form_type_pylons_casualties
    };

    private final BaseEntryFragment.Builder builder;
    @StringRes
    public final int titleId;
    @IdRes
    public final int menuActionId;
    public final String filename;
    private final Class<? extends Converter> converterClass;
    private final Class<? extends Uploader> uploaderClass;
    private final boolean enabled;

    EntryType(BaseEntryFragment.Builder builder, @StringRes int titleId, @IdRes int menuActionId, String filename, Class<? extends Converter> converterClass, Class<? extends Uploader> uploaderClass, boolean enabled) {
        this.builder = builder;
        this.titleId = titleId;
        this.menuActionId = menuActionId;
        this.filename = filename;
        this.converterClass = converterClass;
        this.uploaderClass = uploaderClass;
        this.enabled = enabled;
    }

    public Fragment buildFragment(double lat, double lon) {
        return builder.build(lat, lon);
    }

    public Fragment loadFragment(long id) {
        return loadFragment(id, false);
    }

    public Fragment loadFragment(long id, boolean readOnly) {
        return builder.load(id, readOnly);
    }

    public static String[] getTitles(Resources resources) {
        final ArrayList<String> titles = new ArrayList<>();
        for (EntryType formType : values()) {
            if (formType.titleId > 0) {
                if (formType.enabled) {
                    titles.add(resources.getString(formType.titleId));
                }
            }
        }

        return titles.toArray(new String[]{});
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
