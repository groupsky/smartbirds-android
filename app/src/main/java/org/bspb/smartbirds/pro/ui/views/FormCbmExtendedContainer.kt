package org.bspb.smartbirds.pro.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.androidannotations.annotations.EView
import org.androidannotations.annotations.sharedpreferences.Pref
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.prefs.CbmExtendedPrefs_


@EView
internal open class FormCbmExtendedContainer
constructor(ctx: Context, attrs: AttributeSet) : LinearLayout(ctx, attrs), SingleChoiceExtendedFormInput.OnShortcutSelectedListener {

    private var buttonSlotOne: Button
    private var buttonSlotTwo: Button
    private var isButtonOneAssigned: Boolean = false
    private var isButtonTwoAssigned: Boolean = false
    private val shortcutsModelWrite = ShortcutsPrefsDataModel()
    private var gson: Gson
    private lateinit var shortcutsModelRead: ShortcutsPrefsDataModel
    private var singleChoiceForm: SingleChoiceExtendedFormInput
    protected var prefs: CbmExtendedPrefs_

    data class ShortcutsPrefsDataModel(
            @SerializedName("shortcut_one") var shortcutOne: String = "",
            @SerializedName("shortcut_two") var shortcutTwo: String = "",
            @SerializedName("shortcut_three") var shortcutThree: String = "",
            @SerializedName("shortcut_four") var shortcutFour: String = ""
    )

    init{
        orientation = VERTICAL
        val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.form_cbm_extended_container_layout, this)

        buttonSlotOne = findViewById(R.id.shortcut_one)
        buttonSlotTwo = findViewById(R.id.shortcut_two)
        singleChoiceForm = findViewById(R.id.form_cbm_name)
        prefs = CbmExtendedPrefs_(context)
        gson = Gson()

        registerListeners()

        if(prefs.cbmExtendedZone().exists()){
            val json = prefs.cbmExtendedZone().get()
            shortcutsModelRead = gson.fromJson(json, ShortcutsPrefsDataModel::class.java)
            if(shortcutsModelRead.shortcutOne.isNotEmpty()) {
                buttonSlotOne.text = shortcutsModelRead.shortcutOne
                isButtonOneAssigned = true
            } else {
                buttonSlotOne.text = context.getText(R.string.monitoring_cbm_shortcuts_default_label)
            }
            if(shortcutsModelRead.shortcutTwo.isNotEmpty()){
                buttonSlotTwo.text = shortcutsModelRead.shortcutTwo
                isButtonTwoAssigned = true
            }
            else {
                buttonSlotTwo.text = context.getText(R.string.monitoring_cbm_shortcuts_default_label)
            }
        }
    }


    private fun registerListeners(){
        singleChoiceForm.setEventListener(this)

        buttonSlotOne.setOnClickListener {
            if(!isButtonOneAssigned){
                singleChoiceForm.showPopupDialog(SingleChoiceExtendedFormInput.ShortcutsEnum.SHORTCUT_ONE)
            } else {
                singleChoiceForm.selection = shortcutsModelRead.shortcutOne
            }
        }

        buttonSlotTwo.setOnClickListener {
            if(!isButtonTwoAssigned){
                singleChoiceForm.showPopupDialog(SingleChoiceExtendedFormInput.ShortcutsEnum.SHORTCUT_TWO)
            } else {
                singleChoiceForm.selection = shortcutsModelRead.shortcutTwo
            }
        }

        buttonSlotOne.setOnLongClickListener {
            singleChoiceForm.showPopupDialog(SingleChoiceExtendedFormInput.ShortcutsEnum.SHORTCUT_ONE)
            true
        }

        buttonSlotTwo.setOnLongClickListener {
            singleChoiceForm.showPopupDialog(SingleChoiceExtendedFormInput.ShortcutsEnum.SHORTCUT_TWO)
            true
        }
    }

    override fun onShortcutOneAssigned(mSelectedItem: SingleChoiceExtendedFormInput.NomenclatureItem) {
        buttonSlotOne.text = mSelectedItem.label
        shortcutsModelWrite.shortcutOne = mSelectedItem.label
        prepareShortcutsForSaving()
        isButtonOneAssigned = true
    }

    override fun onShortcutTwoAssigned(mSelectedItem: SingleChoiceExtendedFormInput.NomenclatureItem) {
        buttonSlotTwo.text = mSelectedItem.label
        shortcutsModelWrite.shortcutTwo = mSelectedItem.label
        prepareShortcutsForSaving()
        isButtonTwoAssigned = true
    }

    private fun prepareShortcutsForSaving(){
        val json = gson.toJson(shortcutsModelWrite)
        prefs.cbmExtendedZone().put(json.toString())
    }

}