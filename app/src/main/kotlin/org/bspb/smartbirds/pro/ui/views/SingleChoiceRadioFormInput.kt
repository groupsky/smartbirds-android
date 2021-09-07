package org.bspb.smartbirds.pro.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.*
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.backend.dto.Nomenclature
import org.bspb.smartbirds.pro.ui.exception.ViewValidationException
import org.bspb.smartbirds.pro.ui.utils.Configuration
import org.bspb.smartbirds.pro.ui.utils.NomenclaturesBean
import org.bspb.smartbirds.pro.ui.utils.NomenclaturesBean_
import org.bspb.smartbirds.pro.ui.views.SingleChoiceFormInput.NomenclatureItem
import org.bspb.smartbirds.pro.utils.inflate

class SingleChoiceRadioFormInput : FrameLayout, SupportRequiredView, SupportStorage {


    private var mValues: List<Nomenclature>? = null
    private var mRequired = false
    private var mEntriesKey: CharSequence? = null
    private var mHint: CharSequence? = null
    private var mIsVertical = false


    private lateinit var hintTextView: TextView
    private lateinit var radioGroup: RadioGroup

    private var mSelectedItem: NomenclatureItem? = null

    private var nomenclatures: NomenclaturesBean = NomenclaturesBean_.getInstance_(context)

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        R.attr.singleChoiceRadioFormInputStyle
    )

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.SingleChoiceRadioFormInput,
            defStyleAttr,
            0
        )
        try {
            mHint = a.getText(R.styleable.SingleChoiceRadioFormInput_hint)
            mEntriesKey =
                a.getText(R.styleable.SingleChoiceRadioFormInput_entriesType)
            mRequired = a.getBoolean(R.styleable.SingleChoiceRadioFormInput_required, false)
            mIsVertical =
                a.getInteger(R.styleable.SingleChoiceRadioFormInput_orientation, 0) == 1
        } finally {
            a.recycle()
        }

        initView()
    }

    private fun initView() {
        inflate(R.layout.single_choice_radio, true)
        hintTextView = findViewById(R.id.single_choice_radio_hint)
        radioGroup = findViewById(R.id.single_choice_radio_group)

        if (mIsVertical) {
            radioGroup.orientation = LinearLayout.VERTICAL
        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != -1) {
                hintTextView.error = null
            }

            if (checkedId == -1) {
                mSelectedItem = null
            } else {
                val radioButton = radioGroup.findViewById<View>(checkedId)
                val idx = radioGroup.indexOfChild(radioButton)
                mValues?.also {
                    mSelectedItem = NomenclatureItem(it[idx])
                }
            }
        }

        mHint?.apply {
            if (isNotEmpty()) {
                hintTextView.text = mHint.toString() + ":"
            }
        }

        require(mEntriesKey != null && mEntriesKey!!.isNotEmpty()) { "Entries are required for this form input." }

        mValues = nomenclatures.getNomenclature(mEntriesKey.toString())

        mValues?.also {
            for (item in it) {
                val radioButton = RadioButton(context)
                radioButton.text = item.label.get(context.getString(R.string.locale))
                radioGroup.addView(radioButton)
            }
        }
    }

    override fun checkRequired() {
        if (mRequired && isEnabled) {
            if (radioGroup.checkedRadioButtonId == -1) {
                hintTextView.error = context.getString(R.string.required_field)
                throw ViewValidationException()
            } else {
                hintTextView.error = null
            }
        } else {
            hintTextView.error = null
        }
    }

    override fun serializeToStorage(storage: MutableMap<String, String>, fieldName: String) {
        storage[fieldName] =
            mSelectedItem?.label.toString().replace("\n", Configuration.MULTIPLE_CHOICE_DELIMITER)
        val locale = context.getString(R.string.locale)
        if (mSelectedItem != null) {
            if (mSelectedItem!!.nomenclature.label.hasValue(locale)) {
                storage["$fieldName.$locale"] = mSelectedItem!!.nomenclature.label[locale]
            }
            storage["$fieldName.en"] = mSelectedItem!!.nomenclature.label["en"]
        } else {
            storage["$fieldName.$locale"] = ""
            storage["$fieldName.en"] = ""
        }
    }

    override fun restoreFromStorage(storage: MutableMap<String, String>, fieldName: String) {
        val value = storage["$fieldName.${Configuration.NOMENCLATURE_ID_LANGUAGE}"]
        mValues?.forEachIndexed { index, nomenclature ->
            if (nomenclature.label[Configuration.NOMENCLATURE_ID_LANGUAGE] == value) {
                (radioGroup.getChildAt(index) as RadioButton).isChecked = true
                mSelectedItem = NomenclatureItem(nomenclature)
            }
        }
    }
}