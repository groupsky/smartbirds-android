package org.bspb.smartbirds.pro.tools.rule.internal

import androidx.test.core.app.ApplicationProvider
import org.bspb.smartbirds.pro.prefs.UserPrefs
import org.junit.runners.model.Statement


class LoggedIn(private val state: Boolean) : Statement() {
    override fun evaluate() {
        with(UserPrefs(ApplicationProvider.getApplicationContext())) {
            setAuthenticated(state)
            setAuthToken(if (state) "token" else null)
            setEmail(if (state) "user@smartbirds.org" else null)
            setFirstName(if (state) "User" else null)
            setLastName(if (state) "Smartbirds" else null)
            setPassword(if (state) "password" else null)
            setUsername(if (state) "user@smartbirds.org" else null)
            setUserId(if (state) "1" else null)
        }
    }
}