package org.bspb.smartbirds.pro.tools.action

import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.actionWithAssertions

object SBViewActions {
    fun typeTextAndEnable(stringToBeTyped: String?): ViewAction? {
        return actionWithAssertions(TypeTextAndEnableAction(stringToBeTyped))
    }
}