package org.bspb.smartbirds.pro.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.adapter.ReportAdapter
import org.bspb.smartbirds.pro.databinding.FragmentMonitoringReportBinding
import org.bspb.smartbirds.pro.utils.debugLog
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
    private lateinit var recyclerView: RecyclerView
    private var adapter = ReportAdapter()
    private lateinit var binding: FragmentMonitoringReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        monitoringCode = requireArguments().getString(ARG_MONITORING_CODE, "")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_monitoring_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        viewModel.init(monitoringCode)
        loadReport()
    }

    private fun initViews() {
        binding = FragmentMonitoringReportBinding.bind(requireView())
        adapter = ReportAdapter()
        val layoutManager = LinearLayoutManager(context)
        binding.emptyView.visibility = View.GONE
        binding.reportEntries.layoutManager = layoutManager
        binding.reportEntries.adapter = adapter
    }

    private fun loadReport() {
        viewModel.entries?.observe(viewLifecycleOwner) { entries ->
            adapter.reportEntries = viewModel.prepareReportEntries(requireContext(), entries)
        }
    }
}