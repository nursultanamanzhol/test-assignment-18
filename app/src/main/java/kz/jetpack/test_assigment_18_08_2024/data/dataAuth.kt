package kz.jetpack.test_assigment_18_08_2024.data

data class dataAuth(
    val access_token: String,
    val is_user_exists: Boolean,
    val refresh_token: String,
    val user_id: Int
)