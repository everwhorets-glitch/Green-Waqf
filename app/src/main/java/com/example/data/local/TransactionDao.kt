package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.WasteTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM waste_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<WasteTransaction>>

    @Query("SELECT * FROM waste_transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransactionsForUser(userId: String): Flow<List<WasteTransaction>>

    @Query("SELECT * FROM waste_transactions WHERE userClass = :className ORDER BY timestamp DESC")
    fun getTransactionsForClass(className: String): Flow<List<WasteTransaction>>

    @Query("SELECT * FROM waste_transactions WHERE timestamp >= :startTime ORDER BY timestamp DESC")
    fun getTransactionsSince(startTime: Long): Flow<List<WasteTransaction>>

    @Query("SELECT * FROM waste_transactions WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getTransactionsBetween(startTime: Long, endTime: Long): Flow<List<WasteTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: WasteTransaction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<WasteTransaction>)

    @Query("SELECT COUNT(*) FROM waste_transactions")
    suspend fun getTransactionCount(): Int
}
