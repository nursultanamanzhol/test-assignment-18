package kz.jetpack.test_assigment_18_08_2024.data

data class UserProfile(
    val id: Int,
    val name: String,
    val username: String,
    val phoneNumber: String,
    val avatarUri: String // Здесь хранится путь или URI аватарки
)
