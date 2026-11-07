package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.UserAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_accounts ORDER BY balance DESC, totalWeightKg DESC")
    fun getAllUsers(): Flow<List<UserAccount>>

    @Query("SELECT * FROM user_accounts WHERE id = :id LIMIT 1")
    fun getUserById(id: String): Flow<UserAccount?>

    @Query("SELECT * FROM user_accounts WHERE id = :id LIMIT 1")
    suspend fun getUserByIdDirect(id: String): UserAccount?

    @Query("SELECT * FROM user_accounts WHERE phoneNumber = :phone LIMIT 1")
    suspend fun getUserByPhoneDirect(phone: String): UserAccount?

    @Query("SELECT * FROM user_accounts WHERE className = :className ORDER BY name ASC")
    fun getUsersByClass(className: String): Flow<List<UserAccount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccount)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserAccount>)

    @Update
    suspend fun updateUser(user: UserAccount)

    @Query("UPDATE user_accounts SET balance = balance + :addedBalance, totalWeightKg = totalWeightKg + :addedWeight, ecoPoints = ecoPoints + :addedPoints WHERE id = :userId")
    suspend fun addDeposit(userId: String, addedBalance: Long, addedWeight: Double, addedPoints: Int)

    @Query("UPDATE user_accounts SET ecoPoints = ecoPoints + :bonusPoints WHERE id = :userId")
    suspend fun addEcoPoints(userId: String, bonusPoints: Int)

    @Query("SELECT COUNT(*) FROM user_accounts")
    suspend fun getUserCount(): Int
}
