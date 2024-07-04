package org.bspb.smartbirds.pro.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.prefs.CommonPrefs
import org.bspb.smartbirds.pro.ui.views.SwitchFormInput

class NewFishesEntryMainFormFragment : BaseFormFragment() {

    private var picturesFragment: NewEntryPicturesFragment? = null
    private var confidential: SwitchFormInput? = null
    private lateinit var commonPrefs: CommonPrefs

    companion object {
        fun newInstance(isNewEntry: Boolean, readOnly: Boolean): NewFishesEntryMainFormFragment {
            return NewFishesEntryMainFormFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_IS_NEW_ENTRY, isNewEntry)
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
            R.layout.fragment_monitoring_form_new_fishes_entry,
            container,
            false
        )
    }

    override fun onBeforeCreate(savedInstanceState: Bundle?) {
        super.onBeforeCreate(savedInstanceState)
        commonPrefs = CommonPrefs(requireContext())
    }

    override fun initViews() {
        super.initViews()
        confidential = view?.findViewById(R.id.form_fish_confidential)
    }

    override fun onResume() {
        super.onResume()
        if (isNewEntry) {
            confidential!!.isChecked = commonPrefs.getConfidentialRecord()
        }
    }

    override fun onPause() {
        super.onPause()
        commonPrefs.setConfidentialRecord(confidential!!.isChecked)
    }


    override fun serialize(): HashMap<String, String> {
        val data = super.serialize()
        data.putAll(picturesFragment!!.serialize())
        return data
    }

    override fun deserialize(data: HashMap<String?, String?>) {
        super.deserialize(data)
        // In some cases picturesFragment is still null. Try to find it by id
        if (picturesFragment == null) {
            picturesFragment =
                childFragmentManager.findFragmentById(R.id.pictures_fragment) as NewEntryPicturesFragment?
        }
        if (picturesFragment != null) {
            picturesFragment!!.doDeserialize(monitoringCode, data)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (picturesFragment == null) {
            picturesFragment =
                childFragmentManager.findFragmentById(R.id.pictures_fragment) as NewEntryPicturesFragment?
        }
        super.onViewCreated(view, savedInstanceState)
    }
}