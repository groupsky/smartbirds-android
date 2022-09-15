package org.bspb.smartbirds.pro.ui.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.enums.EntryType

@EFragment
open class NewFishEntryFormFragment : BaseTabEntryFragment() {
    @AfterViews
    protected fun setupTabs() {
        setAdapter(object : FragmentStatePagerAdapter(fragmentManager!!) {
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> NewFishEntryMainFormFragment_.builder().setNewEntry(isNewEntry).readOnly(readOnly)
                        .build()
                    1 -> NewFishEntryCommonFormFragment_.builder().setNewEntry(isNewEntry).readOnly(readOnly)
                        .build()
                    else -> throw IllegalArgumentException("Unhandled position$position")
                }
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return getString(if (position == 0) R.string.tab_required else R.string.tab_optional)
            }

            override fun getCount(): Int {
                return 2
            }
        })
    }

    override fun getEntryType(): EntryType? {
        return EntryType.FISH
    }

    class Builder : BaseEntryFragment.Builder {
        override fun build(lat: Double, lon: Double, geolocationAccuracy: Double): Fragment? {
            return NewFishEntryFormFragment_.builder().lat(lat).lon(lon).geolocationAccuracy(geolocationAccuracy)
                .build()
        }

        override fun load(id: Long, readOnly: Boolean): Fragment? {
            return NewFishEntryFormFragment_.builder().entryId(id).readOnly(readOnly).build()
        }
    }
}