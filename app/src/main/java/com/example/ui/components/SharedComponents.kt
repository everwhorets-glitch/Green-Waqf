package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WasteCommodityCatalog
import com.example.data.model.WasteTransaction
import com.example.ui.theme.*
import com.example.viewmodel.PeriodFilter
import com.example.viewmodel.RekapSummary
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SegmentButton(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) Color.White else Color.Transparent,
        shadowElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) BestariGreenPrimary else BestariOnSurfaceVariant
            )
        }
    }
}

@Composable
fun QuickWeightChip(label: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = BestariSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, BestariOutline),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = BestariOnSurface,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun ReceiptRow(label: String, value: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (isHighlight) BestariGreenDark else BestariOnSurfaceVariant,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = if (isHighlight) BestariGreenPrimary else BestariOnSurface,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.SemiBold
        )
    }
}

@Composable
fun RecentTransactionItem(trx: WasteTransaction) {
    val commodity = remember(trx.commodityCode) {
        WasteCommodityCatalog.findByCode(trx.commodityCode) ?: WasteCommodityCatalog.defaultCommodity()
    }
    val formattedDate = remember(trx.timestamp) {
        val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID"))
        sdf.format(Date(trx.timestamp))
    }
    val formattedAmount = remember(trx.totalAmount) {
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        formatter.maximumFractionDigits = 0
        formatter.format(trx.totalAmount)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = BestariSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BestariOutlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(commodity.bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Recycling,
                        contentDescription = commodity.name,
                        tint = commodity.accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "${trx.userName} (${trx.userClass})",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = BestariOnSurface,
                        maxLines = 1
                    )
                    Text(
                        text = "${trx.commodityName} • ${trx.quantity} ${trx.unit} • $formattedDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = BestariOnSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "+$formattedAmount",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = BestariGreenPrimary
                )
                Text(
                    text = "+${trx.pointsEarned} pts",
                    style = MaterialTheme.typography.labelSmall,
                    color = LeaderboardGold,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

fun buildReportString(summary: RekapSummary, period: PeriodFilter): String {
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    return """
        *LAPORAN REKAPITULASI BANK BESTARI*
        *MAN 1 YOGYAKARTA*
        Periode: ${period.label}
        ----------------------------------
        • Total Sampah: ${summary.totalWeightKg} kg
        • Total Tabungan Nasabah: Rp ${formatter.format(summary.totalAmountRupiah)}
        • Frekuensi Transaksi: ${summary.totalTransactionCount} kali
        • Kelas Teraktif: ${summary.topClassName}
        ----------------------------------
        _Madrasah Adiwiyata Mandiri MAN 1 Yogyakarta_
    """.trimIndent()
}
