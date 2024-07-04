package org.bspb.smartbirds.pro.ui.fragment

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import de.greenrobot.event.EventBusException
import kotlinx.coroutines.launch
import org.bspb.smartbirds.pro.BuildConfig
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.content.MonitoringEntry
import org.bspb.smartbirds.pro.enums.EntryType
import org.bspb.smartbirds.pro.enums.EntryType.EntryFragment
import org.bspb.smartbirds.pro.events.EEventBus
import org.bspb.smartbirds.pro.events.EntrySubmitted
import org.bspb.smartbirds.pro.service.DataService
import org.bspb.smartbirds.pro.tools.Reporting
import org.bspb.smartbirds.pro.ui.utils.Configuration
import org.bspb.smartbirds.pro.viewmodel.BaseEntryViewModel
import java.text.ParseException
import java.util.Date

abstract class BaseEntryFragment : BaseFormFragment(), EntryFragment {

    companion object {
        protected val TAG = SmartBirdsApplication.TAG + "." + javaClass.simpleName

        const val ARG_LAT = "lat"
        const val ARG_LON = "lon"
        const val ARG_GEOLOCATION_ACCURACY = "geolocationAccuracy"
        const val ARG_ENTRY_ID = "entryId"
        private const val STATE_STORED_ENTRY = "storedEntry"
    }

    protected var lat = 0.0
    protected var lon = 0.0
    protected var geolocationAccuracy = 0.0
    protected var entryId: Long = 0

    protected val eventBus: EEventBus by lazy { EEventBus.getInstance() }

    protected lateinit var menuCrash: MenuItem
    protected lateinit var menuSubmit: MenuItem

    protected var btnSubmit: View? = null

    /**
     * Available only when loaded from storage
     */
    protected var entryTimestamp: Date? = null

    protected var storedEntry: MonitoringEntry? = null

    protected abstract fun getEntryType(): EntryType?

    protected val viewModel: BaseEntryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (entryId > 0) {
            loadEntry()
        }
    }

    override fun onBeforeCreate(savedInstanceState: Bundle?) {
        super.onBeforeCreate(savedInstanceState)
        restoreInstanceState(savedInstanceState)
        checkArguments()
        setHasOptionsMenu(true)
    }

    override fun readArgs() {
        super.readArgs()
        arguments?.let {
            lat = it.getDouble(ARG_LAT, lat)
            lon = it.getDouble(ARG_LON, lon)
            geolocationAccuracy = it.getDouble(ARG_GEOLOCATION_ACCURACY, geolocationAccuracy)
            entryId = it.getLong(ARG_ENTRY_ID, entryId)
        }
    }

    private fun restoreInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState ?: return
        lat = savedInstanceState.getDouble(ARG_LAT, lat)
        lon = savedInstanceState.getDouble(ARG_LON, lon)
        geolocationAccuracy =
            savedInstanceState.getDouble(ARG_GEOLOCATION_ACCURACY, geolocationAccuracy)
        storedEntry = savedInstanceState.getParcelable(STATE_STORED_ENTRY)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putDouble(ARG_LAT, lat)
        outState.putDouble(ARG_LON, lon)
        outState.putDouble(ARG_GEOLOCATION_ACCURACY, geolocationAccuracy)
        outState.putParcelable(STATE_STORED_ENTRY, storedEntry)
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.debug_menu, menu)
        inflater.inflate(R.menu.form_entry, menu)
        menuCrash = menu.findItem(R.id.action_crash)
        menuSubmit = menu.findItem(R.id.action_submit)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.action_submit) {
            onMenuSubmitClicked(item)
            return true
        }
        if (itemId == R.id.action_crash) {
            crash()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadEntry() {
        if (viewModel.haveDeserialized) return
        lifecycleScope.launch {
            storedEntry = viewModel.loadEntry(entryId)
            if (storedEntry == null) {
                requireActivity().finish()
                Reporting.logException(IllegalStateException("Entry couldn't be loaded for id: $entryId"))
            }
            doDeserialize(storedEntry!!.monitoringCode, storedEntry!!.data)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.startService(DataService.intent(context))
    }

    override fun onStart() {
        super.onStart()
        try {
            eventBus.register(this)
        } catch (e: EventBusException) {
            // silently ignore it
        }
    }

    override fun onStop() {
        eventBus.unregister(this)
        super.onStop()
    }

    override fun doDeserialize(monitoringCode: String?, data: HashMap<String?, String?>?) {
        viewModel.haveDeserialized = true
        super.doDeserialize(monitoringCode, data)
    }

    override fun deserialize(data: HashMap<String?, String?>) {
        super.deserialize(data)
        val latVal = data[getString(R.string.tag_lat)]
        if (!TextUtils.isEmpty(latVal)) lat = latVal!!.toDouble()
        val lonVal = data[getString(R.string.tag_lon)]
        if (!TextUtils.isEmpty(lonVal)) lon = lonVal!!.toDouble()
        val accuracyVal = data[getString(R.string.tag_geolocation_accuracy)]
        if (!TextUtils.isEmpty(accuracyVal)) geolocationAccuracy = accuracyVal!!.toDouble()
        checkCoordinates()
        val dateVal = data[getString(R.string.entry_date)]
        val timeVal = data[getString(R.string.entry_time)]
        if (!TextUtils.isEmpty(dateVal) && !TextUtils.isEmpty(timeVal)) try {
            entryTimestamp = Configuration.parseDateTime(dateVal, timeVal)
        } catch (e: ParseException) {
            Reporting.logException(e)
        }
    }

    protected open fun checkArguments() {
        if (entryId == 0L) {
            checkCoordinates()
        }
    }


    override fun initViews() {
        super.initViews()
        btnSubmit = view?.findViewById(R.id.btn_submit)
        btnSubmit?.setOnClickListener { onSubmitClicked() }
        if (btnSubmit != null && readOnly) {
            btnSubmit!!.isEnabled = false
        }
    }

    protected open fun checkCoordinates() {
        if (lat == 0.0 || lon == 0.0) {
            Reporting.logException(IllegalStateException("Creating entry fragment with zero coordinates"))
        }
    }

    protected open fun serialize(entryTime: Date?): HashMap<String, String> {
        val data = super.serialize()
        data[getString(R.string.tag_lat)] = lat.toString()
        data[getString(R.string.tag_lon)] = lon.toString()
        data[getString(R.string.tag_geolocation_accuracy)] = geolocationAccuracy.toString()
        data[getString(R.string.entry_date)] = Configuration.STORAGE_DATE_FORMAT.format(entryTime)
        data[getString(R.string.entry_time)] = Configuration.STORAGE_TIME_FORMAT.format(entryTime)
        return data
    }

    protected open fun submitData(data: HashMap<String, String>?) {
        eventBus.post(EntrySubmitted(monitoringCode, entryId, data, getEntryType()))
    }

    protected open fun submitData() {
        submitData(serialize(if (entryTimestamp != null) entryTimestamp else Date()))
    }

    open fun onMenuSubmitClicked(item: MenuItem?) {
        onSubmitClicked()
    }

    open fun onSubmitClicked() {
        if (isValid) {
            menuSubmit.isEnabled = false
            if (btnSubmit != null) {
                btnSubmit!!.isEnabled = false
            }
            submitData()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menuCrash.apply {
            isVisible = BuildConfig.DEBUG
        }
    }

    open fun crash() {
        throw RuntimeException("Test Crash")
    }

    override fun isDirty(): Boolean {
        if (storedEntry == null) return false
        if (entryTimestamp == null) return false
        val data = serialize(entryTimestamp)
        return data != storedEntry!!.data
    }

    interface Builder {
        fun build(lat: Double, lon: Double, geolocationAccuracy: Double): Fragment?

        fun load(id: Long, readOnly: Boolean = false): Fragment?
    }

    override fun isNewEntry(): Boolean {
        return entryId == 0L
    }
}