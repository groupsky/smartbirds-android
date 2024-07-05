package org.bspb.smartbirds.pro.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.backend.dto.Nomenclature
import org.bspb.smartbirds.pro.enums.EntryType
import org.bspb.smartbirds.pro.prefs.BirdsMigrationsPrefs
import org.bspb.smartbirds.pro.prefs.CommonPrefs
import org.bspb.smartbirds.pro.tools.Reporting
import org.bspb.smartbirds.pro.tools.SBGsonParser
import org.bspb.smartbirds.pro.ui.views.NomenclatureItem
import org.bspb.smartbirds.pro.ui.views.QuickChoiceFormInput
import org.bspb.smartbirds.pro.ui.views.SingleChoiceFormInput
import org.bspb.smartbirds.pro.ui.views.SwitchFormInput
import java.util.Date

class NewBirdsMigrationsEntryFormFragment : BaseEntryFragment() {

    private var picturesFragment: NewEntryPicturesFragment? = null
    private var confidential: SwitchFormInput? = null
    private var speciesQuickChoice: QuickChoiceFormInput? = null
    private var speciesInput: SingleChoiceFormInput? = null
    private var migrationPoint: SingleChoiceFormInput? = null

    private lateinit var commonPrefs: CommonPrefs
    private lateinit var migrationPrefs: BirdsMigrationsPrefs

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState) ?: inflater.inflate(
            R.layout.fragment_monitoring_form_new_birds_migrations_entry,
            container,
            false
        )
    }

    override fun onBeforeCreate(savedInstanceState: Bundle?) {
        super.onBeforeCreate(savedInstanceState)
        commonPrefs = CommonPrefs(requireContext())
        migrationPrefs = BirdsMigrationsPrefs(requireContext())
    }


    override fun initViews() {
        super.initViews()
        confidential = view?.findViewById(R.id.form_birds_migrations_confidential)
        speciesQuickChoice = view?.findViewById(R.id.form_birds_migrations_species_quick)
        speciesInput = view?.findViewById(R.id.form_birds_migrations_species)
        migrationPoint = view?.findViewById(R.id.form_birds_migrations_migration_point)
        speciesQuickChoice?.onItemSelected = { nomenclatureItem: NomenclatureItem? ->
            speciesInput?.setSelection(nomenclatureItem)
        }
    }


    override fun onResume() {
        super.onResume()
        if (isNewEntry) {
            confidential!!.isChecked = commonPrefs.getConfidentialRecord()
            migrationPrefs.getMigrationPoint()?.let { nomenclatureJson ->
                try {
                    val nomenclature = SBGsonParser.createParser()
                        .fromJson(nomenclatureJson, Nomenclature::class.java)
                    nomenclature?.let {
                        it.localeLabel = it.label.get(context?.getString(R.string.locale))
                        migrationPoint?.setSelectionIfAvailable(NomenclatureItem(it))
                    }
                } catch (t: Throwable) {
                    Reporting.logException(t)
                }

            }
        }
    }

    override fun onPause() {
        super.onPause()
        commonPrefs.setConfidentialRecord(confidential!!.isChecked)
        migrationPoint?.selectedItem?.let {
            migrationPrefs.setMigrationPoint(SBGsonParser.createParser().toJson(it))
        }
    }

    override fun getEntryType(): EntryType? {
        return EntryType.BIRDS_MIGRATIONS
    }

    override fun serialize(): HashMap<String, String> {
        val data = super.serialize()
        data.putAll(picturesFragment!!.serialize())
        return data
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
        override fun build(lat: Double, lon: Double, geolocationAccuracy: Double): Fragment? {
            return NewBirdsMigrationsEntryFormFragment().apply {
                arguments = Bundle().apply {
                    putDouble(ARG_LAT, lat)
                    putDouble(ARG_LON, lon)
                    putDouble(ARG_GEOLOCATION_ACCURACY, geolocationAccuracy)
                }
            }
        }

        override fun load(id: Long, readOnly: Boolean): Fragment? {
            return NewBirdsMigrationsEntryFormFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_ENTRY_ID, id)
                    putBoolean(ARG_READ_ONLY, readOnly)
                }
            }
        }
    }
}