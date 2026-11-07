package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.viewmodel.BankBestariViewModel

@Composable
fun DepositScreen(
    viewModel: BankBestariViewModel,
    modifier: Modifier = Modifier
) {
    AdminScreen(viewModel = viewModel, modifier = modifier)
}
