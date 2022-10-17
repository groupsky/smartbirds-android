package org.bspb.smartbirds.pro.ui.fragment

import android.os.Bundle
import android.view.View
import org.androidannotations.annotations.EFragment
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.utils.debugLog

@EFragment(R.layout.fragment_monitoring_form_fishes_common_entry)
open class NewFishesEntryCommonFormFragment : BaseFormFragment() {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        debugLog("MonitoringCODE $monitoringCode")
    }
    override fun serialize(): HashMap<String, String> {
        return HashMap()
    }

    fun serializeCommonData(): HashMap<String, String> {
        ensureForm()
        return form.serialize()
    }

    override fun deserialize(data: java.util.HashMap<String, String>?) {
        super.deserialize(data)

    }
}