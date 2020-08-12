package org.bspb.smartbirds.pro.prefs;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by groupsky on 05.10.16.
 */

/**
 * This prefs gets cleared every time the user logs out
 */
@SharedPref(SharedPref.Scope.UNIQUE)
public interface UserPrefs {

    @DefaultBoolean(false)
    boolean isAuthenticated();

    String authToken();

    String username();

    // TODO: use more secure store like credential store or the like
    String password();

    String userId();

    String firstName();

    String lastName();

    String email();

    String bgAtlasCells();
}
