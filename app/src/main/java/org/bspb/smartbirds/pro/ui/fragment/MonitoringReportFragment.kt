package org.bspb.smartbirds.pro.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.viewmodel.MonitoringReportViewModel

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

    private val viewModel: MonitoringReportViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        monitoringCode = requireArguments().getString(ARG_MONITORING_CODE, "")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_monitoring_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.init(monitoringCode)
        loadReport()
    }

    private fun loadReport() {
        viewModel.entries?.observe(viewLifecycleOwner) { entries ->
            Toast.makeText(
                requireContext(),
                "Loading report for $monitoringCode with ${entries.size} entries",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}