package org.bspb.smartbirds.pro.ui.views

import android.content.Context
import android.util.AttributeSet
import org.bspb.smartbirds.pro.R

class PositiveFloatNumberFormInput : TextFormInput {

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, R.attr.positiveFloatNumberFormInputStyle)

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)
}