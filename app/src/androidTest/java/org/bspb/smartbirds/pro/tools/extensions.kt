package org.bspb.smartbirds.pro.tools

import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.Matchers.allOf

fun toolbarWithTitle(@StringRes title: Int): ViewInteraction =
    onView(allOf(withText(title), withParent(isAssignableFrom(Toolbar::class.java))))
