package org.bspb.smartbirds.pro.prefs

import android.content.Context

class UserPrefs(context: Context) {

    companion object {
        const val KEY_IS_AUTHENTICATED = "isAuthenticated"
        const val KEY_AUTH_TOKEN = "authToken"
        const val KEY_USERNAME = "username"
        const val KEY_PASSWORD = "password"
        const val KEY_USER_ID = "userId"
        const val KEY_FIRST_NAME = "firstName"
        const val KEY_LAST_NAME = "lastName"
        const val KEY_EMAIL = "email"
        const val KEY_BG_ATLAS_CELLS = "bgAtlasCells"
    }

    private val prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    fun clear() {
        prefs.edit().clear().apply()
    }

    fun isAuthenticated(): Boolean {
        return prefs.getBoolean(KEY_IS_AUTHENTICATED, false)
    }

    fun setAuthenticated(isAuthenticated: Boolean) {
        prefs.edit().putBoolean(KEY_IS_AUTHENTICATED, isAuthenticated).apply()
    }

    fun getAuthToken(): String {
        return prefs.getString(KEY_AUTH_TOKEN, "") ?: ""
    }

    fun setAuthToken(authToken: String?) {
        prefs.edit().putString(KEY_AUTH_TOKEN, authToken).apply()
    }

    fun getUsername(): String {
        return prefs.getString(KEY_USERNAME, "") ?: ""
    }

    fun setUsername(username: String?) {
        prefs.edit().putString(KEY_USERNAME, username).apply()
    }

    fun getPassword(): String {
        return prefs.getString(KEY_PASSWORD, "") ?: ""
    }

    fun setPassword(password: String?) {
        prefs.edit().putString(KEY_PASSWORD, password).apply()
    }

    fun getUserId(): String {
        return prefs.getString(KEY_USER_ID, "") ?: ""
    }

    fun setUserId(userId: String?) {
        prefs.edit().putString(KEY_USER_ID, userId).apply()
    }

    fun getFirstName(): String {
        return prefs.getString(KEY_FIRST_NAME, "") ?: ""
    }

    fun setFirstName(firstName: String?) {
        prefs.edit().putString(KEY_FIRST_NAME, firstName).apply()
    }

    fun getLastName(): String {
        return prefs.getString(KEY_LAST_NAME, "") ?: ""
    }

    fun setLastName(lastName: String?) {
        prefs.edit().putString(KEY_LAST_NAME, lastName).apply()
    }

    fun getEmail(): String {
        return prefs.getString(KEY_EMAIL, "") ?: ""
    }

    fun setEmail(email: String?) {
        prefs.edit().putString(KEY_EMAIL, email).apply()
    }

    fun getBgAtlasCells(): String {
        return prefs.getString(KEY_BG_ATLAS_CELLS, "") ?: ""
    }

    fun setBgAtlasCells(bgAtlasCells: String?) {
        prefs.edit().putString(KEY_BG_ATLAS_CELLS, bgAtlasCells).apply()
    }

}