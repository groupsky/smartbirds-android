package org.bspb.smartbirds.pro.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.FragmentById
import org.androidannotations.annotations.ViewById
import org.androidannotations.annotations.sharedpreferences.Pref
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.enums.EntryType
import org.bspb.smartbirds.pro.prefs.CommonPrefs_
import org.bspb.smartbirds.pro.ui.views.SwitchFormInput
import java.util.*

@EFragment(R.layout.fragment_monitoring_form_new_pylons_casualties_entry)
open class NewPylonsCasualtiesEntryFormFragment : BaseEntryFragment() {

    @JvmField
    @FragmentById(value = R.id.pictures_fragment, childFragment = true)
    protected var picturesFragment: NewEntryPicturesFragment? = null


    @JvmField
    @ViewById(R.id.form_pylons_casualties_confidential)
    protected var confidential: SwitchFormInput? = null

    @JvmField
    @Pref
    protected var commonPrefs: CommonPrefs_? = null

    override fun onResume() {
        super.onResume()
        if (isNewEntry) {
            confidential!!.isChecked = commonPrefs!!.confidentialRecord().get()
        }
    }

    override fun onPause() {
        super.onPause()
        commonPrefs!!.confidentialRecord().put(confidential!!.isChecked)
    }

    override fun getEntryType(): EntryType? {
        return EntryType.PYLONS_CASUALTIES
    }

    override fun serialize(entryTime: Date?): HashMap<String, String> {
        val data = super.serialize(entryTime)
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

    class Builder : BaseEntryFragment.Builder {
        override fun build(lat: Double, lon: Double): Fragment? {
            return NewPylonsCasualtiesEntryFormFragment_.builder().lat(lat).lon(lon).build()
        }

        override fun load(id: Long, readOnly: Boolean): Fragment? {
            return NewPylonsCasualtiesEntryFormFragment_.builder().entryId(id).readOnly(readOnly).build()
        }
    }
}