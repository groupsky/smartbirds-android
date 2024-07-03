package org.bspb.smartbirds.pro.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.prefs.CommonPrefs
import org.bspb.smartbirds.pro.prefs.UserPrefs
import org.bspb.smartbirds.pro.service.DataService
import org.bspb.smartbirds.pro.ui.utils.FormUtils
import org.bspb.smartbirds.pro.ui.views.DateFormInput
import org.bspb.smartbirds.pro.ui.views.MultipleTextFormInput
import org.bspb.smartbirds.pro.ui.views.TimeFormInput
import java.util.Calendar

abstract class BaseCommonFormFragment : Fragment() {
    companion object {
        private const val TAG = SmartBirdsApplication.TAG + ".CommonForm"
    }

    protected var form: FormUtils.FormModel? = null
    protected lateinit var startDateView: DateFormInput
    protected lateinit var startTimeView: TimeFormInput
    protected lateinit var endDateView: DateFormInput
    protected lateinit var observers: MultipleTextFormInput
    protected lateinit var locationView: TextView
    protected lateinit var prefs: CommonPrefs
    private lateinit var userPrefs: UserPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        initPrefs()
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        loadSavedData()
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.action_submit) {
            save()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initViews() {
        startDateView = requireView().findViewById(R.id.form_common_start_date)
        startTimeView = requireView().findViewById(R.id.form_common_start_time)
        endDateView = requireView().findViewById(R.id.form_common_end_date)
        observers = requireView().findViewById(R.id.observers)
        locationView = requireView().findViewById(R.id.location)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.startService(DataService.intent(context))
    }

    override fun onResume() {
        super.onResume()
        observers.setText(prefs.getCommonOtherObservers())
    }

    override fun onPause() {
        super.onPause()
        prefs.setCommonOtherObservers(observers.text.toString())
    }

    private fun initPrefs() {
        prefs = CommonPrefs(requireContext())
        userPrefs = UserPrefs(requireContext())
    }

    private fun loadSavedData() {
        form = FormUtils.traverseForm(view)
        startDateView.value = Calendar.getInstance()
        startTimeView.setValue(Calendar.getInstance())
        endDateView.value = Calendar.getInstance()
    }

    fun save() {
        val data = form!!.serialize()
        data[getString(R.string.tag_user_id)] = userPrefs.getUserId()
        data[getString(R.string.tag_user_first_name)] = userPrefs.getFirstName()
        data[getString(R.string.tag_user_last_name)] = userPrefs.getLastName()
        data[getString(R.string.tag_user_email)] = userPrefs.getEmail()
        persistForm(data)
    }

    fun loadForm(data: HashMap<String, String>?) {
        if (!data.isNullOrEmpty()) {
            form!!.deserialize(data)
        }
    }

    fun validate(): Boolean {
        return form!!.validateFields()
    }

    abstract fun persistForm(data: HashMap<String, String>)
}