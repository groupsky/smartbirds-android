package org.bspb.smartbirds.pro.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by groupsky on 22.03.17.
 */

public class CsvPreparer {
    public static PreparedLine prepareCsvLine(HashMap<String, String> data) {
        final ArrayList<String> inKeys = new ArrayList<>(data.keySet());
        final Integer[] order = new Integer[inKeys.size()];
        for (int i = 0; i < order.length; i++) order[i] = i;
        Arrays.sort(order, new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return inKeys.get(a).compareTo(inKeys.get(b));
            }
        });

        final ArrayList<String> inValues = new ArrayList<>(data.values());
        final String[] keys = new String[order.length];
        final String[] values = new String[order.length];
        for (int i = 0; i < order.length; i++) {
            keys[i] = inKeys.get(order[i]);
            values[i] = inValues.get(order[i]);
        }
        return new PreparedLine(keys, values);
    }

    public static class PreparedLine {
        public final String[] keys;
        public final String[] values;

        public PreparedLine(String[] keys, String[] values) {
            this.keys = keys;
            this.values = values;
        }
    }
}
