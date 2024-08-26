package kz.jetpack.test_assigment_18_08_2024.presentation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kz.jetpack.test_assigment_18_08_2024.auth.JwtManager
import kz.jetpack.test_assigment_18_08_2024.network.RetrofitInstance
import kz.jetpack.test_assigment_18_08_2024.ui.theme.background
import kz.jetpack.test_assigment_18_08_2024.ui.theme.errorColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    jwtManager: JwtManager,
    onLoginSuccess: () -> Unit,
    onRegistrationNeeded: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var phoneNumber by remember { mutableStateOf("+7") }
    var smsCode by remember { mutableStateOf("") }
    var showCodeInput by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .background(background)
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            if (!showCodeInput) {
                TextField(
                    value = phoneNumber,
                    onValueChange = {
                        if (it.startsWith("+7")) {
                            phoneNumber = it
                        }
                    },
                    label = { Text("Enter Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isLoading = true
                            try {
                                RetrofitInstance.api.sendAuthCode(mapOf("phone" to phoneNumber))
                                showCodeInput = true
                                errorMessage = null
                            } catch (e: Exception) {
                                errorMessage = "Failed to send code: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("GET OTP")
                }
            } else {
                TextField(
                    value = smsCode,
                    onValueChange = { smsCode = it },
                    label = { Text("Enter Code") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = VisualTransformation.None,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isLoading = true
                            try {
                                val requestBody = mapOf(
                                    "phone" to phoneNumber,
                                    "code" to smsCode
                                )
                                val response = RetrofitInstance.api.checkAuthCode(requestBody)
                                jwtManager.accessToken = response.access_token
                                jwtManager.refreshToken = response.refresh_token
                                Log.d("ServerResponse", response.toString()) // Добавьте лог для проверки всего ответа
                                Log.d("UserExists", response.is_user_exists.toString()) // Этот лог должен показывать 'true', если сервер возвращает 'true'

                                if (response.is_user_exists) {
                                    // Если пользователь существует, пропускаем регистрацию и переходим на экран профиля
                                    println("User exists, proceeding to profile.")
                                    onLoginSuccess()
                                } else {
                                    // Если пользователя нет, переходим на экран регистрации
                                    println("User does not exist, proceeding to registration.")
                                    onRegistrationNeeded(phoneNumber)
                                }
                                errorMessage = null
                            } catch (e: Exception) {
                                errorMessage = "Failed to verify code: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("VERIFY")
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = errorMessage ?: "", color = errorColor)
            }
        }
    }
}
