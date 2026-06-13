package com.example

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberError
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSecondary
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceLight
import com.example.ui.theme.CyberTextOnPrimary
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary

@Composable
fun MfaLoginScreen(
    viewModel: MfaViewModel,
    modifier: Modifier = Modifier
) {
    val email by viewModel.loginEmail.collectAsState()
    val password by viewModel.loginPassword.collectAsState()
    val pin by viewModel.loginPin.collectAsState()
    val errorMsg by viewModel.loginError.collectAsState()
    val authState by viewModel.authState.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }
    var pinVisible by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBg)
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // High-tech lock header
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = "Shield Guard",
                tint = CyberPrimary,
                modifier = Modifier
                    .size(80.dp)
                    .drawBehind {
                        // Cyan circle glow under icon
                        drawCircle(
                            color = CyberPrimary.copy(alpha = 0.15f),
                            radius = size.minDimension / 1.1f,
                            center = center
                        )
                    }
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "SecureMFA",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Akademik taqdimot simulyatori v1.0",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = CyberPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Main login panel
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberPrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "TIZIMGA KIRISH",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CyberPrimary,
                            letterSpacing = 1.5.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Email Input
                    OutlinedTextField(
                        value = email,
                        onValueChange = { viewModel.loginEmail.value = it },
                        label = { Text("Elektron pochta (Email)") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = CyberPrimary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = CyberTextPrimary,
                            unfocusedTextColor = CyberTextPrimary,
                            focusedBorderColor = CyberPrimary,
                            unfocusedBorderColor = CyberTextSecondary.copy(alpha = 0.5f),
                            focusedLabelColor = CyberPrimary,
                            unfocusedLabelColor = CyberTextSecondary,
                            cursorColor = CyberPrimary
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("email_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { viewModel.loginPassword.value = it },
                        label = { Text("Parol") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = CyberPrimary) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Parolni ko'rsatish/yashirish",
                                    tint = CyberTextSecondary
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = CyberTextPrimary,
                            unfocusedTextColor = CyberTextPrimary,
                            focusedBorderColor = CyberPrimary,
                            unfocusedBorderColor = CyberTextSecondary.copy(alpha = 0.5f),
                            focusedLabelColor = CyberPrimary,
                            unfocusedLabelColor = CyberTextSecondary,
                            cursorColor = CyberPrimary
                        ),
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // PIN Code Input
                    OutlinedTextField(
                        value = pin,
                        onValueChange = { if (it.length <= 4) viewModel.loginPin.value = it },
                        label = { Text("Xavfsizlik PIN kodi (4-xonali)") },
                        leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = CyberPrimary) },
                        trailingIcon = {
                            IconButton(onClick = { pinVisible = !pinVisible }) {
                                Icon(
                                    imageVector = if (pinVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "PINni ko'rsatish/yashirish",
                                    tint = CyberTextSecondary
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = CyberTextPrimary,
                            unfocusedTextColor = CyberTextPrimary,
                            focusedBorderColor = CyberPrimary,
                            unfocusedBorderColor = CyberTextSecondary.copy(alpha = 0.5f),
                            focusedLabelColor = CyberPrimary,
                            unfocusedLabelColor = CyberTextSecondary,
                            cursorColor = CyberPrimary
                        ),
                        singleLine = true,
                        visualTransformation = if (pinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pin_input")
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Submit Button
                    Button(
                        onClick = { viewModel.processLogin() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberPrimary,
                            contentColor = CyberTextOnPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        enabled = authState != AuthState.LoginSimulating,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_button")
                    ) {
                        if (authState == AuthState.LoginSimulating) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    color = CyberTextOnPrimary,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.5.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Simulyatsiya yuklanmoqda...",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "MFA AUTENTIFIKATSIYA",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Autofill helper section for stress-free presentation
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberSecondary.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceLight),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = CyberSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Taqdimotchi uchun Avto-To'ldirish:",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = CyberSecondary,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Email: demo@mfa.uz\nParol: password123\nPIN: 1234",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyberTextSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = {
                            viewModel.loginEmail.value = "demo@mfa.uz"
                            viewModel.loginPassword.value = "password123"
                            viewModel.loginPin.value = "1234"
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = CyberSecondary),
                        modifier = Modifier
                            .align(Alignment.End)
                            .testTag("autofill_button")
                    ) {
                        Text(
                            text = "[ Avtomatik to'ldirish ]",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }
            }

            // Error alerting snackbar
            AnimatedVisibility(
                visible = errorMsg != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                errorMsg?.let { msg ->
                    Snackbar(
                        action = {
                            TextButton(onClick = { viewModel.clearLoginError() }) {
                                Text("OK", color = CyberPrimary)
                            }
                        },
                        containerColor = CyberSurfaceLight,
                        contentColor = CyberError,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .border(1.dp, CyberError.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    ) {
                        Text(msg, fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
