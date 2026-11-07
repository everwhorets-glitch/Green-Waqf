package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserAccount
import com.example.data.model.WasteCommodity
import com.example.data.model.WasteCommodityCatalog
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.BankBestariViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: BankBestariViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val depositState by viewModel.depositState.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val rekapSummary by viewModel.rekapSummary.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val allClassCollectiveSavings by viewModel.allClassCollectiveSavings.collectAsState()

    var activeAdminTab by remember { mutableStateOf(0) }
    var selectedClassForDetail by remember { mutableStateOf<com.example.data.model.ClassCollectiveSavings?>(null) }
    var commodityCategoryFilter by remember { mutableStateOf("SEMUA") }
    var commoditySearch by remember { mutableStateOf("") }
    var studentSearch by remember { mutableStateOf("") }
    var manualIdInput by remember { mutableStateOf("") }
    var showStudentPicker by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }

    // Laser scan animation
    val infiniteTransition = rememberInfiniteTransition(label = "laser_admin")
    val laserOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_y_admin"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BestariBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Panel Admin Bank Bestari",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = BestariGreenPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFDC2626)
                            ) {
                                Text(
                                    text = "ADMIN",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Pengurus Bank Sampah • MAN 1 YOGYAKARTA",
                            style = MaterialTheme.typography.labelSmall,
                            color = BestariOnSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showLogoutConfirm = true },
                        modifier = Modifier.testTag("btn_logout_admin")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Keluar Admin",
                            tint = Color(0xFFDC2626)
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
            // Admin Navigation Subtabs
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                color = BestariSurfaceVariant
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item { AdminNavChip("Scan Setor", activeAdminTab == 0) { activeAdminTab = 0 } }
                    item { AdminNavChip("Tabungan 27 Kelas", activeAdminTab == 1) { activeAdminTab = 1 } }
                    item { AdminNavChip("Jadwal & Guru", activeAdminTab == 2) { activeAdminTab = 2 } }
                    item { AdminNavChip("Harga (42)", activeAdminTab == 3) { activeAdminTab = 3 } }
                    item { AdminNavChip("Siswa (${allUsers.size})", activeAdminTab == 4) { activeAdminTab = 4 } }
                    item { AdminNavChip("Rekap", activeAdminTab == 5) { activeAdminTab = 5 } }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            when (activeAdminTab) {
                0 -> {
                    // TAB 0: SCAN QR & INPUT SETORAN
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Scanner Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = BestariSurface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BestariOutline)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Scan QR Akun Siswa",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (depositState.scannedUser != null) BestariMintContainer else BestariSurfaceVariant
                                        ) {
                                            Text(
                                                text = if (depositState.scannedUser != null) "Nasabah Terdeteksi" else "Menunggu QR",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (depositState.scannedUser != null) BestariOnMintContainer else BestariOnSurfaceVariant,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Viewfinder
                                    Box(
                                        modifier = Modifier
                                            .size(190.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color(0xFF0F1710))
                                            .border(2.dp, BestariGreenPrimary.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(145.dp)
                                                .border(2.dp, Color(0xFF4ADE80).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                        )

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(2.5.dp)
                                                .offset(y = (-65 + (laserOffset * 130)).dp)
                                                .background(
                                                    Brush.horizontalGradient(
                                                        listOf(Color.Transparent, Color(0xFF4ADE80), Color.White, Color(0xFF4ADE80), Color.Transparent)
                                                    )
                                                )
                                        )

                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = Icons.Default.QrCodeScanner,
                                                contentDescription = null,
                                                tint = Color.White.copy(alpha = 0.4f),
                                                modifier = Modifier.size(48.dp)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Arahkan ke QR Siswa",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White.copy(alpha = 0.7f),
                                                fontSize = 10.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Fast Buttons: Pilih dari Database Siswa or Manual ID
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { showStudentPicker = true },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("btn_pick_student_deposit"),
                                            colors = ButtonDefaults.buttonColors(containerColor = BestariGreenPrimary),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Pilih Nasabah (${allUsers.size})")
                                        }

                                        if (depositState.scannedUser != null) {
                                            OutlinedButton(
                                                onClick = { viewModel.clearScannedUser() },
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Icon(Icons.Default.Refresh, contentDescription = "Reset")
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = manualIdInput,
                                            onValueChange = { manualIdInput = it },
                                            placeholder = { Text("Ketik No. HP / ID QR Siswa", fontSize = 11.sp) },
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Button(
                                            onClick = {
                                                if (manualIdInput.isNotBlank()) {
                                                    viewModel.handleScannedQr(manualIdInput.trim())
                                                    manualIdInput = ""
                                                }
                                            },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = BestariSageSecondary)
                                        ) {
                                            Text("Cari")
                                        }
                                    }
                                }
                            }
                        }

                        // Scanned Student Info Card
                        val scanned = depositState.scannedUser
                        if (scanned != null) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(containerColor = BestariMintContainer),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BestariGreenPrimary.copy(alpha = 0.3f))
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clip(CircleShape)
                                                    .background(BestariGreenPrimary),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(
                                                        text = scanned.name,
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = BestariGreenDark
                                                    )
                                                    Text(
                                                        text = "Kelas ${scanned.className} • HP: ${scanned.phoneNumber}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = BestariOnMintContainer
                                                    )
                                                }
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = Color.White.copy(alpha = 0.8f)
                                            ) {
                                                Text(
                                                    text = scanned.id.takeLast(9),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = BestariGreenPrimary,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))
                                        Divider(color = BestariGreenPrimary.copy(alpha = 0.15f))
                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Saldo Saat Ini: Rp ${NumberFormat.getNumberInstance(Locale("id", "ID")).format(scanned.balance)}",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold,
                                                color = BestariOnMintContainer
                                            )
                                            Text(
                                                text = "Total Sampah: ${scanned.totalWeightKg} kg",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.SemiBold,
                                                color = BestariOnMintContainer
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Commodity Selector from 42 Items
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "1. Pilih Jenis Sampah (42 Komoditas)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = BestariSurface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BestariOutline)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        // Current Selection Display
                                        val cur = depositState.selectedCommodity
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = cur.bgColor
                                                    ) {
                                                        Text(
                                                            text = cur.code,
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            color = cur.accentColor,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = cur.name,
                                                        style = MaterialTheme.typography.titleSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = BestariOnSurface,
                                                        maxLines = 1
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "Harga Beli (Nasabah): Rp ${NumberFormat.getNumberInstance(Locale("id", "ID")).format(cur.priceBuyCustomer)} / ${cur.unit} (Jual BSI: Rp ${NumberFormat.getNumberInstance(Locale("id", "ID")).format(cur.priceSellBsi)})",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = BestariGreenPrimary,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))
                                        Divider(color = BestariOutlineVariant)
                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = "Ganti Komoditas:",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = BestariOnSurfaceVariant
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Horizontal scroll of popular/all commodities
                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            items(WasteCommodityCatalog.items) { item ->
                                                val isSelected = depositState.selectedCommodity.code == item.code
                                                Surface(
                                                    shape = RoundedCornerShape(10.dp),
                                                    color = if (isSelected) item.bgColor else BestariSurfaceVariant,
                                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, item.accentColor) else null,
                                                    modifier = Modifier.clickable { viewModel.setDepositCommodity(item) }
                                                ) {
                                                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                                        Text(
                                                            text = "${item.code}: ${item.name.take(18)}...",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isSelected) item.accentColor else BestariOnSurface
                                                        )
                                                        Text(
                                                            text = "Rp ${item.priceBuyCustomer}/${item.unit}",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontSize = 10.sp,
                                                            color = BestariSageSecondary
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Weight / Quantity Input
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "2. Timbang / Masukkan Jumlah (${depositState.selectedCommodity.unit})",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                OutlinedTextField(
                                    value = depositState.quantityInput,
                                    onValueChange = { viewModel.setDepositQuantity(it) },
                                    label = { Text("Jumlah / Berat (${depositState.selectedCommodity.unit})") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    trailingIcon = {
                                        Text(
                                            text = depositState.selectedCommodity.unit,
                                            fontWeight = FontWeight.Bold,
                                            color = BestariOnSurfaceVariant,
                                            modifier = Modifier.padding(end = 16.dp)
                                        )
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("admin_quantity_input")
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    QuickWeightChip("+0.5", onClick = { viewModel.addDepositQuantity(0.5) })
                                    QuickWeightChip("+1.0", onClick = { viewModel.addDepositQuantity(1.0) })
                                    QuickWeightChip("+2.0", onClick = { viewModel.addDepositQuantity(2.0) })
                                    QuickWeightChip("+5.0", onClick = { viewModel.addDepositQuantity(5.0) })
                                }
                            }
                        }

                        // Calculation & Margin Card
                        item {
                            val cur = depositState.selectedCommodity
                            val qty = depositState.quantityInput.toDoubleOrNull() ?: 0.0
                            val totalBsi = Math.round(qty * cur.priceSellBsi)
                            val margin = totalBsi - depositState.calculatedAmount

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = BestariSurfaceVariant),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BestariOutline)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Saldo Masuk ke Siswa",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = BestariOnSurfaceVariant
                                            )
                                            Text(
                                                text = "Rp ${NumberFormat.getNumberInstance(Locale("id", "ID")).format(depositState.calculatedAmount)}",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = BestariGreenPrimary
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "Kas / Margin Bank",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = BestariOnSurfaceVariant
                                            )
                                            Text(
                                                text = "+Rp ${NumberFormat.getNumberInstance(Locale("id", "ID")).format(margin)}",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = LeaderboardBronze
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (depositState.errorMessage != null) {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFFEE2E2),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = depositState.errorMessage!!,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF991B1B),
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }

                        // Submit Button
                        item {
                            Button(
                                onClick = { viewModel.submitDeposit() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("admin_submit_deposit"),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BestariGreenPrimary),
                                enabled = !depositState.isSubmitting
                            ) {
                                if (depositState.isSubmitting) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                } else {
                                    Icon(Icons.Default.Savings, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Simpan Setoran & Masukkan Saldo")
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }

                1 -> {
                    // TAB 1: DATABASE TABUNGAN MAJMUK 27 KELAS (Image 1 historical + current deposits)
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text(
                                text = "Database Tabungan Majmuk 27 Kelas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = BestariGreenDark
                            )
                            Text(
                                text = "Akumulasi saldo kas kelas dari setoran per siswa & data bulan kemarin",
                                style = MaterialTheme.typography.labelSmall,
                                color = BestariOnSurfaceVariant
                            )
                        }

                        items(allClassCollectiveSavings) { cls ->
                            val totalRp = remember(cls.totalAmountRupiah) {
                                NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
                                    maximumFractionDigits = 0
                                }.format(cls.totalAmountRupiah)
                            }
                            val histRp = remember(cls.historicalAmountRupiah) {
                                NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
                                    maximumFractionDigits = 0
                                }.format(cls.historicalAmountRupiah)
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { selectedClassForDetail = cls },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = BestariSurface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BestariOutlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "Kelas ${cls.className}",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = BestariGreenDark
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (cls.scheduleDay == "Senin") Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                                            ) {
                                                Text(
                                                    text = "Hari ${cls.scheduleDay}",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (cls.scheduleDay == "Senin") Color(0xFF166534) else Color(0xFF92400E),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = totalRp,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = BestariGreenPrimary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Total Sampah: ${cls.totalWeightKg} kg • ${cls.memberCount} Siswa",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = BestariOnSurfaceVariant
                                        )
                                        Text(
                                            text = "Bulan Lalu: $histRp",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = BestariSageSecondary
                                        )
                                    }

                                    if (cls.historicalBreakdown.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Ketuk untuk melihat detail rincian komoditas",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = BestariGreenPrimary,
                                            fontSize = 10.sp
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

                2 -> {
                    // TAB 2: JADWAL 27 KELAS & 21 JADWAL PENDAMPINGAN GURU (Image 2 & Image 3)
                    AdminFullScheduleView()
                }

                3 -> {
                    // TAB 3: DATABASE HARGA SAMPAH (42 Komoditas)
                    val filteredCommodities = remember(commodityCategoryFilter, commoditySearch) {
                        WasteCommodityCatalog.items.filter { item ->
                            val matchCat = commodityCategoryFilter == "SEMUA" || item.category.equals(commodityCategoryFilter, ignoreCase = true)
                            val matchQuery = commoditySearch.isBlank() ||
                                    item.name.contains(commoditySearch, ignoreCase = true) ||
                                    item.code.contains(commoditySearch, ignoreCase = true)
                            matchCat && matchQuery
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text(
                                text = "Database Harga Komoditas Bank Sampah",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Harga Beli (diberikan ke nasabah) & Harga Jual (ke BSI)",
                                style = MaterialTheme.typography.bodySmall,
                                color = BestariOnSurfaceVariant
                            )
                        }

                        // Search Bar
                        item {
                            OutlinedTextField(
                                value = commoditySearch,
                                onValueChange = { commoditySearch = it },
                                placeholder = { Text("Cari barang atau kode (misal: PET, A-01, Kardus)...", fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                trailingIcon = {
                                    if (commoditySearch.isNotEmpty()) {
                                        IconButton(onClick = { commoditySearch = "" }) {
                                            Icon(Icons.Default.Close, contentDescription = null)
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth().testTag("search_commodity_input")
                            )
                        }

                        // Category Chips
                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(WasteCommodityCatalog.categories) { cat ->
                                    FilterChip(
                                        selected = commodityCategoryFilter == cat,
                                        onClick = { commodityCategoryFilter = cat },
                                        label = { Text(cat) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = BestariGreenPrimary,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }
                        }

                        items(filteredCommodities, key = { it.code + it.no }) { com ->
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
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = com.bgColor
                                            ) {
                                                Text(
                                                    text = com.code,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = com.accentColor,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "[${com.category}]",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = BestariSageSecondary,
                                                fontSize = 10.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${com.no}. ${com.name}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = BestariOnSurface
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "Beli: Rp ${NumberFormat.getNumberInstance(Locale("id", "ID")).format(com.priceBuyCustomer)}",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = BestariGreenPrimary
                                        )
                                        Text(
                                            text = "Jual: Rp ${NumberFormat.getNumberInstance(Locale("id", "ID")).format(com.priceSellBsi)} / ${com.unit}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = BestariOnSurfaceVariant,
                                            fontSize = 11.sp
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

                4 -> {
                    // TAB 4: DATABASE SISWA & NASABAH
                    val filteredStudents = remember(allUsers, studentSearch) {
                        if (studentSearch.isBlank()) {
                            allUsers
                        } else {
                            allUsers.filter {
                                it.name.contains(studentSearch, ignoreCase = true) ||
                                it.className.contains(studentSearch, ignoreCase = true) ||
                                it.phoneNumber.contains(studentSearch, ignoreCase = true) ||
                                it.id.contains(studentSearch, ignoreCase = true)
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text(
                                text = "Database Siswa Nasabah Terdaftar",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Total ${allUsers.size} siswa telah membuka rekening di Bank Bestari",
                                style = MaterialTheme.typography.bodySmall,
                                color = BestariOnSurfaceVariant
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = studentSearch,
                                onValueChange = { studentSearch = it },
                                placeholder = { Text("Cari siswa, kelas, no HP...", fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (filteredStudents.isEmpty()) {
                            item {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 24.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    color = BestariSurfaceVariant
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = if (allUsers.isEmpty()) "Database nasabah masih kosong.\nSiswa akan muncul di sini setelah membuat akun di aplikasinya." else "Tidak ada siswa yang cocok dengan pencarian.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                            color = BestariOnSurfaceVariant
                                        )
                                    }
                                }
                            }
                        } else {
                            items(filteredStudents, key = { it.id }) { user ->
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
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = user.name,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = BestariOnSurface
                                            )
                                            Text(
                                                text = "Kelas ${user.className} • HP: ${user.phoneNumber}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = BestariSageSecondary
                                            )
                                            Text(
                                                text = "Total Sampah: ${user.totalWeightKg} kg • ID: ${user.id}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = BestariOnSurfaceVariant,
                                                fontSize = 10.sp
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "Rp ${NumberFormat.getNumberInstance(Locale("id", "ID")).format(user.balance)}",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = BestariGreenPrimary
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = BestariMintContainer,
                                                modifier = Modifier.clickable {
                                                    viewModel.setScannedUser(user)
                                                    activeAdminTab = 0
                                                    Toast.makeText(context, "${user.name} dipilih untuk setoran", Toast.LENGTH_SHORT).show()
                                                }
                                            ) {
                                                Text(
                                                    text = "Setor Sampah",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = BestariGreenDark,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }

                5 -> {
                    // TAB 5: REKAPITULASI LAPORAN
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                com.example.viewmodel.PeriodFilter.entries.forEach { period ->
                                    val isSelected = period == selectedPeriod
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.setPeriod(period) },
                                        label = { Text(period.label) },
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
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = BestariMintContainer)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Total Tabungan Tersimpan: Rp ${NumberFormat.getNumberInstance(Locale("id", "ID")).format(rekapSummary.totalAmountRupiah)}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = BestariGreenDark
                                    )
                                    Text(
                                        text = "Total Sampah Terkumpul: ${rekapSummary.totalWeightKg} kg (${rekapSummary.totalTransactionCount} transaksi)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = BestariOnMintContainer
                                    )
                                }
                            }
                        }

                        items(allTransactions, key = { it.id }) { trx ->
                            RecentTransactionItem(trx = trx)
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }

    // Success Receipt Dialog
    if (depositState.submissionResult != null) {
        val trx = depositState.submissionResult!!
        val formattedDate = remember(trx.timestamp) {
            SimpleDateFormat("dd MMMM yyyy, HH:mm 'WIB'", Locale("id", "ID")).format(Date(trx.timestamp))
        }
        val formattedAmount = remember(trx.totalAmount) {
            NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(trx.totalAmount)
        }

        AlertDialog(
            onDismissRequest = { viewModel.dismissDepositResult() },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissDepositResult() },
                    colors = ButtonDefaults.buttonColors(containerColor = BestariGreenPrimary)
                ) {
                    Text("Selesai & Setor Lagi")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        val text = "STRUK BANK BESTARI MAN 1 YOGYAKARTA\nNo: ${trx.receiptCode}\nNama: ${trx.userName}\nKelas: ${trx.userClass}\nBarang: ${trx.commodityName} (${trx.commodityCode})\nJumlah: ${trx.quantity} ${trx.unit}\nSaldo Masuk ke Siswa: $formattedAmount\nWaktu: $formattedDate"
                        clipboardManager.setText(AnnotatedString(text))
                        Toast.makeText(context, "Struk disalin ke papan klip!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Salin Struk")
                }
            },
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(BestariMintContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BestariGreenPrimary, modifier = Modifier.size(30.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Setoran Berhasil Disimpan!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BestariGreenPrimary
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(BestariSurfaceVariant)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ReceiptRow("No. Struk", trx.receiptCode)
                    ReceiptRow("Nama Siswa", trx.userName)
                    ReceiptRow("Kelas", trx.userClass)
                    ReceiptRow("Jenis Barang", "${trx.commodityName} (${trx.commodityCode})")
                    ReceiptRow("Jumlah Timbangan", "${trx.quantity} ${trx.unit}")
                    ReceiptRow("Harga Beli Nasabah", "Rp ${NumberFormat.getNumberInstance(Locale("id", "ID")).format(trx.priceBuyCustomer)} / ${trx.unit}")
                    Divider(color = BestariOutline, modifier = Modifier.padding(vertical = 4.dp))
                    ReceiptRow("Saldo Masuk ke Siswa", formattedAmount, isHighlight = true)
                }
            }
        )
    }

    // Modal Student Picker for Fast Scan
    if (showStudentPicker) {
        AlertDialog(
            onDismissRequest = { showStudentPicker = false },
            confirmButton = {
                TextButton(onClick = { showStudentPicker = false }) { Text("Tutup") }
            },
            title = { Text("Pilih Nasabah dari Database", fontWeight = FontWeight.Bold) },
            text = {
                if (allUsers.isEmpty()) {
                    Text("Belum ada siswa yang mendaftar di database aplikasi.")
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(allUsers) { user ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = BestariSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.setScannedUser(user)
                                        showStudentPicker = false
                                        Toast.makeText(context, "Siswa ${user.name} dipilih", Toast.LENGTH_SHORT).show()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(user.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                        Text("Kelas ${user.className} • HP: ${user.phoneNumber}", style = MaterialTheme.typography.bodySmall, color = BestariSageSecondary)
                                    }
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BestariGreenPrimary)
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    // Class Detail Dialog for Admin
    selectedClassForDetail?.let { cls ->
        AlertDialog(
            onDismissRequest = { selectedClassForDetail = null },
            confirmButton = {
                Button(
                    onClick = { selectedClassForDetail = null },
                    colors = ButtonDefaults.buttonColors(containerColor = BestariGreenPrimary)
                ) {
                    Text("Tutup")
                }
            },
            title = {
                Column {
                    Text(
                        text = "Rincian Setoran Kelas ${cls.className}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BestariGreenDark
                    )
                    Text(
                        text = "Data Setoran Resmi Bulan Kemarin (MAN 1 YK)",
                        style = MaterialTheme.typography.labelSmall,
                        color = BestariOnSurfaceVariant
                    )
                }
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    val totalFormatted = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
                        maximumFractionDigits = 0
                    }.format(cls.totalAmountRupiah)

                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = BestariMintContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Total Akumulasi Saldo Kas", style = MaterialTheme.typography.labelSmall, color = BestariOnMintContainer)
                                Text(totalFormatted, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = BestariGreenDark)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Total Sampah: ${cls.totalWeightKg} kg • Hari: ${cls.scheduleDay}", style = MaterialTheme.typography.bodySmall, color = BestariOnMintContainer)
                            }
                        }
                    }

                    if (cls.historicalBreakdown.isEmpty()) {
                        item {
                            Text("Tidak ada setoran tercatat pada bulan kemarin (Rp 0).", style = MaterialTheme.typography.bodySmall, color = BestariOnSurfaceVariant)
                        }
                    } else {
                        item {
                            Text("Rincian Komoditas Bulan Kemarin:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = BestariOnSurface)
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        items(cls.historicalBreakdown) { item ->
                            val itemRp = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
                                maximumFractionDigits = 0
                            }.format(item.rupiahAmount)

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
                            Divider(color = BestariOutlineVariant)
                        }
                    }
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

    // Logout Dialog
    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("Keluar dari Panel Admin?") },
            text = { Text("Anda harus memasukkan username dan password kembali untuk mengakses panel admin ini.") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirm = false
                        viewModel.logout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Keluar Admin")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
fun AdminNavChip(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) Color.White else Color.Transparent,
        shadowElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) BestariGreenPrimary else BestariOnSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}
