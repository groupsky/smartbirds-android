package org.bspb.smartbirds.pro.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.enums.EntryType

class NewBatsEntryFormFragment : BaseTabEntryFragment() {

    override fun setupTabs() {
        setAdapter(object : FragmentStatePagerAdapter(parentFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> NewBatsMainEntryFormFragment.newInstance(isNewEntry, readOnly)
                    1 -> NewBatsOptionalEntryFormFragment.newInstance(isNewEntry, readOnly)
                    else -> throw IllegalArgumentException("Unhandled position$position")
                }
            }

            override fun getPageTitle(position: Int): CharSequence {
                return activity!!.getString(if (position == 0) R.string.tab_required else R.string.tab_optional)
            }

            override fun getCount(): Int {
                return 2
            }
        })
    }

    override fun getEntryType(): EntryType? {
        return EntryType.BATS
    }

    class Builder : BaseEntryFragment.Builder {
        override fun build(lat: Double, lon: Double, geolocationAccuracy: Double): Fragment? {
            return NewBatsEntryFormFragment().apply {
                arguments = Bundle().apply {
                    putDouble(ARG_LAT, lat)
                    putDouble(ARG_LON, lon)
                    putDouble(ARG_GEOLOCATION_ACCURACY, geolocationAccuracy)
                }
            }
        }

        override fun load(id: Long, readOnly: Boolean): Fragment? {
            return NewBatsEntryFormFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_ENTRY_ID, id)
                    putBoolean(ARG_READ_ONLY, readOnly)
                }
            }
        }
    }

}