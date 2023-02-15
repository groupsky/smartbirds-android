package org.bspb.smartbirds.pro.ui.fragment

import android.os.Bundle
import android.view.View
import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.FragmentById
import org.androidannotations.annotations.ViewById
import org.androidannotations.annotations.sharedpreferences.Pref
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.backend.dto.Nomenclature
import org.bspb.smartbirds.pro.prefs.BatsPrefs_
import org.bspb.smartbirds.pro.prefs.CommonPrefs_
import org.bspb.smartbirds.pro.tools.Reporting
import org.bspb.smartbirds.pro.tools.SBGsonParser
import org.bspb.smartbirds.pro.ui.views.FloatNumberFormInput
import org.bspb.smartbirds.pro.ui.views.NomenclatureItem
import org.bspb.smartbirds.pro.ui.views.SingleChoiceConfigFormInput
import org.bspb.smartbirds.pro.ui.views.SingleChoiceFormInput
import org.bspb.smartbirds.pro.ui.views.SwitchFormInput
import java.util.*

@EFragment(R.layout.fragment_monitoring_form_new_bats_entry)
open class NewBatsMainEntryFormFragment : BaseFormFragment() {

    @JvmField
    @FragmentById(value = R.id.pictures_fragment, childFragment = true)
    protected var picturesFragment: NewEntryPicturesFragment? = null


    @JvmField
    @ViewById(R.id.form_bats_confidential)
    protected var confidential: SwitchFormInput? = null

    @JvmField
    @Pref
    protected var commonPrefs: CommonPrefs_? = null

    @JvmField
    @Pref
    protected var batsPrefs: BatsPrefs_? = null

    @JvmField
    @ViewById(R.id.form_bats_metodology)
    protected var metodology: SingleChoiceFormInput? = null

    @JvmField
    @ViewById(R.id.form_bats_t_cave)
    protected var tempCave: FloatNumberFormInput? = null

    @JvmField
    @ViewById(R.id.form_bats_h_cave)
    protected var humidityCave: FloatNumberFormInput? = null

    @JvmField
    @ViewById(R.id.form_bats_typloc)
    protected var typeLoc: SingleChoiceFormInput? = null

    @JvmField
    @ViewById(R.id.form_bats_habitat)
    protected var habitat: SingleChoiceFormInput? = null

    override fun onResume() {
        super.onResume()
        if (isNewEntry) {
            confidential!!.isChecked = commonPrefs!!.confidentialRecord().get()
            batsPrefs!!.metodology()?.get()?.let { nomenclatureJson ->
                try {
                    val nomenclature = SBGsonParser.createParser()
                        .fromJson(nomenclatureJson, Nomenclature::class.java)
                    nomenclature?.let {
                        it.localeLabel = it.label.get(context?.getString(R.string.locale))
                        metodology?.setSelectionIfAvailable(NomenclatureItem(it))
                    }
                } catch (t: Throwable) {
                    Reporting.logException(t)
                }
            }
            batsPrefs!!.habitat()?.get()?.let { nomenclatureJson ->
                try {
                    val nomenclature = SBGsonParser.createParser()
                        .fromJson(nomenclatureJson, Nomenclature::class.java)
                    nomenclature?.let {
                        it.localeLabel = it.label.get(context?.getString(R.string.locale))
                        habitat?.setSelectionIfAvailable(NomenclatureItem(it))
                    }
                } catch (t: Throwable) {
                    Reporting.logException(t)
                }
            }
            batsPrefs!!.typeLoc()?.get()?.let { nomenclatureJson ->
                try {
                    val nomenclature = SBGsonParser.createParser()
                        .fromJson(nomenclatureJson, Nomenclature::class.java)
                    nomenclature?.let {
                        it.localeLabel = it.label.get(context?.getString(R.string.locale))
                        typeLoc?.setSelectionIfAvailable(NomenclatureItem(it))
                    }
                } catch (t: Throwable) {
                    Reporting.logException(t)
                }
            }
            batsPrefs!!.tempCave()?.get()?.let { temp ->
                tempCave?.setText(temp)
            }
            batsPrefs!!.humidityCave()?.get()?.let { temp ->
                humidityCave?.setText(temp)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        commonPrefs!!.confidentialRecord().put(confidential!!.isChecked)
        metodology?.selectedItem?.let {
            batsPrefs!!.metodology().put(SBGsonParser.createParser().toJson(it))
        }
        habitat?.selectedItem?.let {
            batsPrefs!!.habitat().put(SBGsonParser.createParser().toJson(it))
        }
        typeLoc?.selectedItem?.let {
            batsPrefs!!.typeLoc().put(SBGsonParser.createParser().toJson(it))
        }
        tempCave?.text?.let {
            batsPrefs!!.tempCave().put(it.toString())
        }
        humidityCave?.text?.let {
            batsPrefs!!.humidityCave().put(it.toString())
        }
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