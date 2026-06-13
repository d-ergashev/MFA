package com.example

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.ui.theme.CyberTertiary

@Composable
fun MfaDashboardScreen(
    viewModel: MfaViewModel,
    modifier: Modifier = Modifier
) {
    val activeTab by viewModel.activeTab.collectAsState()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBg),
        bottomBar = {
            NavigationBar(
                containerColor = CyberSurface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("bottom_navigation_bar")
            ) {
                val tabs = listOf(
                    Triple(DashboardTab.HOME, "Asosiy", Icons.Default.Home),
                    Triple(DashboardTab.SECURITY, "Xavfsizlik", Icons.Default.Shield),
                    Triple(DashboardTab.DEVICES, "Qurilmalar", Icons.Default.Devices),
                    Triple(DashboardTab.PERMISSIONS, "Ruxsatlar", Icons.Default.VerifiedUser),
                    Triple(DashboardTab.PROFILE, "Profil", Icons.Default.AccountCircle)
                )

                tabs.forEach { (tab, label, icon) ->
                    val selected = activeTab == tab
                    NavigationBarItem(
                        selected = selected,
                        onClick = { viewModel.changeTab(tab) },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (selected) CyberSecondary else CyberTextSecondary
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = if (selected) CyberSecondary else CyberTextSecondary
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = CyberSecondary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CyberBg)
                .padding(innerPadding)
        ) {
            when (activeTab) {
                DashboardTab.HOME -> HomeTabContent(viewModel)
                DashboardTab.SECURITY -> SecurityTabContent(viewModel)
                DashboardTab.DEVICES -> DevicesTabContent(viewModel)
                DashboardTab.PERMISSIONS -> PermissionsTabContent(viewModel)
                DashboardTab.PROFILE -> ProfileTabContent(viewModel)
            }
        }
    }
}

// ---------------------- TABS IMPLEMENTATIONS ----------------------

@Composable
fun HomeTabContent(viewModel: MfaViewModel) {
    val trustedDevices by viewModel.trustedDevices.collectAsState()
    val securityLogs by viewModel.securityLogs.collectAsState()
    val profileName by viewModel.profileName.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Welcoming Title Header block like mockup
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Salom, $profileName!",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 24.sp
                    )
                )
                Text(
                    text = "Account Security (Himoriyalangan)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = CyberPrimary,
                        fontFamily = FontFamily.Monospace
                    ),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(CyberSurfaceLight)
                    .border(1.dp, CyberPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notif",
                    tint = CyberSecondary,
                    modifier = Modifier.size(20.dp)
                )
                // Red glowing indicator
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(CyberError)
                        .align(Alignment.TopEnd)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Premium Security status card matching mockup
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, CyberSecondary.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(CyberSecondary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Shield",
                        tint = CyberSecondary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Security Status",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CyberSecondary.copy(alpha = 0.2f))
                            .border(1.dp, CyberSecondary, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "PROTECTED (HIMOYA LANGAN)",
                            color = CyberSecondary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Oxirgi tekshiruv: Bugun, 10:15 AM",
                        color = CyberTextSecondary,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Trusted Devices section matching mockup
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Ishonchli Qurilmalar",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = FontFamily.Monospace
                )
            )
            Text(
                text = "${trustedDevices.size} ta qurilma",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = CyberPrimary,
                    fontFamily = FontFamily.Monospace
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal items row like premium mockup
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(trustedDevices) { device ->
                Card(
                    modifier = Modifier
                        .size(width = 160.dp, height = 110.dp)
                        .border(
                            width = 1.2.dp,
                            color = if (device.isCurrent) CyberSecondary else CyberPrimary.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = CyberSurfaceLight),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(
                                imageVector = Icons.Default.Devices,
                                contentDescription = null,
                                tint = if (device.isCurrent) CyberSecondary else CyberPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (device.isCurrent) CyberSecondary else CyberPrimary)
                            )
                        }

                        Column {
                            Text(
                                text = device.model,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                            Text(
                                text = if (device.isCurrent) "Ushbu qurilma" else device.status,
                                color = CyberTextSecondary,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recent Login Activity List matching mockup
        Text(
            text = "Yaqindagi Xavfsizlik Faolligi",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = FontFamily.Monospace
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyberPrimary.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                securityLogs.take(5).forEachIndexed { index, log ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (log.success) CyberSecondary.copy(alpha = 0.12f) else CyberError.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (log.success) Icons.Default.CheckCircle else Icons.Default.Block,
                                contentDescription = null,
                                tint = if (log.success) CyberSecondary else CyberError,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = log.message,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                            Text(
                                text = log.time,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CyberTextSecondary,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }
                        
                        Text(
                            text = if (log.success) "Success" else "Fail",
                            color = if (log.success) CyberSecondary else CyberError,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    if (index < 4 && index < securityLogs.size - 1) {
                        Divider(color = CyberSurfaceLight, thickness = 1.dp)
                    }
                }
            }
        }
    }
}

@Composable
fun SecurityTabContent(viewModel: MfaViewModel) {
    val isScanning by viewModel.isScanning.collectAsState()
    val permissions by viewModel.securityPermissions.collectAsState()
    
    // Compute dynamic security score
    val grantedCount = permissions.count { it.isGranted }
    val score = 50 + (grantedCount * 12.5f).toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Xavfsizlik Diagnostikasi",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = FontFamily.Monospace
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        // Spectacular visual cyber dial gauge
        Box(
            modifier = Modifier
                .size(180.dp)
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            val sweepAngle = 360f * (score / 100f)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        // Background track circle
                        drawCircle(
                            color = Color.White.copy(alpha = 0.06f),
                            style = Stroke(width = 12.dp.toPx())
                        )
                        // Glowing arc bar
                        drawArc(
                            color = if (score > 80) CyberSecondary else CyberPrimary,
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(width = 12.dp.toPx())
                        )
                    }
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$score%",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = if (score > 80) CyberSecondary else CyberPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 32.sp
                    )
                )
                Text(
                    text = if (score > 90) "XAVFSIZLIK: MAKS" else "XAVFSIZLIK: O'RTA",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CyberTextSecondary,
                        fontFamily = FontFamily.Monospace
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Scanning action trigger button
        Button(
            onClick = { viewModel.runSecurityScan() },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isScanning) CyberSurfaceLight else CyberPrimary,
                contentColor = if (isScanning) CyberTextSecondary else CyberTextOnPrimary
            ),
            shape = RoundedCornerShape(8.dp),
            enabled = !isScanning,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("scan_security_button")
        ) {
            if (isScanning) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        color = CyberSecondary,
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "TIZIMNI SKANERLASH...",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "XAVFSIZLIKNI QAYTA SCAN QILISH",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Security checklist status overview cards block
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyberPrimary.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "XAVFSIZLIK CHEKLISTI",
                    color = CyberPrimary,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                val checklists = listOf(
                    "Tizim kirish paroli muvaffaqiyatli solishtirilgan" to true,
                    "Touch-ID barmoq izi biometriya tasdiqlangan" to true,
                    "SMS kod tasdiqlash xizmati ulangan" to true,
                    "Remote Push xabarnoma datchigi ulangan" to (grantedCount >= 2)
                )

                checklists.forEachIndexed { idx, pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (pair.second) Icons.Default.CheckCircle else Icons.Default.Block,
                            contentDescription = null,
                            tint = if (pair.second) CyberSecondary else CyberError,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = pair.first,
                            color = if (pair.second) Color.White else CyberTextSecondary,
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    if (idx < checklists.lastIndex) {
                        Divider(color = CyberSurfaceLight, thickness = 1.dp)
                    }
                }
            }
        }
    }
}

@Composable
fun DevicesTabContent(viewModel: MfaViewModel) {
    val trustedDevices by viewModel.trustedDevices.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "Xavfsiz Qurilmalaringiz",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = FontFamily.Monospace
            ),
            modifier = Modifier.padding(vertical = 12.dp)
        )

        // Action block to append device
        Button(
            onClick = { viewModel.addMockDevice() },
            colors = ButtonDefaults.buttonColors(
                containerColor = CyberSecondary,
                contentColor = CyberTextOnPrimary
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("add_trusted_device_button")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "YANGI ISHONCHLI QURILMA QO'SHISH",
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Device cards list
        if (trustedDevices.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Ishonchli qurilmalar ro'yxati bo'sh.",
                    color = CyberTextSecondary,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            trustedDevices.forEach { device ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .border(
                            1.dp,
                            if (device.isCurrent) CyberSecondary.copy(alpha = 0.4f) else CyberPrimary.copy(alpha = 0.15f),
                            RoundedCornerShape(12.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Devices,
                            contentDescription = null,
                            tint = if (device.isCurrent) CyberSecondary else CyberPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = device.model,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontFamily = FontFamily.Monospace
                                    )
                                )
                                if (device.isCurrent) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(CyberSecondary.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Joriy",
                                            color = CyberSecondary,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                    text = "Status: ${device.status}",
                                    color = CyberTextSecondary,
                                    fontFamily = FontFamily.Monospace,
                                    style = MaterialTheme.typography.labelSmall
                            )
                        }

                        IconButton(
                            onClick = { viewModel.removeDevice(device.id, device.model) },
                            modifier = Modifier.testTag("delete_${device.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove device",
                                tint = CyberError,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionsTabContent(viewModel: MfaViewModel) {
    val permissions by viewModel.securityPermissions.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "Tizim Ruxsatnomalari",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = FontFamily.Monospace
            ),
            modifier = Modifier.padding(vertical = 12.dp)
        )

        Text(
            text = "SecureAuth MFA mobil ilovasi barqaror va xavfsiz himoyalashni ta'minlashi uchun quyidagi muhim tizimli ruxsatnomalar zarur:",
            style = MaterialTheme.typography.bodySmall.copy(
                color = CyberTextSecondary,
                fontFamily = FontFamily.Monospace,
                lineHeight = 16.sp
            ),
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Custom list item mapping of permissions with active toggle switches
        permissions.forEach { permission ->
            val iconSelected = when (permission.id) {
                "bio" -> Icons.Default.Lock
                "notif" -> Icons.Default.NotificationsActive
                "camera" -> Icons.Default.Camera
                else -> Icons.Default.LocationOn
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .border(
                        1.dp,
                        if (permission.isGranted) CyberSecondary.copy(alpha = 0.3f) else CyberPrimary.copy(alpha = 0.1f),
                        RoundedCornerShape(12.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (permission.isGranted) CyberSecondary.copy(alpha = 0.12f) else CyberPrimary.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = iconSelected,
                            contentDescription = null,
                            tint = if (permission.isGranted) CyberSecondary else CyberPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = permission.name,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = permission.description,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyberTextSecondary,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 13.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Material design nice Switch
                    Switch(
                        checked = permission.isGranted,
                        onCheckedChange = { viewModel.togglePermission(permission.id) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyberSecondary,
                            checkedTrackColor = CyberSecondary.copy(alpha = 0.25f),
                            uncheckedThumbColor = CyberTextSecondary,
                            uncheckedTrackColor = CyberSurfaceLight
                        ),
                        modifier = Modifier.testTag("switch_${permission.id}")
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileTabContent(viewModel: MfaViewModel) {
    val profileName by viewModel.profileName.collectAsState()
    var editName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Profile Card with Glowing circle avatar
        Box(
            modifier = Modifier
                .size(100.dp)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(CyberSecondary.copy(alpha = 0.25f), Color.Transparent)
                        ),
                        radius = size.minDimension / 1.1f
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(CyberSurfaceLight)
                    .border(2.dp, CyberSecondary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (profileName.isNotEmpty()) profileName.first().uppercase() else "E",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = CyberSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 36.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = profileName,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontSize = 22.sp
            )
        )
        Text(
            text = "Sinf: TIZIM AUTORIZATSIYASI",
            style = MaterialTheme.typography.labelSmall.copy(
                color = CyberSecondary,
                fontFamily = FontFamily.Monospace
            ),
            modifier = Modifier.padding(top = 2.dp, bottom = 24.dp)
        )

        // Change Profile Name Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyberPrimary.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Ismni Taxbirlash",
                    color = CyberPrimary,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        placeholder = { Text("Taxallus kiritish", color = CyberTextSecondary, fontFamily = FontFamily.Monospace) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = CyberSecondary,
                            unfocusedBorderColor = CyberPrimary.copy(alpha = 0.3f),
                            cursorColor = CyberSecondary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .testTag("profile_name_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            viewModel.updateProfileName(editName)
                            editName = ""
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberSecondary,
                            contentColor = CyberTextOnPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        enabled = editName.isNotBlank(),
                        modifier = Modifier
                            .height(54.dp)
                            .testTag("profile_name_save_button")
                    ) {
                        Text("YANGILASH", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Device encryption metadata card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyberPrimary.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "XAVFSIZLIK DETALLARI",
                    color = CyberPrimary,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )

                val details = listOf(
                    "Elektron pochta" to "demo@mfa.uz",
                    "Telefon raqam" to "+998 (90) ***-44-33",
                    "Kriptografiya" to "AES-GCM-256",
                    "Faol seans" to "AUTHORIZED"
                )

                details.forEach { (lbl, valStr) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(lbl, color = CyberTextSecondary, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                        Text(valStr, color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // High contrast system shutdown delete button
        Button(
            onClick = { viewModel.logout() },
            colors = ButtonDefaults.buttonColors(
                containerColor = CyberError,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("logout_reset_button")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.PowerSettingsNew, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "TIZIMNI BLOKLASH (CHIQISH)",
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
