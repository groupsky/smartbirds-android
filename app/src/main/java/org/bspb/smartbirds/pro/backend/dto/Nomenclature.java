package org.bspb.smartbirds.pro.backend.dto;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.bspb.smartbirds.pro.tools.SBGsonParser;

import java.util.Locale;

import static org.bspb.smartbirds.pro.db.NomenclatureColumns.DATA;
import static org.bspb.smartbirds.pro.db.NomenclatureColumns.LABEL_BG;
import static org.bspb.smartbirds.pro.db.NomenclatureColumns.LABEL_EN;
import static org.bspb.smartbirds.pro.db.NomenclatureColumns.TYPE;

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

    transient public String labelId;
    transient public String localeLabel;

    public Nomenclature() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Nomenclature that = (Nomenclature) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return label != null ? label.equals(that.label) : that.label == null;

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
        sb.append(", labelId='").append(labelId).append('\'');
        sb.append(", localeLabel='").append(localeLabel).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public static Nomenclature fromCursor(Cursor cursor, String locale) {
        String data = cursor.getString(cursor.getColumnIndexOrThrow(DATA));
        Nomenclature nomenclature;
        if (TextUtils.isEmpty(data)) {
            nomenclature = new Nomenclature();
            nomenclature.type = cursor.getString(cursor.getColumnIndexOrThrow(TYPE));
            Label label = new Label();
            label.addValue("bg", cursor.getString(cursor.getColumnIndexOrThrow(LABEL_BG)));
            label.addValue("en", cursor.getString(cursor.getColumnIndexOrThrow(LABEL_EN)));
            nomenclature.label = label;
        } else {
            nomenclature = SBGsonParser.createParser().fromJson(data, Nomenclature.class);
        }
        if (nomenclature.type.startsWith("species_")) {
            nomenclature.label = new SpeciesLabel(nomenclature.label);
        }
        nomenclature.localeLabel = nomenclature.label.get(locale);
        nomenclature.labelId = cursor.getString(cursor.getColumnIndexOrThrow(LABEL_EN));
        return nomenclature;
    }

    public static Nomenclature fromSpecies(Nomenclature species) {
        Nomenclature nomenclature = new Nomenclature();
        nomenclature.type = "species_" + species.type;
        nomenclature.label = new SpeciesLabel(species.label);
        return nomenclature;
    }

    public ContentValues toCV() {
        ContentValues cv = new ContentValues();
        cv.put(TYPE, type);
        cv.put(LABEL_EN, label.getLabelId());
        cv.put(DATA, SBGsonParser.createParser().toJson(this));
        return cv;
    }
}
