package org.bspb.smartbirds.pro.ui.fragment

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.loader.app.LoaderManager
import de.greenrobot.event.EventBusException
import kotlinx.coroutines.launch
import org.androidannotations.annotations.*
import org.bspb.smartbirds.pro.BuildConfig
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.content.MonitoringEntry
import org.bspb.smartbirds.pro.enums.EntryType
import org.bspb.smartbirds.pro.enums.EntryType.EntryFragment
import org.bspb.smartbirds.pro.events.EEventBus
import org.bspb.smartbirds.pro.events.EntrySubmitted
import org.bspb.smartbirds.pro.service.DataService_
import org.bspb.smartbirds.pro.tools.Reporting
import org.bspb.smartbirds.pro.ui.utils.Configuration
import org.bspb.smartbirds.pro.viewmodel.BaseEntryViewModel
import java.text.ParseException
import java.util.*

@EFragment
@OptionsMenu(R.menu.debug_menu, R.menu.form_entry)
abstract class BaseEntryFragment : BaseFormFragment(), EntryFragment {

    companion object {
        protected val TAG = SmartBirdsApplication.TAG + "." + javaClass.simpleName

        protected const val ARG_LAT = "lat"
        protected const val ARG_LON = "lon"
    }

    @JvmField
    @FragmentArg(ARG_LAT)
    @InstanceState
    protected var lat = 0.0

    @JvmField
    @FragmentArg(ARG_LON)
    @InstanceState
    protected var lon = 0.0

    @JvmField
    @FragmentArg
    protected var entryId: Long = 0

    @Bean
    protected lateinit var eventBus: EEventBus

    @OptionsMenuItem(R.id.action_crash)
    protected lateinit var menuCrash: MenuItem

    @OptionsMenuItem(R.id.action_submit)
    protected lateinit var menuSubmit: MenuItem

    @JvmField
    @ViewById(R.id.btn_submit)
    protected var btnSubmit: View? = null

    /**
     * Available only when loaded from storage
     */
    protected var entryTimestamp: Date? = null

    private var haveDeserialized = false

    @JvmField
    @InstanceState
    protected var storedEntry: MonitoringEntry? = null

    protected abstract fun getEntryType(): EntryType?

    private val viewModel: BaseEntryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if (entryId > 0) {
            loadEntry()
        }
    }

    private fun loadEntry() {
        if (haveDeserialized) return
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
        DataService_.intent(context).start()
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
        haveDeserialized = true
        LoaderManager.getInstance(requireActivity()).destroyLoader(this.hashCode())
        super.doDeserialize(monitoringCode, data)
    }

    override fun deserialize(data: HashMap<String?, String?>) {
        super.deserialize(data)
        val latVal = data[getString(R.string.tag_lat)]
        if (!TextUtils.isEmpty(latVal)) lat = latVal!!.toDouble()
        val lonVal = data[getString(R.string.tag_lon)]
        if (!TextUtils.isEmpty(lonVal)) lon = lonVal!!.toDouble()
        checkCoordinates()
        val dateVal = data[getString(R.string.entry_date)]
        val timeVal = data[getString(R.string.entry_time)]
        if (!TextUtils.isEmpty(dateVal) && !TextUtils.isEmpty(timeVal)) try {
            entryTimestamp = Configuration.parseDateTime(dateVal, timeVal)
        } catch (e: ParseException) {
            Reporting.logException(e)
        }
    }

    @AfterInject
    protected open fun checkArguments() {
        if (entryId == 0L) {
            checkCoordinates()
        }
    }

    @AfterViews
    open fun initViews() {
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

    @OptionsItem(R.id.action_submit)
    open fun onMenuSubmitClicked(item: MenuItem?) {
        onSubmitClicked()
    }

    @Click(R.id.btn_submit)
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
        menuCrash?.apply {
            isVisible = BuildConfig.DEBUG
        }
    }

    @OptionsItem(R.id.action_crash)
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
        fun build(lat: Double, lon: Double): Fragment?

        fun load(id: Long, readOnly: Boolean = false): Fragment?
    }

    override fun isNewEntry(): Boolean {
        return entryId == 0L
    }
}