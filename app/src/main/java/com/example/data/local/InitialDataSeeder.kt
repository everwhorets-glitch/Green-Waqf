package com.example.data.local

import com.example.data.model.UserAccount
import com.example.data.model.WasteTransaction

object InitialDataSeeder {
    // Database starts completely clean with zero accounts as requested by user
    fun seedUsers(): List<UserAccount> = emptyList()
    fun seedTransactions(users: List<UserAccount>): List<WasteTransaction> = emptyList()
}
