package org.bspb.smartbirds.pro.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.backend.dto.Nomenclature
import org.bspb.smartbirds.pro.ui.exception.ViewValidationException
import org.bspb.smartbirds.pro.ui.utils.Configuration
import org.bspb.smartbirds.pro.utils.NomenclaturesManager
import org.bspb.smartbirds.pro.utils.debugLog
import org.bspb.smartbirds.pro.utils.inflate

class ToggleGroupFormInput : FrameLayout, SupportRequiredView, SupportStorage {


    private var mValues: List<Nomenclature>? = null
    private var mRequired = false
    private var mEntriesKey: CharSequence? = null
    private var mHint: CharSequence? = null
    private var mIsVertical = false
    private var mIsSingleSelection = false


    private lateinit var hintTextView: TextView
    private lateinit var toggleGroup: MaterialButtonToggleGroup

    private var mSelectedItem: NomenclatureItem? = null

    private var nomenclatures = NomenclaturesManager.getInstance()

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        R.attr.toggleGroupFormInputStyle
    )

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.ToggleGroupFormInput,
            defStyleAttr,
            0
        )
        try {
            mHint = a.getText(R.styleable.ToggleGroupFormInput_hint)
            mEntriesKey =
                a.getText(R.styleable.ToggleGroupFormInput_entriesType)
            mRequired = a.getBoolean(R.styleable.ToggleGroupFormInput_required, false)
            mIsVertical =
                a.getInteger(R.styleable.ToggleGroupFormInput_orientation, 0) == 1
            mIsSingleSelection =
                a.getInteger(R.styleable.ToggleGroupFormInput_singleSelection, 0) == 1
        } finally {
            a.recycle()
        }

        initView()
    }

    private fun initView() {
        inflate(R.layout.single_choice_toggle, true)
        hintTextView = findViewById(R.id.single_choice_toggle_hint)
        toggleGroup = findViewById(R.id.single_choice_toggle_group)

        if (mIsVertical) {
            toggleGroup.orientation = LinearLayout.VERTICAL
        }

        toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (checkedId != -1) {
                hintTextView.error = null
            }

            if (checkedId == -1) {
                mSelectedItem = null
            } else {
                val toggleButton = toggleGroup.findViewById<View>(checkedId)
                val idx = toggleGroup.indexOfChild(toggleButton)
                mValues?.also {
                    mSelectedItem = NomenclatureItem(it[idx])
                }
            }
        }


        debugLog("hint: $mHint")
        mHint?.apply {
            if (isNotEmpty()) {
                hintTextView.text = mHint.toString() + ":"
            }
        }

        require(mEntriesKey != null && mEntriesKey!!.isNotEmpty()) { "Entries are required for this form input." }

        mValues = nomenclatures.getNomenclature(mEntriesKey.toString())

        mValues?.also {
            for (item in it) {
                var toggleButton: MaterialButton = inflate(R.layout.toggle_group_button, false) as MaterialButton
                toggleButton.text = item.label.get(context.getString(R.string.locale))
                var layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                toggleButton.layoutParams = layoutParams

                toggleGroup.addView(toggleButton)
            }
        }
    }

    override fun checkRequired() {
        if (mRequired && isEnabled) {
            if (toggleGroup.checkedButtonId == -1) {
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
            if (mSelectedItem!!.nomenclature?.label?.hasValue(locale) == true) {
                storage["$fieldName.$locale"] = mSelectedItem!!.nomenclature!!.label!!.get(locale)
            }
            storage["$fieldName.en"] = mSelectedItem!!.nomenclature?.label?.get("en") ?: ""
        } else {
            storage["$fieldName.$locale"] = ""
            storage["$fieldName.en"] = ""
        }
    }

    override fun restoreFromStorage(storage: MutableMap<String, String>, fieldName: String) {
        val value = storage["$fieldName.${Configuration.NOMENCLATURE_ID_LANGUAGE}"]
        mValues?.forEachIndexed { index, nomenclature ->
            if (nomenclature.label[Configuration.NOMENCLATURE_ID_LANGUAGE] == value) {
                (toggleGroup.getChildAt(index) as MaterialButton).isChecked = true
                mSelectedItem = NomenclatureItem(nomenclature)
            }
        }
    }
}