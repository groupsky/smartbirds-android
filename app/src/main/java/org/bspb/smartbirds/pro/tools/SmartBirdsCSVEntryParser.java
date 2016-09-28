package org.bspb.smartbirds.pro.tools;

import com.googlecode.jcsv.reader.CSVEntryParser;

import java.util.regex.Pattern;

import static org.bspb.smartbirds.pro.ui.utils.Configuration.MULTIPLE_CHOICE_DELIMITER;

/**
 * Created by groupsky on 28.09.16.
 */

public class SmartBirdsCSVEntryParser implements CSVEntryParser<String[]> {
    @Override
    public String[] parseEntry(String... data) {
        String[] convertedData = new String[data.length];
        for (int i=0; i<data.length; i++) {
            convertedData[i] = data[i].replaceAll(Pattern.quote(MULTIPLE_CHOICE_DELIMITER), "\n");
        }
        return convertedData;
    }
}
