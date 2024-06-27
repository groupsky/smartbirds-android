package org.bspb.smartbirds.pro.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.FragmentById
import org.androidannotations.annotations.ViewById
import org.androidannotations.annotations.sharedpreferences.Pref
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.backend.dto.Nomenclature
import org.bspb.smartbirds.pro.enums.EntryType
import org.bspb.smartbirds.pro.prefs.BirdsMigrationsPrefs_
import org.bspb.smartbirds.pro.prefs.CommonPrefs
import org.bspb.smartbirds.pro.tools.Reporting
import org.bspb.smartbirds.pro.tools.SBGsonParser
import org.bspb.smartbirds.pro.ui.views.NomenclatureItem
import org.bspb.smartbirds.pro.ui.views.QuickChoiceFormInput
import org.bspb.smartbirds.pro.ui.views.SingleChoiceFormInput
import org.bspb.smartbirds.pro.ui.views.SwitchFormInput
import java.util.Date

@EFragment(R.layout.fragment_monitoring_form_new_birds_migrations_entry)
open class NewBirdsMigrationsEntryFormFragment : BaseEntryFragment() {

    @JvmField
    @FragmentById(value = R.id.pictures_fragment, childFragment = true)
    protected var picturesFragment: NewEntryPicturesFragment? = null


    @JvmField
    @ViewById(R.id.form_birds_migrations_confidential)
    protected var confidential: SwitchFormInput? = null

    @JvmField
    @ViewById(R.id.form_birds_migrations_species_quick)
    protected var speciesQuickChoice: QuickChoiceFormInput? = null

    @JvmField
    @ViewById(R.id.form_birds_migrations_species)
    protected var speciesInput: SingleChoiceFormInput? = null

    @JvmField
    @ViewById(R.id.form_birds_migrations_migration_point)
    protected var migrationPoint: SingleChoiceFormInput? = null

    protected lateinit var commonPrefs: CommonPrefs

    @Pref
    protected lateinit var migrationPrefs: BirdsMigrationsPrefs_

    @AfterViews
    open fun initQuickChoice() {
        speciesQuickChoice?.onItemSelected = { nomenclatureItem: NomenclatureItem? ->
            speciesInput?.setSelection(nomenclatureItem)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isNewEntry) {
            confidential!!.isChecked = commonPrefs.getConfidentialRecord()
            migrationPrefs.migrationPoint().get()?.let { nomenclatureJson ->
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
            migrationPrefs.migrationPoint().put(SBGsonParser.createParser().toJson(it))
        }
    }

    override fun getEntryType(): EntryType? {
        return EntryType.BIRDS_MIGRATIONS
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
        commonPrefs = CommonPrefs(requireContext())
        super.onViewCreated(view, savedInstanceState)
    }

    class Builder : BaseEntryFragment.Builder {
        override fun build(lat: Double, lon: Double, geolocationAccuracy: Double): Fragment? {
            return NewBirdsMigrationsEntryFormFragment_.builder().lat(lat).lon(lon)
                .geolocationAccuracy(geolocationAccuracy).build()
        }

        override fun load(id: Long, readOnly: Boolean): Fragment? {
            return NewBirdsMigrationsEntryFormFragment_.builder().entryId(id).readOnly(readOnly)
                .build()
        }
    }
}