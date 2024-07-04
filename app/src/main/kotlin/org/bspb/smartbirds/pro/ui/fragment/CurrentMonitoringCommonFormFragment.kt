package org.bspb.smartbirds.pro.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.events.EEventBus
import org.bspb.smartbirds.pro.events.GetMonitoringCommonData
import org.bspb.smartbirds.pro.events.MonitoringCommonData
import org.bspb.smartbirds.pro.events.SetMonitoringCommonData


class CurrentMonitoringCommonFormFragment : BaseCommonFormFragment() {

    companion object {
        private const val TAG = SmartBirdsApplication.TAG + ".CommonForm"

        private const val ARG_IS_FINISHING = "isFinishing"

        fun newInstance(isFinishing: Boolean): CurrentMonitoringCommonFormFragment {
            return CurrentMonitoringCommonFormFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_IS_FINISHING, isFinishing)
                }
            }
        }
    }

    private val bus: EEventBus by lazy { EEventBus.getInstance() }

    private var isFinishing: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        arguments?.let { args ->
            isFinishing = args.getBoolean(ARG_IS_FINISHING, false)
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState) ?: inflater.inflate(
            R.layout.fragment_monitoring_form_common,
            container,
            false
        )
    }

    override fun onStart() {
        super.onStart()
        bus.register(this)
        bus.postSticky(GetMonitoringCommonData())
    }

    override fun onStop() {
        bus.unregister(this)
        super.onStop()
    }


    override fun persistForm(data: HashMap<String, String>) {
        bus.post(SetMonitoringCommonData(data))
    }

    fun onEventMainThread(event: MonitoringCommonData) {
        loadForm(event.data)
    }
}