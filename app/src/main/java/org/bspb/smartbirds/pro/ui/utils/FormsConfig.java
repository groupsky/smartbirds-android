package org.bspb.smartbirds.pro.ui.utils;

import org.bspb.smartbirds.pro.R;

public enum FormsConfig {
    species_class(
            new String[]{"birds", "herptiles", "mammals", "invertebrates", "plants"},
            new int[]{R.string.class_birds, R.string.class_herptiles, R.string.class_mammals, R.string.class_invertebrates, R.string.class_plants}
    ),
    threats_primary_types(
            new String[]{"threat", "poison"},
            new int[]{R.string.primary_type_threat, R.string.primary_type_poison}
    ),
    threats_poisoned_types(
            new String[]{"dead", "alive", "bait"},
            new int[]{R.string.poisoned_type_dead, R.string.poisoned_type_alive, R.string.poisoned_type_bait}
    );

    private String[] values;
    private int[] labels;

    FormsConfig(String[] values, int[] labels) {
        this.values = values;
        this.labels = labels;
    }

    public String[] getValues() {
        return values;
    }

    public int[] getLabels() {
        return labels;
    }
}
