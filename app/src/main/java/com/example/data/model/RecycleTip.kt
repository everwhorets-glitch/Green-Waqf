package com.example.data.model

data class RecycleTip(
    val id: String,
    val title: String,
    val category: String, // "Pilah Sampah", "Kode Plastik", "Adiwiyata Mansa", "Zero Waste"
    val summary: String,
    val fullContent: String,
    val iconKey: String,
    val steps: List<String> = emptyList()
)

object RecycleTipCatalog {
    val sampleTips = listOf(
        RecycleTip(
            id = "tip_1",
            title = "Standar Penyetoran Botol Plastik Bank Bestari",
            category = "Pilah Sampah",
            summary = "Cara mempersiapkan botol plastik agar dihargai maksimal Rp 3.500/kg tanpa penolakan timbangan.",
            fullContent = "Botol plastik adalah komoditas utama Bank Bestari MAN 1 Yogyakarta. Untuk memastikan kualitas daur ulang terbaik dan berat yang akurat, ikuti pedoman 3T (Tiriskan, Tekan, Tutup).",
            iconKey = "recycling",
            steps = listOf(
                "Kosongkan seluruh cairan di dalam botol air mineral.",
                "Bilas bagian dalam dengan sedikit air bersih dan tiriskan hingga kering.",
                "Lepas label plastik merk luar bila memungkinkan (dapat disetor terpisah).",
                "Tekan/remas botol (pipihkan) dari bawah ke atas agar menghemat volume penyimpanan karung.",
                "Pasang kembali tutup botolnya agar tetap padat dan ringkas."
            )
        ),
        RecycleTip(
            id = "tip_2",
            title = "Mengenal Kode Segitiga Daur Ulang Plastik",
            category = "Kode Plastik",
            summary = "Kenali angka 1 sampai 7 di bawah kemasan plastik madrasah dan nilainya.",
            fullContent = "Setiap plastik memiliki kode resin identifikasi dari angka 1 hingga 7. Memahami kode ini memudahkan klasifikasi sebelum dibawa ke loket Bank Bestari.",
            iconKey = "category",
            steps = listOf(
                "Kode 1 (PET/PETE): Botol air mineral bening, botol soda. Nilai daur ulang paling tinggi (Rp 3.500/kg).",
                "Kode 2 (HDPE): Botol sampo tebal, botol detergen, tutup galon. Kuat dan sangat mudah didaur ulang.",
                "Kode 3 (PVC): Pipa, kemasan blister mainan. Jarang didaur ulang di madrasah karena zat aditif.",
                "Kode 4 (LDPE): Kantong kresek bening, plastik bubble wrap. Diterima jika dalam kondisi bersih.",
                "Kode 5 (PP): Kotak makanan bekal, sedotan, gelas cup es teh kantin Mansa. Sangat diminati pabrik daur ulang.",
                "Kode 6 (PS): Styrofoam & sendok plastik sekali pakai. Hindari penggunaan di lingkungan madrasah!",
                "Kode 7 (OTHER): Campuran polimer seperti bioplastik & polycarbonate."
            )
        ),
        RecycleTip(
            id = "tip_3",
            title = "Gerakan Tumbler & Lunchbox Madrasah Adiwiyata",
            category = "Adiwiyata Mansa",
            summary = "Upaya preventif mengurangi timbulan sampah kantin hingga 70% setiap hari di MAN 1 Yogyakarta.",
            fullContent = "Sebagai madrasah Adiwiyata Mandiri, langkah terbaik mengelola sampah adalah dengan mencegahnya diproduksi (Reduce). Bawa botol minum sendiri dan wadah makanan saat jajan di kantin.",
            iconKey = "eco",
            steps = listOf(
                "Bawa tumbler berkapasitas minimal 600ml dari rumah.",
                "Manfaatkan fasilitas galon isi ulang air mineral di koridor kelas dan musala.",
                "Gunakan kotak makan sendiri saat memesan gorengan atau nasi di kantin kejujuran.",
                "Tolak kantong plastik sekali pakai saat membeli alat tulis di koperasi madrasah."
            )
        ),
        RecycleTip(
            id = "tip_4",
            title = "Penyelamatan Jelantah Jadi Lilin & Biodiesel",
            category = "Zero Waste",
            summary = "Minyak goreng bekas dapur asrama dan rumah tangga bernilai Rp 6.000/liter di Bank Bestari.",
            fullContent = "Jangan pernah membuang minyak jelantah ke wastafel atau selokan sekolah karena dapat membeku, menyumbat saluran air, serta mencemari air tanah Yogyakarta. Kumpulkan dan setorkan ke Bank Bestari!",
            iconKey = "water_drop",
            steps = listOf(
                "Dinginkan minyak bekas penggorengan hingga suhu ruang.",
                "Saring remah-remah sisa makanan menggunakan saringan halus atau kain kasa.",
                "Simpan ke dalam botol jerigen atau botol plastik bekas yang bersih dan tertutup rapat.",
                "Bawa ke loket Bank Bestari pada hari pengumpulan minyak jelantah."
            )
        ),
        RecycleTip(
            id = "tip_5",
            title = "Sistem Kompos Daun Gugur & Sisa Kantin",
            category = "Pilah Sampah",
            summary = "Pemanfaatan sampah organik taman sekolah menjadi pupuk tanaman hidroponik MAN 1 Yogyakarta.",
            fullContent = "Sampah organik daun mahoni dan trembesi di halaman madrasah dapat diolah melalui komposter takakura dan biopori. Bank Bestari mendukung pemilahan organik di sumbernya.",
            iconKey = "park",
            steps = listOf(
                "Pisahkan sampah organik basah (sisa buah/sayur) dari sampah daun kering.",
                "Cacah daun kering menjadi ukuran lebih kecil agar cepat terdekomposisi.",
                "Masukkan ke lubang biopori di dekat taman kelas atau bak komposter madrasah.",
                "Siram dengan cairan EM4 encer untuk mempercepat proses fermentasi pupuk."
            )
        )
    )
}
