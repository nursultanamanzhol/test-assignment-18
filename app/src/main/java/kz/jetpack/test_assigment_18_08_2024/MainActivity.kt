package kz.jetpack.test_assigment_18_08_2024

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kz.jetpack.test_assigment_18_08_2024.auth.JwtManager
import kz.jetpack.test_assigment_18_08_2024.presentation.AuthScreen
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
    val coroutineScope = rememberCoroutineScope()

    when (currentScreen.value) {
        "auth" -> AuthScreen(
            jwtManager = jwtManager,
            onLoginSuccess = {
                // Обработка успешного входа
                currentScreen.value = "home" // Здесь укажите экран, куда должно переходить после успешного входа
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
                // Обработка успешной регистрации
                currentScreen.value = "home" // Или другой экран, на который вы хотите перейти
            },
            onRegisterFailure = { errorMessage ->
                // Вывести сообщение об ошибке
                println("Registration Error: $errorMessage")
            }
        )
        // Добавьте другие экраны по мере необходимости
    }
}
