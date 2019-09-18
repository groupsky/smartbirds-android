package org.bspb.smartbirds.pro.ui.utils;

import org.bspb.smartbirds.pro.R;

import java.util.Arrays;
import java.util.List;

public class FormsConfig {

    public interface NomenclatureConfig {
        int getLabelId();

        String getId();

        boolean isSame(String value);

        NomenclatureConfig find(String value);
    }


    public enum SpeciesClass implements NomenclatureConfig {
        birds(R.string.class_birds),
        herptiles(R.string.class_herptiles),
        mammals(R.string.class_mammals),
        invertebrates(R.string.class_invertebrates),
        plants(R.string.class_plants);

        final int labelId;

        SpeciesClass(final int labelId) {
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

        @Override
        public boolean isSame(String value) {
            return this.name().equals(value);
        }

        @Override
        public NomenclatureConfig find(String value) {
            try {
                return valueOf(value);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    public enum ThreatsPrimaryType implements NomenclatureConfig {
        threat(R.string.primary_type_threat),
        poison(R.string.primary_type_poison);

        final int labelId;

        ThreatsPrimaryType(final int labelId) {
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

        @Override
        public boolean isSame(String value) {
            return this.name().equals(value);
        }

        @Override
        public NomenclatureConfig find(String value) {
            try {
                return valueOf(value);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    public enum ThreatsPoisonedType implements NomenclatureConfig {
        dead(R.string.poisoned_type_dead),
        alive(R.string.poisoned_type_alive),
        bait(R.string.poisoned_type_bait);

        final int labelId;

        ThreatsPoisonedType(final int labelId) {
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

        @Override
        public boolean isSame(String value) {
            return this.name().equals(value);
        }

        @Override
        public NomenclatureConfig find(String value) {
            try {
                return valueOf(value);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    public static final List<NomenclatureConfig[]> configs = Arrays.asList(
            new NomenclatureConfig[][]{
                    SpeciesClass.values(),
                    ThreatsPrimaryType.values(),
                    ThreatsPoisonedType.values()
            }
    );
}