package org.bspb.smartbirds.pro.ui

import android.os.Bundle
import android.view.MenuItem
import org.androidannotations.annotations.*
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.service.DataService_
import org.bspb.smartbirds.pro.ui.fragment.BrowseMonitoringCommonFormFragment
import org.bspb.smartbirds.pro.ui.fragment.BrowseMonitoringCommonFormFragment_

@EActivity(R.layout.activity_start_monitoring)
@OptionsMenu(R.menu.monitoring_edit_common_form)
open class BrowseMonitoringCommonFormActivity : BaseActivity() {
    companion object {
        private const val TAG = SmartBirdsApplication.TAG + ".EditMonitoring"
    }

    private var formFragmentBrowse: BrowseMonitoringCommonFormFragment? = null

    @Extra
    protected lateinit var monitoringCode: String

    @OptionsMenuItem(R.id.action_submit)
    open fun setMenuSubmit(menuSubmit: MenuItem) {
        menuSubmit.setTitle(R.string.menu_monitoring_save)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataService_.intent(this).start()
    }

    @AfterViews
    fun createFragment() {
        formFragmentBrowse = supportFragmentManager.findFragmentById(R.id.container) as BrowseMonitoringCommonFormFragment?
        if (formFragmentBrowse == null) {
            formFragmentBrowse = BrowseMonitoringCommonFormFragment_.builder().monitoringCode(monitoringCode).build()
            formFragmentBrowse?.let {
                supportFragmentManager.beginTransaction()
                        .add(R.id.container, it)
                        .commit()
            }
        }
        setTitle(R.string.title_activity_edit)
    }

    @OptionsItem(R.id.action_submit)
    fun save() {
        if (formFragmentBrowse!!.validate()) {
            formFragmentBrowse!!.save()
            setResult(RESULT_OK)
            finish()
        }
    }

    @OptionsItem(android.R.id.home)
    fun cancel() {
        setResult(RESULT_CANCELED)
        finish()
    }
}