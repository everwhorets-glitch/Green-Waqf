package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "waste_transactions")
data class WasteTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val receiptCode: String, // e.g. "BST-202611-001"
    val userId: String,
    val userName: String,
    val userClass: String,
    val commodityCode: String, // e.g. "B-01"
    val commodityName: String, // e.g. "Botol (PET/bodong) bersih"
    val category: String, // "PLASTIK", "KERTAS", dll.
    val unit: String, // "kg", "biji", "lembar", "liter"
    val quantity: Double, // berat kg atau jumlah biji/lembar/liter
    val priceSellBsi: Long, // Harga Jual ke BSI
    val priceBuyCustomer: Long, // Harga Beli yang diberikan ke nasabah
    val totalAmount: Long, // Rp = quantity * priceBuyCustomer
    val pointsEarned: Int, // Eco Points earned
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)
