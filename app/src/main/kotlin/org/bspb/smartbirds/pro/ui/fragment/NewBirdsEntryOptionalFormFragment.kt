package org.bspb.smartbirds.pro.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.bspb.smartbirds.pro.R

class NewBirdsEntryOptionalFormFragment : BaseFormFragment() {

    companion object {
        fun newInstance(isNewEntry: Boolean, readOnly: Boolean): NewBirdsEntryOptionalFormFragment {
            val fragment = NewBirdsEntryOptionalFormFragment()
            val args = Bundle()
            args.putBoolean(ARG_IS_NEW_ENTRY, isNewEntry)
            args.putBoolean(ARG_READ_ONLY, readOnly)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState) ?: inflater.inflate(
            R.layout.fragment_monitoring_form_new_birds_optional_entry,
            container,
            false
        )
    }
}