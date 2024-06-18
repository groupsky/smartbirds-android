package org.bspb.smartbirds.pro.backend

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.bspb.smartbirds.pro.BuildConfig
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.backend.dto.LoginRequest
import org.bspb.smartbirds.pro.events.EEventBus
import org.bspb.smartbirds.pro.events.EEventBus_
import org.bspb.smartbirds.pro.events.UserDataEvent
import org.bspb.smartbirds.pro.prefs.UserPrefs_
import org.bspb.smartbirds.pro.sync.AuthenticationManager
import org.bspb.smartbirds.pro.sync.AuthenticationManager_
import org.bspb.smartbirds.pro.tools.Reporting
import java.io.IOException

class AuthenticationInterceptor private constructor() : Interceptor {
    companion object {
        const val TAG: String = SmartBirdsApplication.TAG + ".AuthIntrcptr"

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: AuthenticationInterceptor? = null

        fun getInstance(): AuthenticationInterceptor {
            checkNotNull(INSTANCE) { "AuthenticatorInterceptor instance is null. init() must be called before getting the instance." }
            return INSTANCE!!
        }

        fun init(context: Context) {
            if (INSTANCE != null) {
                return
            }

            synchronized(this) {
                INSTANCE = AuthenticationInterceptor()
                INSTANCE!!.context = context
                INSTANCE!!.prefs = UserPrefs_(context)
                INSTANCE!!.backend = Backend_.getInstance_(context)
                INSTANCE!!.bus = EEventBus_.getInstance_(context)
                INSTANCE!!.authenticationManager = AuthenticationManager_.getInstance_(context)
                INSTANCE
            }
        }
    }

    private lateinit var context: Context
    private lateinit var prefs: UserPrefs_
    private lateinit var backend: Backend
    private lateinit var bus: EEventBus
    private lateinit var authenticationManager: AuthenticationManager
    private var authorization: String? = null

    fun clearAuthorization() {
        authorization = null
        prefs.clear()
        prefs.isAuthenticated().put(false)
    }

    fun setAuthorization(authorization: String?, username: String?, password: String?) {
        this.authorization = authorization
        prefs.authToken().put(authorization)
        prefs.isAuthenticated().put(!TextUtils.isEmpty(authorization))
        prefs.username().put(username)
        prefs.password().put(password)
        Log.i(TAG, String.format("Authorization: %s", authorization))
    }

    @Synchronized
    fun tryRelogin(failedAuth: String?): Boolean {
        try {
            if (!TextUtils.equals(authorization, failedAuth)) return !TextUtils.isEmpty(
                authorization
            )
            val loginResponse =
                backend.api().login(LoginRequest(prefs.username().get(), prefs.password().get()))
                    .execute()
            if (loginResponse.isSuccessful) {
                authorization = loginResponse.body()!!.token
                prefs.authToken().put(authorization)
                bus.postSticky(UserDataEvent(loginResponse.body()!!.user))
                return true
            } else {
                authorization = null
                return false
            }
        } catch (t: Throwable) {
            Reporting.logException(t)
            return false
        }
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!BuildConfig.BACKEND_BASE_URL.contains(chain.request().url.host) || chain.request()
                .url.encodedPath.contains("session")
        ) {
            Log.d(TAG, "no auth required")
            return chain.proceed(chain.request())
        }
        var retries = 0

        var response: Response? = null

        while (true) {
            val auth = authorization!!
            if (TextUtils.isEmpty(auth)) {
                Log.d(TAG, "not authorized")
                return chain.proceed(chain.request())
            }
            val newRequest: Request =
                chain.request().newBuilder().addHeader("x-sb-csrf-token", auth).build()
            Log.d(TAG, String.format("Authorization: %s", auth))
            response?.close()
            response = chain.proceed(newRequest)
            if (!response.isSuccessful && response.code == 401) {
                Log.w(TAG, "Invalid authorization!")
                if (retries++ > 3 || !tryRelogin(auth)) {
                    clearAuthorization()
                    authenticationManager.logout()
                    return response
                }
            } else {
                return response
            }
        }
    }
}