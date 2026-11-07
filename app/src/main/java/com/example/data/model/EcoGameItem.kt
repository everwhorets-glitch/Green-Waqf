package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class BinType(
    val id: String,
    val title: String,
    val color: Color,
    val bgTint: Color,
    val iconName: String,
    val exampleText: String
) {
    PLASTIC(
        id = "plastic",
        title = "Plastik & Anorganik",
        color = Color(0xFFEAB308), // Kuning
        bgTint = Color(0xFFFEF9C3),
        iconName = "inventory_2",
        exampleText = "Botol, kresek, kaset, cup"
    ),
    ORGANIC(
        id = "organic",
        title = "Sampah Organik",
        color = Color(0xFF16A34A), // Hijau
        bgTint = Color(0xFFDCFCE7),
        iconName = "park",
        exampleText = "Sisa makanan, daun, buah"
    ),
    PAPER(
        id = "paper",
        title = "Kertas & Karton",
        color = Color(0xFF2563EB), // Biru
        bgTint = Color(0xFFDBEAFE),
        iconName = "description",
        exampleText = "Kardus, buku, HVS, koran"
    ),
    METAL_B3(
        id = "metal_b3",
        title = "B3 & Logam",
        color = Color(0xFFDC2626), // Merah
        bgTint = Color(0xFFFEE2E2),
        iconName = "warning",
        exampleText = "Kaleng, baterai, bohlam"
    )
}

data class EcoGameItem(
    val id: String,
    val name: String,
    val imageUrl: String,
    val correctBin: BinType,
    val reasonWhy: String,
    val funFact: String,
    val points: Int = 10
)

object EcoGameItemCatalog {
    // 22 real stock image items covering diverse everyday waste at MAN 1 Yogyakarta
    val items = listOf(
        EcoGameItem(
            id = "g_item_1",
            name = "Botol Air Mineral PET",
            imageUrl = "https://images.unsplash.com/photo-1572018820626-444455208643?w=600&auto=format&fit=crop&q=80",
            correctBin = BinType.PLASTIC,
            reasonWhy = "Botol ini terbuat dari plastik Polyethylene Terephthalate (PET) bening yang dapat didaur ulang menjadi serat baju atau botol baru.",
            funFact = "Di Bank Bestari, botol PET bersih (B-01) memiliki harga beli tertinggi Rp 2.800/kg!"
        ),
        EcoGameItem(
            id = "g_item_2",
            name = "Kulit Pisang Istirahat",
            imageUrl = "https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e?w=600&auto=format&fit=crop&q=80",
            correctBin = BinType.ORGANIC,
            reasonWhy = "Kulit pisang adalah materi alami organik yang mudah terurai dan kaya kalium untuk pupuk kompos biopori madrasah.",
            funFact = "Kompos biopori MAN 1 Yogyakarta memanfaatkan sisa buah kantin untuk menyuburkan kebun hidroponik."
        ),
        EcoGameItem(
            id = "g_item_3",
            name = "Kardus Cokelat Tebal",
            imageUrl = "https://images.unsplash.com/photo-1530587191325-3db32d826c18?w=600&auto=format&fit=crop&q=80",
            correctBin = BinType.PAPER,
            reasonWhy = "Kardus bergelombang tersusun dari serat kayu daur ulang yang dapat dipulping kembali menjadi kotak kemasan ramah lingkungan.",
            funFact = "Mendaur ulang 1 ton kardus dapat menyelamatkan 17 batang pohon dewasa dari penebangan!"
        ),
        EcoGameItem(
            id = "g_item_4",
            name = "Kaleng Minuman Bersoda",
            imageUrl = "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?w=600&auto=format&fit=crop&q=80",
            correctBin = BinType.METAL_B3,
            reasonWhy = "Kaleng minuman terbuat dari logam aluminium yang dapat dilebur 100% tanpa penurunan kualitas fisik.",
            funFact = "Daur ulang aluminium menghemat hingga 95% energi dibanding menambang bijih bauksit baru."
        ),
        EcoGameItem(
            id = "g_item_5",
            name = "Kaset Pita Audio Bekas",
            imageUrl = "https://images.unsplash.com/photo-1549887534-1541e9326642?w=600&auto=format&fit=crop&q=80",
            correctBin = BinType.PLASTIC,
            reasonWhy = "Casing kaset pita terbuat dari plastik polistiren (PS) kaku dan pita poliester yang termasuk kategori plastik daur ulang.",
            funFact = "Di Bank Bestari, kaset pita dan keping CD masuk ke komoditas B-07 (CD Keping / paralon / kaset)."
        ),
        EcoGameItem(
            id = "g_item_6",
            name = "Keping CD / DVD Komputer",
            imageUrl = "https://images.unsplash.com/photo-1526738549149-8e07eca6c147?w=600&auto=format&fit=crop&q=80",
            correctBin = BinType.PLASTIC,
            reasonWhy = "Keping cakram digital dibuat dari polikarbonat murni bermutu tinggi yang sangat dicari industri plastik teknis.",
            funFact = "Keping CD bekas yang disetor dilelehkan menjadi bahan baku lampu otomotif dan helm pelindung."
        ),
        EcoGameItem(
            id = "g_item_7",
            name = "Kertas Lembar HVS Ulangan",
            imageUrl = "https://images.unsplash.com/photo-1586075010923-2dd4570fb338?w=600&auto=format&fit=crop&q=80",
            correctBin = BinType.PAPER,
            reasonWhy = "Kertas HVS putih bersih (Arsip 1) memiliki serat selulosa murni yang mudah diolah menjadi kertas tulis baru.",
            funFact = "Pisahkan staples kawat dari lembar ulangan sebelum disetorkan ke bank sampah agar timbangan bersih."
        ),
        EcoGameItem(
            id = "g_item_8",
            name = "Baterai Jam Dinding Habis",
            imageUrl = "https://images.unsplash.com/photo-1619725002198-6a689b72f41d?w=600&auto=format&fit=crop&q=80",
            correctBin = BinType.METAL_B3,
            reasonWhy = "Baterai bekas mengandung logam berat beracun (seng, mangan, merkuri) yang merupakan limbah Bahan Berbahaya dan Beracun (B3).",
            funFact = "Satu baterai kecil yang bocor ke tanah dapat mencemari ribuan liter air tanah di sekitarnya!"
        ),
        EcoGameItem(
            id = "g_item_9",
            name = "Kantong Kresek Belanja",
            imageUrl = "https://images.unsplash.com/photo-1597348989645-46b190ce4918?w=600&auto=format&fit=crop&q=80",
            correctBin = BinType.PLASTIC,
            reasonWhy = "Kantong kresek terbuat dari film polietilena kerapatan rendah (LDPE) kering yang dapat dicacah dan dipelet ulang.",
            funFact = "Di Bank Bestari, kresek kering masuk dalam komoditas B-11 (HD/kresek kering/plastik bening)."
        ),
        EcoGameItem(
            id = "g_item_10",
            name = "Daun Kering Pohon Madrasah",
            imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600&auto=format&fit=crop&q=80",
            correctBin = BinType.ORGANIC,
            reasonWhy = "Daun kering halaman madrasah merupakan sumber karbon cokelat terbaik untuk menjaga kelembapan mikroba kompos.",
            funFact = "Mencampur daun kering dan sisa kantin dengan perbandingan 2:1 menghasilkan kompos yang tidak berbau sama sekali."
        ),
        EcoGameItem(
            id = "g_item_11",
            name = "Tutup Botol Minuman",
            imageUrl = "https://images.unsplash.com/photo-1605600659873-d808a13e4d2a?w=600&auto=format&fit=crop&q=80",
            correctBin = BinType.PLASTIC,
            reasonWhy = "Tutup botol terbuat dari High-Density Polyethylene (HDPE) atau Polypropylene (PP) yang padat dan bernilai daur ulang tinggi.",
            funFact = "Tutup botol (B-19) disetor terpisah dari badan botol dengan harga beli Rp 2.000/kg di Bank Bestari."
        ),
        EcoGameItem(
            id = "g_item_12",
            name = "Sisa Sayuran Kantin Madrasah",
            imageUrl = "https://images.unsplash.com/photo-1540420773420-3366772f4999?w=600&auto=format&fit=crop&q=80",
            correctBin = BinType.ORGANIC,
            reasonWhy = "Sisa sayuran mentah atau matang adalah biomassa hijau kaya nitrogen yang lekas membusuk dan menyuburkan tanah.",
            funFact = "Sisa sayur kantin MAN 1 Yogyakarta dapat dijadikan pakan larva BSF (Black Soldier Fly) yang kaya protein."
        ),
        EcoGameItem(
            id = "g_item_13",
            name = "Koran Harian Bekas",
            imageUrl = "https://images.unsplash.com/photo-1504711434969-e33886168f5c?w=600&auto=format&fit=crop&q=80",
            correctBin = BinType.PAPER,
            reasonWhy = "Kertas koran KW1 terbuat dari pulp kayu mekanis yang sangat cepat diserap kembali dalam proses pembuatan karton dan tisu.",
            funFact = "Koran KW1 bernilai Rp 3.200/kg di Bank Bestari, salah satu jenis kertas termahal yang disetor."
        ),
        EcoGameItem(
            id = "g_item_14",
            name = "Bohlam Lampu Neon Rusak",
            imageUrl = "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=600&auto=format&fit=crop&q=80",
            correctBin = BinType.METAL_B3,
            reasonWhy = "Bohlam lampu fluorescent mengandung uap raksa (merkuri) dan filamen logam yang berbahaya bila dibuang sembarangan.",
            funFact = "Tong merah B3 di sekolah disiapkan khusus untuk menampung lampu putus agar tidak terinjak atau pecah."
        ),
        EcoGameItem(
            id = "g_item_15",
            name = "Emberan Plastik Pecah",
            imageUrl = "https://images.unsplash.com/photo-1584727638096-042c45049ebe?w=600&auto=format&fit=crop&q=80",
            correctBin = BinType.PLASTIC,
            reasonWhy = "Ember pecah terbuat dari plastik keras PP/HDPE tebal yang digiling menjadi biji plastik pencetak baskom baru.",
            funFact = "Emberan putihan (B-08) dihargai Rp 1.600/kg sedangkan emberan warna campur (B-09) dihargai Rp 800/kg."
        ),
        EcoGameItem(
            id = "g_item_16",
            name = "Cangkang Kulit Telur",
            imageUrl = "https://images.unsplash.com/photo-1587486913049-53fc88980cfc?w=600&auto=format&fit=crop&q=80",
            correctBin = BinType.ORGANIC,
            reasonWhy = "Kulit telur tersusun dari 95% kalsium karbonat organik yang memperkuat dinding sel tanaman saat dicampur di tanah.",
            funFact = "Tumbuk kulit telur hingga halus lalu taburkan di pot bunga mawar atau cabai sebagai pupuk kalsium alami!"
        ),
        EcoGameItem(
            id = "g_item_17",
            name = "Karton Wadah Telur Duplek",
            imageUrl = "https://images.unsplash.com/photo-1582722872445-44dc5f7e3c8f?w=600&auto=format&fit=crop&q=80",
            correctBin = BinType.PAPER,
            reasonWhy = "Karton tray telur (duplek) adalah olahan kertas daur ulang berpori yang mudah terurai dan dapat dicetak kembali.",
            funFact = "Karton duplek telur masuk ke kode A-06 dengan harga Rp 80/kg di Bank Bestari."
        ),
        EcoGameItem(
            id = "g_item_18",
            name = "Gelas Plastik Minuman Es",
            imageUrl = "https://images.unsplash.com/photo-1517256064527-09c73fc73e38?w=600&auto=format&fit=crop&q=80",
            correctBin = BinType.PLASTIC,
            reasonWhy = "Gelas cup bening dibuat dari plastik Polipropilena (PP) yang dapat dilelehkan menjadi ember atau hanger pakaian.",
            funFact = "Gelas plastik bersih tanpa sisa es/sirup dihargai Rp 2.160/kg di Bank Bestari (kode B-03)."
        ),
        EcoGameItem(
            id = "g_item_19",
            name = "Jerigen Putih Bekas Sabun",
            imageUrl = "https://images.unsplash.com/photo-1585837575652-267c041d77d4?w=600&auto=format&fit=crop&q=80",
            correctBin = BinType.PLASTIC,
            reasonWhy = "Jerigen susu/sabun putih (B-12) terbuat dari plastik HDPE tebal berkualitas tinggi yang tahan bahan kimia.",
            funFact = "Jerigen putih dihargai Rp 2.000/kg karena merupakan plastik murni yang sangat disukai pabrik daur ulang."
        ),
        EcoGameItem(
            id = "g_item_20",
            name = "Buku Tulis Bekas Ulangan",
            imageUrl = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600&auto=format&fit=crop&q=80",
            correctBin = BinType.PAPER,
            reasonWhy = "Buku catatan bekas tanpa sampul (Arsip 2 kode A-02) terdiri dari lembaran kertas bergaris yang mudah didaur ulang.",
            funFact = "Kumpulkan buku catatan semester lalu bersama teman sekelas untuk menambah saldo tabungan majmuk kelas!"
        ),
        EcoGameItem(
            id = "g_item_21",
            name = "Paku & Kaleng Cat Logam",
            imageUrl = "https://images.unsplash.com/photo-1581783342308-f792dbdd27c5?w=600&auto=format&fit=crop&q=80",
            correctBin = BinType.METAL_B3,
            reasonWhy = "Kaleng cat dan paku bekas mengandung residu pelarut kimia berbahaya serta besi tajam yang wajib dipisah ke B3/logam.",
            funFact = "Pemisahan paku dan kaleng cat mencegah luka bagi petugas kebersihan sekolah saat pengangkutan sampah."
        ),
        EcoGameItem(
            id = "g_item_22",
            name = "Potongan Apel & Jeruk",
            imageUrl = "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=600&auto=format&fit=crop&q=80",
            correctBin = BinType.ORGANIC,
            reasonWhy = "Sisa buah kaya cairan asam sitrat dan gula alami yang mempercepat proses fermentasi eco-enzyme ramah lingkungan.",
            funFact = "Eco-enzyme dari kulit jeruk dan nanas di madrasah dapat digunakan sebagai cairan pembersih lantai alami."
        )
    )
}
