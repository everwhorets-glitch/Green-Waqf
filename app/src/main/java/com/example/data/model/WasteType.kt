package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

data class WasteCommodity(
    val no: Int,
    val category: String, // KERTAS, PLASTIK, KACA/BOTOL, LOGAM, LAIN-LAIN
    val name: String,
    val code: String,
    val unit: String, // kg, biji, lembar, liter
    val priceSellBsi: Long, // Harga Jual (ke BSI)
    val priceBuyCustomer: Long, // Harga Beli (diberikan ke nasabah)
    val accentColor: Color = BestariGreenPrimary,
    val bgColor: Color = BestariMintContainer
)

object WasteCommodityCatalog {
    val items = listOf(
        // === KERTAS ===
        WasteCommodity(1, "KERTAS", "Arsip 1/ HVS/ A", "A-01", "kg", 1800, 1440, AccentPaper, AccentPaperBg),
        WasteCommodity(2, "KERTAS", "Arsip 2/ buku tanpa sampul / B", "A-02", "kg", 1400, 1120, AccentPaper, AccentPaperBg),
        WasteCommodity(3, "KERTAS", "Arsip 3 (buku bersampul)/ C", "A-03", "kg", 1000, 800, AccentPaper, AccentPaperBg),
        WasteCommodity(4, "KERTAS", "Buram", "A-04", "kg", 800, 640, AccentPaper, AccentPaperBg),
        WasteCommodity(5, "KERTAS", "Buram bersampul/ duplek/ koran rusak/ majalah/ kalender/ undangan/ sampul", "A-05", "kg", 600, 480, AccentPaper, AccentPaperBg),
        WasteCommodity(6, "KERTAS", "Duplek telur", "A-06", "kg", 100, 80, AccentPaper, AccentPaperBg),
        WasteCommodity(7, "KERTAS", "Kardus", "A-07", "kg", 1600, 1280, AccentPaper, AccentPaperBg),
        WasteCommodity(8, "KERTAS", "Koran KW1 (kondisi bagus)", "A-08", "kg", 4000, 3200, AccentPaper, AccentPaperBg),

        // === PLASTIK ===
        WasteCommodity(9, "PLASTIK", "Botol (PET/bodong) bersih (lepas tutup dan label)", "B-01", "kg", 3500, 2800, AccentPlastic, AccentPlasticBg),
        WasteCommodity(10, "PLASTIK", "Botol (PET/bodong) kotor", "B-02", "kg", 2000, 1600, AccentPlastic, AccentPlasticBg),
        WasteCommodity(11, "PLASTIK", "Gelas bersih", "B-03", "kg", 2700, 2160, AccentPlastic, AccentPlasticBg),
        WasteCommodity(12, "PLASTIK", "Gelas Kotor", "B-04", "kg", 1500, 1200, AccentPlastic, AccentPlasticBg),
        WasteCommodity(13, "PLASTIK", "Gelas Plastik Kotor", "B-04", "kg", 1500, 1200, AccentPlastic, AccentPlasticBg),
        WasteCommodity(14, "PLASTIK", "Gelas Warna / Campur", "B-05", "kg", 1000, 800, AccentPlastic, AccentPlasticBg),
        WasteCommodity(15, "PLASTIK", "Botol plastik Oli warna", "B-06", "kg", 2000, 1600, AccentPlastic, AccentPlasticBg),
        WasteCommodity(16, "PLASTIK", "CD Keping/paralon", "B-07", "kg", 500, 400, AccentPlastic, AccentPlasticBg),
        WasteCommodity(17, "PLASTIK", "Emberan Putihan", "B-08", "kg", 2000, 1600, AccentPlastic, AccentPlasticBg),
        WasteCommodity(18, "PLASTIK", "Emberan Warna / plastik campur / Selang", "B-09", "kg", 1000, 800, AccentPlastic, AccentPlasticBg),
        WasteCommodity(19, "PLASTIK", "Galon 15 ltr dengan tutup - Aqua biru / Lemineral / Cleo", "B-10", "biji", 550, 440, AccentPlastic, AccentPlasticBg),
        WasteCommodity(20, "PLASTIK", "HD/ kresek (kering)/PP/plastik daunan/ plastik bening/ plastik gula", "B-11", "kg", 300, 240, AccentPlastic, AccentPlasticBg),
        WasteCommodity(21, "PLASTIK", "Jerigen putih susu, naso", "B-12", "kg", 2500, 2000, AccentPlastic, AccentPlasticBg),
        WasteCommodity(22, "PLASTIK", "Kerasan", "B-13", "kg", 500, 400, AccentPlastic, AccentPlasticBg),
        WasteCommodity(23, "PLASTIK", "PET warna, botol minyak", "B-15", "kg", 1000, 800, AccentPlastic, AccentPlasticBg),
        WasteCommodity(24, "PLASTIK", "Plastik kaca", "B-16", "kg", 2500, 2000, AccentPlastic, AccentPlasticBg),
        WasteCommodity(25, "PLASTIK", "Toples bening", "B-18", "kg", 1500, 1200, AccentPlastic, AccentPlasticBg),
        WasteCommodity(26, "PLASTIK", "Tutup botol", "B-19", "kg", 2500, 2000, AccentPlastic, AccentPlasticBg),
        WasteCommodity(27, "PLASTIK", "Twin wall kotak makan bening (PPN)", "B-20", "kg", 2500, 2000, AccentPlastic, AccentPlasticBg),
        WasteCommodity(28, "PLASTIK", "Botol oli", "B-21", "kg", 1000, 800, AccentPlastic, AccentPlasticBg),
        WasteCommodity(29, "PLASTIK", "Gelas warna/ campur bersih", "B-22", "kg", 1200, 960, AccentPlastic, AccentPlasticBg),

        // === KACA/BOTOL ===
        WasteCommodity(30, "KACA/BOTOL", "Beling (Kg)/ botol kaca kecil -kecil", "C-01", "kg", 50, 40, AccentGlass, AccentGlassBg),
        WasteCommodity(31, "KACA/BOTOL", "Botol Keca /saos", "C-06", "biji", 300, 240, AccentGlass, AccentGlassBg),
        WasteCommodity(32, "KACA/BOTOL", "Botol Gepeng/ orson/ marjan/ sirup", "C-07", "biji", 100, 80, AccentGlass, AccentGlassBg),
        WasteCommodity(33, "KACA/BOTOL", "Botol Sprit / Fanta / Cola (ditimbang)", "C-10", "kg", 100, 80, AccentGlass, AccentGlassBg),

        // === LOGAM ===
        WasteCommodity(34, "LOGAM", "Kaleng", "D-09", "kg", 1500, 1200, AccentMetal, AccentMetalBg),

        // === LAIN-LAIN ===
        WasteCommodity(35, "LAIN-LAIN", "Bagor beras 25 kg (per lembar)", "F-01", "lembar", 50, 40, BestariSageSecondary, BestariSageContainer),
        WasteCommodity(36, "LAIN-LAIN", "Bagor beras besar 50 kg (per lembar)", "F-02", "lembar", 400, 320, BestariSageSecondary, BestariSageContainer),
        WasteCommodity(37, "LAIN-LAIN", "Bagor beras pakan ayam (per lembar)", "F-03", "lembar", 800, 640, BestariSageSecondary, BestariSageContainer),
        WasteCommodity(38, "LAIN-LAIN", "Bagor jumbo paket (per lembar)", "F-04", "lembar", 1000, 800, BestariSageSecondary, BestariSageContainer),
        WasteCommodity(39, "LAIN-LAIN", "Bagor ukuran kecil (per kg)", "F-05", "kg", 150, 120, BestariSageSecondary, BestariSageContainer),
        WasteCommodity(40, "LAIN-LAIN", "Gembosan/ sepatu/ sandal (per kg)", "F-06", "kg", 200, 160, BestariSageSecondary, BestariSageContainer),
        WasteCommodity(41, "LAIN-LAIN", "Minyak jelantah (liter)", "F-07", "liter", 5000, 4000, AccentOil, AccentOilBg),
        WasteCommodity(42, "LAIN-LAIN", "Nasi aking/ sego garing (kg)", "F-08", "kg", 2500, 2000, BestariSageSecondary, BestariSageContainer)
    )

    val categories = listOf("SEMUA", "KERTAS", "PLASTIK", "KACA/BOTOL", "LOGAM", "LAIN-LAIN")

    fun findByCode(code: String): WasteCommodity? =
        items.find { it.code.equals(code, ignoreCase = true) }

    fun defaultCommodity(): WasteCommodity = items[8] // Botol PET B-01
}
