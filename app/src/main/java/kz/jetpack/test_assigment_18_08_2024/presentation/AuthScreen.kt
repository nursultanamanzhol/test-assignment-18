package kz.jetpack.test_assigment_18_08_2024.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kz.jetpack.test_assigment_18_08_2024.R
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
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    var phoneNumber by remember { mutableStateOf("+7") }
    var smsCode by remember { mutableStateOf("") }
    var showCodeInput by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var img = if (!showCodeInput) {
        painterResource(id = R.drawable.verification_svgrepo_com)
    } else {
        painterResource(id = R.drawable.sms_svgrepo_com)
    }

    LazyColumn(
        modifier = Modifier
            .background(background)
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Image(
                painter = img,
                contentDescription = null,
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Verification",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (!showCodeInput) "We will send you One Time Code on your phone number"
                else "You will get an OTP via SMS",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))

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
                                jwtManager.accessToken = response.accessToken
                                jwtManager.refreshToken = response.refreshToken
                                if (response.isUserExists) {
                                    onLoginSuccess()
                                } else {
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
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { /* Реализуйте логику повторной отправки */ }) {
                    Text(text = "Resend again", color = MaterialTheme.colorScheme.primary)
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = errorMessage ?: "", color = errorColor)
            }
        }
    }
}