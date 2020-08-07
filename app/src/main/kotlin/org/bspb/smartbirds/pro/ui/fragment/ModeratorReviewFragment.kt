package org.bspb.smartbirds.pro.ui.fragment

import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.partial_monitoring_entry_list_row.*
import org.androidannotations.annotations.CheckedChange
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

        showModeratorReviewWarningIfNeeded()
        return res
    }

    open fun showModeratorReviewWarningIfNeeded() {
        if (picturesFragment != null && picturesFragment!!.picturesCount > 0) {
            warningModeratorReview.visibility = View.GONE
        } else {
            if (moderatorReview.isChecked) {
                warningModeratorReview.visibility = View.VISIBLE
            } else {
                warningModeratorReview.visibility = View.GONE
            }
        }
    }
}