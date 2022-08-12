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
import org.bspb.smartbirds.pro.enums.EntryType
import org.bspb.smartbirds.pro.prefs.BirdsMigrationsPrefs_
import org.bspb.smartbirds.pro.prefs.CommonPrefs_
import org.bspb.smartbirds.pro.ui.views.NomenclatureItem
import org.bspb.smartbirds.pro.ui.views.QuickChoiceFormInput
import org.bspb.smartbirds.pro.ui.views.SingleChoiceFormInput
import org.bspb.smartbirds.pro.ui.views.SwitchFormInput
import java.util.*

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

    @JvmField
    @Pref
    protected var commonPrefs: CommonPrefs_? = null

    @JvmField
    @Pref
    protected var migrationPrefs: BirdsMigrationsPrefs_? = null

    @AfterViews
    open fun initQuickChoice() {
        speciesQuickChoice?.onItemSelected = { nomenclatureItem: NomenclatureItem? ->
            speciesInput?.setSelection(nomenclatureItem)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isNewEntry) {
            confidential!!.isChecked = commonPrefs!!.confidentialRecord().get()
            migrationPoint?.setText(migrationPrefs!!.migrationPoint().get())
        }
    }

    override fun onPause() {
        super.onPause()
        commonPrefs!!.confidentialRecord().put(confidential!!.isChecked)
        migrationPrefs!!.migrationPoint().put(migrationPoint?.text?.toString())
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
        super.onViewCreated(view, savedInstanceState)
    }

    class Builder : BaseEntryFragment.Builder {
        override fun build(lat: Double, lon: Double): Fragment? {
            return NewBirdsMigrationsEntryFormFragment_.builder().lat(lat).lon(lon).build()
        }

        override fun load(id: Long, readOnly: Boolean): Fragment? {
            return NewBirdsMigrationsEntryFormFragment_.builder().entryId(id).readOnly(readOnly)
                .build()
        }
    }
}