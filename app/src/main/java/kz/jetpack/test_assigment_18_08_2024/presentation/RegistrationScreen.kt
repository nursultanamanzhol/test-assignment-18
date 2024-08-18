package kz.jetpack.test_assigment_18_08_2024.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kz.jetpack.test_assigment_18_08_2024.data.RegistrationData
import kz.jetpack.test_assigment_18_08_2024.network.RetrofitInstance
import kz.jetpack.test_assigment_18_08_2024.ui.theme.background
import kz.jetpack.test_assigment_18_08_2024.ui.theme.errorColor
import androidx.compose.foundation.lazy.LazyColumn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    phoneNumber: String,
    coroutineScope: CoroutineScope,
    onRegisterSuccess: () -> Unit,
    onRegisterFailure: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var usernameError by remember { mutableStateOf<String?>(null) }

    fun validateUsername(input: String): Boolean {
        val regex = Regex("^[A-Za-z0-9_-]{5,}$")
        return regex.matches(input)
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
            TextField(
                value = phoneNumber,
                onValueChange = {},
                label = { Text("Phone Number") },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = username,
                onValueChange = {
                    username = it
                    usernameError = if (!validateUsername(it)) {
                        "Username must contain at least 5 characters, including letters, numbers, - or _."
                    } else {
                        null
                    }
                },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                isError = usernameError != null
            )
            if (usernameError != null) {
                Text(
                    text = usernameError ?: "",
                    color = errorColor,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (usernameError == null) {
                        coroutineScope.launch {
                            isLoading = true
                            try {
                                val registrationData = RegistrationData(phoneNumber, name, username)
                                val response = RetrofitInstance.api.registerUser(registrationData)
                                onRegisterSuccess()
                            } catch (e: Exception) {
                                onRegisterFailure("Registration failed: ${e.message}")
                                errorMessage = e.message
                            } finally {
                                isLoading = false
                            }
                        }
                    } else {
                        onRegisterFailure("Username is invalid")
                    }
                },
                enabled = !isLoading
            ) {
                Text("Register")
            }
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = errorMessage ?: "", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
