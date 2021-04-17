package org.bspb.smartbirds.pro.ui.views

import android.content.Context
import android.util.AttributeSet
import org.bspb.smartbirds.pro.R

class PositiveDecimalNumberFormInput : TextFormInput {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.positiveDecimalNumberFormInputStyle)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}