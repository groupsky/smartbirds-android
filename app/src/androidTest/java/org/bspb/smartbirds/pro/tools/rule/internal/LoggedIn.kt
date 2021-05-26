package org.bspb.smartbirds.pro.tools.rule.internal

import androidx.test.core.app.ApplicationProvider
import org.bspb.smartbirds.pro.prefs.UserPrefs_
import org.junit.runners.model.Statement


class LoggedIn(private val state: Boolean) : Statement() {
    override fun evaluate() {
        UserPrefs_(ApplicationProvider.getApplicationContext())
            .edit()
            .isAuthenticated.put(state)
            .authToken().put(if (state) "token" else null)
            .email().put(if (state) "user@smartbirds.org" else null)
            .firstName().put(if (state) "User" else null)
            .lastName().put(if (state) "Smartbirds" else null)
            .password().put(if (state) "password" else null)
            .username().put(if (state) "user@smartbirds.org" else null)
            .userId().put(if (state) "1" else null)
            .apply()

    }
}