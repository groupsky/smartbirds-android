package org.bspb.smartbirds.pro.ui.utils;

/**
 * Created by groupsky on 20.03.17.
 */

public interface Constants {

    @SuppressWarnings("PointlessBitwiseExpression")
    public static final int VIEWTYPE_MAP = 1 << 0;
    public static final int VIEWTYPE_LIST = 1 << 1;
    public static final int VIEWTYPE_COMBINED = VIEWTYPE_MAP | VIEWTYPE_LIST;

}
