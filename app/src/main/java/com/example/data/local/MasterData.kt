package com.example.data.local

import com.example.data.model.EquipmentMaster
import com.example.data.model.EquipmentType
import com.example.data.model.Period
import com.example.data.model.User
import com.example.data.model.UserRole

object MasterData {
    val periods = listOf(
        Period(
            id = "PER-2026-10-B",
            name = "Periode II - Oktober 2026",
            startDate = "16 Okt 2026",
            endDate = "31 Okt 2026",
            isActive = true,
            daysRemaining = 3
        ),
        Period(
            id = "PER-2026-10-A",
            name = "Periode I - Oktober 2026",
            startDate = "01 Okt 2026",
            endDate = "15 Okt 2026",
            isActive = false,
            daysRemaining = 0
        ),
        Period(
            id = "PER-2026-09-B",
            name = "Periode II - September 2026",
            startDate = "16 Sep 2026",
            endDate = "30 Sep 2026",
            isActive = false,
            daysRemaining = 0
        )
    )

    val equipmentList = listOf(
        EquipmentMaster(
            id = "EQ-01",
            name = "Fusion Splicer",
            category = "Alat Kerja (Alker)",
            type = EquipmentType.PHOTO_OCR,
            photoGuide = "Ambil foto bagian display dan Serial Number (SN). Mesin harus bersih dari sisa potongan core fiber.",
            sampleTip = "Foto tegak lurus dengan pencahayaan cukup, display LED terlihat jelas.",
            ocrFieldLabel = "Serial Number & Status Kalibrasi",
            iconName = "hardware"
        ),
        EquipmentMaster(
            id = "EQ-02",
            name = "One Click Cleanser",
            category = "Alat Kerja (Alker)",
            type = EquipmentType.PHOTO,
            photoGuide = "Foto tampak samping keseluruhan dan ujung pena pembersih. Pastikan penutup terpasang.",
            sampleTip = "Perlihatkan sisa indikator gulungan pita cleaner.",
            ocrFieldLabel = null,
            iconName = "cleaning"
        ),
        EquipmentMaster(
            id = "EQ-03",
            name = "Fiber Stripper",
            category = "Alat Kerja (Alker)",
            type = EquipmentType.PHOTO,
            photoGuide = "Foto mata pisau stripper dalam kondisi terbuka sedikit untuk melihat kebersihan lubang notch (125um, 250um, 3mm).",
            sampleTip = "Pastikan mata pisau tidak ada yang rompal atau berkarat.",
            ocrFieldLabel = null,
            iconName = "cut"
        ),
        EquipmentMaster(
            id = "EQ-04",
            name = "OTDR",
            category = "Alat Kerja (Alker)",
            type = EquipmentType.PHOTO_OCR,
            photoGuide = "Layar OTDR WAJIB DALAM KEADAAN MENYALA. Tampilkan layar trace event atau status ukur dBm.",
            sampleTip = "Hindari pantulan lampu/sinar matahari pada kaca layar OTDR.",
            ocrFieldLabel = "Nilai Ukur dB / Jarak (KM)",
            iconName = "analytics"
        ),
        EquipmentMaster(
            id = "EQ-05",
            name = "Testphone",
            category = "Alat Kerja (Alker)",
            type = EquipmentType.PHOTO,
            photoGuide = "Foto bodi testphone, kabel jepit buaya, dan keypad nomor dalam kondisi utuh.",
            sampleTip = "Tunjukkan kabel tidak terkelupas dan konektor bersih.",
            ocrFieldLabel = null,
            iconName = "phone"
        ),
        EquipmentMaster(
            id = "EQ-06",
            name = "LAN Tester",
            category = "Alat Kerja (Alker)",
            type = EquipmentType.PHOTO_OCR,
            photoGuide = "Foto saat unit menyala, pastikan LED pin 1-8 tampak aktif berurutan.",
            sampleTip = "Tampilkan unit master dan remote berdampingan.",
            ocrFieldLabel = "Status Indikator LED 1-8",
            iconName = "cable"
        ),
        EquipmentMaster(
            id = "EQ-07",
            name = "Optical Power Meter (OPM)",
            category = "Alat Kerja (Alker)",
            type = EquipmentType.PHOTO_OCR,
            photoGuide = "Layar LCD harus menyala menampilkan wavelength (1310/1490/1550nm) dan angka dBm.",
            sampleTip = "Pastikan debu pada adapter konektor FC/SC sudah dibersihkan.",
            ocrFieldLabel = "Wavelength (nm) & Power (dBm)",
            iconName = "speed"
        ),
        EquipmentMaster(
            id = "EQ-08",
            name = "Visual Fault Locator (VFL)",
            category = "Alat Kerja (Alker)",
            type = EquipmentType.PHOTO,
            photoGuide = "Foto saat tombol daya ON dan laser merah menyala stabil (jangan sorot langsung ke kamera).",
            sampleTip = "Sorotkan laser ke permukaan datar putih sebagai bukti nyala laser.",
            ocrFieldLabel = null,
            iconName = "flashlight"
        ),
        EquipmentMaster(
            id = "EQ-09",
            name = "Tangga Teleskopik / Fiber",
            category = "Sarana Kerja (Sarker)",
            type = EquipmentType.PHOTO,
            photoGuide = "Foto tangga berdiri atau terlipat rapi. Tunjukkan safety strap pengunci dan karet kaki tangga.",
            sampleTip = "Pastikan engsel tangga tidak bengkok dan tali pengikat dalam kondisi prima.",
            ocrFieldLabel = null,
            iconName = "stairs"
        ),
        EquipmentMaster(
            id = "EQ-10",
            name = "Toolkit Set",
            category = "Alat Kerja (Alker)",
            type = EquipmentType.PHOTO,
            photoGuide = "Buka tas toolkit dan foto seluruh perkakas tersusun rapi (tang potong, obeng, cutter, isolasi, meteran).",
            sampleTip = "Jangan ada alat yang tertumpuk agar admin mudah menghitung kelengkapan.",
            ocrFieldLabel = null,
            iconName = "handyman"
        ),
        EquipmentMaster(
            id = "EQ-11",
            name = "HP Android Lapangan",
            category = "Sarana Kerja (Sarker)",
            type = EquipmentType.PHOTO_OCR,
            photoGuide = "Foto layar HP yang menampilkan layar aplikasi kerja lapangan atau halaman Pengaturan Tentang Ponsel.",
            sampleTip = "Pastikan kondisi fisik layar tidak pecah parah dan baterai mencukupi.",
            ocrFieldLabel = "Nomor Model / Versi Android",
            iconName = "smartphone"
        ),
        EquipmentMaster(
            id = "EQ-12",
            name = "Body Harness dan APD",
            category = "Sarana Kerja (Sarker)",
            type = EquipmentType.PHOTO,
            photoGuide = "Foto lengkap APD: Helm Safety (tali dagu utuh), Rompi K3 scotlight, Sarung Tangan, Sepatu Safety, dan Full Body Harness.",
            sampleTip = "Harness harus terbentang dan terlihat tidak ada jahitan yang robek.",
            ocrFieldLabel = null,
            iconName = "security"
        ),
        EquipmentMaster(
            id = "EQ-13",
            name = "Kendaraan Operasional",
            category = "Sarana Kerja (Sarker)",
            type = EquipmentType.PHOTO_OCR,
            photoGuide = "FOTO WAJIB MENAMPILKAN ANGKA SPEEDOMETER/ODOMETER DENGAN JELAS + Plat Nomor Kendaraan.",
            sampleTip = "Nyalakan kunci kontak agar angka speedometer digital/analog terlihat tajam tanpa pantulan silau.",
            ocrFieldLabel = "Angka Odometer / Kilometer",
            iconName = "motorcycle"
        ),
        EquipmentMaster(
            id = "EQ-14",
            name = "ID Card Teknisi",
            category = "Sarana Kerja (Sarker)",
            type = EquipmentType.PHOTO_OCR,
            photoGuide = "Foto kartu pengenal fisik Telkom Akses tegak lurus. NIK, Nama, dan Foto wajah wajib terbaca jelas.",
            sampleTip = "Letakkan ID Card di atas permukaan polos dan jangan gunakan flash berlebih.",
            ocrFieldLabel = "NIK & Nama Lengkap",
            iconName = "badge"
        ),
        EquipmentMaster(
            id = "EQ-15",
            name = "Optical Fiber Ranger / Mini OTDR",
            category = "Alat Kerja (Alker)",
            type = EquipmentType.PHOTO_OCR,
            photoGuide = "Foto layar menyala yang memperlihatkan grafik event fiber atau pembacaan jarak patahan (Fault Distance).",
            sampleTip = "Ambil foto dengan orientasi landscape jika layar memanjang.",
            ocrFieldLabel = "Fault Distance (m) / Status Event",
            iconName = "timeline"
        )
    )

    val technicians = listOf(
        User(
            id = "TECH-01",
            name = "Ahmad Fauzan",
            nik = "940218",
            email = "fauzan.padang@telkomakses.co.id",
            phone = "0812-6789-1021",
            role = UserRole.TECHNICIAN,
            stoUnit = "STO Padang Baru",
            team = "Tim Alpha Assurance"
        ),
        User(
            id = "TECH-02",
            name = "Budi Kurniawan",
            nik = "938172",
            email = "budi.kurniawan@telkomakses.co.id",
            phone = "0813-7451-9988",
            role = UserRole.TECHNICIAN,
            stoUnit = "STO Belimbing",
            team = "Tim Beta Fiber"
        ),
        User(
            id = "TECH-03",
            name = "Rian Pratama",
            nik = "951820",
            email = "rian.pratama@telkomakses.co.id",
            phone = "0852-6311-4090",
            role = UserRole.TECHNICIAN,
            stoUnit = "STO Simpang Haru",
            team = "Tim Gamma Maintenance"
        ),
        User(
            id = "TECH-04",
            name = "Dedi Saputra",
            nik = "927641",
            email = "dedi.saputra@telkomakses.co.id",
            phone = "0821-7100-3321",
            role = UserRole.TECHNICIAN,
            stoUnit = "STO Ulak Karang",
            team = "Tim Delta Provisioning"
        )
    )

    val defaultAdmin = User(
        id = "ADM-01",
        name = "Rahma Wulandari, S.T.",
        nik = "890124",
        email = "rahma.wulandari@telkomakses.co.id",
        phone = "0811-6602-5511",
        role = UserRole.ADMIN,
        stoUnit = "Kantor Assurance Telkom Akses Padang",
        team = "Quality Assurance & Operational Lead"
    )

    val stoList = listOf(
        "Semua STO",
        "STO Padang Baru",
        "STO Belimbing",
        "STO Simpang Haru",
        "STO Ulak Karang"
    )
}
