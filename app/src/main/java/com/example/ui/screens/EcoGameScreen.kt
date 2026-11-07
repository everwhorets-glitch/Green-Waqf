package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.BinType
import com.example.ui.theme.*
import com.example.viewmodel.BankBestariViewModel
import com.example.viewmodel.EcoGameViewModel
import com.example.viewmodel.GameState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcoGameScreen(
    gameViewModel: EcoGameViewModel,
    bankViewModel: BankBestariViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by gameViewModel.uiState.collectAsState()
    val activeUser by bankViewModel.activeUser.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BestariBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Game Pilah Sampah",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = BestariGreenPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = AccentPlasticBg
                            ) {
                                Text(
                                    text = "EDUKATIF",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AccentPlastic,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Sortir Gambar Sampah ke Tong yang Tepat • MAN 1 Yogyakarta",
                            style = MaterialTheme.typography.labelSmall,
                            color = BestariOnSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BestariBackground)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            when (uiState.gameState) {
                GameState.READY -> {
                    // Ready / Menu State
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(BestariGreenPrimary, Color(0xFF4ADE80))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsEsports,
                                contentDescription = "Eco Game",
                                tint = Color.White,
                                modifier = Modifier.size(52.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "Tantangan Pilah Sampah",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = BestariGreenDark,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Perhatikan foto sampah nyata yang muncul! Sortir ke tong Plastik, Organik, Kertas, atau B3/Logam. Dapatkan penjelasan edukasi di setiap jawaban.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = BestariOnSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = BestariMintContainer),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BestariGreenPrimary.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = "Trophy",
                                        tint = LeaderboardGold,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Skor Tertinggi Kamu",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BestariGreenDark
                                    )
                                }
                                Text(
                                    text = "${uiState.highScore} Poin",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = BestariGreenDark
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { gameViewModel.startGame() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("start_game_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BestariGreenPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Mulai",
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Mulai Bermain (45 Detik)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                GameState.PLAYING -> {
                    // Active Gameplay Screen with scroll so buttons never get cut off
                    val scrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Top HUD Bar: Timer, Hearts, Score
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Timer Pill
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (uiState.timeLeftSeconds <= 10) Color(0xFFFEE2E2) else BestariMintContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = "Timer",
                                        tint = if (uiState.timeLeftSeconds <= 10) Color(0xFFDC2626) else BestariGreenPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${uiState.timeLeftSeconds}s",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (uiState.timeLeftSeconds <= 10) Color(0xFFDC2626) else BestariGreenDark
                                    )
                                }
                            }

                            // Lives ❤️❤️❤️
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                repeat(3) { index ->
                                    val isAlive = index < uiState.lives
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = "Nyawa",
                                        tint = if (isAlive) Color(0xFFE11D48) else Color(0xFFD1D5DB),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            // Score & Combo Streak
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${uiState.score} Pts",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = BestariGreenPrimary
                                )
                                if (uiState.comboStreak >= 2) {
                                    Text(
                                        text = "🔥 ${uiState.comboStreak}x Combo!",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD97706)
                                    )
                                }
                            }
                        }

                        // Waste Item Card (Prominent stock image display)
                        val item = uiState.currentItem
                        if (item != null) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("current_waste_card"),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = BestariSurface),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, BestariGreenPrimary.copy(alpha = 0.25f)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // Stock Photo Image with fallback
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(170.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color(0xFFF3F4F6)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(context)
                                                .data(item.imageUrl)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = item.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )

                                        // Category badge overlay
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color.Black.copy(alpha = 0.55f),
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .padding(8.dp)
                                        ) {
                                            Text(
                                                text = "Foto Sampah Nyata",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = BestariGreenDark,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        // Educational Feedback & Reason Card (Appears after answer)
                        AnimatedVisibility(
                            visible = uiState.isShowingExplanation && uiState.feedbackMessage != null,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (uiState.lastFeedbackCorrect == true) Color(0xFFECFDF5) else Color(0xFFFEF2F2)
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.5.dp,
                                    if (uiState.lastFeedbackCorrect == true) Color(0xFF10B981) else Color(0xFFEF4444)
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (uiState.lastFeedbackCorrect == true) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                            contentDescription = "Status",
                                            tint = if (uiState.lastFeedbackCorrect == true) Color(0xFF059669) else Color(0xFFDC2626),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = uiState.feedbackMessage.orEmpty(),
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (uiState.lastFeedbackCorrect == true) Color(0xFF065F46) else Color(0xFF991B1B)
                                        )
                                    }

                                    if (!uiState.educationalExplanation.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "💡 Alasan Edukasi:",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = BestariOnSurface
                                        )
                                        Text(
                                            text = uiState.educationalExplanation.orEmpty(),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = BestariOnSurfaceVariant,
                                            lineHeight = 18.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Button(
                                        onClick = { gameViewModel.proceedToNextItem() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(44.dp)
                                            .testTag("btn_proceed_next_waste"),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (uiState.lastFeedbackCorrect == true) Color(0xFF059669) else BestariGreenPrimary
                                        )
                                    ) {
                                        Text(
                                            text = "Lanjut ke Sampah Berikutnya",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.ArrowForward,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // 4 Interactive Bin Buttons (Plastik, Organik, Kertas, B3)
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Pilih Tong Sampah yang Sesuai:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = BestariGreenDark
                            )

                            // Row 1: Plastik & Organik
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                BinButton(
                                    bin = BinType.PLASTIC,
                                    enabled = !uiState.isShowingExplanation,
                                    modifier = Modifier.weight(1f),
                                    onClick = { gameViewModel.submitAnswer(BinType.PLASTIC) }
                                )
                                BinButton(
                                    bin = BinType.ORGANIC,
                                    enabled = !uiState.isShowingExplanation,
                                    modifier = Modifier.weight(1f),
                                    onClick = { gameViewModel.submitAnswer(BinType.ORGANIC) }
                                )
                            }

                            // Row 2: Kertas & B3/Logam (Buttons 3 & 4 fixed so texts never disappear)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                BinButton(
                                    bin = BinType.PAPER,
                                    enabled = !uiState.isShowingExplanation,
                                    modifier = Modifier.weight(1f),
                                    onClick = { gameViewModel.submitAnswer(BinType.PAPER) }
                                )
                                BinButton(
                                    bin = BinType.METAL_B3,
                                    enabled = !uiState.isShowingExplanation,
                                    modifier = Modifier.weight(1f),
                                    onClick = { gameViewModel.submitAnswer(BinType.METAL_B3) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                GameState.GAME_OVER -> {
                    // Game Over / Results Screen
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .clip(CircleShape)
                                .background(if (uiState.score >= 100) BestariMintContainer else Color(0xFFFEF3C7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (uiState.score >= 100) Icons.Default.Stars else Icons.Default.EmojiEvents,
                                contentDescription = "Result",
                                tint = if (uiState.score >= 100) BestariGreenPrimary else LeaderboardGold,
                                modifier = Modifier.size(46.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (uiState.score >= 100) "Luar Biasa, Pahlawan Lingkungan!" else "Permainan Selesai!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = BestariGreenDark,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Kamu berhasil menyortir ${uiState.correctCount} dari ${uiState.totalAnswered} foto sampah dengan benar.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = BestariOnSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Stats Summary Box
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = BestariSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BestariOutline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Skor Akhir", style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        "${uiState.score} Poin",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = BestariGreenPrimary
                                    )
                                }
                                Divider(color = BestariOutlineVariant)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Pilah Benar", style = MaterialTheme.typography.bodyMedium)
                                    Text("${uiState.correctCount} Barang", fontWeight = FontWeight.SemiBold)
                                }
                                Divider(color = BestariOutlineVariant)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Skor Tertinggi", style = MaterialTheme.typography.bodyMedium)
                                    Text("${uiState.highScore} Poin", fontWeight = FontWeight.Bold, color = LeaderboardGold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Claim Points to Active Account
                        if (!uiState.pointsClaimed && activeUser != null && uiState.score > 0) {
                            Button(
                                onClick = {
                                    bankViewModel.addEcoPoints(activeUser!!.id, uiState.score / 2)
                                    gameViewModel.markPointsClaimed()
                                    Toast.makeText(context, "+${uiState.score / 2} Eco Points berhasil ditambahkan ke akun ${activeUser!!.name}!", Toast.LENGTH_LONG).show()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("claim_game_points_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BestariGreenPrimary)
                            ) {
                                Icon(Icons.Default.VolunteerActivism, contentDescription = "Claim")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Klaim Poin ke Tabungan Akun (+${uiState.score / 2} pts)")
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { gameViewModel.resetToMenu() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Menu Utama")
                            }

                            Button(
                                onClick = { gameViewModel.startGame() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BestariSageSecondary)
                            ) {
                                Text("Main Lagi")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BinButton(
    bin: BinType,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .heightIn(min = 90.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .testTag("bin_button_${bin.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) bin.bgTint else bin.bgTint.copy(alpha = 0.6f)
        ),
        border = androidx.compose.foundation.BorderStroke(2.dp, bin.color.copy(alpha = 0.75f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(bin.color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (bin) {
                        BinType.PLASTIC -> Icons.Default.Inventory2
                        BinType.ORGANIC -> Icons.Default.Park
                        BinType.PAPER -> Icons.Default.Description
                        BinType.METAL_B3 -> Icons.Default.Warning
                    },
                    contentDescription = bin.title,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = bin.title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827),
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Text(
                text = bin.exampleText,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF374151),
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 12.sp
            )
        }
    }
}
