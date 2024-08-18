package kz.jetpack.test_assigment_18_08_2024.data

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val isUserExists: Boolean
)