package org.bspb.smartbirds.pro.backend.dto;

import org.bspb.smartbirds.pro.ui.utils.Configuration;

import java.util.Locale;

public class SpeciesLabel extends Label {

    public SpeciesLabel(Label label) {
        this.values = label.values;
    }

    @Override
    public String getLabelId() {
        return get("la");
    }

    @Override
    public String get(Locale locale) {
        String localeLabel = super.get(locale);
        String laLabel = values.get("la");
        return laLabel + Configuration.MULTIPLE_CHOICE_DELIMITER + localeLabel;
    }
}
