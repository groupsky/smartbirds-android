package org.bspb.smartbirds.pro.ui.fragment

import android.content.Context
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.androidannotations.annotations.AfterInject
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.OptionsItem
import org.androidannotations.annotations.ViewById
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

@EFragment()
abstract class BaseCommonFormFragment : Fragment() {
    companion object {
        private const val TAG = SmartBirdsApplication.TAG + ".CommonForm"
    }

    protected var form: FormUtils.FormModel? = null

    @ViewById(R.id.form_common_start_date)
    protected lateinit var startDateView: DateFormInput

    @ViewById(R.id.form_common_start_time)
    protected lateinit var startTimeView: TimeFormInput

    @ViewById(R.id.form_common_end_date)
    protected lateinit var endDateView: DateFormInput

    protected lateinit var prefs: CommonPrefs

    protected lateinit var userPrefs: UserPrefs

    @ViewById(R.id.observers)
    protected lateinit var observers: MultipleTextFormInput

    @ViewById(R.id.location)
    protected lateinit var locationView: TextView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DataService.intent(context).start()
    }

    override fun onResume() {
        super.onResume()
        observers.setText(prefs.getCommonOtherObservers())
    }

    override fun onPause() {
        super.onPause()
        prefs.setCommonOtherObservers(observers.text.toString())
    }

    @AfterInject
    open fun initPrefs() {
        prefs = CommonPrefs(requireContext())
        userPrefs = UserPrefs(requireContext())
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