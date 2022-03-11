package org.bspb.smartbirds.pro.backend.dto;

import android.content.Context;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.db.model.NomenclatureModel;
import org.bspb.smartbirds.pro.tools.SBGsonParser;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Created by groupsky on 27.09.16.
 */
public class Nomenclature {

    @Expose
    @SerializedName("type")
    public String type;

    @Expose
    @SerializedName("label")
    public Label label;

    /**
     * @deprecated Should use label.get(locale) instead.
     */
    @Deprecated
    transient public String localeLabel;

    public Nomenclature() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Nomenclature that = (Nomenclature) o;

        if (!Objects.equals(type, that.type)) return false;
        return Objects.equals(label, that.label);

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (label != null ? label.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Nomenclature{");
        sb.append("type='").append(type).append('\'');
        sb.append(", label=").append(label);
        sb.append(", localeLabel='").append(localeLabel).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public static Nomenclature fromSpecies(Nomenclature species, String locale) {
        Nomenclature nomenclature = new Nomenclature();
        nomenclature.type = "species_" + species.type;
        nomenclature.label = new SpeciesLabel(species.label);
        nomenclature.localeLabel = nomenclature.label.get(locale);
        return nomenclature;
    }

    @NotNull
    public static Nomenclature fromData(@NotNull String data, String locale) {
        Nomenclature nomenclature = SBGsonParser.createParser().fromJson(data, Nomenclature.class);
        if (nomenclature.type.startsWith("species_")) {
            nomenclature.label = new SpeciesLabel(nomenclature.label);
        }
        nomenclature.localeLabel = nomenclature.label.get(locale);
        return nomenclature;
    }

    public NomenclatureModel convertToEntity() {
        return new NomenclatureModel(0, type, SBGsonParser.createParser().toJson(this));
    }

    public NomenclatureModel convertSpeciesToEntity(Context context) {
        Nomenclature nomenclature = Nomenclature.fromSpecies(this, context.getString(R.string.locale));
        return new NomenclatureModel(0, nomenclature.type, SBGsonParser.createParser().toJson(nomenclature));
    }
}
