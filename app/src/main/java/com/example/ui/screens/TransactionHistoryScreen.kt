package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClassCollectiveSavings
import com.example.data.model.WasteCommodityCatalog
import com.example.data.model.WasteTransaction
import com.example.ui.components.RecentTransactionItem
import com.example.ui.components.SegmentButton
import com.example.ui.components.buildReportString
import com.example.ui.theme.*
import com.example.viewmodel.AuthRole
import com.example.viewmodel.BankBestariViewModel
import com.example.viewmodel.PeriodFilter
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(
    viewModel: BankBestariViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val allTransactions by viewModel.allTransactions.collectAsState()
    val studentTransactions by viewModel.studentTransactions.collectAsState()
    val activeUser by viewModel.activeUser.collectAsState()
    val authRole by viewModel.authRole.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val rekapSummary by viewModel.rekapSummary.collectAsState()
    val myClassCollective by viewModel.myClassCollective.collectAsState()
    val myClassTransactions by viewModel.myClassTransactions.collectAsState()
    val allClassCollectiveSavings by viewModel.allClassCollectiveSavings.collectAsState()

    // 0 = Tabungan Saya / Riwayat, 1 = Tabungan Majmuk Kelas, 2 = Rekapitulasi Madrasah
    var activeSubTab by remember { mutableStateOf(if (authRole == AuthRole.STUDENT) 1 else 0) }
    var categoryFilter by remember { mutableStateOf("SEMUA") }
    var searchQuery by remember { mutableStateOf("") }

    val baseTransactions = if (authRole == AuthRole.STUDENT) studentTransactions else allTransactions

    val filteredTransactions = remember(baseTransactions, selectedPeriod, categoryFilter, searchQuery) {
        val now = System.currentTimeMillis()
        val calendar = java.util.Calendar.getInstance()

        baseTransactions.filter { trx ->
            val matchPeriod = when (selectedPeriod) {
                PeriodFilter.DAILY -> {
                    calendar.timeInMillis = now
                    calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
                    calendar.set(java.util.Calendar.MINUTE, 0)
                    calendar.set(java.util.Calendar.SECOND, 0)
                    calendar.set(java.util.Calendar.MILLISECOND, 0)
                    trx.timestamp >= calendar.timeInMillis
                }
                PeriodFilter.WEEKLY -> {
                    val oneWeek = now - (7 * 24 * 60 * 60 * 1000L)
                    trx.timestamp >= oneWeek
                }
                PeriodFilter.MONTHLY -> {
                    val oneMonth = now - (30 * 24 * 60 * 60 * 1000L)
                    trx.timestamp >= oneMonth
                }
                PeriodFilter.ALL_TIME -> true
            }

            val matchCategory = categoryFilter == "SEMUA" || trx.category.equals(categoryFilter, ignoreCase = true)
            val matchSearch = searchQuery.isBlank() ||
                    trx.userName.contains(searchQuery, ignoreCase = true) ||
                    trx.commodityName.contains(searchQuery, ignoreCase = true) ||
                    trx.receiptCode.contains(searchQuery, ignoreCase = true)

            matchPeriod && matchCategory && matchSearch
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BestariBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (authRole == AuthRole.STUDENT) "Tabungan & Rekapitulasi" else "Rekapitulasi Bank Bestari",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BestariGreenPrimary
                        )
                        Text(
                            text = "Bank Sampah MAN 1 Yogyakarta",
                            style = MaterialTheme.typography.labelSmall,
                            color = BestariOnSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val text = buildReportString(rekapSummary, selectedPeriod)
                            clipboardManager.setText(AnnotatedString(text))
                            Toast.makeText(context, "Ringkasan rekapitulasi disalin!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Bagikan Rekap",
                            tint = BestariGreenPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BestariBackground)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Sub-tabs segment
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(14.dp),
                color = BestariSurfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    if (authRole == AuthRole.STUDENT) {
                        SegmentButton(
                            text = "Tabungan Kelas",
                            isSelected = activeSubTab == 1,
                            modifier = Modifier.weight(1.1f),
                            onClick = { activeSubTab = 1 }
                        )
                        SegmentButton(
                            text = "Tabungan Saya",
                            isSelected = activeSubTab == 0,
                            modifier = Modifier.weight(1f),
                            onClick = { activeSubTab = 0 }
                        )
                        SegmentButton(
                            text = "Rekap Madrasah",
                            isSelected = activeSubTab == 2,
                            modifier = Modifier.weight(1f),
                            onClick = { activeSubTab = 2 }
                        )
                    } else {
                        SegmentButton(
                            text = "Riwayat Semua",
                            isSelected = activeSubTab == 0,
                            modifier = Modifier.weight(1f),
                            onClick = { activeSubTab = 0 }
                        )
                        SegmentButton(
                            text = "Tabungan 27 Kelas",
                            isSelected = activeSubTab == 1,
                            modifier = Modifier.weight(1.2f),
                            onClick = { activeSubTab = 1 }
                        )
                        SegmentButton(
                            text = "Rekap Madrasah",
                            isSelected = activeSubTab == 2,
                            modifier = Modifier.weight(1f),
                            onClick = { activeSubTab = 2 }
                        )
                    }
                }
            }

            // Tab 1: Tabungan Majmuk Kelas (Dedicated view for student's class)
            if (activeSubTab == 1 && authRole == AuthRole.STUDENT) {
                TabunganMajmukDetailView(
                    className = activeUser?.className ?: "Kelas",
                    collective = myClassCollective,
                    classTransactions = myClassTransactions
                )
            } else if (activeSubTab == 1 && authRole == AuthRole.ADMIN) {
                // Admin 27 Class Collective View
                AdminClassCollectiveView(allClassCollectiveSavings)
            } else if (activeSubTab == 2) {
                // Tab 2: Rekapitulasi Madrasah
                RekapitulasiMadrasahView(
                    selectedPeriod = selectedPeriod,
                    rekapSummary = rekapSummary,
                    onSelectPeriod = { viewModel.setPeriod(it) }
                )
            } else {
                // Tab 0: Riwayat Setoran (Personal for Student / All for Admin)
                PersonalHistoryView(
                    transactions = filteredTransactions,
                    selectedPeriod = selectedPeriod,
                    onSelectPeriod = { viewModel.setPeriod(it) },
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    categoryFilter = categoryFilter,
                    onCategoryFilterChange = { categoryFilter = it }
                )
            }
        }
    }
}

@Composable
fun TabunganMajmukDetailView(
    className: String,
    collective: ClassCollectiveSavings?,
    classTransactions: List<WasteTransaction>
) {
    val formattedTotal = remember(collective?.totalAmountRupiah) {
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        formatter.maximumFractionDigits = 0
        formatter.format(collective?.totalAmountRupiah ?: 0.0)
    }
    val formattedHistorical = remember(collective?.historicalAmountRupiah) {
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        formatter.maximumFractionDigits = 0
        formatter.format(collective?.historicalAmountRupiah ?: 0.0)
    }
    val formattedMember = remember(collective?.currentMemberAmountRupiah) {
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        formatter.maximumFractionDigits = 0
        formatter.format(collective?.currentMemberAmountRupiah ?: 0L)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Main Collective Savings Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BestariMintContainer),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, BestariGreenPrimary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TABUNGAN MAJMUK KELAS",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = BestariOnMintContainer.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "Kelas $className",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = BestariGreenDark
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BestariGreenPrimary
                        ) {
                            Text(
                                text = "Jadwal: ${collective?.scheduleDay ?: "Senin"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Total Saldo Kas Tabungan Kelas",
                        style = MaterialTheme.typography.labelMedium,
                        color = BestariOnMintContainer
                    )
                    Text(
                        text = formattedTotal,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = BestariGreenPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = BestariGreenPrimary.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Sampah", style = MaterialTheme.typography.labelSmall, color = BestariOnMintContainer)
                            Text("${collective?.totalWeightKg ?: 0.0} kg", fontWeight = FontWeight.Bold, color = BestariGreenDark)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Bulan Kemarin", style = MaterialTheme.typography.labelSmall, color = BestariOnMintContainer)
                            Text(formattedHistorical, fontWeight = FontWeight.SemiBold, color = BestariOnMintContainer)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Siswa Terdaftar", style = MaterialTheme.typography.labelSmall, color = BestariOnMintContainer)
                            Text("${collective?.memberCount ?: 0} Siswa", fontWeight = FontWeight.Bold, color = BestariGreenDark)
                        }
                    }
                }
            }
        }

        // Historical Breakdown from Image 1
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BestariSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BestariOutline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.HistoryEdu, contentDescription = null, tint = BestariGreenPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Rincian Setoran Bulan Kemarin",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = BestariGreenDark
                            )
                        }
                        Text(
                            text = "Tabel Resmi",
                            style = MaterialTheme.typography.labelSmall,
                            color = BestariSageSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val items = collective?.historicalBreakdown ?: emptyList()
                    if (items.isEmpty()) {
                        Text(
                            text = "Belum ada catatan setoran bulan kemarin untuk kelas ini (Rp 0).",
                            style = MaterialTheme.typography.bodySmall,
                            color = BestariOnSurfaceVariant
                        )
                    } else {
                        items.forEach { item ->
                            val itemRp = remember(item.rupiahAmount) {
                                val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                                formatter.maximumFractionDigits = 0
                                formatter.format(item.rupiahAmount)
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${item.commodityName} (${item.commodityCode})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = BestariOnSurface
                                )
                                Text(
                                    text = "${item.weightKg} kg • $itemRp",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BestariGreenPrimary
                                )
                            }
                            Divider(color = BestariOutlineVariant.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }

        // Live App Deposits by Class Members
        item {
            Text(
                text = "Setoran Anggota Siswa di Aplikasi (${classTransactions.size})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = BestariOnSurface
            )
        }

        if (classTransactions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BestariSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BestariOutlineVariant)
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Belum ada setoran baru yang diinput admin untuk siswa kelas $className.",
                            style = MaterialTheme.typography.bodySmall,
                            color = BestariOnSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(classTransactions) { trx ->
                RecentTransactionItem(trx = trx)
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun AdminClassCollectiveView(classes: List<ClassCollectiveSavings>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Database Tabungan Majmuk 27 Kelas MAN 1 YK",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = BestariGreenDark
            )
            Text(
                text = "Akumulasi saldo dan total sampah per kelas (Data resmi + setoran baru)",
                style = MaterialTheme.typography.labelSmall,
                color = BestariOnSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(classes) { item ->
            val totalRp = remember(item.totalAmountRupiah) {
                val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                formatter.maximumFractionDigits = 0
                formatter.format(item.totalAmountRupiah)
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BestariSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BestariOutlineVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Kelas ${item.className}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = BestariGreenDark
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (item.scheduleDay == "Senin") Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                            ) {
                                Text(
                                    text = item.scheduleDay,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.scheduleDay == "Senin") Color(0xFF166534) else Color(0xFF92400E),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Total Sampah: ${item.totalWeightKg} kg • ${item.memberCount} Siswa",
                            style = MaterialTheme.typography.bodySmall,
                            color = BestariOnSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = totalRp,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = BestariGreenPrimary
                        )
                        Text(
                            text = "Lalu: ${item.historicalWeightKg} kg",
                            style = MaterialTheme.typography.labelSmall,
                            color = BestariSageSecondary
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PersonalHistoryView(
    transactions: List<WasteTransaction>,
    selectedPeriod: PeriodFilter,
    onSelectPeriod: (PeriodFilter) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    categoryFilter: String,
    onCategoryFilterChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PeriodFilter.entries.forEach { period ->
                val isSelected = period == selectedPeriod
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectPeriod(period) },
                    label = {
                        Text(
                            text = period.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BestariGreenPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Cari jenis sampah, nama, atau kode...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (transactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BestariSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BestariOutlineVariant)
                    ) {
                        Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Tidak ada transaksi yang cocok untuk periode ini.",
                                style = MaterialTheme.typography.bodySmall,
                                color = BestariOnSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(transactions) { trx ->
                    RecentTransactionItem(trx = trx)
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun RekapitulasiMadrasahView(
    selectedPeriod: PeriodFilter,
    rekapSummary: com.example.viewmodel.RekapSummary,
    onSelectPeriod: (PeriodFilter) -> Unit
) {
    val formattedTotalAmount = remember(rekapSummary.totalAmountRupiah) {
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        formatter.maximumFractionDigits = 0
        formatter.format(rekapSummary.totalAmountRupiah)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PeriodFilter.entries.forEach { period ->
                    val isSelected = period == selectedPeriod
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectPeriod(period) },
                        label = {
                            Text(
                                text = period.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BestariGreenPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BestariMintContainer),
                border = androidx.compose.foundation.BorderStroke(1.dp, BestariGreenPrimary.copy(alpha = 0.25f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Rekapitulasi Madrasah • ${selectedPeriod.label}",
                        style = MaterialTheme.typography.labelSmall,
                        color = BestariOnMintContainer.copy(alpha = 0.8f),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = formattedTotalAmount,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = BestariGreenDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total Bobot: ${rekapSummary.totalWeightKg} kg",
                            fontWeight = FontWeight.SemiBold,
                            color = BestariGreenDark
                        )
                        Text(
                            text = "${rekapSummary.totalTransactionCount} Transaksi",
                            color = BestariOnMintContainer
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BestariSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BestariOutline)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Rincian Berdasarkan Kategori Sampah",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = BestariGreenDark
                    )
                    CategoryRow(name = "Plastik & Botol", weight = rekapSummary.plasticWeightKg, color = BestariGreenPrimary)
                    CategoryRow(name = "Kertas, Kardus & Arsip", weight = rekapSummary.paperWeightKg, color = Color(0xFF2563EB))
                    CategoryRow(name = "Logam & Kaleng", weight = rekapSummary.metalWeightKg, color = Color(0xFFDC2626))
                    CategoryRow(name = "Kaca / Beling", weight = rekapSummary.glassWeightKg, color = Color(0xFF0D9488))
                    CategoryRow(name = "Lain-lain / Minyak Jelantah", weight = rekapSummary.otherWeightKg, color = Color(0xFFD97706))
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun CategoryRow(name: String, weight: Double, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(name, style = MaterialTheme.typography.bodySmall)
        }
        Text(
            text = "$weight kg",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}
