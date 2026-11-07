package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_accounts")
data class UserAccount(
    @PrimaryKey
    val id: String, // e.g. "BST-MAN1-X-A-001" - Encoded in QR Code
    val name: String,
    val className: String, // e.g. "X A", "XI PK 1"
    val phoneNumber: String = "",
    val nis: String = "",
    val address: String = "",
    val bio: String = "",
    val balance: Long = 0L, // Saldo tabungan rupiah
    val totalWeightKg: Double = 0.0, // Total kg sampah yang disetor
    val ecoPoints: Int = 0, // Poin kebersihan/keaktifan
    val isClassRepresentative: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toQrPayload(): String {
        return "$id|$name|$className|$phoneNumber"
    }

    companion object {
        fun fromQrPayload(payload: String): QrParseResult {
            val parts = payload.split("|")
            return if (parts.size >= 4) {
                QrParseResult.Success(
                    id = parts[0].trim(),
                    name = parts[1].trim(),
                    className = parts[2].trim(),
                    phoneNumber = parts[3].trim()
                )
            } else if (payload.startsWith("BST-MAN1-") || payload.length >= 5) {
                QrParseResult.IdOnly(payload.trim())
            } else {
                QrParseResult.Invalid
            }
        }
    }
}

sealed interface QrParseResult {
    data class Success(
        val id: String,
        val name: String,
        val className: String,
        val phoneNumber: String
    ) : QrParseResult
    data class IdOnly(val id: String) : QrParseResult
    object Invalid : QrParseResult
}
