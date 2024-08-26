package kz.jetpack.test_assigment_18_08_2024.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.auth0.android.jwt.JWT
import kz.jetpack.test_assigment_18_08_2024.network.RetrofitInstance

class JwtManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    var accessToken: String?
        get() = prefs.getString("access_token", null)
        set(value) {
            prefs.edit().putString("access_token", value).apply()
        }

    var refreshToken: String?
        get() = prefs.getString("refresh_token", null)
        set(value) {
            prefs.edit().putString("refresh_token", value).apply()
        }

    fun clearTokens() {
        prefs.edit().clear().apply()
    }

    fun isTokenExpired(): Boolean {
        val token = accessToken ?: return true
        val jwt = JWT(token)
        val isExpired = jwt.isExpired(10)
        Log.d("JwtManager", "Token expired: $isExpired")
        return isExpired
    }

    suspend fun refreshToken(): Boolean {
        Log.d("JwtManager", "Attempting to refresh token")
        val currentRefreshToken = refreshToken ?: return false
        return try {
            val response = RetrofitInstance.api.refreshToken(mapOf("refresh_token" to currentRefreshToken))
            accessToken = response.access_token
            refreshToken = response.refresh_token
            Log.d("JwtManager", "Token refreshed successfully")
            true
        } catch (e: Exception) {
            Log.e("JwtManager", "Failed to refresh token", e)
            clearTokens()
            false
        }
    }

}
