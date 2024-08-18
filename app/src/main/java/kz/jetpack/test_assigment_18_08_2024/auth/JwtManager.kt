package kz.jetpack.test_assigment_18_08_2024.auth

import android.content.Context
import android.content.SharedPreferences
import com.auth0.android.jwt.JWT

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

    fun isTokenExpired(): Boolean {
        val token = accessToken ?: return true
        val jwt = JWT(token)
        return jwt.isExpired(10) // Проверка на истечение с учётом 10 секунд
    }

    fun clearTokens() {
        prefs.edit().clear().apply()
    }
}
