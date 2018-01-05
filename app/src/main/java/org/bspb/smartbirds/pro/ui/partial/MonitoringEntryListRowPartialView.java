package org.bspb.smartbirds.pro.ui.partial;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.content.MonitoringEntry;
import org.bspb.smartbirds.pro.enums.EntryType;

import java.util.Locale;

/**
 * Created by groupsky on 08.03.17.
 */

@EViewGroup(R.layout.partial_monitoring_entry_list_row)
public class MonitoringEntryListRowPartialView extends LinearLayout implements Checkable {

    private static final String TAG = SmartBirdsApplication.TAG + ".MonEnLRPV";

    @ViewById(R.id.type)
    TextView typeView;

    @ViewById(R.id.species)
    TextView speciesView;

    @ViewById(R.id.count)
    TextView countView;

    @ViewById(R.id.status)
    TextView statusView;

    private boolean isChecked;

    private MonitoringEntry entry;

    public MonitoringEntryListRowPartialView(Context context) {
        this(context, null);
    }

    public MonitoringEntryListRowPartialView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MonitoringEntryListRowPartialView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @AfterViews
    protected void bind() {
        Log.d(TAG, "bind after views");
        if (entry != null) bind(entry);
    }

    public void bind(MonitoringEntry entry) {
        Log.d(TAG, "bind");
        this.entry = entry;
        if (entry == null) return;
        if (speciesView == null) return;
        Context context = getContext();

        switch (entry.type) {
            case BIRDS:
            case HUMID:
                typeView.setText(entry.type == EntryType.BIRDS ? R.string.entry_type_birds : R.string.entry_type_humid);
                speciesView.setText(entry.data.get(context.getString(R.string.tag_species_scientific_name)));
                String tagBirdsCountType = context.getString(R.string.tag_count_type);
                String countsType = entry.data.get(tagBirdsCountType + ".en");
                String count;
                switch (countsType != null ? countsType.toLowerCase(Locale.ENGLISH) : "") {
                    case "exact number": // Exact count
                        count = entry.data.get(context.getString(R.string.tag_count));
                        break;
                    case "min.": // Min count
                        count = entry.data.get(context.getString(R.string.tag_min));
                        break;
                    case "max.": // Max count
                        count = entry.data.get(context.getString(R.string.tag_max));
                        break;
                    case "range": // Range count
                        count = entry.data.get(context.getString(R.string.tag_min)) + " - " + entry.data.get(context.getString(R.string.tag_max));
                        break;
                    case "unspecified number": // Unspecified
                    default:
                        count = "";
                        break;
                }
                countView.setText(entry.data.get(tagBirdsCountType) + " " + count + " " + entry.data.get(context.getString(R.string.tag_count_unit)).toLowerCase(Locale.getDefault()));
                break;
            case CBM:
                typeView.setText(R.string.entry_type_cbm);
                speciesView.setText(entry.data.get(context.getString(R.string.tag_observed_bird)));
                countView.setText(entry.data.get(context.getString(R.string.tag_count_subject)));
                break;
            case HERP:
                typeView.setText(R.string.entry_type_herp);
                speciesView.setText(entry.data.get(context.getString(R.string.tag_species_scientific_name)));
                countView.setText(entry.data.get(context.getString(R.string.tag_count)));
                break;
            case HERPTILE:
                typeView.setText(R.string.entry_type_herptile);
                speciesView.setText(entry.data.get(context.getString(R.string.tag_species_scientific_name)));
                countView.setText(entry.data.get(context.getString(R.string.tag_count)));
                break;
            case MAMMAL:
                typeView.setText(R.string.entry_type_mammal);
                speciesView.setText(entry.data.get(context.getString(R.string.tag_species_scientific_name)));
                countView.setText(entry.data.get(context.getString(R.string.tag_count)));
                break;
            case CICONIA:
                typeView.setText(R.string.entry_type_ciconia);
                speciesView.setText(entry.data.get(context.getString(R.string.tag_substrate_type)));
                countView.setText(entry.data.get(context.getString(R.string.tag_number_juveniles_in_nest)));
                break;
        }
    }

    @Override
    public void setChecked(boolean checked) {
        Log.d(TAG, String.format(Locale.ENGLISH, "setChecked: %s", checked));
        isChecked = checked;
        if (isChecked) {
            setBackgroundResource(R.color.backgroundSelected);
        } else {
            setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked);
    }
}
