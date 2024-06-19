package org.bspb.smartbirds.pro.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EditorAction;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.backend.Backend;
import org.bspb.smartbirds.pro.backend.LoginResultEvent;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.LoginStateEvent;
import org.bspb.smartbirds.pro.events.UserDataEvent;
import org.bspb.smartbirds.pro.prefs.UserPrefs_;
import org.bspb.smartbirds.pro.sync.AuthenticationManager;

/**
 * A login screen that offers login via email/password.
 */
@EActivity(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = SmartBirdsApplication.TAG + ".LoginActivity";

    // UI references.
    @ViewById(R.id.email)
    EditText mEmailView;
    @ViewById(R.id.password)
    EditText mPasswordView;
    @ViewById(R.id.login_progress)
    View mProgressView;
    @ViewById(R.id.login_form)
    View mLoginFormView;
    @ViewById(R.id.gdpr_panel)
    View mGdprPanel;
    @ViewById(R.id.gdpr_consent)
    CheckBox mGdprConsent;
    @ViewById(R.id.gdpr_info)
    TextView mGdprInfo;
    @ViewById(R.id.login_error)
    TextView mError;

    Backend backend = Backend.Companion.getInstance();

    EEventBus bus = EEventBus.getInstance();
    @Pref
    UserPrefs_ prefs;

    private boolean isLoginRunning;
    private boolean missingGdpr = false;

    @Override
    protected void onStart() {
        super.onStart();
        bus.registerSticky(this);
    }

    @Override
    protected void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        bus.removeStickyEvent(LoginResultEvent.class);
        super.onDestroy();
    }


    @AfterViews
    void initLoginForm() {
        if (prefs.username().exists()) {
            mEmailView.setText(prefs.username().get());
        }
    }


    @Click(R.id.register_button)
    protected void register() {
        String registerUrl = getString(R.string.register_url);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(registerUrl)));
    }

    @Click(R.id.gdpr_info)
    protected void showGdprInfo() {
        String gdprUrl = getString(R.string.gdpr_info_url);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(gdprUrl)));
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    @Click(R.id.email_sign_in_button)
    @EditorAction(R.id.password)
    protected void attemptLogin() {
        if (isLoginRunning) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mError.setText("");
        mError.setVisibility(View.GONE);
        bus.removeStickyEvent(LoginResultEvent.class);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        Boolean gdprConsent = null;
        if (missingGdpr) {
            gdprConsent = mGdprConsent.isChecked();
        }


        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for gdpr consent
        if (missingGdpr) {
            if (!gdprConsent) {
                mGdprConsent.setError(getString(R.string.error_missing_gdpr_consent));
                focusView = mGdprConsent;
                cancel = true;
            }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            AuthenticationManager authManager = new AuthenticationManager(this);
            authManager.login(email, password, gdprConsent);
        }
    }


    private boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    public void onEventMainThread(LoginStateEvent loginRunning) {
        Log.d(TAG, String.format("login running: %s", loginRunning));
        showProgress(isLoginRunning = loginRunning.isRunning());
    }

    public void onEventMainThread(LoginResultEvent loginResult) {
        Log.d(TAG, String.format("login result: %s", loginResult));
        mGdprPanel.setVisibility(View.GONE);
        mError.setVisibility(View.GONE);
        mError.setText("");
        missingGdpr = false;
        switch (loginResult.status) {
            case SUCCESS:
                bus.postSticky(new UserDataEvent(loginResult.user));
                MainActivity_.intent(this).start();
                finish();
                break;
            case CONNECTIVITY:
                mEmailView.setError("connectivity error");
                mEmailView.requestFocus();
                break;
            case PASSWORD_SHORT:
                mPasswordView.setError(getString(R.string.error_invalid_password));
                mPasswordView.requestFocus();
                break;
            case BAD_PASSWORD:
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
                break;
            case MISSING_GDPR:
                missingGdpr = true;
                mGdprPanel.setVisibility(View.VISIBLE);
                mError.setVisibility(View.VISIBLE);
                mError.setText(loginResult.message);
                break;
            case ERROR:
                if (loginResult.message != null) {
                    mError.setVisibility(View.VISIBLE);
                    mError.setText(loginResult.message);
                }
                break;
        }
    }
}

