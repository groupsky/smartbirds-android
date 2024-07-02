package org.bspb.smartbirds.pro.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.Reporting
import org.bspb.smartbirds.pro.ui.fragment.NewThreatsEntryRequiredFormFragment.OnPrimaryTypeChangedListener
import org.bspb.smartbirds.pro.ui.utils.FormsConfig

class NewThreatsEntryOptionalFormFragment : OnPrimaryTypeChangedListener, BaseFormFragment() {

    private var primaryType: String? = null

    companion object {
        fun newInstance(newEntry: Boolean, readOnly: Boolean): NewThreatsEntryOptionalFormFragment {
            return NewThreatsEntryOptionalFormFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_IS_NEW_ENTRY, newEntry)
                    putBoolean(ARG_READ_ONLY, readOnly)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState) ?: inflater.inflate(
            R.layout.fragment_monitoring_form_new_threats_optional_entry,
            container,
            false
        )
    }

    override fun onBeforeCreate(savedInstanceState: Bundle?) {
        super.onBeforeCreate(savedInstanceState)
        if (savedInstanceState != null) {
            primaryType = savedInstanceState.getString("primaryType")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("primaryType", primaryType)
    }

    override fun initViews() {
        super.initViews()
        handlePrimaryType()
    }

    override fun onPrimaryTypeChange(primaryType: String?) {
        this.primaryType = primaryType
        handlePrimaryType()
    }

    private fun handlePrimaryType() {
        view ?: return
        try {
            if (primaryType != null && FormsConfig.ThreatsPrimaryType.poison.isSame(primaryType)) {
                requireView().findViewById<View>(R.id.sample_container_1).visibility = View.VISIBLE
                requireView().findViewById<View>(R.id.sample_container_2).visibility = View.VISIBLE
                requireView().findViewById<View>(R.id.sample_container_3).visibility = View.VISIBLE
            } else {
                requireView().findViewById<View>(R.id.sample_container_1).visibility = View.GONE
                requireView().findViewById<View>(R.id.sample_container_2).visibility = View.GONE
                requireView().findViewById<View>(R.id.sample_container_3).visibility = View.GONE
            }
        } catch (t: Throwable) {
            Reporting.logException(t)
        }
    }
}