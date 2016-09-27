package org.bspb.smartbirds.pro.ui.views;

import java.util.Map;

/**
 * Created by groupsky on 22.03.16.
 */
public interface SupportStorage {
    void serializeToStorage(Map<String, String> storage, String fieldName);

    void restoreFromStorage(Map<String, String> storage, String fieldName);
}
