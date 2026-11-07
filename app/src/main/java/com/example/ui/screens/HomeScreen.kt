package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClassCollectiveSavings
import com.example.data.model.Man1Class
import com.example.data.model.Man1ClassDirectory
import com.example.data.model.MentoringSchedule
import com.example.data.model.UserAccount
import com.example.data.model.WasteTransaction
import com.example.ui.components.BestariNavTab
import com.example.ui.components.DigitalStudentCard
import com.example.ui.theme.*
import com.example.util.QrCodeUtil
import com.example.viewmodel.BankBestariViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: BankBestariViewModel,
    onNavigateTab: (BestariNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val activeUser by viewModel.activeUser.collectAsState()
    val studentTransactions by viewModel.studentTransactions.collectAsState()
    val myClassCollective by viewModel.myClassCollective.collectAsState()
    val mySchedule by viewModel.studentDepositSchedule.collectAsState()
    val myMentoring by viewModel.studentMentoringSchedule.collectAsState()

    var showQrFullScreen by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BestariBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "BANK BESTARI",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = BestariGreenPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = BestariMintContainer
                            ) {
                                Text(
                                    text = "SISWA",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BestariOnMintContainer,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "MAN 1 YOGYAKARTA",
                            style = MaterialTheme.typography.labelSmall,
                            color = BestariOnSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showEditProfileDialog = true },
                        modifier = Modifier.testTag("btn_top_edit_profile")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ManageAccounts,
                            contentDescription = "Edit Profil",
                            tint = BestariGreenPrimary
                        )
                    }
                    IconButton(
                        onClick = { showLogoutConfirm = true },
                        modifier = Modifier.testTag("btn_logout_student")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Keluar Akun",
                            tint = BestariSageSecondary
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

            // Digital Member Card for Student with Edit Button
            item {
                if (activeUser != null) {
                    DigitalStudentCard(
                        user = activeUser!!,
                        onScanQrClicked = { showQrFullScreen = true },
                        onEditProfileClicked = { showEditProfileDialog = true }
                    )
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BestariSurfaceVariant)
                    ) {
                        Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Memuat data akun siswa...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = BestariOnSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Big Action: Tunjukkan QR Code ke Admin
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { showQrFullScreen = true }
                        .testTag("btn_show_my_qr"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = BestariMintContainer),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BestariGreenPrimary.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(BestariGreenPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "QR Akun",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Tunjukkan QR untuk Setor",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = BestariGreenDark
                                )
                                Text(
                                    text = "Scan barcode oleh Admin saat penimbangan sampah",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = BestariOnMintContainer
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.OpenInFull,
                            contentDescription = "Buka QR",
                            tint = BestariGreenPrimary
                        )
                    }
                }
            }

            // Tabungan Majmuk Kelas Card (Integrated Class Collective Balance)
            item {
                activeUser?.let { user ->
                    TabunganMajmukHighlightCard(
                        user = user,
                        collective = myClassCollective,
                        onViewDetail = { onNavigateTab(BestariNavTab.HISTORY) }
                    )
                }
            }

            // Dedicated Class Schedule & Mentoring Card (Student ONLY sees their own class schedule)
            item {
                activeUser?.let { user ->
                    MyClassScheduleCard(
                        user = user,
                        schedule = mySchedule,
                        mentoring = myMentoring,
                        onSendH1 = {
                            viewModel.sendHMinusOneNotification(context)
                            Toast.makeText(context, "Notifikasi H-1 dikirim untuk kelas ${user.className}!", Toast.LENGTH_SHORT).show()
                        },
                        onSendDayOf = {
                            viewModel.sendDayOfNotification(context)
                            Toast.makeText(context, "Notifikasi Hari H dikirim untuk kelas ${user.className}!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            // Quick Shortcut Row to Leaderboard and Eco Game
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickStudentCard(
                        title = "Peringkat Kelas",
                        subtitle = "Klasemen 27 Kelas",
                        icon = Icons.Default.EmojiEvents,
                        color = LeaderboardGold,
                        bgColor = AccentPaperBg,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(BestariNavTab.LEADERBOARD) }
                    )
                    QuickStudentCard(
                        title = "Game Pilah",
                        subtitle = "Foto sampah nyata",
                        icon = Icons.Default.SportsEsports,
                        color = AccentGlass,
                        bgColor = AccentGlassBg,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(BestariNavTab.MORE) }
                    )
                }
            }

            // Student's Personal Deposit History
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Riwayat Setoran Tabungan Saya",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = BestariOnSurface
                    )
                    Text(
                        text = "${studentTransactions.size} Transaksi",
                        style = MaterialTheme.typography.labelSmall,
                        color = BestariOnSurfaceVariant
                    )
                }
            }

            if (studentTransactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BestariSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BestariOutlineVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Recycling,
                                contentDescription = null,
                                tint = BestariSageSecondary,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Belum Ada Setoran Sampah",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = BestariOnSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Bawa sampah terpilah (botol plastik, kardus, dsb.) ke Bank Bestari saat jam istirahat untuk mulai menabung!",
                                style = MaterialTheme.typography.bodySmall,
                                color = BestariOnSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(studentTransactions.take(10)) { trx ->
                    StudentTransactionCard(trx = trx)
                }

                if (studentTransactions.size > 10) {
                    item {
                        TextButton(
                            onClick = { onNavigateTab(BestariNavTab.HISTORY) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Lihat Seluruh Riwayat Setoran (${studentTransactions.size})")
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Modal Edit Profil Siswa (Name, Class, Phone, NIS, Address, Bio)
    if (showEditProfileDialog && activeUser != null) {
        EditProfileModal(
            currentUser = activeUser!!,
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, className, phone, nis, address, bio ->
                viewModel.updateStudentProfile(name, className, phone, nis, address, bio) { success, msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    if (success) {
                        showEditProfileDialog = false
                    }
                }
            }
        )
    }

    // Full Screen QR Code Dialog
    if (showQrFullScreen && activeUser != null) {
        val qrBitmap = remember(activeUser!!.id) {
            QrCodeUtil.generateQrImageBitmap(activeUser!!.toQrPayload(), sizePx = 700)
        }

        AlertDialog(
            onDismissRequest = { showQrFullScreen = false },
            confirmButton = {
                Button(
                    onClick = { showQrFullScreen = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BestariGreenPrimary)
                ) {
                    Text("Tutup")
                }
            },
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Scan Barcode Setoran",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = BestariGreenPrimary
                    )
                    Text(
                        text = "Arahkan QR ini ke Petugas Admin Bank Bestari",
                        style = MaterialTheme.typography.labelSmall,
                        color = BestariOnSurfaceVariant
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier
                            .size(240.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .border(2.dp, BestariGreenPrimary.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                            .padding(12.dp),
                        color = Color.White
                    ) {
                        if (qrBitmap != null) {
                            Image(
                                bitmap = qrBitmap,
                                contentDescription = "Barcode Nasabah",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = BestariGreenPrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = activeUser!!.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Kelas ${activeUser!!.className} • ${activeUser!!.id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = BestariSageSecondary
                    )
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    // Logout Confirmation
    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("Keluar dari Akun Siswa?") },
            text = { Text("Anda akan diarahkan kembali ke layar pemilihan akun/login.") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirm = false
                        viewModel.logout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Keluar", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLogoutConfirm = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun TabunganMajmukHighlightCard(
    user: UserAccount,
    collective: ClassCollectiveSavings?,
    onViewDetail: () -> Unit
) {
    val formattedTotalBalance = remember(collective?.totalAmountRupiah) {
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        formatter.maximumFractionDigits = 0
        formatter.format(collective?.totalAmountRupiah ?: 0.0)
    }
    val formattedHistBalance = remember(collective?.historicalAmountRupiah) {
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        formatter.maximumFractionDigits = 0
        formatter.format(collective?.historicalAmountRupiah ?: 0.0)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_tabungan_majmuk_kelas"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BestariSurface),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, BestariGreenPrimary.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BestariMintContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = "Tabungan Kelas",
                            tint = BestariGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Tabungan Majmuk Kelas ${user.className}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = BestariGreenDark
                        )
                        Text(
                            text = "Akumulasi Setoran Seluruh Siswa & Data Lalu",
                            style = MaterialTheme.typography.labelSmall,
                            color = BestariOnSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BestariMintContainer
                ) {
                    Text(
                        text = "Kolektif",
                        style = MaterialTheme.typography.labelSmall,
                        color = BestariGreenDark,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Highlight Numbers Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(BestariSurfaceVariant)
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Saldo Kas Kelas",
                        style = MaterialTheme.typography.labelSmall,
                        color = BestariOnSurfaceVariant
                    )
                    Text(
                        text = formattedTotalBalance,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = BestariGreenPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total Sampah Terkumpul",
                        style = MaterialTheme.typography.labelSmall,
                        color = BestariOnSurfaceVariant
                    )
                    Text(
                        text = "${collective?.totalWeightKg ?: 0.0} kg",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BestariGreenDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sub-breakdown: Saldo Bulan Kemarin + Setoran Siswa Baru
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Bulan Kemarin: $formattedHistBalance (${collective?.historicalWeightKg ?: 0.0} kg)",
                    style = MaterialTheme.typography.labelSmall,
                    color = BestariSageSecondary
                )
                Text(
                    text = "Aktif: ${collective?.memberCount ?: 0} Siswa",
                    style = MaterialTheme.typography.labelSmall,
                    color = BestariGreenDark,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onViewDetail,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Lihat Rincian Tabungan Majmuk Kelas", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun MyClassScheduleCard(
    user: UserAccount,
    schedule: Man1Class?,
    mentoring: MentoringSchedule?,
    onSendH1: () -> Unit,
    onSendDayOf: () -> Unit
) {
    val day = schedule?.scheduleDay ?: "Senin"
    val mentorName = mentoring?.mentors ?: "Bpk/Ibu Guru Pendamping Adiwiyata"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_jadwal_kelas_saya"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BestariSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BestariOutline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AccentPaperBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = "Jadwal",
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Jadwal Setor & Pendampingan",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = BestariGreenDark
                        )
                        Text(
                            text = "Khusus Kelas ${user.className} MAN 1 Yogyakarta",
                            style = MaterialTheme.typography.labelSmall,
                            color = BestariOnSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFEF3C7)
                ) {
                    Text(
                        text = "Hari $day",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF92400E),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = BestariSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = BestariGreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Waktu Penimbangan:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = BestariOnSurface
                            )
                            Text(
                                text = "Setiap Hari $day (Istirahat 1: 09.30 - 10.00 & Istirahat 2: 12.00 - 12.30 WIB)",
                                style = MaterialTheme.typography.bodySmall,
                                color = BestariOnSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.SupervisorAccount,
                            contentDescription = null,
                            tint = BestariGreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Guru Pendamping Penyetoran:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = BestariOnSurface
                            )
                            Text(
                                text = mentorName,
                                style = MaterialTheme.typography.bodySmall,
                                color = BestariGreenDark,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    if (mentoring?.announcer != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = null,
                                tint = BestariSageSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pengumuman: ${mentoring.announcer}",
                                style = MaterialTheme.typography.labelSmall,
                                color = BestariOnSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Notification Trigger Buttons for H-1 and Day Of
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onSendH1,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Tes Notif H-1", fontSize = 11.sp)
                }

                Button(
                    onClick = onSendDayOf,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BestariGreenPrimary)
                ) {
                    Text("Tes Notif Hari H", fontSize = 11.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileModal(
    currentUser: UserAccount,
    onDismiss: () -> Unit,
    onSave: (name: String, className: String, phone: String, nis: String, address: String, bio: String) -> Unit
) {
    var nameInput by remember { mutableStateOf(currentUser.name) }
    var classInput by remember { mutableStateOf(currentUser.className) }
    var phoneInput by remember { mutableStateOf(currentUser.phoneNumber) }
    var nisInput by remember { mutableStateOf(currentUser.nis) }
    var addressInput by remember { mutableStateOf(currentUser.address) }
    var bioInput by remember { mutableStateOf(currentUser.bio) }

    var classExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Edit Profil & Biodata Siswa",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BestariGreenPrimary
                )
                Text(
                    text = "Lengkapi NIS, Alamat, dan Biodata Anda",
                    style = MaterialTheme.typography.labelSmall,
                    color = BestariOnSurfaceVariant
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Nama Lengkap Siswa") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_name"),
                        singleLine = true
                    )
                }

                // Class Dropdown Selector
                item {
                    ExposedDropdownMenuBox(
                        expanded = classExpanded,
                        onExpandedChange = { classExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = classInput,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Pilih Kelas (27 Kelas MAN 1 YK)") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = classExpanded,
                            onDismissRequest = { classExpanded = false }
                        ) {
                            Man1ClassDirectory.all27Classes.forEach { mClass ->
                                DropdownMenuItem(
                                    text = { Text("${mClass.name} (Jadwal: ${mClass.scheduleDay})") },
                                    onClick = {
                                        classInput = mClass.name
                                        classExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = nisInput,
                        onValueChange = { nisInput = it },
                        label = { Text("Nomor Induk Siswa (NIS)") },
                        placeholder = { Text("Contoh: 12345") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_nis"),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("Nomor Telepon / WhatsApp") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_phone"),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = addressInput,
                        onValueChange = { addressInput = it },
                        label = { Text("Alamat Tempat Tinggal") },
                        placeholder = { Text("Contoh: Jl. Magelang No. 4, Yogyakarta") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_address"),
                        maxLines = 2
                    )
                }

                item {
                    OutlinedTextField(
                        value = bioInput,
                        onValueChange = { bioInput = it },
                        label = { Text("Biodata / Motto Kebersihan") },
                        placeholder = { Text("Contoh: Siap pilah sampah untuk madrasah Adiwiyata") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_bio"),
                        maxLines = 2
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(nameInput, classInput, phoneInput, nisInput, addressInput, bioInput)
                },
                modifier = Modifier.testTag("btn_save_profile"),
                colors = ButtonDefaults.buttonColors(containerColor = BestariGreenPrimary)
            ) {
                Text("Simpan Perubahan")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Batal")
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White
    )
}

@Composable
fun QuickStudentCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    bgColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BestariSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BestariOutline)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = BestariOnSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun StudentTransactionCard(trx: WasteTransaction) {
    val formattedDate = remember(trx.timestamp) {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        sdf.format(Date(trx.timestamp))
    }
    val formattedAmount = remember(trx.totalAmount) {
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        formatter.maximumFractionDigits = 0
        formatter.format(trx.totalAmount)
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(BestariMintContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = BestariGreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = trx.commodityName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = BestariOnSurface,
                        maxLines = 1
                    )
                    Text(
                        text = "${trx.quantity} ${trx.unit} • $formattedDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = BestariOnSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "+$formattedAmount",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = BestariGreenPrimary
                )
                Text(
                    text = "+${trx.pointsEarned} pts",
                    style = MaterialTheme.typography.labelSmall,
                    color = LeaderboardGold,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
