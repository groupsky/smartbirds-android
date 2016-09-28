package org.bspb.smartbirds.pro.forms.convert.fields;

import android.text.TextUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static org.bspb.smartbirds.pro.ui.utils.Configuration.STORAGE_DATE_FORMAT;

public class DateConverter extends DateTimeConverter {

    public DateConverter(String csvDateField, String jsonField) {
        super(csvDateField, null, jsonField);
    }

    @Override
    protected Date parse(Map<String, String> csv, Set<String> usedCsvFields) throws ParseException {
        String dateValue = csv.get(csvDateField);
        usedCsvFields.add(csvDateField);

        if (TextUtils.isEmpty(dateValue)) {
            return null;
        } else {
            return STORAGE_DATE_FORMAT.parse(dateValue);
        }
    }
}
