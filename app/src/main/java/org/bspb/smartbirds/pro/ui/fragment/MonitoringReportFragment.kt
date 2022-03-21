package org.bspb.smartbirds.pro.ui.fragment

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment

class MonitoringReportFragment : Fragment() {

    companion object {
        private const val ARG_MONITORING_CODE = "monitoringCode"
        fun newInstance(monitoringCode: String): MonitoringReportFragment {
            val fragment = MonitoringReportFragment()
            fragment.arguments = Bundle()
            fragment.requireArguments().putString(ARG_MONITORING_CODE, monitoringCode)

            return fragment
        }
    }

    private lateinit var monitoringCode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        monitoringCode = requireArguments().getString(ARG_MONITORING_CODE, "")

        loadReport()
    }

    private fun loadReport() {
        Toast.makeText(requireContext(), "Loading report for $monitoringCode", Toast.LENGTH_LONG).show()
    }
}