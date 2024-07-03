package org.bspb.smartbirds.pro.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.ui.views.SwitchFormInput

class ModeratorReviewFragment : Fragment() {

    var picturesFragment: NewEntryPicturesFragment? = null

    private lateinit var moderatorReview: SwitchFormInput
    private lateinit var warningModeratorReview: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState) ?: inflater.inflate(
            R.layout.fragment_moderator_review,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        moderatorReview = view.findViewById(R.id.form_moderator_review)
        warningModeratorReview = view.findViewById(R.id.warning_moderator_review)
    }

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
