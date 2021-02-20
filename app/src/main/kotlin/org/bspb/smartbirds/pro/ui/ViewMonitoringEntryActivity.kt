package org.bspb.smartbirds.pro.ui

import android.os.Bundle
import android.os.PersistableBundle
import androidx.fragment.app.Fragment
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EActivity
import org.androidannotations.annotations.Extra
import org.androidannotations.annotations.FragmentById
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.enums.EntryType

@EActivity(R.layout.activity_form)
open class ViewMonitoringEntryActivity : BaseActivity() {

    @Extra
    @JvmField
    protected var entryId: Long = 0

    @Extra
    protected lateinit var entryType: EntryType

    @FragmentById(R.id.container)
    @JvmField
    protected var fragment: Fragment? = null

    @AfterViews
    protected open fun setupFragment() {
        if (entryId <= 0) {
            finish()
            return
        }
        if (fragment == null) {
            fragment = entryType.loadFragment(entryId, true)
            supportFragmentManager.beginTransaction().replace(R.id.container, fragment!!).commit()
        }
    }
}