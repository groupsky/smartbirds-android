package org.bspb.smartbirds.pro.tools.action

import android.view.View
import android.widget.Checkable
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.actionWithAssertions
import org.hamcrest.BaseMatcher
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.isA
import org.hamcrest.Description

object SBViewActions {
    fun typeTextAndEnable(stringToBeTyped: String?): ViewAction? {
        return actionWithAssertions(TypeTextAndEnableAction(stringToBeTyped))
    }

    fun setChecked(checked: Boolean) = object : ViewAction {
        val checkableViewMatcher = object : BaseMatcher<View>() {
            override fun matches(item: Any?): Boolean = isA(Checkable::class.java).matches(item)
            override fun describeTo(description: Description?) {
                description?.appendText("is Checkable instance ")
            }
        }

        override fun getConstraints(): BaseMatcher<View> = checkableViewMatcher
        override fun getDescription(): String? = null
        override fun perform(uiController: UiController?, view: View) {
            val checkableView: Checkable = view as Checkable
            checkableView.isChecked = checked
        }
    }
}