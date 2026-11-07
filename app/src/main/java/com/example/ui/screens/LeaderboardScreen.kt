package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.BankBestariViewModel
import com.example.viewmodel.ClassLeaderboardEntry
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    viewModel: BankBestariViewModel,
    modifier: Modifier = Modifier
) {
    val leaderboard by viewModel.classLeaderboard.collectAsState()
    val gradeFilter by viewModel.leaderboardGradeFilter.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredList = remember(leaderboard, searchQuery) {
        if (searchQuery.isBlank()) {
            leaderboard
        } else {
            leaderboard.filter { it.className.contains(searchQuery, ignoreCase = true) }
        }
    }

    val top1 = filteredList.getOrNull(0)
    val top2 = filteredList.getOrNull(1)
    val top3 = filteredList.getOrNull(2)
    val remaining = if (filteredList.size > 3) filteredList.drop(3) else emptyList()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BestariBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Papan Peringkat 27 Kelas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = BestariGreenPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Trophy",
                                tint = LeaderboardGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = "Kelas Teraktif Pilah Sampah • MAN 1 YOGYAKARTA",
                            style = MaterialTheme.typography.labelSmall,
                            color = BestariOnSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BestariBackground)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(2.dp))
            }

            // Grade Filter Tabs: Semua 27 Kelas, Kelas 10 (9), Kelas 11 (9), Kelas 12 (9)
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = gradeFilter == null,
                            onClick = { viewModel.setLeaderboardGradeFilter(null) },
                            label = { Text("Semua 27 Kelas") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BestariGreenPrimary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("filter_all_classes")
                        )
                    }
                    item {
                        FilterChip(
                            selected = gradeFilter == 10,
                            onClick = { viewModel.setLeaderboardGradeFilter(10) },
                            label = { Text("Kelas 10 (9)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BestariGreenPrimary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("filter_grade_10")
                        )
                    }
                    item {
                        FilterChip(
                            selected = gradeFilter == 11,
                            onClick = { viewModel.setLeaderboardGradeFilter(11) },
                            label = { Text("Kelas 11 (9)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BestariGreenPrimary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("filter_grade_11")
                        )
                    }
                    item {
                        FilterChip(
                            selected = gradeFilter == 12,
                            onClick = { viewModel.setLeaderboardGradeFilter(12) },
                            label = { Text("Kelas 12 (9)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BestariGreenPrimary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("filter_grade_12")
                        )
                    }
                }
            }

            // Search Bar for Class
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari kelas (misal: XI PK 1, X A, XII G)...", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Cari",
                            tint = BestariSageSecondary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Top 3 Podium
            if (filteredList.isNotEmpty() && searchQuery.isBlank()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("leaderboard_podium_card"),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = BestariMintContainer.copy(alpha = 0.6f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BestariGreenPrimary.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🏆 PODIUM ADIWIYATA TERBAIK",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = BestariGreenDark,
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                // 2nd Place (Silver)
                                if (top2 != null) {
                                    PodiumPillar(
                                        entry = top2,
                                        rank = 2,
                                        color = LeaderboardSilver,
                                        heightDp = 110.dp,
                                        modifier = Modifier.weight(1f)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }

                                // 1st Place (Gold)
                                if (top1 != null) {
                                    PodiumPillar(
                                        entry = top1,
                                        rank = 1,
                                        color = LeaderboardGold,
                                        heightDp = 145.dp,
                                        modifier = Modifier.weight(1.1f)
                                    )
                                }

                                // 3rd Place (Bronze)
                                if (top3 != null) {
                                    PodiumPillar(
                                        entry = top3,
                                        rank = 3,
                                        color = LeaderboardBronze,
                                        heightDp = 90.dp,
                                        modifier = Modifier.weight(1f)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            // Section Title for Remaining Ranks
            item {
                Text(
                    text = if (searchQuery.isBlank()) "Daftar Peringkat Lengkap" else "Hasil Pencarian Kelas",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = BestariOnSurface
                )
            }

            // List of Classes
            val listToDisplay = if (searchQuery.isBlank()) remaining else filteredList
            if (listToDisplay.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = BestariSurfaceVariant
                    ) {
                        Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                            Text("Tidak ada kelas yang cocok dengan pencarian", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            } else {
                items(listToDisplay, key = { it.className }) { entry ->
                    LeaderboardClassRow(entry = entry)
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun PodiumPillar(
    entry: ClassLeaderboardEntry,
    rank: Int,
    color: Color,
    heightDp: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Crown / Rank Circle
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "#$rank",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = entry.className,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = BestariOnSurface,
            maxLines = 1,
            textAlign = TextAlign.Center
        )

        Text(
            text = "${entry.totalWeightKg} kg",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = BestariGreenPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Pedestal Box
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(heightDp),
            shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f)),
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Stars,
                    contentDescription = "Poin",
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${entry.totalPoints} pts",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = BestariOnSurface,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun LeaderboardClassRow(entry: ClassLeaderboardEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = BestariSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BestariOutlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Rank Number Pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (entry.rank <= 3) BestariMintContainer else BestariSurfaceVariant,
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${entry.rank}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (entry.rank <= 3) BestariGreenDark else BestariOnSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Kelas ${entry.className}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = BestariOnSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = BestariMintContainer.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = "Tingkat ${entry.grade}",
                                style = MaterialTheme.typography.labelSmall,
                                color = BestariSageSecondary,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = "${entry.badgeName} • Tabungan: Rp ${NumberFormat.getNumberInstance(Locale("id", "ID")).format(entry.totalEarnings)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = BestariOnSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${entry.totalWeightKg} kg",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = BestariGreenPrimary
                )
                Text(
                    text = "⭐ ${entry.totalPoints} pts",
                    style = MaterialTheme.typography.labelSmall,
                    color = LeaderboardBronze,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
