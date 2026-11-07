package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.Man1Class
import com.example.data.model.Man1ClassDirectory
import com.example.data.model.MentoringSchedule
import com.example.data.model.MentoringScheduleCatalog
import com.example.data.model.RecycleTip
import com.example.data.model.RecycleTipCatalog
import com.example.ui.components.SegmentButton
import com.example.ui.theme.*
import com.example.viewmodel.AuthRole
import com.example.viewmodel.BankBestariViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleAndEducationScreen(
    viewModel: BankBestariViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var activeSubTab by remember { mutableStateOf(1) } // 0 = Tips Edukasi, 1 = Jadwal Kelas & Notifikasi
    var selectedTipDetail by remember { mutableStateOf<RecycleTip?>(null) }

    val activeUser by viewModel.activeUser.collectAsState()
    val authRole by viewModel.authRole.collectAsState()
    val isNotificationEnabled by viewModel.isNotificationEnabled.collectAsState()
    val studentSchedule by viewModel.studentDepositSchedule.collectAsState()
    val studentMentoring by viewModel.studentMentoringSchedule.collectAsState()

    // Permission launcher for Android 13+ POST_NOTIFICATIONS
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.sendDayOfNotification(context)
            Toast.makeText(context, "Izin diberikan! Notifikasi pengingat berhasil dikirim.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Izin notifikasi tidak diaktifkan.", Toast.LENGTH_SHORT).show()
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
                            text = if (authRole == AuthRole.STUDENT) "Jadwal Setor Kelas Saya" else "Jadwal 27 Kelas & Pendampingan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BestariGreenPrimary
                        )
                        Text(
                            text = "Bank Bestari • MAN 1 Yogyakarta",
                            style = MaterialTheme.typography.labelSmall,
                            color = BestariOnSurfaceVariant
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
            // Sub-tab Switcher
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
                    SegmentButton(
                        text = "Jadwal & Notifikasi",
                        isSelected = activeSubTab == 1,
                        modifier = Modifier.weight(1.1f),
                        onClick = { activeSubTab = 1 }
                    )
                    SegmentButton(
                        text = "Tips Daur Ulang",
                        isSelected = activeSubTab == 0,
                        modifier = Modifier.weight(1f),
                        onClick = { activeSubTab = 0 }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (activeSubTab == 0) {
                // Tips Edukasi Daur Ulang
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(RecycleTipCatalog.sampleTips, key = { it.id }) { tip ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { selectedTipDetail = tip }
                                .testTag("tip_card_${tip.id}"),
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
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = BestariSurfaceVariant
                                    ) {
                                        Text(
                                            text = tip.category,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = BestariSageSecondary,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Default.ArrowForwardIos,
                                        contentDescription = "Detail",
                                        tint = BestariSageSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = tip.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = BestariOnSurface
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = tip.summary,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = BestariOnSurfaceVariant
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            } else {
                // Jadwal View: Strict filter for students (own class only)
                if (authRole == AuthRole.STUDENT) {
                    val user = activeUser
                    if (user != null) {
                        StudentOwnClassScheduleView(
                            userClass = user.className,
                            schedule = studentSchedule,
                            mentoring = studentMentoring,
                            isNotificationEnabled = isNotificationEnabled,
                            onToggleNotification = { viewModel.toggleNotification(it) },
                            onTestH1 = {
                                checkAndTriggerNotification(context, notificationPermissionLauncher) {
                                    viewModel.sendHMinusOneNotification(context)
                                    Toast.makeText(context, "Pengingat H-1 dikirim untuk kelas ${user.className}!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onTestDayOf = {
                                checkAndTriggerNotification(context, notificationPermissionLauncher) {
                                    viewModel.sendDayOfNotification(context)
                                    Toast.makeText(context, "Pengingat Hari H dikirim untuk kelas ${user.className}!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = BestariGreenPrimary)
                        }
                    }
                } else {
                    // Admin view: All 27 classes schedule (Image 2) and all 21 mentoring rows (Image 3)
                    AdminFullScheduleView()
                }
            }
        }
    }

    // Detail Dialog for Tips
    selectedTipDetail?.let { tip ->
        AlertDialog(
            onDismissRequest = { selectedTipDetail = null },
            confirmButton = {
                Button(
                    onClick = { selectedTipDetail = null },
                    colors = ButtonDefaults.buttonColors(containerColor = BestariGreenPrimary)
                ) {
                    Text("Paham")
                }
            },
            title = {
                Text(
                    text = tip.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BestariGreenDark
                )
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        Text(
                            text = tip.summary,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = BestariGreenPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = tip.fullContent,
                            style = MaterialTheme.typography.bodySmall,
                            color = BestariOnSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    }
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}

private fun checkAndTriggerNotification(
    context: android.content.Context,
    launcher: androidx.activity.result.ActivityResultLauncher<String>,
    onGranted: () -> Unit
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            onGranted()
        } else {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    } else {
        onGranted()
    }
}

@Composable
fun StudentOwnClassScheduleView(
    userClass: String,
    schedule: Man1Class?,
    mentoring: MentoringSchedule?,
    isNotificationEnabled: Boolean,
    onToggleNotification: (Boolean) -> Unit,
    onTestH1: () -> Unit,
    onTestDayOf: () -> Unit
) {
    val day = schedule?.scheduleDay ?: "Senin"
    val mentorNames = mentoring?.mentors ?: "Bpk/Ibu Guru Pendamping Adiwiyata"
    val announcerName = mentoring?.announcer ?: "Tim Pengumuman Speaker Madrasah"
    val dateText = mentoring?.fullDate ?: "Jadwal Terdekat"

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Privacy notice badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFEFF6FF),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Privasi",
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Jadwal ini khusus untuk kelas Anda ($userClass). Informasi kelas lain tidak ditampilkan untuk menjaga ketertiban penyetoran.",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF1E40AF),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        item {
            // Main Class Schedule Card
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
                                text = "JADWAL PENYETORAN SAMPAH",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = BestariOnMintContainer.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "Kelas $userClass",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = BestariGreenDark
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = BestariGreenPrimary
                        ) {
                            Text(
                                text = "HARI $day",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = BestariGreenPrimary.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Waktu Penimbangan di Gazebo Bank Bestari:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = BestariOnMintContainer
                    )
                    Text(
                        text = "• Istirahat 1: 09.30 - 10.00 WIB\n• Istirahat 2: 12.00 - 12.30 WIB",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BestariGreenDark,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        item {
            // Mentoring Schedule Details (Image 3)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BestariSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BestariOutline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SupervisorAccount,
                            contentDescription = null,
                            tint = BestariGreenPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Jadwal Pendampingan Terdekat",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = BestariGreenDark
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    DetailItemRow(label = "Hari & Tanggal", value = dateText)
                    DetailItemRow(label = "Guru Pendamping", value = mentorNames, isHighlight = true)
                    DetailItemRow(label = "Pengumuman Speaker", value = announcerName)

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = BestariSurfaceVariant
                    ) {
                        Text(
                            text = "ℹ️ Tugas Pendamping: Mendampingi murid saat menyetorkan, menimbang, dan mencatat hasil timbang sampah ke Bank Bestari.",
                            style = MaterialTheme.typography.labelSmall,
                            color = BestariOnSurfaceVariant,
                            modifier = Modifier.padding(10.dp),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        item {
            // Push Notification Card (H-1 and Day Of)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
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
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = BestariGreenPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Notifikasi Pengingat Setor",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = BestariGreenDark
                                )
                                Text(
                                    text = "Pengingat H-1 & Hari H Penyetoran",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BestariOnSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = isNotificationEnabled,
                            onCheckedChange = onToggleNotification,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = BestariGreenPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Uji Coba Notifikasi Mandiri:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = BestariOnSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onTestH1,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Kirim Notif H-1", fontSize = 11.sp)
                        }

                        Button(
                            onClick = onTestDayOf,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BestariGreenPrimary)
                        ) {
                            Text("Kirim Notif Hari H", fontSize = 11.sp)
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

@Composable
fun DetailItemRow(label: String, value: String, isHighlight: Boolean = false) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = BestariOnSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isHighlight) BestariGreenDark else BestariOnSurface
        )
    }
}

@Composable
fun AdminFullScheduleView() {
    val seninClasses = remember { Man1ClassDirectory.getScheduleForDay("Senin") }
    val selasaClasses = remember { Man1ClassDirectory.getScheduleForDay("Selasa") }
    val mentoringList = remember { MentoringScheduleCatalog.schedules }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Database Jadwal Setor 27 Kelas (Senin & Selasa)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = BestariGreenDark
            )
            Text(
                text = "Berdasarkan ketetapan resmi Bank Bestari MAN 1 Yogyakarta",
                style = MaterialTheme.typography.labelSmall,
                color = BestariOnSurfaceVariant
            )
        }

        // Senin Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BestariSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BestariOutline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFDCFCE7)
                    ) {
                        Text(
                            text = "HARI SENIN (13 KELAS)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF166534),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Kelas 10: X E, X F, X G, X PK 1, X PK 2\n• Kelas 11: XI A, XI B, XI C, XI D\n• Kelas 12: XII A, XII B, XII C, XII D",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BestariOnSurface,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        // Selasa Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BestariSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BestariOutline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFEF3C7)
                    ) {
                        Text(
                            text = "HARI SELASA (14 KELAS)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Kelas 10: X A, X B, X C, X D\n• Kelas 11: XI E, XI F, XI G, XI PK 1, XI PK 2\n• Kelas 12: XII E, XII F, XII G, XII PK 1, XII PK 2",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BestariOnSurface,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        // Mentoring Table (Image 3)
        item {
            Text(
                text = "Tabel 21 Jadwal Pendampingan Guru",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = BestariGreenDark
            )
        }

        items(mentoringList) { m ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = BestariSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BestariOutlineVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${m.no}. ${m.fullDate}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = BestariGreenPrimary
                        )
                        Text(
                            text = "Hari ${m.dayName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = BestariSageSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Pendamping: ${m.mentors}",
                        style = MaterialTheme.typography.bodySmall,
                        color = BestariOnSurface
                    )
                    Text(
                        text = "Pengumuman: ${m.announcer}",
                        style = MaterialTheme.typography.labelSmall,
                        color = BestariOnSurfaceVariant
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
