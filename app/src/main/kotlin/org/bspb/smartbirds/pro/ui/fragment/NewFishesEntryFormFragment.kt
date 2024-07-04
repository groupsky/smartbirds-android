package org.bspb.smartbirds.pro.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.content.Monitoring
import org.bspb.smartbirds.pro.enums.EntryType
import org.bspb.smartbirds.pro.events.SubmitFishesCommonForm
import org.bspb.smartbirds.pro.utils.MonitoringManager
import org.bspb.smartbirds.pro.viewmodel.FishesFormViewModel

class NewFishesEntryFormFragment : BaseTabEntryFragment() {

    private val monitoringManager = MonitoringManager.getInstance()
    private val fishFormViewModel: FishesFormViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadFishCommonData()
    }

    override fun setupTabs() {
        setAdapter(object : FragmentStatePagerAdapter(parentFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> NewFishesEntryMainFormFragment.newInstance(isNewEntry, readOnly)
                    1 -> NewFishesEntryCommonFormFragment.newInstance(isNewEntry, readOnly)
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

    override fun submitData(data: HashMap<String, String>?) {
        if (viewPager != null && viewPager.adapter != null) {
            val adapter = viewPager.adapter!!
            for (i in 0 until adapter.count) {
                val fragment =
                    adapter.instantiateItem(viewPager, i) as? NewFishesEntryCommonFormFragment
                        ?: continue
                val fishCommonData = fragment.serializeCommonData()
                eventBus.post(SubmitFishesCommonForm(monitoringCode, fishCommonData))
            }
        }
        super.submitData(data)
    }

    override fun deserializeTabs(data: java.util.HashMap<String, String>?) {
        if (viewPager == null) return
        val adapter = viewPager.adapter ?: return
        for (i in 0 until adapter.count) {
            val fragment =
                adapter.instantiateItem(viewPager, i) as? NewFishesEntryMainFormFragment ?: continue
            fragment.doDeserialize(monitoringCode, data)
        }
    }

    private fun loadFishCommonData() {
        if (fishFormViewModel.haveDeserializedCommonForm) return
        lifecycleScope.launch {
            var monitoring: Monitoring? = null

            if (entryId > 0) {
                viewModel.loadEntry(entryId)?.let {
                    monitoring = monitoringManager.getMonitoring(it.monitoringCode)
                }
            } else {
                monitoring = monitoringManager.getActiveMonitoring()
            }

            monitoring?.let {
                if (viewPager != null && viewPager.adapter != null) {
                    val adapter = viewPager.adapter!!
                    for (i in 0 until adapter.count) {
                        val fragment =
                            adapter.instantiateItem(
                                viewPager,
                                i
                            ) as? NewFishesEntryCommonFormFragment
                                ?: continue
                        fragment.doDeserialize(it.code, it.commonForm)
                        fishFormViewModel.haveDeserializedCommonForm = true
                    }
                }
            }
        }
    }

    class Builder : BaseEntryFragment.Builder {
        override fun build(lat: Double, lon: Double, geolocationAccuracy: Double): Fragment? {
            return NewFishesEntryFormFragment().apply {
                arguments = Bundle().apply {
                    putDouble(ARG_LAT, lat)
                    putDouble(ARG_LON, lon)
                    putDouble(ARG_GEOLOCATION_ACCURACY, geolocationAccuracy)
                }
            }
        }

        override fun load(id: Long, readOnly: Boolean): Fragment? {
            return NewFishesEntryFormFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_ENTRY_ID, id)
                    putBoolean(ARG_READ_ONLY, readOnly)
                }
            }
        }
    }
}