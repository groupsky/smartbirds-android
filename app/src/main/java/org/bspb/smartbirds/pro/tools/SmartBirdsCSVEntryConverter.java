package org.bspb.smartbirds.pro.tools;

import com.googlecode.jcsv.writer.CSVEntryConverter;

import static org.bspb.smartbirds.pro.ui.utils.Configuration.MULTIPLE_CHOICE_DELIMITER;

/**
 * Created by groupsky on 28.09.16.
 */

public class SmartBirdsCSVEntryConverter implements CSVEntryConverter<String[]> {
    @Override
    public String[] convertEntry(String[] data) {
        String[] convertedData = new String[data.length];
        for (int i=0; i<data.length; i++) {
            convertedData[i] = data[i].replaceAll("\n", MULTIPLE_CHOICE_DELIMITER);
        }
        return convertedData;
    }
}
