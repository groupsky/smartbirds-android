package org.bspb.smartbirds.pro.ui.views;

import org.bspb.smartbirds.pro.ui.exception.ViewValidationException;

/**
 * Created by dani on 14-11-13.
 */
public interface SupportRequiredView {

    void checkRequired() throws ViewValidationException;
}
