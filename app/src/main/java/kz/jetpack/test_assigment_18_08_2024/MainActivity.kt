package kz.jetpack.test_assigment_18_08_2024

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import kz.jetpack.test_assigment_18_08_2024.auth.JwtManager
import kz.jetpack.test_assigment_18_08_2024.data.UserProfile
import kz.jetpack.test_assigment_18_08_2024.network.RetrofitInstance
import kz.jetpack.test_assigment_18_08_2024.presentation.AuthScreen
import kz.jetpack.test_assigment_18_08_2024.presentation.EditProfileScreen
import kz.jetpack.test_assigment_18_08_2024.presentation.ProfileScreen
import kz.jetpack.test_assigment_18_08_2024.presentation.RegistrationScreen
import kz.jetpack.test_assigment_18_08_2024.ui.theme.TestAssigment18082024Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val jwtManager = JwtManager(this)

        setContent {
            TestAssigment18082024Theme {
                MainScreen(jwtManager)
            }
        }
    }
}

@Composable
fun MainScreen(jwtManager: JwtManager) {
    val currentScreen = remember { mutableStateOf("auth") }
    val phoneNumber = remember { mutableStateOf("") }
    val userProfile = remember { mutableStateOf<UserProfile?>(null) }
    val coroutineScope = rememberCoroutineScope()

    when (currentScreen.value) {
        "auth" -> AuthScreen(
            jwtManager = jwtManager,
            onLoginSuccess = {
                // Загружаем профиль пользователя после успешного входа
                coroutineScope.launch {
                    try {
                        // Проверка истечения токена
                        if (jwtManager.isTokenExpired()) {
                            val refreshed = jwtManager.refreshToken()
                            if (!refreshed) {
                                Log.e("MainScreen", "Failed to refresh token")
                                return@launch
                            }
                        }

                        val profile = RetrofitInstance.api.getUserProfile()
                        userProfile.value = profile
                        currentScreen.value =
                            "profile" // Переход на экран профиля после успешного входа
                    } catch (e: Exception) {
                        Log.e("MainScreen", "Failed to load profile", e)
                    }
                }
            },
            onRegistrationNeeded = { phone ->
                phoneNumber.value = phone
                currentScreen.value = "registration"
            }
        )

        "registration" -> RegistrationScreen(
            phoneNumber = phoneNumber.value,
            coroutineScope = coroutineScope,
            onRegisterSuccess = {
                // После успешной регистрации загружаем профиль пользователя
                coroutineScope.launch {
                    try {
                        val profile = RetrofitInstance.api.getUserProfile()
                        userProfile.value = profile
                        currentScreen.value =
                            "profile" // Переход на экран профиля после успешной регистрации
                    } catch (e: Exception) {
                        // Обработка ошибки загрузки профиля
                    }
                }
            },
            onRegisterFailure = { errorMessage ->
                // Вывести сообщение об ошибке
                println("Registration Error: $errorMessage")
            }
        )

        "profile" -> userProfile.value?.let { profile ->
            ProfileScreen(
                userProfile = profile,
                onEditClick = { currentScreen.value = "editProfile" }
            )
        }

        "editProfile" -> userProfile.value?.let { profile ->
            EditProfileScreen(
                userProfile = profile,
                onSaveClick = { updatedProfile ->
                    coroutineScope.launch {
                        try {
                            // Обновление данных на сервере
                            RetrofitInstance.api.updateUserProfile(updatedProfile)
                            // Обновление данных в приложении
                            userProfile.value = updatedProfile
                            currentScreen.value = "profile"
                        } catch (e: Exception) {
                            // Обработка ошибки
                        }
                    }
                }
            )
        }

        else -> {
            // Можно обработать переход на другие экраны или добавить экран с ошибкой
            Text(text = "Unknown screen")
        }
    }
}
