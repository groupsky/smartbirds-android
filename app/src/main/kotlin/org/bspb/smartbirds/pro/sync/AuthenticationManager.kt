package org.bspb.smartbirds.pro.sync

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import org.androidannotations.annotations.Background
import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EBean
import org.androidannotations.annotations.RootContext
import org.androidannotations.annotations.sharedpreferences.Pref
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.backend.AuthenticationInterceptor
import org.bspb.smartbirds.pro.backend.Backend
import org.bspb.smartbirds.pro.backend.LoginResultEvent
import org.bspb.smartbirds.pro.backend.dto.CheckSessionRequest
import org.bspb.smartbirds.pro.backend.dto.LoginRequest
import org.bspb.smartbirds.pro.backend.dto.LoginResponse
import org.bspb.smartbirds.pro.events.*
import org.bspb.smartbirds.pro.prefs.SmartBirdsPrefs_
import org.bspb.smartbirds.pro.service.SyncService_
import org.bspb.smartbirds.pro.tools.Reporting
import org.bspb.smartbirds.pro.tools.SBGsonParser
import java.io.IOException


@EBean(scope = EBean.Scope.Default)
open class AuthenticationManager {

    companion object {
        private const val TAG = SmartBirdsApplication.TAG + ".AuthenticationSvc"
        var isDownloading = false
    }

    @Pref
    protected lateinit var prefs: SmartBirdsPrefs_

    @Bean
    protected lateinit var backend: Backend

    @Bean
    protected lateinit var authenticationInterceptor: AuthenticationInterceptor

    @Bean
    protected lateinit var bus: EEventBus

    @RootContext
    protected lateinit var context: Context

    @Background
    open fun logout() {
        authenticationInterceptor.clearAuthorization()
        bus.removeStickyEvent(LoginResultEvent::class.java)
        bus.postSticky(LogoutEvent())
    }

    @Background
    open fun login(email: String?, password: String?, gdprConsent: Boolean?) {
        Log.d(TAG, String.format("login: %s %s", email, password))
        bus.postSticky(LoginStateEvent(true))
        try {
            val result = doLogin(email, password, gdprConsent)
            Log.d(TAG, String.format("login: %s %s => %s", email, password, result))
            bus.postSticky(result)
            bus.postSticky(UserDataEvent(result.user))
        } finally {
            bus.postSticky(LoginStateEvent(false))
        }
    }

    private fun doLogin(email: String?, password: String?, gdprConsent: Boolean?): LoginResultEvent {
        val response = try {
            backend.api().login(LoginRequest(email, password, gdprConsent)).execute()
        } catch (e: IOException) {
            Reporting.logException(e)
            return LoginResultEvent(LoginResultEvent.Status.CONNECTIVITY)
        }

        Log.d(TAG, "Response = "+response)

        if (response == null) {
            return LoginResultEvent(LoginResultEvent.Status.ERROR)
        }

        if (!response.isSuccessful) {
            var errorResponse = ""
            try {
                errorResponse = response.errorBody()!!.string()
                Log.i(TAG, String.format("Login error %d: %s", response.code(), errorResponse))
            } catch (e: IOException) {
                Reporting.logException(e)
            }
            val loginResponse = SBGsonParser.createParser().fromJson(errorResponse, LoginResponse::class.java)
            var errorMessage: String? = null
            if (loginResponse != null) {
                errorMessage = loginResponse.error
            }
            return when (response.code()) {
                Backend.HTTP_STATUS_BAD_REQUEST -> LoginResultEvent(LoginResultEvent.Status.ERROR, errorMessage)
                Backend.HTTP_STATUS_UNAUTHORIZED -> {
                    if (loginResponse != null && loginResponse.token == null) {
                        if (LoginResponse.REQUIRE_GDPR == loginResponse.require) {
                            return LoginResultEvent(LoginResultEvent.Status.MISSING_GDPR, loginResponse.error)
                        }
                    }
                    LoginResultEvent(LoginResultEvent.Status.BAD_PASSWORD)
                }
                else -> LoginResultEvent(LoginResultEvent.Status.CONNECTIVITY)
            }
        }

        authenticationInterceptor.setAuthorization(response.body()!!.token, email, password)

        SyncService_.intent(context).initialSync().start()

        return LoginResultEvent(response.body()!!.user)
    }

    @WorkerThread
    fun checkSession() {
        try {
            isDownloading = true
            val response = backend.api().checkSession(CheckSessionRequest()).execute()
            if (response.isSuccessful) {
                bus.postSticky(UserDataEvent(response.body()?.user))
            }
        } catch (t: Throwable) {
            Reporting.logException(t)
        } finally {
            isDownloading = false
        }
    }
}