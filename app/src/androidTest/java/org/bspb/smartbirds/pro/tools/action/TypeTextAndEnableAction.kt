package org.bspb.smartbirds.pro.tools.action

import android.view.View
import android.widget.TextView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.TypeTextAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.supportsInputMethods
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import java.util.*

class TypeTextAndEnableAction(val text: String?) : ViewAction {

    private val originalTypeTextAction: TypeTextAction = TypeTextAction(text)

    override fun getConstraints(): Matcher<View> {
        var matchers = Matchers.allOf(ViewMatchers.isDisplayed())
        // SearchView does not support input methods itself (rather it delegates to an internal text
        // view for input).
        return Matchers.allOf(
            matchers, Matchers.anyOf(
                supportsInputMethods(), isAssignableFrom(
                    TextView::class.java
                )
            )
        )
    }

    override fun getDescription() = String.format(Locale.ROOT, "enable and type text(%s)", text)

    override fun perform(uiController: UiController, view: View) {
        if (!view.isEnabled) {
            view.isEnabled = true
        }
        originalTypeTextAction.perform(uiController, view)
    }
}