package com.example.hostelallotementandpgfinder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hostelallotementandpgfinder.network.ApiClient
import com.example.hostelallotementandpgfinder.data.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignupClick: () -> Unit
) {
    var studentId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Title
        Text(
            text = "Student Portal",
            fontSize = 32.sp,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Hostel & PG Finder",
            fontSize = 20.sp,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Find your perfect accommodation",
            fontSize = 16.sp,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Student ID Field
        OutlinedTextField(
            value = studentId,
            onValueChange = { studentId = it },
            label = { Text("Student ID") },
            placeholder = { Text("Enter your student ID") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            placeholder = { Text("Enter your password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        // Error Message
        if (showError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Login Button
        Button(
            onClick = {
                if (studentId.isNotEmpty() && password.isNotEmpty()) {
                    isLoading = true
                    showError = false
                    
                    coroutineScope.launch {
                        try {
                            val loginRequest = LoginRequest(
                                student_id = studentId,
                                password = password
                            )
                            
                            val response = ApiClient.apiService.login(loginRequest)
                            
                            if (response.isSuccessful && response.body()?.success == true) {
                                // Store token if needed
                                onLoginSuccess()
                            } else {
                                showError = true
                                errorMessage = response.body()?.message ?: "Login failed"
                            }
                        } catch (e: Exception) {
                            showError = true
                            errorMessage = "Network error: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                } else {
                    showError = true
                    errorMessage = "Please fill in all fields"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF9C27B0), // Deep purple
                contentColor = Color.White
            ),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = "Login as Student",
                    fontSize = 18.sp
                )
            }
        }

        // Signup Link
        TextButton(
            onClick = onSignupClick,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(
                text = "Don't have an account? Sign Up",
                color = Color(0xFF9C27B0), // Deep purple to match login button
                fontSize = 16.sp
            )
        }

        // Demo credentials hint
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Demo Student Login:",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Student ID: 2024001",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Password: student123",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
