package com.example

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.repository.BankBestariRepository
import com.example.ui.components.BestariBottomBar
import com.example.ui.components.BestariNavTab
import com.example.ui.components.SegmentButton
import com.example.ui.screens.*
import com.example.ui.theme.BestariBackground
import com.example.ui.theme.BestariSurfaceVariant
import com.example.ui.theme.MyApplicationTheme
import com.example.util.NotificationHelper
import com.example.viewmodel.AuthRole
import com.example.viewmodel.BankBestariViewModel
import com.example.viewmodel.EcoGameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Notification Channel for Daily Class Reminders
        NotificationHelper.createNotificationChannel(applicationContext)

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = BankBestariRepository(database)

        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val application = context.applicationContext as Application

                val bankViewModel: BankBestariViewModel = viewModel(
                    factory = BankBestariViewModel.provideFactory(application, repository)
                )
                val gameViewModel: EcoGameViewModel = viewModel()

                val authRole by bankViewModel.authRole.collectAsState()

                when (authRole) {
                    AuthRole.NONE -> {
                        AuthScreen(viewModel = bankViewModel)
                    }
                    AuthRole.ADMIN -> {
                        AdminScreen(viewModel = bankViewModel)
                    }
                    AuthRole.STUDENT -> {
                        StudentApp(
                            bankViewModel = bankViewModel,
                            gameViewModel = gameViewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StudentApp(
    bankViewModel: BankBestariViewModel,
    gameViewModel: EcoGameViewModel,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf(BestariNavTab.HOME) }
    var moreSubTab by remember { mutableStateOf(0) } // 0 = Game Pilah, 1 = Tips & Jadwal

    // Custom Back Handling
    BackHandler(enabled = currentTab != BestariNavTab.HOME) {
        currentTab = BestariNavTab.HOME
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BestariBackground,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            BestariBottomBar(
                currentTab = currentTab,
                onTabSelected = { currentTab = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                BestariNavTab.HOME -> {
                    HomeScreen(
                        viewModel = bankViewModel,
                        onNavigateTab = { target ->
                            if (target == BestariNavTab.MORE) {
                                moreSubTab = 0
                            }
                            currentTab = target
                        }
                    )
                }
                BestariNavTab.HISTORY -> {
                    TransactionHistoryScreen(
                        viewModel = bankViewModel
                    )
                }
                BestariNavTab.LEADERBOARD -> {
                    LeaderboardScreen(
                        viewModel = bankViewModel
                    )
                }
                BestariNavTab.MORE -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 6.dp),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                            color = BestariSurfaceVariant
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                            ) {
                                SegmentButton(
                                    text = "🎮 Game Pilah Sampah",
                                    isSelected = moreSubTab == 0,
                                    modifier = Modifier.weight(1f),
                                    onClick = { moreSubTab = 0 }
                                )
                                SegmentButton(
                                    text = "📖 Tips & Jadwal 27 Kelas",
                                    isSelected = moreSubTab == 1,
                                    modifier = Modifier.weight(1f),
                                    onClick = { moreSubTab = 1 }
                                )
                            }
                        }

                        if (moreSubTab == 0) {
                            EcoGameScreen(
                                gameViewModel = gameViewModel,
                                bankViewModel = bankViewModel
                            )
                        } else {
                            ScheduleAndEducationScreen(
                                viewModel = bankViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
