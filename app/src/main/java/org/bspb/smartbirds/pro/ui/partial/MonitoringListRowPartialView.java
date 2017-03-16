package org.bspb.smartbirds.pro.ui.partial;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.content.Monitoring;

/**
 * Created by groupsky on 08.03.17.
 */

@EViewGroup(R.layout.partial_monitoring_list_row)
public class MonitoringListRowPartialView extends LinearLayout {

    private static final String TAG = SmartBirdsApplication.TAG + ".MonLRPV";

    @ViewById(R.id.place)
    TextView placeView;

    @ViewById(R.id.start)
    TextView startView;

    @ViewById(R.id.status)
    TextView statusView;

    @StringRes(R.string.tag_location)
    String tagLocation;

    @StringRes(R.string.tag_begin_date)
    String tagBeginDate;

    @StringRes(R.string.tag_begin_time)
    String tagBeginTime;
    private Monitoring monitoring;

    public MonitoringListRowPartialView(Context context) {
        this(context, null);
    }

    public MonitoringListRowPartialView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MonitoringListRowPartialView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @AfterViews
    protected void bind() {
        Log.d(TAG, "bind after views");
        if (monitoring != null) bind(monitoring);
    }

    public void bind(Monitoring monitoring) {
        Log.d(TAG, "bind");
        this.monitoring = monitoring;
        if (monitoring == null) return;
        if (placeView == null) return;
        placeView.setText(monitoring.commonForm.get(tagLocation));
        startView.setText(monitoring.commonForm.get(tagBeginDate) + ", " + monitoring.commonForm.get(tagBeginTime));
        statusView.setText(monitoring.status.name());
    }
}
