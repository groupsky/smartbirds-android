package org.bspb.smartbirds.pro.ui.utils;

import org.bspb.smartbirds.pro.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormsConfig {

    public interface NomenclatureConfig {
        int getLabelId();

        String getId();
    }


    public enum SpeciesClass implements NomenclatureConfig {
        birds(R.string.class_birds),
        herptiles(R.string.class_herptiles),
        mammals(R.string.class_mammals),
        invertebrates(R.string.class_invertebrates),
        plants(R.string.class_plants);

        int labelId;

        SpeciesClass(int labelId) {
            this.labelId = labelId;
        }

        @Override
        public int getLabelId() {
            return labelId;
        }

        @Override
        public String getId() {
            return name();
        }
    }

    public enum ThreatsPrymaryType implements NomenclatureConfig {
        threat(R.string.primary_type_threat),
        poison(R.string.primary_type_poison);

        int labelId;

        ThreatsPrymaryType(int labelId) {
            this.labelId = labelId;
        }

        @Override
        public int getLabelId() {
            return labelId;
        }

        @Override
        public String getId() {
            return name();
        }
    }

    public enum ThreatsPoisonedType implements NomenclatureConfig {
        dead(R.string.poisoned_type_dead),
        alive(R.string.poisoned_type_alive),
        bait(R.string.poisoned_type_bait);

        int labelId;

        ThreatsPoisonedType(int labelId) {
            this.labelId = labelId;
        }

        @Override
        public int getLabelId() {
            return labelId;
        }

        @Override
        public String getId() {
            return name();
        }
    }

    public static List<NomenclatureConfig[]> configs = new ArrayList<NomenclatureConfig[]>() {
        {
            add(SpeciesClass.values());
            add(ThreatsPrymaryType.values());
            add(ThreatsPoisonedType.values());
        }
    };
}