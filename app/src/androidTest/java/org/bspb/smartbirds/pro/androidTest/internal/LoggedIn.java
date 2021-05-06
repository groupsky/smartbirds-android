package org.bspb.smartbirds.pro.androidTest.internal;

import androidx.test.core.app.ApplicationProvider;

import org.bspb.smartbirds.pro.prefs.UserPrefs_;
import org.junit.runners.model.Statement;

/**
 * Test helper to set logged in or logged out state - it is tightly coupled with how the app
 * handles the authentication so the tests are not
 */
public class LoggedIn extends Statement {
    private final boolean state;

    public LoggedIn(boolean state) {
        this.state = state;
    }

    @Override
    public void evaluate() throws Throwable {
        new UserPrefs_(ApplicationProvider.getApplicationContext())
                .edit()
                .isAuthenticated().put(state)
                .authToken().put(state ? "token" : null)
                .email().put(state ? "user@smartbirds.org" : null)
                .firstName().put(state ? "User" : null)
                .lastName().put(state ? "Smartbirds" : null)
                .password().put(state ? "password" : null)
                .username().put(state ? "user@smartbirds.org" : null)
                .userId().put(state ? "1" : null)
                .apply();

    }
}
