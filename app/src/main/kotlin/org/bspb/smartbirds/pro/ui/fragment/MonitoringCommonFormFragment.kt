package org.bspb.smartbirds.pro.ui.fragment

import android.content.Context
import android.database.Cursor
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import org.androidannotations.annotations.*
import org.androidannotations.annotations.sharedpreferences.Pref
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.db.LocationColumns
import org.bspb.smartbirds.pro.db.SmartBirdsProvider
import org.bspb.smartbirds.pro.events.EEventBus
import org.bspb.smartbirds.pro.events.GetMonitoringCommonData
import org.bspb.smartbirds.pro.events.MonitoringCommonData
import org.bspb.smartbirds.pro.events.SetMonitoringCommonData
import org.bspb.smartbirds.pro.prefs.CommonPrefs_
import org.bspb.smartbirds.pro.prefs.UserPrefs_
import org.bspb.smartbirds.pro.service.DataService_
import org.bspb.smartbirds.pro.tools.ArrayUtils
import org.bspb.smartbirds.pro.ui.utils.Configuration
import org.bspb.smartbirds.pro.ui.utils.FormUtils
import org.bspb.smartbirds.pro.ui.utils.FormUtils.FormModel
import org.bspb.smartbirds.pro.ui.views.DateFormInput
import org.bspb.smartbirds.pro.ui.views.MultipleTextFormInput
import org.bspb.smartbirds.pro.ui.views.TimeFormInput
import java.util.*

@EFragment(R.layout.fragment_monitoring_form_common)
open class MonitoringCommonFormFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {

    companion object {
        private const val TAG = SmartBirdsApplication.TAG + ".CommonForm"
        private const val LOADER_NEAERST_PLACES = 1
    }

    @Bean
    protected lateinit var bus: EEventBus

    private var form: FormModel? = null

    @ViewById(R.id.form_common_start_date)
    protected lateinit var startDateView: DateFormInput

    @ViewById(R.id.form_common_start_time)
    protected lateinit var startTimeView: TimeFormInput

    @ViewById(R.id.form_common_end_date)
    protected lateinit var endDateView: DateFormInput

    @Pref
    protected lateinit var prefs: CommonPrefs_

    @Pref
    protected lateinit var userPrefs: UserPrefs_

    @ViewById(R.id.observers)
    protected lateinit var observers: MultipleTextFormInput

    @ViewById(R.id.location)
    protected lateinit var locationView: TextView

    @ViewById(R.id.button_refresh_location)
    protected lateinit var buttonRefreshLocation: View

    @ViewById(R.id.progress_location_refreshing)
    protected lateinit var progressLocationRefreshing: View

    @JvmField
    @InstanceState
    protected var lastLocation: Location? = null

    @JvmField
    @InstanceState
    protected var manualLocation = false

    @JvmField
    @InstanceState
    protected var reloadLocationLoader = false

    @JvmField
    @Transient
    protected var updatingLocationFromGPS = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DataService_.intent(context).start()
    }

    override fun onStart() {
        super.onStart()
        bus.register(this)
        bus.postSticky(GetMonitoringCommonData())
    }

    override fun onResume() {
        super.onResume()
        observers.setText(prefs.commonOtherObservers().get())
    }

    override fun onPause() {
        super.onPause()
        prefs.commonOtherObservers().put(observers.text.toString())
    }

    override fun onStop() {
        bus.unregister(this)
        super.onStop()
    }

    @AfterViews
    open fun loadSavedData() {
        form = FormUtils.traverseForm(view)
        startDateView.value = Calendar.getInstance()
        startTimeView.setValue(Calendar.getInstance())
        endDateView.value = Calendar.getInstance()
    }

    @OptionsItem(R.id.action_submit)
    fun save() {
        val data = form!!.serialize()
        data[getString(R.string.tag_user_id)] = userPrefs.userId().get()
        data[getString(R.string.tag_user_first_name)] = userPrefs.firstName().get()
        data[getString(R.string.tag_user_last_name)] = userPrefs.lastName().get()
        data[getString(R.string.tag_user_email)] = userPrefs.email().get()
        bus.post(SetMonitoringCommonData(data))
    }

    fun onEventMainThread(event: MonitoringCommonData) {
        if (event.data != null && event.data.isNotEmpty()) {
            manualLocation = true
            form!!.deserialize(event.data)
        }
    }

    fun onEvent(location: Location?) {
        if (location == null) return
        lastLocation = location
        if (!reloadLocationLoader) {
            val loader: Loader<Any>? = loaderManager.getLoader<Any>(LOADER_NEAERST_PLACES)
            if (loader != null && loader.isStarted) return
        } else {
            reloadLocationLoader = false
        }
        loaderManager.restartLoader(LOADER_NEAERST_PLACES, null, this)
    }

    fun validate(): Boolean {
        return form!!.validateFields()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        when (id) {
            LOADER_NEAERST_PLACES -> {
                val location = lastLocation
                if (manualLocation) {
                    buttonRefreshLocation.visibility = View.VISIBLE
                    progressLocationRefreshing.visibility = View.INVISIBLE
                }
                if (location == null) {
                    reloadLocationLoader = true
                }
                buttonRefreshLocation.visibility = View.INVISIBLE
                progressLocationRefreshing.visibility = View.VISIBLE
                return CursorLoader(activity!!, SmartBirdsProvider.Locations.CONTENT_URI_LIMIT_1,
                        ArrayUtils.join(org.bspb.smartbirds.pro.backend.dto.Location.DEFAULT_PROJECTION,
                                SmartBirdsProvider.Locations.distance(location?.latitude
                                        ?: 0.0, location?.longitude
                                        ?: 0.0)),  // in case we don't have location or we don't want to modify it, force a false where clause
                        if (location == null || manualLocation) "1=0" else null, null,  // order by disance in ascending order
                        LocationColumns.DISTANCE)
            }
        }
        return CursorLoader(context!!)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor?) {
        if (reloadLocationLoader) return
        buttonRefreshLocation.visibility = View.VISIBLE
        progressLocationRefreshing.visibility = View.INVISIBLE
        Log.d(TAG, String.format(Locale.ENGLISH, "found places: %d", cursor!!.count))
        if (cursor.moveToFirst() && !cursor.isAfterLast) {
            val location = org.bspb.smartbirds.pro.backend.dto.Location.fromCursor(cursor)
            Log.d(TAG, String.format(Locale.ENGLISH, "place: %s", location))
            Log.d(TAG, String.format(Locale.ENGLISH, "distance: %s", cursor.getDouble(cursor.getColumnIndexOrThrow(LocationColumns.DISTANCE))))
            val location1 = lastLocation ?: return
            val res = FloatArray(1)
            Location.distanceBetween(location1.latitude, location1.longitude, location.latitude, location.longitude, res)
            Log.d(TAG, String.format(Locale.ENGLISH, "distance (m): %f", res[0]))
            if (res[0] > Configuration.MAX_DISTANCE_LOCATION_METERS) {
                Toast.makeText(activity, R.string.no_place_nearby, Toast.LENGTH_SHORT).show()
                return
            }
            if (!manualLocation) {
                updatingLocationFromGPS = true
                try {
                    locationView.text = location.name[getString(R.string.locale)]
                } finally {
                    updatingLocationFromGPS = false
                }
            }
        } else {
            Toast.makeText(activity, R.string.no_place_nearby, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
    }

    @Click(R.id.button_refresh_location)
    open fun refreshLocation() {
        buttonRefreshLocation.visibility = View.INVISIBLE
        progressLocationRefreshing.visibility = View.VISIBLE
        manualLocation = false
        loaderManager.restartLoader(LOADER_NEAERST_PLACES, null, this)
    }

    @TextChange(R.id.location)
    open fun textChange() {
        if (updatingLocationFromGPS) return
        Log.d(TAG, "location changed manually")
        manualLocation = true
        buttonRefreshLocation.visibility = View.VISIBLE
        progressLocationRefreshing.visibility = View.INVISIBLE
    }
}