package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Sealed represented view states during authentication
sealed interface AuthState {
    object LoggedOut : AuthState
    object LoginSimulating : AuthState
    object BiometricPending : AuthState
    object OtpPending : AuthState
    object PushPending : AuthState
    object Authorized : AuthState
}

// Device data structure
data class TrustedDevice(
    val id: String,
    val model: String,
    val status: String,
    val isCurrent: Boolean = false
)

// Security log data structure
data class SecurityLog(
    val id: String,
    val time: String,
    val message: String,
    val success: Boolean = true
)

class MfaViewModel : ViewModel() {

    // Auth screen transitions
    private val _authState = MutableStateFlow<AuthState>(AuthState.LoggedOut)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Inputs
    var loginEmail = MutableStateFlow("")
    var loginPassword = MutableStateFlow("")
    var loginPin = MutableStateFlow("")
    
    // OTP inputs
    private val _otpInputCode = MutableStateFlow("")
    val otpInputCode: StateFlow<String> = _otpInputCode.asStateFlow()

    // Screen feedbacks
    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _otpError = MutableStateFlow<String?>(null)
    val otpError: StateFlow<String?> = _otpError.asStateFlow()

    private val _notificationMessage = MutableStateFlow<String?>(null)
    val notificationMessage: StateFlow<String?> = _notificationMessage.asStateFlow()

    // Mock Database for trusted devices
    private val _trustedDevices = MutableStateFlow<List<TrustedDevice>>(
        listOf(
            TrustedDevice("1", "Samsung Galaxy S23", "Hozirgi", isCurrent = true),
            TrustedDevice("2", "iPhone 13", "2 kun oldin"),
            TrustedDevice("3", "MacBook Pro M2", "Yaqinda, 11:45")
        )
    )
    val trustedDevices: StateFlow<List<TrustedDevice>> = _trustedDevices.asStateFlow()

    // Mock Database for logs
    private val _securityLogs = MutableStateFlow<List<SecurityLog>>(
        listOf(
            SecurityLog("101", "Bugun, 14:30", "Tizimga muvaffaqiyatli kirish (Toshkent)", success = true),
            SecurityLog("102", "Kecha, 09:15", "Xato parol kiritilgan qurilma aniqlandi", success = false),
            SecurityLog("103", "3 kun oldin", "Xavfsizlik PIN kodi yangilandi", success = true)
        )
    )
    val securityLogs: StateFlow<List<SecurityLog>> = _securityLogs.asStateFlow()

    fun clearLoginError() {
        _loginError.value = null
    }

    fun clearOtpError() {
        _otpError.value = null
    }

    fun clearNotification() {
        _notificationMessage.value = null
    }

    fun onOtpCodeChange(newCode: String) {
        if (newCode.length <= 6) {
            _otpInputCode.value = newCode
        }
    }

    // Dynamic log generator helper
    private fun addLog(message: String, isSuccess: Boolean = true) {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime = "Bugun, ${format.format(Date())}"
        val newLog = SecurityLog(
            id = System.currentTimeMillis().toString(),
            time = currentTime,
            message = message,
            success = isSuccess
        )
        _securityLogs.update { listOf(newLog) + it }
    }

    // A. User Login Process (Mocked Auth)
    fun processLogin() {
        viewModelScope.launch {
            val email = loginEmail.value.trim()
            val password = loginPassword.value
            val pin = loginPin.value

            if (email != "demo@mfa.uz" || password != "password123" || pin != "1234") {
                _loginError.value = "Kiritilgan ma'lumotlar noto'g'ri! (Email: demo@mfa.uz, Parol: password123, PIN: 1234)"
                addLog("Muvaffaqiyatsiz Login urinishi: $email", isSuccess = false)
                return@launch
            }

            // Start loading simulation
            _loginError.value = null
            _authState.value = AuthState.LoginSimulating
            
            // Simulating API network delay of 2 seconds
            delay(2000)

            addLog("Email va parol tasdiqlandi. Biometrik bosqichga o'tildi")
            _authState.value = AuthState.BiometricPending
        }
    }

    // B. Biometric Auth Process
    fun authenticateBiometrics(success: Boolean) {
        if (success) {
            addLog("Biometrik barmoq izi muvaffaqiyatli tasdiqlandi")
            // Proceed to Step 2: OTP (SMS)
            _authState.value = AuthState.OtpPending
            
            // Trigger delayed local SMS notification code simulation
            viewModelScope.launch {
                delay(1000)
                _notificationMessage.value = "TASDIQLASH KODI: 554433\nSecureMFA tizimiga kirish uchun parolingiz."
            }
        } else {
            addLog("Biometrik skanerlash urinishi rad etildi", isSuccess = false)
        }
    }

    // C. OTP Verification code
    fun verifyOtp() {
        if (_otpInputCode.value == "554433") {
            _otpError.value = null
            addLog("SMS OTP kodi xavfsiz qabul qilindi va tasdiqlandi")
            
            // Proceed to Step 3: Mock Push notification screen before final authorization
            _authState.value = AuthState.PushPending
        } else {
            _otpError.value = "Tasdiqlash kodi xato! Iltimos, qayta urining."
            addLog("Noto'g'ri OTP kodi kiritildi: ${_otpInputCode.value}", isSuccess = false)
        }
    }

    // D. Mock Push Notification Action
    fun handlePushResponse(approved: Boolean) {
        if (approved) {
            addLog("Push xabarnomasi foydalanuvchi tomonidan tasdiqlandi")
            addLog("SecureMFA_Demo orqali to'liq autentifikatsiya yakunlandi")
            _authState.value = AuthState.Authorized
        } else {
            addLog("Push so'rovi rad etildi. Kirish bloklandi", isSuccess = false)
            // Send back to start or stay on stage
            _authState.value = AuthState.LoggedOut
            // Reset fields
            loginPassword.value = ""
            loginPin.value = ""
        }
    }

    // E. Remove Trusted Device
    fun removeDevice(deviceId: String, deviceModel: String) {
        _trustedDevices.update { list ->
            list.filter { it.id != deviceId }
        }
        addLog("$deviceModel qurilmasi ishonchli ro'yxatdan o'chirildi", isSuccess = true)
    }

    // Full Reset
    fun logout() {
        loginEmail.value = ""
        loginPassword.value = ""
        loginPin.value = ""
        _otpInputCode.value = ""
        _loginError.value = null
        _otpError.value = null
        _notificationMessage.value = null
        _authState.value = AuthState.LoggedOut
        addLog("Sessiya yopildi / Tizimdan chiqildi")
    }
}
