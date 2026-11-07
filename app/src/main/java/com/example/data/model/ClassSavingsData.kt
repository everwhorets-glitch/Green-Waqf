package com.example.data.model

data class ClassHistoricalItem(
    val commodityName: String,
    val commodityCode: String,
    val weightKg: Double,
    val rupiahAmount: Double
)

data class ClassHistoricalRecord(
    val no: Int,
    val className: String,
    val items: List<ClassHistoricalItem>,
    val totalWeightKg: Double,
    val totalAmountRupiah: Double
)

data class ClassCollectiveSavings(
    val className: String,
    val grade: Int,
    val scheduleDay: String,
    val historicalWeightKg: Double,
    val historicalAmountRupiah: Double,
    val currentMemberWeightKg: Double,
    val currentMemberAmountRupiah: Long,
    val totalWeightKg: Double,
    val totalAmountRupiah: Double,
    val memberCount: Int,
    val recentDepositsCount: Int,
    val historicalBreakdown: List<ClassHistoricalItem>
)

object ClassHistoricalDirectory {
    // Exact transcription from Image 1 (Tabel Setoran Bulan Kemarin per 27 Kelas MAN 1 Yogyakarta)
    val records: List<ClassHistoricalRecord> = listOf(
        ClassHistoricalRecord(
            no = 1,
            className = "X PK 1",
            items = listOf(
                ClassHistoricalItem("Arsip 2", "A-02", 5.725, 6412.0)
            ),
            totalWeightKg = 5.725,
            totalAmountRupiah = 6412.00
        ),
        ClassHistoricalRecord(
            no = 2,
            className = "X PK 2",
            items = listOf(
                ClassHistoricalItem("Botol bersih", "B-01", 0.57, 1596.0),
                ClassHistoricalItem("Botol bodong", "B-02", 0.88, 1408.0)
            ),
            totalWeightKg = 1.45,
            totalAmountRupiah = 3004.00
        ),
        ClassHistoricalRecord(
            no = 3,
            className = "X A",
            items = listOf(
                ClassHistoricalItem("Botol bersih", "B-01", 2.62, 7336.0),
                ClassHistoricalItem("Botol bodong", "B-02", 0.845, 1352.0),
                ClassHistoricalItem("Tutup botol", "B-19", 0.05, 100.0),
                ClassHistoricalItem("Kardus", "A-07", 0.705, 902.40)
            ),
            totalWeightKg = 4.22,
            totalAmountRupiah = 9690.40
        ),
        ClassHistoricalRecord(
            no = 4,
            className = "X B",
            items = emptyList(),
            totalWeightKg = 0.0,
            totalAmountRupiah = 0.0
        ),
        ClassHistoricalRecord(
            no = 5,
            className = "X C",
            items = listOf(
                ClassHistoricalItem("Botol bersih", "B-01", 1.585, 4438.0),
                ClassHistoricalItem("Tutup botol", "B-19", 0.095, 190.0),
                ClassHistoricalItem("Arsip 2", "A-02", 0.185, 207.20),
                ClassHistoricalItem("Duplek", "A-05", 0.04, 19.20)
            ),
            totalWeightKg = 1.905,
            totalAmountRupiah = 4854.40
        ),
        ClassHistoricalRecord(
            no = 6,
            className = "X D",
            items = listOf(
                ClassHistoricalItem("Botol bersih", "B-01", 1.03, 2884.0),
                ClassHistoricalItem("Gelas bersih", "B-03", 0.901, 1946.16),
                ClassHistoricalItem("Arsip 1", "A-01", 1.65, 2376.0)
            ),
            totalWeightKg = 3.581,
            totalAmountRupiah = 7206.16
        ),
        ClassHistoricalRecord(
            no = 7,
            className = "X E",
            items = emptyList(),
            totalWeightKg = 0.0,
            totalAmountRupiah = 0.0
        ),
        ClassHistoricalRecord(
            no = 8,
            className = "X F",
            items = emptyList(),
            totalWeightKg = 0.0,
            totalAmountRupiah = 0.0
        ),
        ClassHistoricalRecord(
            no = 9,
            className = "X G",
            items = listOf(
                ClassHistoricalItem("Botol bersih", "B-01", 3.45, 9660.0),
                ClassHistoricalItem("Gelas bersih", "B-03", 0.365, 788.40)
            ),
            totalWeightKg = 3.815,
            totalAmountRupiah = 10448.40
        ),
        ClassHistoricalRecord(
            no = 10,
            className = "XI PK 1",
            items = emptyList(),
            totalWeightKg = 0.0,
            totalAmountRupiah = 0.0
        ),
        ClassHistoricalRecord(
            no = 11,
            className = "XI PK 2",
            items = listOf(
                ClassHistoricalItem("Botol bodong", "B-02", 2.145, 3432.0),
                ClassHistoricalItem("Gelas bersih", "B-03", 0.325, 702.0)
            ),
            totalWeightKg = 2.47,
            totalAmountRupiah = 4134.00
        ),
        ClassHistoricalRecord(
            no = 12,
            className = "XI A",
            items = emptyList(),
            totalWeightKg = 0.0,
            totalAmountRupiah = 0.0
        ),
        ClassHistoricalRecord(
            no = 13,
            className = "XI B",
            items = emptyList(),
            totalWeightKg = 0.0,
            totalAmountRupiah = 0.0
        ),
        ClassHistoricalRecord(
            no = 14,
            className = "XI C",
            items = listOf(
                ClassHistoricalItem("Botol bodong", "B-02", 1.615, 2584.0)
            ),
            totalWeightKg = 1.615,
            totalAmountRupiah = 2584.00
        ),
        ClassHistoricalRecord(
            no = 15,
            className = "XI D",
            items = listOf(
                ClassHistoricalItem("Botol bersih", "B-01", 0.725, 2030.0),
                ClassHistoricalItem("Gelas bersih", "B-03", 0.165, 356.40),
                ClassHistoricalItem("Tutup botol", "B-19", 0.065, 130.0),
                ClassHistoricalItem("Arsip 1", "A-01", 1.06, 1526.40)
            ),
            totalWeightKg = 2.015,
            totalAmountRupiah = 4042.80
        ),
        ClassHistoricalRecord(
            no = 16,
            className = "XI E",
            items = listOf(
                ClassHistoricalItem("Botol bersih", "B-01", 0.92, 2576.0)
            ),
            totalWeightKg = 0.92,
            totalAmountRupiah = 2576.00
        ),
        ClassHistoricalRecord(
            no = 17,
            className = "XI F",
            items = listOf(
                ClassHistoricalItem("Botol bodong", "B-02", 1.07, 1712.0),
                ClassHistoricalItem("Gelas bersih", "B-03", 0.025, 54.0),
                ClassHistoricalItem("Arsip 1", "A-01", 0.21, 302.40),
                ClassHistoricalItem("Duplek", "A-05", 0.075, 36.0)
            ),
            totalWeightKg = 1.38,
            totalAmountRupiah = 2104.40
        ),
        ClassHistoricalRecord(
            no = 18,
            className = "XI G",
            items = listOf(
                ClassHistoricalItem("Botol bersih", "B-01", 0.755, 2114.0),
                ClassHistoricalItem("Arsip 1", "A-01", 0.735, 1058.40)
            ),
            totalWeightKg = 1.49,
            totalAmountRupiah = 3172.40
        ),
        ClassHistoricalRecord(
            no = 19,
            className = "XII PK 1",
            items = emptyList(),
            totalWeightKg = 0.0,
            totalAmountRupiah = 0.0
        ),
        ClassHistoricalRecord(
            no = 20,
            className = "XII PK 2",
            items = listOf(
                ClassHistoricalItem("Botol bersih", "B-01", 2.6, 7280.0),
                ClassHistoricalItem("Botol bodong", "B-02", 0.66, 1056.0),
                ClassHistoricalItem("Gelas bersih", "B-03", 0.905, 1954.80)
            ),
            totalWeightKg = 4.165,
            totalAmountRupiah = 10290.80
        ),
        ClassHistoricalRecord(
            no = 21,
            className = "XII A",
            items = listOf(
                ClassHistoricalItem("Botol bersih", "B-01", 0.98, 2744.0),
                ClassHistoricalItem("Tutup botol", "B-19", 0.09, 180.0)
            ),
            totalWeightKg = 1.07,
            totalAmountRupiah = 2924.00
        ),
        ClassHistoricalRecord(
            no = 22,
            className = "XII B",
            items = emptyList(),
            totalWeightKg = 0.0,
            totalAmountRupiah = 0.0
        ),
        ClassHistoricalRecord(
            no = 23,
            className = "XII C",
            items = emptyList(),
            totalWeightKg = 0.0,
            totalAmountRupiah = 0.0
        ),
        ClassHistoricalRecord(
            no = 24,
            className = "XII D",
            items = emptyList(),
            totalWeightKg = 0.0,
            totalAmountRupiah = 0.0
        ),
        ClassHistoricalRecord(
            no = 25,
            className = "XII E",
            items = listOf(
                ClassHistoricalItem("Botol bersih", "B-01", 1.76, 4928.0),
                ClassHistoricalItem("Botol bodong", "B-02", 1.31, 2096.0),
                ClassHistoricalItem("Tutup botol", "B-19", 0.19, 380.0),
                ClassHistoricalItem("Arsip 2", "A-02", 0.19, 212.80),
                ClassHistoricalItem("Duplek", "A-05", 0.02, 9.60)
            ),
            totalWeightKg = 3.47,
            totalAmountRupiah = 7626.40
        ),
        ClassHistoricalRecord(
            no = 26,
            className = "XII F",
            items = listOf(
                ClassHistoricalItem("Botol bersih", "B-01", 1.17, 3276.0)
            ),
            totalWeightKg = 1.17,
            totalAmountRupiah = 3276.00
        ),
        ClassHistoricalRecord(
            no = 27,
            className = "XII G",
            items = listOf(
                ClassHistoricalItem("Botol bersih", "B-01", 0.785, 2198.0),
                ClassHistoricalItem("Botol bodong", "B-02", 0.41, 656.0),
                ClassHistoricalItem("Gelas bersih", "B-03", 0.495, 1069.20),
                ClassHistoricalItem("Tutup botol", "B-19", 0.07, 140.0),
                ClassHistoricalItem("Arsip 1", "A-01", 0.69, 993.60)
            ),
            totalWeightKg = 2.45,
            totalAmountRupiah = 5056.80
        )
    )

    fun getRecordByClass(className: String): ClassHistoricalRecord? {
        val clean = className.replace(" ", "").uppercase()
        return records.find { it.className.replace(" ", "").uppercase() == clean }
    }
}
