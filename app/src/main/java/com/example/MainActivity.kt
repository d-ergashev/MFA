package com.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val mfaViewModel: MfaViewModel = viewModel()
                val currentAuthState by mfaViewModel.authState.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    val screenModifier = Modifier.padding(innerPadding)
                    
                    // Reactive screen rendering based on the simulated authentication stack state
                    when (currentAuthState) {
                        is AuthState.LoggedOut, is AuthState.LoginSimulating -> {
                            MfaLoginScreen(viewModel = mfaViewModel, modifier = screenModifier)
                        }
                        is AuthState.BiometricPending -> {
                            MfaBiometricScreen(viewModel = mfaViewModel, modifier = screenModifier)
                        }
                        is AuthState.OtpPending -> {
                            MfaOtpScreen(viewModel = mfaViewModel, modifier = screenModifier)
                        }
                        is AuthState.PushPending -> {
                            MfaPushScreen(viewModel = mfaViewModel, modifier = screenModifier)
                        }
                        is AuthState.Authorized -> {
                            MfaDashboardScreen(viewModel = mfaViewModel, modifier = screenModifier)
                        }
                    }
                }
            }
        }
    }
}
