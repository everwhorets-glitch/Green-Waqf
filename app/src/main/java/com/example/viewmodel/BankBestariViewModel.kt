package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.BankBestariRepository
import com.example.util.NotificationHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

enum class AuthRole {
    NONE,
    STUDENT,
    ADMIN
}

enum class PeriodFilter(val label: String) {
    DAILY("Hari Ini"),
    WEEKLY("Minggu Ini"),
    MONTHLY("Bulan Ini"),
    ALL_TIME("Semua Periode")
}

data class RekapSummary(
    val totalWeightKg: Double = 0.0,
    val totalAmountRupiah: Long = 0L,
    val totalTransactionCount: Int = 0,
    val plasticWeightKg: Double = 0.0,
    val paperWeightKg: Double = 0.0,
    val metalWeightKg: Double = 0.0,
    val glassWeightKg: Double = 0.0,
    val otherWeightKg: Double = 0.0,
    val topClassName: String = "-",
    val plasticPercentage: Float = 0f
)

data class ClassLeaderboardEntry(
    val rank: Int,
    val className: String,
    val grade: Int,
    val totalWeightKg: Double,
    val totalPoints: Int,
    val totalEarnings: Long,
    val activeMemberCount: Int,
    val badgeName: String
)

data class DepositFormState(
    val scannedUser: UserAccount? = null,
    val selectedCommodity: WasteCommodity = WasteCommodityCatalog.defaultCommodity(),
    val quantityInput: String = "1.0",
    val calculatedAmount: Long = 2800L, // 1 kg * 2800 (Harga beli B-01)
    val calculatedPoints: Int = 10,
    val notes: String = "",
    val isSubmitting: Boolean = false,
    val submissionResult: WasteTransaction? = null,
    val errorMessage: String? = null
)

class BankBestariViewModel(
    application: Application,
    private val repository: BankBestariRepository
) : AndroidViewModel(application) {

    private val prefs: SharedPreferences = application.getSharedPreferences("bank_bestari_prefs", Context.MODE_PRIVATE)

    // Admin Credentials as specified by user:
    // Username: adminbankbestari_man1yk
    // Password: alnesagogreen@1978
    val adminUsernameExpected = "adminbankbestari_man1yk"
    val adminPasswordExpected = "alnesagogreen@1978"

    private val _authRole = MutableStateFlow(AuthRole.NONE)
    val authRole: StateFlow<AuthRole> = _authRole.asStateFlow()

    val allUsers: StateFlow<List<UserAccount>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions: StateFlow<List<WasteTransaction>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeUser: StateFlow<UserAccount?> = combine(
        repository.activeUserId,
        allUsers
    ) { activeId, users ->
        if (activeId != null) {
            users.find { it.id == activeId }
        } else {
            null
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Transactions specific to the currently logged in student
    val studentTransactions: StateFlow<List<WasteTransaction>> = combine(
        activeUser,
        allTransactions
    ) { user, transactions ->
        if (user != null) {
            transactions.filter { it.userId == user.id }
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Deposit State
    private val _depositState = MutableStateFlow(DepositFormState())
    val depositState: StateFlow<DepositFormState> = _depositState.asStateFlow()

    // Rekapitulasi Period
    private val _selectedPeriod = MutableStateFlow(PeriodFilter.WEEKLY)
    val selectedPeriod: StateFlow<PeriodFilter> = _selectedPeriod.asStateFlow()

    // Leaderboard Grade Filter
    private val _leaderboardGradeFilter = MutableStateFlow<Int?>(null)
    val leaderboardGradeFilter: StateFlow<Int?> = _leaderboardGradeFilter.asStateFlow()

    // Notification Preferences
    private val _isNotificationEnabled = MutableStateFlow(true)
    val isNotificationEnabled: StateFlow<Boolean> = _isNotificationEnabled.asStateFlow()

    private val _userSelectedClass = MutableStateFlow("X A")
    val userSelectedClass: StateFlow<String> = _userSelectedClass.asStateFlow()

    // Rekapitulasi Summary
    val rekapSummary: StateFlow<RekapSummary> = combine(
        allTransactions,
        _selectedPeriod
    ) { transactions, period ->
        calculateRekap(transactions, period)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RekapSummary())

    // Leaderboard for 27 classes
    val classLeaderboard: StateFlow<List<ClassLeaderboardEntry>> = combine(
        allUsers,
        allTransactions,
        _leaderboardGradeFilter
    ) { users, transactions, gradeFilter ->
        computeClassLeaderboard(users, transactions, gradeFilter)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Collective Class Savings for all 27 classes (Image 1 historical + current member deposits)
    val allClassCollectiveSavings: StateFlow<List<ClassCollectiveSavings>> = combine(
        allUsers,
        allTransactions
    ) { users, transactions ->
        computeAllClassCollective(users, transactions)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Collective Class Savings for active student's class
    val myClassCollective: StateFlow<ClassCollectiveSavings?> = combine(
        activeUser,
        allClassCollectiveSavings
    ) { user, classes ->
        if (user != null) {
            val userClean = user.className.replace(" ", "").uppercase()
            classes.find { it.className.replace(" ", "").uppercase() == userClean }
        } else {
            null
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // All transactions belonging to the active student's class
    val myClassTransactions: StateFlow<List<WasteTransaction>> = combine(
        activeUser,
        allTransactions
    ) { user, transactions ->
        if (user != null) {
            val userClean = user.className.replace(" ", "").uppercase()
            transactions.filter { it.userClass.replace(" ", "").uppercase() == userClean }
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Student's specific schedule: class schedule and teacher mentor (Student only sees their own class)
    val studentDepositSchedule: StateFlow<Man1Class?> = activeUser.map { user ->
        user?.let { Man1ClassDirectory.getClassByName(it.className) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val studentMentoringSchedule: StateFlow<MentoringSchedule?> = activeUser.map { user ->
        user?.let { MentoringScheduleCatalog.getUpcomingScheduleForClass(it.className) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        // Restore login session from SharedPreferences
        val savedRole = prefs.getString("SAVED_ROLE", AuthRole.NONE.name) ?: AuthRole.NONE.name
        val savedUserId = prefs.getString("SAVED_STUDENT_ID", null)

        when (savedRole) {
            AuthRole.ADMIN.name -> {
                _authRole.value = AuthRole.ADMIN
            }
            AuthRole.STUDENT.name -> {
                if (!savedUserId.isNullOrBlank()) {
                    repository.setActiveUser(savedUserId)
                    _authRole.value = AuthRole.STUDENT
                } else {
                    _authRole.value = AuthRole.NONE
                }
            }
            else -> {
                _authRole.value = AuthRole.NONE
            }
        }
    }

    // === Authentication Operations ===

    fun loginAsAdmin(usernameInput: String, passwordInput: String): Result<Unit> {
        val cleanUser = usernameInput.trim()
        val cleanPass = passwordInput.trim()

        return if (cleanUser == adminUsernameExpected && cleanPass == adminPasswordExpected) {
            _authRole.value = AuthRole.ADMIN
            prefs.edit().putString("SAVED_ROLE", AuthRole.ADMIN.name).apply()
            Result.success(Unit)
        } else {
            Result.failure(Exception("Username atau password admin salah. Hanya pengurus Bank Bestari yang berwenang."))
        }
    }

    fun registerStudent(name: String, className: String, phoneNumber: String, onComplete: (Boolean, String) -> Unit) {
        if (name.isBlank() || phoneNumber.isBlank()) {
            onComplete(false, "Nama lengkap dan nomor telepon wajib diisi!")
            return
        }

        viewModelScope.launch {
            val existing = repository.getUserByPhone(phoneNumber.trim())
            if (existing != null) {
                // Already registered with this phone number, log in as this user
                repository.setActiveUser(existing.id)
                _authRole.value = AuthRole.STUDENT
                _userSelectedClass.value = existing.className
                prefs.edit()
                    .putString("SAVED_ROLE", AuthRole.STUDENT.name)
                    .putString("SAVED_STUDENT_ID", existing.id)
                    .apply()
                onComplete(true, "Selamat datang kembali, ${existing.name}!")
            } else {
                val newUser = repository.registerUser(name, className, phoneNumber)
                repository.setActiveUser(newUser.id)
                _authRole.value = AuthRole.STUDENT
                _userSelectedClass.value = newUser.className
                prefs.edit()
                    .putString("SAVED_ROLE", AuthRole.STUDENT.name)
                    .putString("SAVED_STUDENT_ID", newUser.id)
                    .apply()
                onComplete(true, "Akun ${newUser.name} berhasil dibuat!")
            }
        }
    }

    fun loginStudentByPhone(phoneNumber: String, onComplete: (Boolean, String) -> Unit) {
        if (phoneNumber.isBlank()) {
            onComplete(false, "Masukkan nomor telepon yang terdaftar!")
            return
        }

        viewModelScope.launch {
            val existing = repository.getUserByPhone(phoneNumber.trim())
            if (existing != null) {
                repository.setActiveUser(existing.id)
                _authRole.value = AuthRole.STUDENT
                _userSelectedClass.value = existing.className
                prefs.edit()
                    .putString("SAVED_ROLE", AuthRole.STUDENT.name)
                    .putString("SAVED_STUDENT_ID", existing.id)
                    .apply()
                onComplete(true, "Login berhasil sebagai ${existing.name} (${existing.className})")
            } else {
                onComplete(false, "Nomor telepon belum terdaftar di Bank Bestari. Silakan daftar baru!")
            }
        }
    }

    fun logout() {
        _authRole.value = AuthRole.NONE
        repository.setActiveUser(null)
        prefs.edit().clear().apply()
        _depositState.value = DepositFormState()
    }

    fun setPeriod(period: PeriodFilter) {
        _selectedPeriod.value = period
    }

    fun setLeaderboardGradeFilter(grade: Int?) {
        _leaderboardGradeFilter.value = grade
    }

    fun toggleNotification(enabled: Boolean) {
        _isNotificationEnabled.value = enabled
    }

    fun setUserSelectedClass(className: String) {
        _userSelectedClass.value = className
    }

    fun sendTestScheduleNotification(context: Context) {
        val targetClass = activeUser.value?.className ?: _userSelectedClass.value
        NotificationHelper.sendScheduleNotification(
            context = context,
            targetClass = targetClass
        )
    }

    // === Admin QR Scanning & Deposit Form ===

    fun handleScannedQr(payload: String) {
        val parseResult = UserAccount.fromQrPayload(payload)
        val users = allUsers.value

        when (parseResult) {
            is QrParseResult.Success -> {
                val existing = users.find { it.id == parseResult.id }
                if (existing != null) {
                    setScannedUser(existing)
                } else {
                    // Try by phone number or ID directly
                    viewModelScope.launch {
                        val byPhone = repository.getUserByPhone(parseResult.phoneNumber)
                        if (byPhone != null) {
                            setScannedUser(byPhone)
                        } else {
                            val newUser = repository.registerUser(
                                name = parseResult.name,
                                className = parseResult.className,
                                phoneNumber = parseResult.phoneNumber
                            )
                            setScannedUser(newUser)
                        }
                    }
                }
            }
            is QrParseResult.IdOnly -> {
                val existing = users.find { it.id.equals(parseResult.id, ignoreCase = true) }
                if (existing != null) {
                    setScannedUser(existing)
                } else {
                    _depositState.update {
                        it.copy(errorMessage = "ID Nasabah '${parseResult.id}' tidak terdaftar di database.")
                    }
                }
            }
            QrParseResult.Invalid -> {
                _depositState.update {
                    it.copy(errorMessage = "Kode QR tidak valid atau bukan format kartu Bank Bestari.")
                }
            }
        }
    }

    fun setScannedUser(user: UserAccount) {
        _depositState.update {
            it.copy(
                scannedUser = user,
                errorMessage = null,
                submissionResult = null
            )
        }
        recalculateDeposit()
    }

    fun clearScannedUser() {
        _depositState.update {
            it.copy(
                scannedUser = null,
                submissionResult = null,
                errorMessage = null
            )
        }
    }

    fun setDepositCommodity(commodity: WasteCommodity) {
        _depositState.update { it.copy(selectedCommodity = commodity) }
        recalculateDeposit()
    }

    fun setDepositQuantity(quantityStr: String) {
        _depositState.update { it.copy(quantityInput = quantityStr) }
        recalculateDeposit()
    }

    fun addDepositQuantity(delta: Double) {
        val current = _depositState.value.quantityInput.toDoubleOrNull() ?: 0.0
        val newQty = (current + delta).coerceAtLeast(0.1)
        val formatted = if (newQty % 1.0 == 0.0) {
            newQty.toInt().toString()
        } else {
            String.format(java.util.Locale.US, "%.1f", newQty)
        }
        _depositState.update { it.copy(quantityInput = formatted) }
        recalculateDeposit()
    }

    fun setDepositNotes(notes: String) {
        _depositState.update { it.copy(notes = notes) }
    }

    private fun recalculateDeposit() {
        val state = _depositState.value
        val qty = state.quantityInput.toDoubleOrNull() ?: 0.0
        val amount = Math.round(qty * state.selectedCommodity.priceBuyCustomer)
        val points = (qty * 10).toInt().coerceAtLeast(1)
        _depositState.update {
            it.copy(
                calculatedAmount = amount,
                calculatedPoints = points
            )
        }
    }

    fun submitDeposit() {
        val state = _depositState.value
        val user = state.scannedUser
        if (user == null) {
            _depositState.update { it.copy(errorMessage = "Scan QR atau pilih nasabah terlebih dahulu!") }
            return
        }

        val qty = state.quantityInput.toDoubleOrNull()
        if (qty == null || qty <= 0.0) {
            _depositState.update { it.copy(errorMessage = "Jumlah / berat harus lebih dari 0!") }
            return
        }

        _depositState.update { it.copy(isSubmitting = true, errorMessage = null) }

        viewModelScope.launch {
            val result = repository.submitDeposit(
                userId = user.id,
                commodity = state.selectedCommodity,
                quantity = qty,
                notes = state.notes
            )

            result.fold(
                onSuccess = { trx ->
                    _depositState.update {
                        it.copy(
                            isSubmitting = false,
                            submissionResult = trx,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { error ->
                    _depositState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = error.message ?: "Gagal memproses setoran"
                        )
                    }
                }
            )
        }
    }

    fun dismissDepositResult() {
        _depositState.update {
            it.copy(
                submissionResult = null,
                quantityInput = "1.0",
                notes = ""
            )
        }
        recalculateDeposit()
    }

    fun addEcoPoints(userId: String, points: Int) {
        viewModelScope.launch {
            repository.addEcoPoints(userId, points)
        }
    }

    private fun calculateRekap(
        transactions: List<WasteTransaction>,
        period: PeriodFilter
    ): RekapSummary {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()

        val filtered = transactions.filter { trx ->
            when (period) {
                PeriodFilter.DAILY -> {
                    calendar.timeInMillis = now
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    trx.timestamp >= calendar.timeInMillis
                }
                PeriodFilter.WEEKLY -> {
                    val oneWeekAgo = now - (7 * 24 * 60 * 60 * 1000L)
                    trx.timestamp >= oneWeekAgo
                }
                PeriodFilter.MONTHLY -> {
                    val oneMonthAgo = now - (30 * 24 * 60 * 60 * 1000L)
                    trx.timestamp >= oneMonthAgo
                }
                PeriodFilter.ALL_TIME -> true
            }
        }

        var totalWeight = 0.0
        var totalAmount = 0L
        var plasticW = 0.0
        var paperW = 0.0
        var metalW = 0.0
        var glassW = 0.0
        var otherW = 0.0

        val classWeightMap = mutableMapOf<String, Double>()

        filtered.forEach { trx ->
            val w = if (trx.unit == "kg") trx.quantity else (trx.quantity * 0.1)
            totalWeight += w
            totalAmount += trx.totalAmount
            classWeightMap[trx.userClass] = (classWeightMap[trx.userClass] ?: 0.0) + w

            when (trx.category.uppercase()) {
                "PLASTIK" -> plasticW += w
                "KERTAS" -> paperW += w
                "LOGAM" -> metalW += w
                "KACA/BOTOL" -> glassW += w
                else -> otherW += w
            }
        }

        val topClass = classWeightMap.maxByOrNull { it.value }?.key ?: "-"
        val plasticPct = if (totalWeight > 0) (plasticW / totalWeight).toFloat() else 0f

        return RekapSummary(
            totalWeightKg = Math.round(totalWeight * 10.0) / 10.0,
            totalAmountRupiah = totalAmount,
            totalTransactionCount = filtered.size,
            plasticWeightKg = Math.round(plasticW * 10.0) / 10.0,
            paperWeightKg = Math.round(paperW * 10.0) / 10.0,
            metalWeightKg = Math.round(metalW * 10.0) / 10.0,
            glassWeightKg = Math.round(glassW * 10.0) / 10.0,
            otherWeightKg = Math.round(otherW * 10.0) / 10.0,
            topClassName = topClass,
            plasticPercentage = plasticPct
        )
    }

    private fun computeClassLeaderboard(
        users: List<UserAccount>,
        transactions: List<WasteTransaction>,
        gradeFilter: Int?
    ): List<ClassLeaderboardEntry> {
        val classMap = mutableMapOf<String, MutableClassStats>()

        Man1ClassDirectory.all27Classes.forEach { mClass ->
            classMap[mClass.name] = MutableClassStats(
                className = mClass.name,
                grade = mClass.grade,
                totalWeight = 0.0,
                totalPoints = 0,
                totalEarnings = 0L,
                members = mutableSetOf()
            )
        }

        users.forEach { user ->
            val stats = classMap[user.className] ?: MutableClassStats(
                className = user.className,
                grade = if (user.className.startsWith("X ")) 10 else if (user.className.startsWith("XI ")) 11 else 12,
                totalWeight = 0.0,
                totalPoints = 0,
                totalEarnings = 0L,
                members = mutableSetOf()
            )
            stats.totalWeight += user.totalWeightKg
            stats.totalPoints += user.ecoPoints
            stats.totalEarnings += user.balance
            stats.members.add(user.id)
            classMap[user.className] = stats
        }

        return classMap.values
            .filter { stats -> gradeFilter == null || stats.grade == gradeFilter }
            .sortedByDescending { it.totalWeight }
            .mapIndexed { index, stats ->
                val badge = when (index) {
                    0 -> "Juara 1 Adiwiyata"
                    1 -> "Juara 2 Pilah Hebat"
                    2 -> "Juara 3 Eco Hero"
                    in 3..5 -> "Top Peduli Lingkungan"
                    else -> "Pejuang Bestari"
                }
                ClassLeaderboardEntry(
                    rank = index + 1,
                    className = stats.className,
                    grade = stats.grade,
                    totalWeightKg = Math.round(stats.totalWeight * 10.0) / 10.0,
                    totalPoints = stats.totalPoints,
                    totalEarnings = stats.totalEarnings,
                    activeMemberCount = stats.members.size,
                    badgeName = badge
                )
            }
    }

    private fun computeAllClassCollective(
        users: List<UserAccount>,
        transactions: List<WasteTransaction>
    ): List<ClassCollectiveSavings> {
        return Man1ClassDirectory.all27Classes.map { mClass ->
            val hist = ClassHistoricalDirectory.getRecordByClass(mClass.name)
            val histWeight = hist?.totalWeightKg ?: 0.0
            val histAmount = hist?.totalAmountRupiah ?: 0.0
            val histItems = hist?.items ?: emptyList()

            val classClean = mClass.name.replace(" ", "").uppercase()
            val classUsers = users.filter { it.className.replace(" ", "").uppercase() == classClean }
            val classTrx = transactions.filter { it.userClass.replace(" ", "").uppercase() == classClean }

            var curWeight = 0.0
            var curAmount = 0L

            classTrx.forEach { trx ->
                val w = if (trx.unit == "kg") trx.quantity else (trx.quantity * 0.1)
                curWeight += w
                curAmount += trx.totalAmount
            }

            val totalWeight = Math.round((histWeight + curWeight) * 100.0) / 100.0
            val totalAmount = histAmount + curAmount

            ClassCollectiveSavings(
                className = mClass.name,
                grade = mClass.grade,
                scheduleDay = mClass.scheduleDay,
                historicalWeightKg = histWeight,
                historicalAmountRupiah = histAmount,
                currentMemberWeightKg = Math.round(curWeight * 100.0) / 100.0,
                currentMemberAmountRupiah = curAmount,
                totalWeightKg = totalWeight,
                totalAmountRupiah = totalAmount,
                memberCount = classUsers.size,
                recentDepositsCount = classTrx.size,
                historicalBreakdown = histItems
            )
        }
    }

    fun updateStudentProfile(
        name: String,
        className: String,
        phoneNumber: String,
        nis: String,
        address: String,
        bio: String,
        onComplete: (Boolean, String) -> Unit
    ) {
        val user = activeUser.value
        if (user == null) {
            onComplete(false, "Sesi akun siswa tidak aktif.")
            return
        }
        if (name.isBlank()) {
            onComplete(false, "Nama lengkap siswa wajib diisi!")
            return
        }

        viewModelScope.launch {
            val result = repository.updateUserProfile(
                userId = user.id,
                name = name,
                className = className,
                phoneNumber = phoneNumber,
                nis = nis,
                address = address,
                bio = bio
            )
            result.fold(
                onSuccess = { updated ->
                    _userSelectedClass.value = updated.className
                    onComplete(true, "Data profil & biodata ${updated.name} berhasil disimpan!")
                },
                onFailure = { error ->
                    onComplete(false, error.message ?: "Gagal memperbarui profil.")
                }
            )
        }
    }

    fun sendHMinusOneNotification(context: Context) {
        val user = activeUser.value
        val className = user?.className ?: _userSelectedClass.value
        val schedule = Man1ClassDirectory.getClassByName(className)
        val day = schedule?.scheduleDay ?: "Senin"
        val mentor = MentoringScheduleCatalog.getUpcomingScheduleForClass(className)?.mentors ?: "Tim Guru Pendamping"
        NotificationHelper.sendHMinusOneReminder(context, className, day, mentor)
    }

    fun sendDayOfNotification(context: Context) {
        val user = activeUser.value
        val className = user?.className ?: _userSelectedClass.value
        val schedule = Man1ClassDirectory.getClassByName(className)
        val day = schedule?.scheduleDay ?: "Senin"
        val mentor = MentoringScheduleCatalog.getUpcomingScheduleForClass(className)?.mentors ?: "Tim Guru Pendamping"
        NotificationHelper.sendDayOfReminder(context, className, day, mentor)
    }

    private data class MutableClassStats(
        val className: String,
        val grade: Int,
        var totalWeight: Double,
        var totalPoints: Int,
        var totalEarnings: Long,
        val members: MutableSet<String>
    )

    companion object {
        fun provideFactory(
            application: Application,
            repository: BankBestariRepository
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BankBestariViewModel(application, repository) as T
                }
            }
        }
    }
}
