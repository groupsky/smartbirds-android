package org.bspb.smartbirds.pro.backend;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.backend.dto.Nomenclature;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import static org.bspb.smartbirds.pro.ui.utils.Configuration.STORAGE_DATE_FORMAT;
import static org.bspb.smartbirds.pro.ui.utils.Configuration.STORAGE_TIME_FORMAT;

/**
 * Created by groupsky on 26.09.16.
 */

public class Converter {

    private static final List<Convert> commonMapping = new ArrayList<>();
    private static final List<Convert> birdsMapping = new ArrayList<>();
    private static final List<Convert> herpMapping = new ArrayList<>();
    private static final List<Convert> ciconiaMapping = new ArrayList<>();
    private static final List<Convert> cbmMapping = new ArrayList<>();
    private static final String TAG = SmartBirdsApplication.TAG + ".Converter";

    private static void add(Context context, List<Convert> list, @StringRes int csvFieldName, String jsonFieldName) {
        add(context, list, csvFieldName, jsonFieldName, null);
    }

    private static void add(Context context, List<Convert> list, @StringRes int csvFieldName, String jsonFieldName, String defaultValue) {
        list.add(new SimpleConverter(context.getString(csvFieldName), jsonFieldName, defaultValue));
    }

    private static void addSingle(Context context, List<Convert> list, @StringRes int csvFieldName, String jsonFieldName) {
        list.add(new SingleChoiceConverter(context, context.getString(csvFieldName), jsonFieldName));
    }

    private static void addMulti(Context context, List<Convert> list, @StringRes int csvFieldName, String jsonFieldName) {
        list.add(new MultipleChoiceConverter(context, context.getString(csvFieldName), jsonFieldName));
    }

    private static void addBool(Context context, List<Convert> list, @StringRes int csvFieldName, String jsonFieldName) {
        list.add(new BooleanConverter(context.getString(csvFieldName), jsonFieldName));
    }

    private static void addSpecies(Context context, List<Convert> list, @StringRes int csvFieldName, String jsonFieldName) {
        list.add(new SpeciesConverter(context, context.getString(csvFieldName), jsonFieldName));
    }

    private static void addDateTime(Context context, List<Convert> list, @StringRes int csvDateFieldName, @StringRes int csvTimeFieldName, String jsonFieldName) {
        list.add(new DateTimeConverter(context.getString(csvDateFieldName), context.getString(csvTimeFieldName), jsonFieldName));
    }

    private static void addDate(Context context, List<Convert> list, @StringRes int csvDateFieldName, String jsonFieldName) {
        list.add(new DateConverter(context.getString(csvDateFieldName), jsonFieldName));
    }

    public static void init(Context context) {
        // common form
        add(context, commonMapping, R.string.tag_location, "location");
        add(context, commonMapping, R.string.tag_lat, "latitude");
        add(context, commonMapping, R.string.tag_lon, "longitude");
        addDateTime(context, commonMapping, R.string.entry_date, R.string.entry_time, "observationDateTime");
        add(context, commonMapping, R.string.monitoring_id, "monitoringCode");
        addDateTime(context, commonMapping, R.string.tag_begin_date, R.string.tag_begin_time, "startDateTime");
        addDateTime(context, commonMapping, R.string.tag_end_date, R.string.tag_end_time, "endDateTime");
        add(context, commonMapping, R.string.tag_observers, "observers");
        addSingle(context, commonMapping, R.string.tag_rain, "rain");
        add(context, commonMapping, R.string.tag_temperature, "temperature");
        addSingle(context, commonMapping, R.string.tag_wind_direction, "windDirection");
        addSingle(context, commonMapping, R.string.tag_wind_strength, "windSpeed");
        addSingle(context, commonMapping, R.string.tag_cloudiness, "cloudiness");
        add(context, commonMapping, R.string.tag_clouds_type, "cloudsType");
        add(context, commonMapping, R.string.tag_visibility_km, "visibility");
        add(context, commonMapping, R.string.tag_weather_other, "mto");
        add(context, commonMapping, R.string.tag_notes, "notes");
        addMulti(context, commonMapping, R.string.tag_threats, "threats");

        // birds
        birdsMapping.addAll(commonMapping);
        addSingle(context, birdsMapping, R.string.tag_source, "source");
        addSpecies(context, birdsMapping, R.string.tag_species_scientific_name, "species");
        addBool(context, birdsMapping, R.string.tag_confidential, "confidential");
        addSingle(context, birdsMapping, R.string.tag_count_unit, "countUnit");
        addSingle(context, birdsMapping, R.string.tag_count_type, "typeUnit");
        addSingle(context, birdsMapping, R.string.tag_nest_type, "typeNesting");
        add(context, birdsMapping, R.string.tag_count, "count", "0");
        add(context, birdsMapping, R.string.tag_min, "countMin", "0");
        add(context, birdsMapping, R.string.tag_max, "countMax", "0");
        addSingle(context, birdsMapping, R.string.tag_sex, "sex");
        addSingle(context, birdsMapping, R.string.tag_age, "age");
        addSingle(context, birdsMapping, R.string.tag_marking, "marking");
        addSingle(context, birdsMapping, R.string.tag_bird_status, "speciesStatus");
        addMulti(context, birdsMapping, R.string.tag_behavior, "behaviour");
        addSingle(context, birdsMapping, R.string.tag_dead_specimen_reason, "deadIndividualCauses");
        addSingle(context, birdsMapping, R.string.tag_substrate, "substrate");
        add(context, birdsMapping, R.string.tag_tree, "tree");
        add(context, birdsMapping, R.string.tag_tree_height, "treeHeight");
        addSingle(context, birdsMapping, R.string.tag_tree_location, "treeLocation");
        addSingle(context, birdsMapping, R.string.tag_nest_height, "nestHeight");
        addSingle(context, birdsMapping, R.string.tag_nest_location, "nestLocation");
        addSingle(context, birdsMapping, R.string.tag_tree_location, "treeLocation");
        addBool(context, birdsMapping, R.string.tag_incubation, "brooding");
        add(context, birdsMapping, R.string.tag_number_of_eggs, "eggsCount");
        add(context, birdsMapping, R.string.tag_number_of_pull, "countNestling");
        add(context, birdsMapping, R.string.tag_number_of_fledglings, "countFledgling");
        add(context, birdsMapping, R.string.tag_number_fledged_juveniles, "countSuccessfullyLeftNest");
        addBool(context, birdsMapping, R.string.tag_nest_guarding, "nestProtected");
        addSingle(context, birdsMapping, R.string.tag_age_female, "ageFemale");
        addSingle(context, birdsMapping, R.string.tag_age_male, "ageMale");
        addSingle(context, birdsMapping, R.string.tag_breeding_success, "nestingSuccess");
        add(context, birdsMapping, R.string.tag_land_uses_300m, "landuse300mRadius");
        add(context, birdsMapping, R.string.tag_remarks_type, "speciesNotes");

        // herps
        herpMapping.addAll(commonMapping);
        addSpecies(context, herpMapping, R.string.tag_species_scientific_name, "species");
        addSingle(context, herpMapping, R.string.tag_sex, "sex");
        addSingle(context, herpMapping, R.string.tag_age, "age");
        addSingle(context, herpMapping, R.string.tag_habitat, "habitat");
        addMulti(context, herpMapping, R.string.tag_threats_other, "threatsHerps");
        add(context, herpMapping, R.string.tag_count, "count");
        add(context, herpMapping, R.string.tag_marking, "marking");
        add(context, herpMapping, R.string.tag_distance_from_axis, "axisDistance");
        add(context, herpMapping, R.string.tag_weight_g, "weight");
        add(context, herpMapping, R.string.tag_scl, "sCLL");
        add(context, herpMapping, R.string.tag_mpl, "mPLLcdC");
        add(context, herpMapping, R.string.tag_mcw, "mCWA");
        add(context, herpMapping, R.string.tag_lcap, "hLcapPl");
        add(context, herpMapping, R.string.tag_t_substrate, "tempSubstrat");
        add(context, herpMapping, R.string.tag_t_air, "tempAir");
        add(context, herpMapping, R.string.tag_t_cloaca, "tempCloaca");
        add(context, herpMapping, R.string.tag_sq_ventr, "sqVentr");
        add(context, herpMapping, R.string.tag_sq_caud, "sqCaud");
        add(context, herpMapping, R.string.tag_sq_dors, "sqDors");
        add(context, herpMapping, R.string.tag_remarks_type, "speciesNotes");

        // ciconia
        ciconiaMapping.addAll(commonMapping);
        addSingle(context, ciconiaMapping, R.string.tag_substrate_type, "primarySubstrateType");
        addSingle(context, ciconiaMapping, R.string.tag_pylon, "electricityPole");
        addBool(context, ciconiaMapping, R.string.tag_nest_artificial_platform, "nestIsOnArtificialPlatform");
        addSingle(context, ciconiaMapping, R.string.tag_pylon_type, "typeElectricityPole");
        addSingle(context, ciconiaMapping, R.string.tag_ciconia_tree, "tree");
        addSingle(context, ciconiaMapping, R.string.tag_building, "building");
        addBool(context, ciconiaMapping, R.string.tag_nest_artificial_platform_human, "nestOnArtificialHumanMadePlatform");
        add(context, ciconiaMapping, R.string.tag_nest_another_substrate, "nestIsOnAnotherTypeOfSubstrate");
        addSingle(context, ciconiaMapping, R.string.tag_nest_not_occupied_this_year, "nestThisYearNotUtilizedByWhiteStorks");
        addSingle(context, ciconiaMapping, R.string.tag_birds_come_to_nest_this_year, "thisYearOneTwoBirdsAppearedInNest");
        addDate(context, ciconiaMapping, R.string.tag_approximate_date_stork_arrival, "approximateDateStorksAppeared");
        addDate(context, ciconiaMapping, R.string.tag_approximate_date_stork_disappear, "approximateDateDisappearanceWhiteStorks");
        addSingle(context, ciconiaMapping, R.string.tag_this_year_in_the_nest_appeared, "thisYearInTheNestAppeared");
        add(context, ciconiaMapping, R.string.tag_number_juveniles_in_nest, "countJuvenilesInNest");
        add(context, ciconiaMapping, R.string.tag_nest_not_inhabited_more_than_year, "nestNotUsedForOverOneYear");
        add(context, ciconiaMapping, R.string.tag_info_for_juveniles_electrocuted, "dataOnJuvenileMortalityFromElectrocutions");
        add(context, ciconiaMapping, R.string.tag_info_for_juveniles_rejected_by_parents, "dataOnJuvenilesExpelledFromParents");
        add(context, ciconiaMapping, R.string.tag_died_from_other_causes, "diedOtherReasons");
        add(context, ciconiaMapping, R.string.tag_cause, "reason");
        add(context, ciconiaMapping, R.string.tag_ciconia_remarks_type, "speciesNotes");

        // cbm
        cbmMapping.addAll(commonMapping);
        addSingle(context, cbmMapping, R.string.tag_transect_number, "plot");
        addSingle(context, cbmMapping, R.string.tag_visit_number, "visit");
        addSingle(context, cbmMapping, R.string.tag_secondary_habitat, "secondaryHabitat");
        addSingle(context, cbmMapping, R.string.tag_primary_habitat, "primaryHabitat");
        addSingle(context, cbmMapping, R.string.tag_distance, "distance");
        addSpecies(context, cbmMapping, R.string.tag_observed_bird, "species");
        add(context, cbmMapping, R.string.tag_count_subject, "count");
        // TODO: zone
        add(context, cbmMapping, R.string.tag_location, "zone");
    }

    private static JsonObject convert(List<String> header, String[] row, List<Convert> converters) throws Exception {
        HashMap<String, String> csv = new HashMap<>();
        Iterator<String> it = header.iterator();
        String columnName;
        for (int idx = 0; it.hasNext() && idx < row.length; idx++) {
            columnName = it.next();
            csv.put(columnName, row[idx]);
        }

        JsonObject result = new JsonObject();
        HashSet<String> usedCsvColumns = new HashSet<>();
        for (Convert converter : converters) {
            converter.convert(csv, result, usedCsvColumns);
        }

        Set<String> unusedColumns = new HashSet<>(csv.keySet());
        unusedColumns.removeAll(usedCsvColumns);
        if (!unusedColumns.isEmpty()) {
            Log.w(TAG, "Unused csv columns: " + TextUtils.join(", ", unusedColumns));
        }

        return result;
    }

    public static JsonObject convertBirds(List<String> header, String[] row) throws Exception {
        return convert(header, row, birdsMapping);
    }

    public static JsonObject convertHerp(List<String> header, String[] row) throws Exception {
        return convert(header, row, herpMapping);
    }

    public static JsonObject convertCiconia(List<String> header, String[] row) throws Exception {
        return convert(header, row, ciconiaMapping);
    }

    public static JsonObject convertCbm(List<String> header, String[] row) throws Exception {
        return convert(header, row, cbmMapping);
    }

    interface Convert {
        void convert(Map<String, String> csv, JsonObject json, Set<String> usedCsvFields) throws Exception;
    }

    private static class SimpleConverter implements Convert {
        final String csvField;
        final String jsonField;
        final String defaultValue;

        SimpleConverter(String csvField, String jsonField, String defaultValue) {
            this.csvField = csvField;
            this.jsonField = jsonField;
            this.defaultValue = defaultValue;
        }

        @Override
        public void convert(Map<String, String> csv, JsonObject json, Set<String> usedCsvFields) throws Exception {
            String value = csv.get(csvField);
            if (TextUtils.isEmpty(value)) value = defaultValue;
            json.addProperty(jsonField, value);
            usedCsvFields.add(csvField);
        }
    }

    private static class BooleanConverter implements Convert {
        final String csvField;
        final String jsonField;

        BooleanConverter(String csvField, String jsonField) {
            this.csvField = csvField;
            this.jsonField = jsonField;
        }

        @Override
        public void convert(Map<String, String> csv, JsonObject json, Set<String> usedCsvFields) throws Exception {
            String value = csv.get(csvField);
            if (!TextUtils.isEmpty(value)) {
                json.addProperty(jsonField, "1".equals(value));
            } else {
                json.add(jsonField, JsonNull.INSTANCE);
            }
            usedCsvFields.add(csvField);
        }
    }

    private static class DateTimeConverter implements Convert {

        static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz", Locale.ENGLISH);
        static final TimeZone tz = TimeZone.getTimeZone("UTC");

        static {
            df.setTimeZone(tz);
        }

        final String csvDateField;
        final String csvTimeField;
        final String jsonField;

        public DateTimeConverter(String csvDateField, String csvTimeField, String jsonField) {
            this.csvDateField = csvDateField;
            this.csvTimeField = csvTimeField;
            this.jsonField = jsonField;
        }

        protected Date parse(Map<String, String> csv, Set<String> usedCsvFields) throws ParseException {
            String dateValue = csv.get(csvDateField);
            String timeValue = csv.get(csvTimeField);
            usedCsvFields.add(csvDateField);
            usedCsvFields.add(csvTimeField);

            if (TextUtils.isEmpty(dateValue) && TextUtils.isEmpty(timeValue)) {
                return null;
            } else {
                Date date = STORAGE_DATE_FORMAT.parse(dateValue);
                Date time = STORAGE_TIME_FORMAT.parse(timeValue);

                return new Date(date.getTime() + time.getTime());
            }
        }

        @Override
        public void convert(Map<String, String> csv, JsonObject json, Set<String> usedCsvFields) throws Exception {
            Date value = parse(csv, usedCsvFields);
            if (value == null) {
                json.add(jsonField, JsonNull.INSTANCE);
            } else {
                String output = df.format(value);

                int inset0 = 9;
                int inset1 = 6;

                String s0 = output.substring(0, output.length() - inset0);
                String s1 = output.substring(output.length() - inset1, output.length());

                String result = s0 + s1;

                result = result.replaceAll("UTC", "+00:00");

                json.addProperty(jsonField, result);
            }
        }
    }

    private static class DateConverter extends DateTimeConverter {
        DateConverter(String csvDateField, String jsonField) {
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

    private static class SingleChoiceConverter implements Convert {

        final String locale;
        final String csvField;
        final String jsonField;

        SingleChoiceConverter(Context context, String csvField, String jsonField) {
            this.locale = context.getString(R.string.locale);
            this.csvField = csvField;
            this.jsonField = jsonField;
        }

        @Override
        public void convert(Map<String, String> csv, JsonObject json, Set<String> usedCsvFields) throws Exception {
            String value = csv.get(csvField);
            if (TextUtils.isEmpty(value)) {
                json.add(jsonField, JsonNull.INSTANCE);
            } else {
                JsonObject label = new JsonObject();
                label.addProperty(locale, value);
                {
                    String bg = csv.get(csvField + ".bg");
                    if (!TextUtils.isEmpty(bg))
                        label.addProperty("bg", bg);
                }
                {
                    String en = csv.get(csvField + ".en");
                    if (!TextUtils.isEmpty(en))
                        label.addProperty("en", en);
                }
                JsonObject field = new JsonObject();
                field.add("label", label);
                json.add(jsonField, field);
            }
            usedCsvFields.add(csvField);
            usedCsvFields.add(csvField+".bg");
            usedCsvFields.add(csvField+".en");
        }
    }

    private static class SpeciesConverter implements Convert {

        final String locale;
        final String csvField;
        final String jsonField;

        SpeciesConverter(Context context, String csvField, String jsonField) {
            this.locale = context.getString(R.string.locale);
            this.csvField = csvField;
            this.jsonField = jsonField;
        }

        @Override
        public void convert(Map<String, String> csv, JsonObject json, Set<String> usedCsvFields) throws Exception {
            String value = csv.get(csvField);
            if (!TextUtils.isEmpty(value)) {
                String[] values = value.split(" *\n *");
                json.addProperty(jsonField, values[0]);
            } else {
                json.add(jsonField, JsonNull.INSTANCE);
            }

            usedCsvFields.add(csvField);
            usedCsvFields.add(csvField+".bg");
            usedCsvFields.add(csvField+".en");
        }
    }

    private static class MultipleChoiceConverter implements Convert {
        final String locale;
        final String csvField;
        final String jsonField;

        MultipleChoiceConverter(Context context, String csvField, String jsonField) {
            this.locale = context.getString(R.string.locale);
            this.csvField = csvField;
            this.jsonField = jsonField;
        }

        @Override
        public void convert(Map<String, String> csv, JsonObject json, Set<String> usedCsvFields) throws Exception {
            String value = csv.get(csvField + ".json");
            if (!TextUtils.isEmpty(value)) {
                Nomenclature[] values = new Gson().fromJson(value, Nomenclature[].class);
                json.add(jsonField, new Gson().toJsonTree(values));
            } else {
                json.add(jsonField, JsonNull.INSTANCE);
            }

            usedCsvFields.add(csvField);
            usedCsvFields.add(csvField+".json");
        }
    }
}
