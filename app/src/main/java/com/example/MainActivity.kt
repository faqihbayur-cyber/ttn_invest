package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.screens.*
import com.example.viewmodel.InvestorViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    SplashScreen(onSplashFinished = { showSplash = false })
                } else {
                    MainAppGate()
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainAppGate() {
    val investorViewModel: InvestorViewModel = viewModel()
    val isLoggedIn by investorViewModel.isLoggedIn.collectAsState()

    if (!isLoggedIn) {
        // Gates unauthenticated users behind the premium lock
        LoginScreen(viewModel = investorViewModel)
    } else {
        // Authenticated Enterprise workspace
        var currentNavTab by remember { mutableStateOf("HOME") }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                    tonalElevation = 8.dp
                ) {
                    // Item 1: Home Dashboard
                    NavigationBarItem(
                        selected = (currentNavTab == "HOME"),
                        onClick = { currentNavTab = "HOME" },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Beranda") },
                        label = { Text("Beranda", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                    )

                    // Item 2: Financial statement reports and regional sebaran maps
                    NavigationBarItem(
                        selected = (currentNavTab == "REPORTS"),
                        onClick = { currentNavTab = "REPORTS" },
                        icon = { Icon(Icons.Default.Assessment, contentDescription = "Laporan") },
                        label = { Text("Laporan", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                    )

                    // Item 3: Media announcements & dividends payout schedule ledger
                    NavigationBarItem(
                        selected = (currentNavTab == "MEDIA"),
                        onClick = { currentNavTab = "MEDIA" },
                        icon = { Icon(Icons.Default.Newspaper, contentDescription = "Media") },
                        label = { Text("Media", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                    )

                    // Item 4: Investor Relations live chats and Profile audits
                    NavigationBarItem(
                        selected = (currentNavTab == "PROFILE"),
                        onClick = { currentNavTab = "PROFILE" },
                        icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Akun") },
                        label = { Text("Akun", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                // Cross fade transitions between sub views
                when (currentNavTab) {
                    "HOME" -> DashboardScreen(viewModel = investorViewModel)
                    "REPORTS" -> FinancialsAndOutletsScreen(viewModel = investorViewModel)
                    "MEDIA" -> MediaAndDividendsScreen(viewModel = investorViewModel)
                    else -> RelationsAndProfileScreen(viewModel = investorViewModel)
                }
            }
        }
    }
}
