package org.bspb.smartbirds.pro.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.backend.dto.Nomenclature
import org.bspb.smartbirds.pro.prefs.BatsPrefs
import org.bspb.smartbirds.pro.prefs.CommonPrefs
import org.bspb.smartbirds.pro.tools.Reporting
import org.bspb.smartbirds.pro.tools.SBGsonParser
import org.bspb.smartbirds.pro.ui.views.FloatNumberFormInput
import org.bspb.smartbirds.pro.ui.views.NomenclatureItem
import org.bspb.smartbirds.pro.ui.views.SingleChoiceFormInput
import org.bspb.smartbirds.pro.ui.views.SwitchFormInput

class NewBatsMainEntryFormFragment : BaseFormFragment() {

    companion object {
        fun newInstance(isNewEntry: Boolean, readOnly: Boolean): NewBatsMainEntryFormFragment {
            return NewBatsMainEntryFormFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_IS_NEW_ENTRY, isNewEntry)
                    putBoolean(ARG_READ_ONLY, readOnly)
                }
            }
        }
    }

    protected var picturesFragment: NewEntryPicturesFragment? = null


    private lateinit var commonPrefs: CommonPrefs
    private lateinit var batsPrefs: BatsPrefs

    private var confidential: SwitchFormInput? = null
    private var metodology: SingleChoiceFormInput? = null
    private var tempCave: FloatNumberFormInput? = null
    private var humidityCave: FloatNumberFormInput? = null
    private var typeLoc: SingleChoiceFormInput? = null
    private var habitat: SingleChoiceFormInput? = null

    override fun onBeforeCreate(savedInstanceState: Bundle?) {
        super.onBeforeCreate(savedInstanceState)
        commonPrefs = CommonPrefs(requireContext())
        batsPrefs = BatsPrefs(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState) ?: inflater.inflate(
            R.layout.fragment_monitoring_form_new_bats_entry,
            container,
            false
        )
    }

    override fun initViews() {
        super.initViews()

        confidential = view?.findViewById(R.id.form_bats_confidential)
        metodology = view?.findViewById(R.id.form_bats_metodology)
        tempCave = view?.findViewById(R.id.form_bats_t_cave)
        humidityCave = view?.findViewById(R.id.form_bats_h_cave)
        typeLoc = view?.findViewById(R.id.form_bats_typloc)
        habitat = view?.findViewById(R.id.form_bats_habitat)
    }

    override fun onResume() {
        super.onResume()
        if (isNewEntry) {
            confidential!!.isChecked = commonPrefs.getConfidentialRecord()
            batsPrefs.getMetodology()?.let { nomenclatureJson ->
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
            batsPrefs.getHabitat()?.let { nomenclatureJson ->
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
            batsPrefs.getTypeLoc()?.let { nomenclatureJson ->
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
            batsPrefs.getTempCave()?.let { temp ->
                tempCave?.setText(temp)
            }
            batsPrefs.getHumidityCave()?.let { temp ->
                humidityCave?.setText(temp)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        commonPrefs.setConfidentialRecord(confidential!!.isChecked)
        metodology?.selectedItem?.let {
            batsPrefs.setMetodology(SBGsonParser.createParser().toJson(it))
        }
        habitat?.selectedItem?.let {
            batsPrefs.setHabitat(SBGsonParser.createParser().toJson(it))
        }
        typeLoc?.selectedItem?.let {
            batsPrefs.setTypeLoc(SBGsonParser.createParser().toJson(it))
        }
        tempCave?.text?.let {
            batsPrefs.setTempCave(it.toString())
        }
        humidityCave?.text?.let {
            batsPrefs.setHumidityCave(it.toString())
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