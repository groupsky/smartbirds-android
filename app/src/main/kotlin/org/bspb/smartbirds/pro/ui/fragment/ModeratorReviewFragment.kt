package org.bspb.smartbirds.pro.ui.fragment

import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.ViewById
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.ui.views.SwitchFormInput

@EFragment(R.layout.fragment_moderator_review)
open class ModeratorReviewFragment : Fragment() {

    var picturesFragment: NewEntryPicturesFragment? = null

    @ViewById(R.id.form_moderator_review)
    protected lateinit var moderatorReview: SwitchFormInput

    @ViewById(R.id.warning_moderator_review)
    protected lateinit var warningModeratorReview: TextView

    fun isValid(): Boolean {
        var res = true
        picturesFragment?.let {
            if (moderatorReview.isChecked && it.picturesCount < 1) {
                moderatorReview.error = "No photos attaches"
                res = false
            }
        }

        if (res) {
            moderatorReview.error = null
        }

        if (res) {
            warningModeratorReview.visibility = View.GONE
        } else {
            warningModeratorReview.visibility = View.VISIBLE
        }

        return res
    }
}
