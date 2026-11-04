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

    val categories = listOf(
        "alat_ukur",
        "kendaraan",
        "perlengkapan",
        "genset",
        "kabel_aksesori",
        "komunikasi"
    )

    fun formatCategory(category: String): String {
        return when (category.lowercase()) {
            "alat_ukur" -> "Alat Ukur"
            "kendaraan" -> "Kendaraan"
            "perlengkapan" -> "Perlengkapan"
            "genset" -> "Genset"
            "kabel_aksesori" -> "Kabel & Aksesori"
            "komunikasi" -> "Komunikasi"
            else -> category.replace("_", " ").replaceFirstChar { it.uppercase() }
        }
    }

    val equipmentList = listOf(
        EquipmentMaster(
            id = "EQ-01",
            name = "Splicer",
            category = "alat_ukur",
            type = EquipmentType.PHOTO_OCR,
            photoGuide = "Ambil foto bagian display LCD dan Serial Number (SN) Fusion Splicer. Mesin harus bersih dari sisa potongan core fiber.",
            sampleTip = "Foto tegak lurus dengan pencahayaan cukup, display LED terlihat jelas.",
            ocrFieldLabel = "Serial Number & Status Arc Count",
            iconName = "hardware"
        ),
        EquipmentMaster(
            id = "EQ-02",
            name = "OPM",
            category = "alat_ukur",
            type = EquipmentType.PHOTO_OCR,
            photoGuide = "Layar LCD Optical Power Meter WAJIB MENYALA menampilkan wavelength (1310/1490/1550nm) dan angka dBm.",
            sampleTip = "Pastikan debu pada adapter konektor FC/SC sudah dibersihkan.",
            ocrFieldLabel = "Wavelength (nm) & Power (dBm)",
            iconName = "speed"
        ),
        EquipmentMaster(
            id = "EQ-03",
            name = "VFL",
            category = "alat_ukur",
            type = EquipmentType.PHOTO_OCR,
            photoGuide = "Foto saat tombol daya Visual Fault Locator ON dan sinar laser merah menyala stabil (jangan sorot ke mata).",
            sampleTip = "Sorotkan laser ke permukaan datar putih sebagai bukti pancaran laser.",
            ocrFieldLabel = "Status Emisi Laser Merah",
            iconName = "flashlight"
        ),
        EquipmentMaster(
            id = "EQ-04",
            name = "LAN Tester",
            category = "alat_ukur",
            type = EquipmentType.PHOTO_OCR,
            photoGuide = "Foto saat unit menyala, pastikan LED pin 1-8 tampak aktif berurutan pada master dan remote.",
            sampleTip = "Tampilkan unit master dan remote berdampingan.",
            ocrFieldLabel = "Status Indikator Pin LED 1-8",
            iconName = "cable"
        ),
        EquipmentMaster(
            id = "EQ-05",
            name = "OTDR",
            category = "alat_ukur",
            type = EquipmentType.PHOTO_OCR,
            photoGuide = "Layar OTDR WAJIB DALAM KEADAAN MENYALA. Tampilkan layar trace event atau status pengukuran redaman dBm.",
            sampleTip = "Hindari pantulan silau cahaya pada kaca layar OTDR.",
            ocrFieldLabel = "Nilai Ukur dB / Jarak (KM)",
            iconName = "analytics"
        ),
        EquipmentMaster(
            id = "EQ-06",
            name = "Mini OTDR",
            category = "alat_ukur",
            type = EquipmentType.PHOTO_OCR,
            photoGuide = "Foto layar menyala yang memperlihatkan grafik event fiber atau pembacaan jarak patahan (Fault Distance).",
            sampleTip = "Ambil foto tegak lurus agar angka event terbaca tajam.",
            ocrFieldLabel = "Fault Distance (m) / Status Event",
            iconName = "timeline"
        ),
        EquipmentMaster(
            id = "EQ-07",
            name = "KBM Roda 2",
            category = "kendaraan",
            type = EquipmentType.PHOTO,
            photoGuide = "Foto sepeda motor operasional lengkap dengan plat nomor BA dan kondisi fisik kendaraan.",
            sampleTip = "Pastikan spion lengkap, ban layak pakai, dan bodi bersih bertanda resmi Telkom Akses.",
            ocrFieldLabel = null,
            iconName = "motorcycle"
        ),
        EquipmentMaster(
            id = "EQ-08",
            name = "KBM Roda 4",
            category = "kendaraan",
            type = EquipmentType.PHOTO,
            photoGuide = "Foto mobil operasional tampak depan/samping lengkap dengan plat nomor dan logo Telkom Akses.",
            sampleTip = "Pastikan kendaraan parkir di area aman dengan pencahayaan terang.",
            ocrFieldLabel = null,
            iconName = "car"
        ),
        EquipmentMaster(
            id = "EQ-09",
            name = "Power Bank Vallins",
            category = "perlengkapan",
            type = EquipmentType.PHOTO_OCR,
            photoGuide = "Foto unit Power Bank Vallins, perlihatkan label kapasitas mAh dan lampu indikator daya aktif.",
            sampleTip = "Nyalakan lampu indikator baterai saat memotret.",
            ocrFieldLabel = "Kapasitas (mAh) & Model",
            iconName = "battery"
        ),
        EquipmentMaster(
            id = "EQ-10",
            name = "Pakaian Seragam",
            category = "perlengkapan",
            type = EquipmentType.PHOTO,
            photoGuide = "Foto seragam kerja resmi Telkom Akses (kemeja/polo berlogo TA) dalam keadaan bersih dan rapi.",
            sampleTip = "Foto tampak depan dengan bordir logo atau badge nama tampak jelas.",
            ocrFieldLabel = null,
            iconName = "checkroom"
        ),
        EquipmentMaster(
            id = "EQ-11",
            name = "ID Card",
            category = "perlengkapan",
            type = EquipmentType.PHOTO_OCR,
            photoGuide = "Foto kartu tanda pengenal fisik Telkom Akses tegak lurus. NIK, Nama, dan Foto wajah wajib terbaca jelas.",
            sampleTip = "Letakkan ID Card di atas permukaan polos dan jangan gunakan flash silau.",
            ocrFieldLabel = "NIK & Nama Lengkap",
            iconName = "badge"
        ),
        EquipmentMaster(
            id = "EQ-12",
            name = "Jas Hujan",
            category = "perlengkapan",
            type = EquipmentType.PHOTO,
            photoGuide = "Foto jas hujan lapangan (baju dan celana) dalam kondisi utuh, bersih, dan tidak robek.",
            sampleTip = "Bentangkan jas hujan agar scotlight keselamatan terlihat.",
            ocrFieldLabel = null,
            iconName = "umbrella"
        ),
        EquipmentMaster(
            id = "EQ-13",
            name = "Tas Punggung",
            category = "perlengkapan",
            type = EquipmentType.PHOTO,
            photoGuide = "Foto tas ransel perlengkapan teknisi tampak depan, periksa kondisi resleting dan jahitan tali.",
            sampleTip = "Pastikan tidak ada sobekan pada kompartemen alat.",
            ocrFieldLabel = null,
            iconName = "backpack"
        ),
        EquipmentMaster(
            id = "EQ-14",
            name = "One Click Cleanser",
            category = "perlengkapan",
            type = EquipmentType.PHOTO_OCR,
            photoGuide = "Foto tampak samping pena pembersih ferrule optic dan indikator sisa pita pembersih.",
            sampleTip = "Tunjukkan label ukuran tip ferrule dan penutup debu terpasang.",
            ocrFieldLabel = "Tipe Ferrule & Indikator Sisa",
            iconName = "cleaning"
        ),
        EquipmentMaster(
            id = "EQ-15",
            name = "Tangga Dorong Aluminium 5.1 Meter",
            category = "perlengkapan",
            type = EquipmentType.PHOTO,
            photoGuide = "Foto tangga dorong aluminium 5.1M posisi terlipat atau tegak. Tunjukkan safety strap dan tapak karet.",
            sampleTip = "Pastikan engsel tangga tidak bengkok dan tali pengikat dalam kondisi prima.",
            ocrFieldLabel = null,
            iconName = "stairs"
        ),
        EquipmentMaster(
            id = "EQ-16",
            name = "Tangga Dorong Telescopic Aluminium (9 Mtr)",
            category = "perlengkapan",
            type = EquipmentType.PHOTO,
            photoGuide = "Foto tangga teleskopik 9 meter, perlihatkan pin pengunci tiap ruas dan kondisi tiang aluminium.",
            sampleTip = "Tampilkan tangga dalam keadaan terlipat rapi dan terikat pada rak kendaraan.",
            ocrFieldLabel = null,
            iconName = "stairs"
        ),
        EquipmentMaster(
            id = "EQ-17",
            name = "Toolkit Fo",
            category = "perlengkapan",
            type = EquipmentType.PHOTO_OCR,
            photoGuide = "Foto isi tas Fiber Optic toolkit terbuka (Cleaver FO, Miller Stripper, Drop Cable Stripper).",
            sampleTip = "Susun alat berurutan agar Serial Number cleaver dan mata pisau terlihat jelas.",
            ocrFieldLabel = "SN Cleaver & Kelengkapan FO",
            iconName = "build"
        ),
        EquipmentMaster(
            id = "EQ-18",
            name = "Toolkit Set",
            category = "perlengkapan",
            type = EquipmentType.PHOTO_OCR,
            photoGuide = "Buka tas toolkit set dan foto seluruh perkakas tersusun rapi (tang potong, obeng, cutter, isolasi, meteran).",
            sampleTip = "Jangan ada alat yang tertumpuk agar mudah diverifikasi kelengkapannya.",
            ocrFieldLabel = "Kelengkapan Perkakas & Kondisi",
            iconName = "handyman"
        ),
        EquipmentMaster(
            id = "EQ-19",
            name = "Tenda Terpal Plastik",
            category = "perlengkapan",
            type = EquipmentType.PHOTO,
            photoGuide = "Foto terpal kerja pelindung splicing dari hujan/panas saat terbentang atau terlipat rapi.",
            sampleTip = "Pastikan terpal bersih tidak berlubang dan tali pengikat tersedia.",
            ocrFieldLabel = null,
            iconName = "roofing"
        ),
        EquipmentMaster(
            id = "EQ-20",
            name = "Genset 1000 Watt & Lampu Penerangan",
            category = "genset",
            type = EquipmentType.PHOTO_OCR,
            photoGuide = "Foto unit generator portabel 1000W dan lampu sorot penerangan kerja malam. Tunjukkan stiker spesifikasi daya.",
            sampleTip = "Foto saat mesin menyala atau lampu penerangan diuji coba.",
            ocrFieldLabel = "Kapasitas Daya (Watt) & SN",
            iconName = "bolt"
        ),
        EquipmentMaster(
            id = "EQ-21",
            name = "Alat Gali (Linggis, Cangkul, Sabit, dll)",
            category = "perlengkapan",
            type = EquipmentType.PHOTO,
            photoGuide = "Foto kelengkapan alat gali tanah: linggis besi, cangkul bergagang, dan sabit pembersih semak.",
            sampleTip = "Susun alat berdampingan di area terbuka dengan pencahayaan terang.",
            ocrFieldLabel = null,
            iconName = "agriculture"
        ),
        EquipmentMaster(
            id = "EQ-22",
            name = "Cable Console + Cable USB to RS232",
            category = "kabel_aksesori",
            type = EquipmentType.PHOTO_OCR,
            photoGuide = "Foto kabel console RJ45-to-DB9 dan converter USB to RS232. Tunjukkan konektor bersih dan pin utuh.",
            sampleTip = "Bentangkan kabel dan fokuskan pada konektor USB dan DB9.",
            ocrFieldLabel = "Tipe Chipset USB & Model",
            iconName = "cable"
        ),
        EquipmentMaster(
            id = "EQ-23",
            name = "Crimping Tools RJ11 dan RJ45 Cat-5",
            category = "kabel_aksesori",
            type = EquipmentType.PHOTO_OCR,
            photoGuide = "Foto tang crimping modular RJ45 & RJ11. Perlihatkan gigi penekan pin tembaga dan pisau pengupas.",
            sampleTip = "Pastikan gagang karet tidak kendor dan per penekan berfungsi baik.",
            ocrFieldLabel = "Model Crimping Tool",
            iconName = "hardware"
        ),
        EquipmentMaster(
            id = "EQ-24",
            name = "Alat komunikasi",
            category = "komunikasi",
            type = EquipmentType.PHOTO_OCR,
            photoGuide = "Foto Handy Talky (HT) atau smartphone komunikasi operasional dengan layar menyala.",
            sampleTip = "Tunjukkan display channel frekuensi kerja aktif atau layar aplikasi komunikasi tim.",
            ocrFieldLabel = "Channel / Model Alat Komunikasi",
            iconName = "phone"
        ),
        EquipmentMaster(
            id = "EQ-25",
            name = "Body Harness, Helm pengaman dan Kaus tangan",
            category = "perlengkapan",
            type = EquipmentType.PHOTO_OCR,
            photoGuide = "Foto lengkap APD K3: Full Body Harness dengan double lanyard, Helm pengaman (tali dagu utuh), dan Kaus tangan kerja.",
            sampleTip = "Harness dibentangkan agar jahitan pengaman dan carabiner terlihat jelas tidak retak.",
            ocrFieldLabel = "Standar EN/SNI & Kondisi APD",
            iconName = "security"
        ),
        EquipmentMaster(
            id = "EQ-26",
            name = "Net Book",
            category = "komunikasi",
            type = EquipmentType.PHOTO_OCR,
            photoGuide = "Foto netbook/laptop kerja teknisi dalam kondisi menyala, menampilkan layar desktop atau console aplikasi konfigurasi.",
            sampleTip = "Tunjukkan keyboard utuh dan layar tidak pecah.",
            ocrFieldLabel = "Nomor Seri / Hostname Perangkat",
            iconName = "laptop"
        ),
        EquipmentMaster(
            id = "EQ-27",
            name = "Paket Internet 2GB",
            category = "komunikasi",
            type = EquipmentType.STATUS,
            photoGuide = "Verifikasi status kuota paket internet minimum 2GB aktif untuk kelancaran pelaporan online teknisi.",
            sampleTip = "Dapat melampirkan screenshot SMS status kuota atau aplikasi MyTelkomsel jika diperlukan.",
            ocrFieldLabel = "Status Kuota Aktif & Masa Berlaku",
            iconName = "wifi"
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
