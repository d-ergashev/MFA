package com.example

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
fun MfaOtpScreen(
    viewModel: MfaViewModel,
    modifier: Modifier = Modifier
) {
    val otpCode by viewModel.otpInputCode.collectAsState()
    val notificationMsg by viewModel.notificationMessage.collectAsState()
    val errorMsg by viewModel.otpError.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBg)
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // Top App Bar with back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.logout() },
                    modifier = Modifier.testTag("otp_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Chiqish",
                        tint = CyberPrimary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                
                // Header Indicator Step
                Text(
                    text = "MFA BOSQICHI: 2 / 3",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = CyberSecondary,
                        letterSpacing = 1.5.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    modifier = Modifier
                        .border(1.dp, CyberSecondary, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Qadam 2: SMS Tasdiqlash",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = FontFamily.Monospace
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Telefon raqamingizga yuborilgan 6-xonali kodni kiriting",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = CyberTextSecondary,
                    fontFamily = FontFamily.Monospace
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // OTP Digits display
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                for (i in 0 until 6) {
                    val digit = if (i < otpCode.length) otpCode[i].toString() else ""
                    val isFocused = i == otpCode.length

                    Box(
                        modifier = Modifier
                            .size(width = 46.dp, height = 54.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = if (isFocused) 2.dp else 1.dp,
                                color = if (isFocused) CyberPrimary else CyberPrimary.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .background(CyberSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = digit,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (digit.isNotEmpty()) CyberSecondary else CyberTextSecondary,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Submitting code button
            Button(
                onClick = { viewModel.verifyOtp() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberPrimary,
                    contentColor = CyberTextOnPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = otpCode.length == 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("verify_otp_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "KODNI TASDIQLASH",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Custom On-Screen Cybersecurity Keypad to avoid using the soft keyboard on emulator
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberPrimary.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val keys = listOf(
                        listOf("1", "2", "3"),
                        listOf("4", "5", "6"),
                        listOf("7", "8", "9"),
                        listOf("Tozalash", "0", "O'chirish")
                    )

                    keys.forEach { rowKeys ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowKeys.forEach { key ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CyberSurfaceLight)
                                        .clickable {
                                            when (key) {
                                                "Tozalash" -> viewModel.onOtpCodeChange("")
                                                "O'chirish" -> {
                                                    if (otpCode.isNotEmpty()) {
                                                        viewModel.onOtpCodeChange(otpCode.dropLast(1))
                                                    }
                                                }
                                                else -> {
                                                    if (otpCode.length < 6) {
                                                        viewModel.onOtpCodeChange(otpCode + key)
                                                    }
                                                }
                                            }
                                        }
                                        .testTag("keypad_$key"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = key,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = if (key == "Tozalash" || key == "O'chirish") CyberPrimary else Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Simulated SMS incoming banner dialog trigger
            AnimatedVisibility(
                visible = errorMsg != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                errorMsg?.let { msg ->
                    Snackbar(
                        action = {
                            TextButton(onClick = { viewModel.clearOtpError() }) {
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

        // Spectacular absolute-positioned incoming SMS system mock popup
        AnimatedVisibility(
            visible = notificationMsg != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier.padding(16.dp)
        ) {
            notificationMsg?.let { msg ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, CyberSecondary, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = CyberSurfaceLight),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Message,
                                contentDescription = "SMS",
                                tint = CyberSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SISTEMA TIZIMI: YANGI SMS",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CyberSecondary,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "Hozir",
                                color = CyberTextSecondary,
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = msg,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    // Quick-inject OTP from message! Super presenter helper
                                    viewModel.onOtpCodeChange("554433")
                                    viewModel.clearNotification()
                                },
                                colors = ButtonDefaults.textButtonColors(contentColor = CyberPrimary)
                            ) {
                                Text(
                                    text = "[ Nusxa olish & To'ldirish ]",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(8.dp))

                            TextButton(
                                onClick = { viewModel.clearNotification() },
                                colors = ButtonDefaults.textButtonColors(contentColor = CyberTextSecondary)
                            ) {
                                Text("Yopish", fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
