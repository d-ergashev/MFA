package com.example

import android.content.Context
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSecondary
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceLight
import com.example.ui.theme.CyberTextOnPrimary
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.LaserGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MfaBiometricScreen(
    viewModel: MfaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isScanningLocally by remember { mutableStateOf(false) }
    var systemPromptAvailable by remember { mutableStateOf(false) }

    // Check biometric compatibility
    remember {
        val biometricManager = BiometricManager.from(context)
        systemPromptAvailable = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        ) == BiometricManager.BIOMETRIC_SUCCESS
    }

    // Circular scanning pulse configuration
    val infiniteTransition = rememberInfiniteTransition(label = "Radar Sweep")
    val scaleFactor by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse Scaling"
    )

    val scanLineFraction by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Scan Line vertical movement"
    )

    // Helper to extract FragmentActivity context for native biometric prompt
    fun getFragmentActivity(context: Context): FragmentActivity? {
        var currentContext = context
        while (currentContext is android.content.ContextWrapper) {
            if (currentContext is FragmentActivity) {
                return currentContext
            }
            currentContext = currentContext.baseContext
        }
        return null
    }

    // Trigger System Standard Biometric Dialog
    fun triggerSystemBiometrics() {
        val activity = getFragmentActivity(context)
        if (activity == null) {
            Toast.makeText(context, "Sizning qurilmangizda activity qo'llab-quvvatlanmadi", Toast.LENGTH_SHORT).show()
            return
        }

        val executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(context, "Biometrik xato: $errString", Toast.LENGTH_SHORT).show()
                    viewModel.authenticateBiometrics(false)
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(context, "Tizim biometrik xizmati tastiqlandi!", Toast.LENGTH_SHORT).show()
                    viewModel.authenticateBiometrics(true)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(context, "Biometrik tasdiqlash muvaffaqiyatsiz bo'ldi", Toast.LENGTH_SHORT).show()
                    viewModel.authenticateBiometrics(false)
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("SecureMFA - Haqiqiy Biometriya")
            .setSubtitle("Universitet taqdimoti uchun haqiqiy tizim testi")
            .setDescription("Skanerga barmoq izi orqali hisobingizni tasdiqlang.")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()

        try {
            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            Toast.makeText(context, "Prompt ishga tushmadi: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

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
            
            // Header Indicator Step
            Text(
                text = "MFA BOSQICHI: 1 / 3",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = CyberSecondary,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace
                ),
                modifier = Modifier
                    .border(1.dp, CyberSecondary, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Qadam 1: Biometrik Identifikatsiya",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = FontFamily.Monospace
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Taqdimotda foydalanish uchun barmoq izi skanerlash bosqichi",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = CyberTextSecondary,
                    fontFamily = FontFamily.Monospace
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )

            // Scanning visualizer
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .border(2.dp, CyberPrimary.copy(alpha = 0.4f), CircleShape)
                    .background(CyberSurface),
                contentAlignment = Alignment.Center
            ) {
                // Background infinite pulse glow
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(scaleFactor)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    CyberPrimary.copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Laser scan bar simulation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 20.dp)
                        .graphicsLayerAndOffset(scanLineFraction, 200)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    LaserGreen,
                                    LaserGreen,
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Fingerprint core logo
                IconButton(
                    onClick = {
                        if (systemPromptAvailable) {
                            triggerSystemBiometrics()
                        } else {
                            // Run visual simulation directly
                            coroutineScope.launch {
                                isScanningLocally = true
                                delay(1800)
                                isScanningLocally = false
                                viewModel.authenticateBiometrics(true)
                            }
                        }
                    },
                    modifier = Modifier
                        .size(120.dp)
                        .testTag("biometric_visual_button")
                ) {
                    if (isScanningLocally) {
                        CircularProgressIndicator(
                            color = LaserGreen,
                            modifier = Modifier.size(80.dp),
                            strokeWidth = 3.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Scan target",
                            tint = if (systemPromptAvailable) CyberPrimary else LaserGreen,
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isScanningLocally) "Hologramma skanerlanmoqda..." else "Skaynerga bosing",
                fontFamily = FontFamily.Monospace,
                color = if (isScanningLocally) CyberSecondary else CyberTextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Option 1: Visual Simulator Button (Safest, works instantly in any emulator)
            Button(
                onClick = {
                    coroutineScope.launch {
                        isScanningLocally = true
                        delay(2000)
                        isScanningLocally = false
                        viewModel.authenticateBiometrics(true)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberSecondary,
                    contentColor = CyberTextOnPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = !isScanningLocally,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("simulate_biometric_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.LockOpen, contentDescription = null)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "BARMOQ IZINI TASTIQLASH (SIMULYATSIYA)",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Option 2: Active hardware standard dialog
            Button(
                onClick = { triggerSystemBiometrics() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberSurfaceLight,
                    contentColor = CyberPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                border = BorderStrokeAndStyle(1.dp, CyberPrimary.copy(alpha = 0.5f)),
                enabled = !isScanningLocally,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("hardware_biometric_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = CyberPrimary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "TIZIM BIOMETRIK PROMPTI (HAQIQIY)",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fallback instruction helper panel
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberPrimary.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Tips",
                        tint = CyberPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Taqdimot uchun eslatma:",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = CyberPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Virtual emulyatorlar barmoq izi uskunalariga ega bo'lmagani sababli, taqdimotda 'SIMULYATSIYA' tugmasidan foydalanish tavsiya etiladi. Haqiqiy telefonda esa 'HAQIQIY PROMPT' tugmasi standard Android barmoq tizimini ishga soladi.",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyberTextSecondary,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 14.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

// Custom layout modifier specifically tailored for the vertical laser bar animation
@Composable
private fun Modifier.graphicsLayerAndOffset(fraction: Float, heightDp: Int): Modifier {
    val offsetVal = (heightDp * fraction).dp
    return this.padding(top = offsetVal)
}

// Simple border helpers to avoid complex gradle compilation incompatibilities on BorderStroke imports
@Composable
private fun BorderStrokeAndStyle(width: androidx.compose.ui.unit.Dp, color: Color) = 
    androidx.compose.foundation.BorderStroke(width, color)
