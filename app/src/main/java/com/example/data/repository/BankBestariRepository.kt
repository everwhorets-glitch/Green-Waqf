package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.UserAccount
import com.example.data.model.WasteCommodity
import com.example.data.model.WasteTransaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BankBestariRepository(
    private val database: AppDatabase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val userDao = database.userDao()
    private val transactionDao = database.transactionDao()

    val allUsers: Flow<List<UserAccount>> = userDao.getAllUsers()
    val allTransactions: Flow<List<WasteTransaction>> = transactionDao.getAllTransactions()

    private val _activeUserId = MutableStateFlow<String?>(null)
    val activeUserId: StateFlow<String?> = _activeUserId.asStateFlow()

    fun setActiveUser(userId: String?) {
        _activeUserId.value = userId
    }

    fun getUserById(id: String): Flow<UserAccount?> = userDao.getUserById(id)

    suspend fun getUserByIdDirect(id: String): UserAccount? = withContext(Dispatchers.IO) {
        userDao.getUserByIdDirect(id)
    }

    suspend fun getUserByPhone(phone: String): UserAccount? = withContext(Dispatchers.IO) {
        userDao.getUserByPhoneDirect(phone.trim())
    }

    fun getTransactionsForUser(userId: String): Flow<List<WasteTransaction>> =
        transactionDao.getTransactionsForUser(userId)

    suspend fun registerUser(name: String, className: String, phoneNumber: String): UserAccount =
        withContext(Dispatchers.IO) {
            val classSlug = className.replace(" ", "-")
            val randCode = String.format("%04d", (System.currentTimeMillis() % 10000).toInt())
            val newId = "BST-MAN1-$classSlug-$randCode"

            val newUser = UserAccount(
                id = newId,
                name = name.trim(),
                className = className,
                phoneNumber = phoneNumber.trim(),
                nis = "",
                balance = 0L,
                totalWeightKg = 0.0,
                ecoPoints = 10, // Poin awal pembukaan rekening
                isClassRepresentative = false
            )
            userDao.insertUser(newUser)
            _activeUserId.value = newId
            newUser
        }

    suspend fun updateUserProfile(
        userId: String,
        name: String,
        className: String,
        phoneNumber: String,
        nis: String,
        address: String,
        bio: String
    ): Result<UserAccount> = withContext(Dispatchers.IO) {
        val existing = userDao.getUserByIdDirect(userId)
            ?: return@withContext Result.failure(Exception("Akun tidak ditemukan"))

        val updated = existing.copy(
            name = name.trim(),
            className = className.trim(),
            phoneNumber = phoneNumber.trim(),
            nis = nis.trim(),
            address = address.trim(),
            bio = bio.trim()
        )
        userDao.updateUser(updated)
        Result.success(updated)
    }

    suspend fun submitDeposit(
        userId: String,
        commodity: WasteCommodity,
        quantity: Double,
        notes: String = ""
    ): Result<WasteTransaction> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByIdDirect(userId)
            ?: return@withContext Result.failure(Exception("Akun nasabah dengan ID $userId tidak ditemukan"))

        val totalAmount = Math.round(quantity * commodity.priceBuyCustomer)
        val pointsEarned = (quantity * 10).toInt().coerceAtLeast(1)

        val dateFormat = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault())
        val receiptCode = "BST-${dateFormat.format(Date())}"

        val transaction = WasteTransaction(
            receiptCode = receiptCode,
            userId = user.id,
            userName = user.name,
            userClass = user.className,
            commodityCode = commodity.code,
            commodityName = commodity.name,
            category = commodity.category,
            unit = commodity.unit,
            quantity = quantity,
            priceSellBsi = commodity.priceSellBsi,
            priceBuyCustomer = commodity.priceBuyCustomer,
            totalAmount = totalAmount,
            pointsEarned = pointsEarned,
            timestamp = System.currentTimeMillis(),
            notes = notes.ifBlank { "Setoran ${commodity.name} ($quantity ${commodity.unit})" }
        )

        transactionDao.insertTransaction(transaction)
        userDao.addDeposit(
            userId = user.id,
            addedBalance = totalAmount,
            addedWeight = if (commodity.unit == "kg") quantity else (quantity * 0.1), // estimasi berat kg untuk total stat
            addedPoints = pointsEarned
        )

        Result.success(transaction)
    }

    suspend fun addEcoPoints(userId: String, points: Int) = withContext(Dispatchers.IO) {
        userDao.addEcoPoints(userId, points)
    }
}
