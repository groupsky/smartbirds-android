package org.bspb.smartbirds.pro.ui.fragment

import android.os.Bundle
import android.view.View
import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.FragmentById
import org.androidannotations.annotations.ViewById
import org.androidannotations.annotations.sharedpreferences.Pref
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.prefs.CommonPrefs_
import org.bspb.smartbirds.pro.ui.views.SwitchFormInput
import java.util.*

@EFragment(R.layout.fragment_monitoring_form_new_fishes_entry)
open class NewFishesEntryMainFormFragment : BaseFormFragment() {

    @JvmField
    @FragmentById(value = R.id.pictures_fragment, childFragment = true)
    protected var picturesFragment: NewEntryPicturesFragment? = null


    @JvmField
    @ViewById(R.id.form_fish_confidential)
    protected var confidential: SwitchFormInput? = null

    @Pref
    protected lateinit var commonPrefs: CommonPrefs_

    override fun onResume() {
        super.onResume()
        if (isNewEntry) {
            confidential!!.isChecked = commonPrefs.confidentialRecord().get()
        }
    }

    override fun onPause() {
        super.onPause()
        commonPrefs.confidentialRecord().put(confidential!!.isChecked)
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