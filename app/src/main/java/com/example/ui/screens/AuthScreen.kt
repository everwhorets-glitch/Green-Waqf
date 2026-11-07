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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Man1ClassDirectory
import com.example.ui.theme.*
import com.example.viewmodel.BankBestariViewModel

enum class AuthMode {
    WELCOME,
    STUDENT_LOGIN,
    STUDENT_REGISTER,
    ADMIN_LOGIN
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: BankBestariViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentMode by remember { mutableStateOf(AuthMode.WELCOME) }

    // Admin login form state
    var adminUsername by remember { mutableStateOf("") }
    var adminPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var adminError by remember { mutableStateOf<String?>(null) }

    // Student form state
    var studentName by remember { mutableStateOf("") }
    var studentClass by remember { mutableStateOf("X A") }
    var studentPhone by remember { mutableStateOf("") }
    var studentError by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BestariBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))

                // Brand Emblem Header
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(BestariGreenDark, BestariGreenPrimary, BestariGreenLight)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = "Logo Bank Bestari",
                        tint = Color.White,
                        modifier = Modifier.size(46.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "BANK BESTARI",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = BestariGreenPrimary,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "MAN 1 YOGYAKARTA",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = BestariSageSecondary
                )

                Text(
                    text = "Bank Sampah Digital Madrasah Adiwiyata",
                    style = MaterialTheme.typography.bodySmall,
                    color = BestariOnSurfaceVariant
                )

                Spacer(modifier = Modifier.height(28.dp))
            }

            when (currentMode) {
                AuthMode.WELCOME -> {
                    item {
                        Text(
                            text = "Silakan pilih peran untuk masuk:",
                            style = MaterialTheme.typography.labelMedium,
                            color = BestariOnSurfaceVariant,
                            modifier = Modifier.padding(bottom = 14.dp)
                        )

                        // Option 1: Masuk Sebagai Siswa
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { currentMode = AuthMode.STUDENT_REGISTER }
                                .testTag("btn_choose_student"),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = BestariSurface),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, BestariGreenPrimary.copy(alpha = 0.3f)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(BestariMintContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = "Siswa",
                                        tint = BestariGreenPrimary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Masuk Sebagai Siswa",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = BestariGreenDark
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Akses tabungan, lihat total sampah & tunjukkan QR Akun",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = BestariOnSurfaceVariant
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Pilih",
                                    tint = BestariGreenPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Option 2: Masuk Sebagai Admin
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .clickable {
                                    adminError = null
                                    currentMode = AuthMode.ADMIN_LOGIN
                                }
                                .testTag("btn_choose_admin"),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = BestariSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BestariOutline),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(BestariSurfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AdminPanelSettings,
                                        contentDescription = "Admin",
                                        tint = BestariSageSecondary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Masuk Sebagai Admin",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = BestariOnSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Scan QR setoran, input berat & kelola database bank sampah",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = BestariOnSurfaceVariant
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Pilih",
                                    tint = BestariSageSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                AuthMode.STUDENT_REGISTER -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = BestariSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BestariOutline)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Buka Akun Siswa Baru",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = BestariGreenPrimary
                                        )
                                        Text(
                                            text = "1 Perangkat untuk 1 Akun Siswa Sendiri",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = BestariOnSurfaceVariant
                                        )
                                    }

                                    IconButton(onClick = { currentMode = AuthMode.WELCOME }) {
                                        Icon(Icons.Default.Close, contentDescription = "Kembali")
                                    }
                                }

                                Divider(color = BestariOutlineVariant)

                                OutlinedTextField(
                                    value = studentName,
                                    onValueChange = {
                                        studentName = it
                                        studentError = null
                                    },
                                    label = { Text("Nama Lengkap Siswa") },
                                    placeholder = { Text("misal: Ahmad Fauzan") },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_student_name")
                                )

                                OutlinedTextField(
                                    value = studentPhone,
                                    onValueChange = {
                                        studentPhone = it
                                        studentError = null
                                    },
                                    label = { Text("Nomor Telepon / WhatsApp") },
                                    placeholder = { Text("misal: 081234567890") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_student_phone")
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "Pilih Kelas (27 Kelas MAN 1 Yogyakarta):",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BestariOnSurface
                                    )

                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(Man1ClassDirectory.getAllClassNames()) { cName ->
                                            val isSelected = studentClass == cName
                                            FilterChip(
                                                selected = isSelected,
                                                onClick = { studentClass = cName },
                                                label = { Text(cName) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = BestariGreenPrimary,
                                                    selectedLabelColor = Color.White
                                                )
                                            )
                                        }
                                    }
                                }

                                if (studentError != null) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFFFEE2E2),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = studentError!!,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF991B1B),
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                }

                                Button(
                                    onClick = {
                                        if (studentName.isBlank() || studentPhone.isBlank()) {
                                            studentError = "Nama dan nomor telepon wajib diisi!"
                                            return@Button
                                        }
                                        isSubmitting = true
                                        viewModel.registerStudent(studentName, studentClass, studentPhone) { success, msg ->
                                            isSubmitting = false
                                            if (success) {
                                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                            } else {
                                                studentError = msg
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("btn_submit_student_register"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = BestariGreenPrimary),
                                    enabled = !isSubmitting
                                ) {
                                    if (isSubmitting) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                    } else {
                                        Icon(Icons.Default.QrCode, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Daftar & Buat QR Akun Saya")
                                    }
                                }

                                TextButton(
                                    onClick = { currentMode = AuthMode.STUDENT_LOGIN },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Sudah pernah mendaftar? Masuk lewat No. Telepon",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BestariGreenPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                AuthMode.STUDENT_LOGIN -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = BestariSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BestariOutline)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Masuk Akun Siswa",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = BestariGreenPrimary
                                        )
                                        Text(
                                            text = "Masukkan nomor telepon yang sudah terdaftar",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = BestariOnSurfaceVariant
                                        )
                                    }

                                    IconButton(onClick = { currentMode = AuthMode.WELCOME }) {
                                        Icon(Icons.Default.Close, contentDescription = "Kembali")
                                    }
                                }

                                OutlinedTextField(
                                    value = studentPhone,
                                    onValueChange = {
                                        studentPhone = it
                                        studentError = null
                                    },
                                    label = { Text("Nomor Telepon / WhatsApp") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                if (studentError != null) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFFFEE2E2),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = studentError!!,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF991B1B),
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                }

                                Button(
                                    onClick = {
                                        isSubmitting = true
                                        viewModel.loginStudentByPhone(studentPhone) { success, msg ->
                                            isSubmitting = false
                                            if (success) {
                                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                            } else {
                                                studentError = msg
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = BestariGreenPrimary),
                                    enabled = !isSubmitting
                                ) {
                                    if (isSubmitting) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                    } else {
                                        Text("Masuk ke Akun Saya")
                                    }
                                }

                                TextButton(
                                    onClick = { currentMode = AuthMode.STUDENT_REGISTER },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Belum punya akun? Buat akun baru disini",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BestariGreenPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                AuthMode.ADMIN_LOGIN -> {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("card_admin_login"),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = BestariSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BestariGreenPrimary.copy(alpha = 0.3f))
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
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
                                                imageVector = Icons.Default.AdminPanelSettings,
                                                contentDescription = "Admin",
                                                tint = BestariGreenPrimary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "Login Khusus Admin",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = BestariGreenPrimary
                                            )
                                            Text(
                                                text = "Akses Pengurus Bank Bestari",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = BestariOnSurfaceVariant
                                            )
                                        }
                                    }

                                    IconButton(onClick = { currentMode = AuthMode.WELCOME }) {
                                        Icon(Icons.Default.Close, contentDescription = "Kembali")
                                    }
                                }

                                Divider(color = BestariOutlineVariant)

                                OutlinedTextField(
                                    value = adminUsername,
                                    onValueChange = {
                                        adminUsername = it
                                        adminError = null
                                    },
                                    label = { Text("Username Admin") },
                                    placeholder = { Text("Masukkan username admin") },
                                    leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_admin_username")
                                )

                                OutlinedTextField(
                                    value = adminPassword,
                                    onValueChange = {
                                        adminPassword = it
                                        adminError = null
                                    },
                                    label = { Text("Password Admin") },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                    trailingIcon = {
                                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                            Icon(
                                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = "Toggle password"
                                            )
                                        }
                                    },
                                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_admin_password")
                                )

                                if (adminError != null) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFFFEE2E2),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ErrorOutline,
                                                contentDescription = null,
                                                tint = Color(0xFFDC2626),
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = adminError!!,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF991B1B)
                                            )
                                        }
                                    }
                                }

                                Button(
                                    onClick = {
                                        val result = viewModel.loginAsAdmin(adminUsername, adminPassword)
                                        result.fold(
                                            onSuccess = {
                                                Toast.makeText(context, "Selamat datang, Admin Bank Bestari!", Toast.LENGTH_SHORT).show()
                                            },
                                            onFailure = { err ->
                                                adminError = err.message ?: "Username atau password salah!"
                                            }
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("btn_submit_admin_login"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = BestariGreenPrimary)
                                ) {
                                    Icon(Icons.Default.Login, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Masuk ke Dashboard Admin")
                                }

                                TextButton(
                                    onClick = { currentMode = AuthMode.WELCOME },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Kembali ke Menu Utama",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BestariSageSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
