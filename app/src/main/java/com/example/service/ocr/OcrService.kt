package com.example.service.ocr

import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

data class OcrExtractionResult(
    val extractedText: String,
    val confidence: Int,
    val hasValidTimestampWatermark: Boolean,
    val detectedDate: String, // "03 November 2026 17:16 WIB"
    val detectedDay: String, // "03"
    val detectedMonth: String, // "November"
    val detectedYear: String, // "2026"
    val detectedTime: String, // "17:16 WIB"
    val detectedLocation: String,
    val warningMessage: String? = null
)

object OcrService {

    private val padangLocations = listOf(
        "Kec. Padang Barat, Kota Padang",
        "Kec. Padang Timur, Kota Padang",
        "STO Belimbing, Kuranji, Kota Padang",
        "STO Padang Baru, Padang Utara",
        "STO Simpang Haru, Padang Timur"
    )

    suspend fun processOcr(equipmentId: String, equipmentName: String): OcrExtractionResult {
        // Simulasi latensi OCR engine (PaddleOCR)
        delay(900)

        val localeId = Locale("id", "ID")
        val now = Date()
        val fullDateFormat = SimpleDateFormat("dd MMMM yyyy HH:mm 'WIB'", localeId)
        val dayFormat = SimpleDateFormat("dd", localeId)
        val monthFormat = SimpleDateFormat("MMMM", localeId)
        val yearFormat = SimpleDateFormat("yyyy", localeId)
        val timeFormat = SimpleDateFormat("HH:mm 'WIB'", localeId)

        val detectedDate = fullDateFormat.format(now)
        val detectedDay = dayFormat.format(now)
        val detectedMonth = monthFormat.format(now)
        val detectedYear = yearFormat.format(now)
        val detectedTime = timeFormat.format(now)

        val location = padangLocations.random()
        val confidence = Random.nextInt(94, 99)

        val equipmentDetail = when (equipmentId) {
            "EQ-01" -> { // Splicer
                val sn = "FSM-70S-${Random.nextInt(10000, 99999)}"
                val arc = Random.nextInt(1200, 3400)
                "ALAT: SPLICER (FUSION SPLICER)\nSN: $sn\nCALIBRATION: VALID\nARC COUNT: $arc\nSTATUS: CLEAN & OPERATIONAL"
            }
            "EQ-02" -> { // OPM
                val dbm = String.format(Locale.US, "%.2f", Random.nextDouble(-19.5, -15.0))
                "ALAT: OPM (OPTICAL POWER METER)\nDISPLAY: MENYALA\nWAVELENGTH: 1490 nm\nPOWER MEASUREMENT: $dbm dBm\nBATTERY: 88%"
            }
            "EQ-03" -> { // VFL
                "ALAT: VFL (VISUAL FAULT LOCATOR)\nLASER STATUS: EMITTING (RED 650nm)\nMODE: CONTINUOUS WAVE (CW)\nOUTPUT POWER: 10mW AKTIF"
            }
            "EQ-04" -> { // LAN Tester
                "ALAT: LAN TESTER\nSTATUS: ACTIVE\nPINS 1-2-3-4-5-6-7-8: ALL PASS / GREEN\nGROUND: OK"
            }
            "EQ-05" -> { // OTDR
                val loss = String.format(Locale.US, "%.2f", Random.nextDouble(0.15, 0.25))
                val power = String.format(Locale.US, "%.2f", Random.nextDouble(-22.0, -17.0))
                "ALAT: OTDR\nSTATUS LAYAR: MENYALA (ACTIVE)\nWL: 1310/1550 nm\nTOTAL LOSS: $loss dB/km\nPOWER LEVEL: $power dBm"
            }
            "EQ-06" -> { // Mini OTDR
                val dist = Random.nextInt(120, 850)
                "ALAT: MINI OTDR / FIBER RANGER\nEVENT DETECTED: FAULT DISTANCE $dist METER\nREFLECTANCE: -45.2 dB\nSTATUS: RUNNING"
            }
            "EQ-07" -> { // KBM Roda 2
                val km = Random.nextInt(32_000, 48_000)
                "KBM RODA 2 (MOTOR OPERASIONAL)\nODOMETER: $km KM\nPLAT: BA 1948 ${listOf("QX", "TR", "AB").random()}\nSTATUS KENDARAAN: LAYAK OPERASIONAL"
            }
            "EQ-08" -> { // KBM Roda 4
                val km = Random.nextInt(65_000, 95_000)
                "KBM RODA 4 (MOBIL OPERASIONAL)\nODOMETER: $km KM\nPLAT: BA 8102 ${listOf("TA", "QA", "BA").random()}\nSTATUS KENDARAAN: LAYAK JALAN"
            }
            "EQ-09" -> { // Power Bank Vallins
                "ALAT: POWER BANK VALLINS\nKAPASITAS: 20000 mAh\nINDIKATOR DAYA: 4/4 LED MENYALA (100%)\nPORT: DUAL USB 5V-2.4A FAST CHARGE"
            }
            "EQ-11" -> { // ID Card
                "KARTU PENGENAL RESMI PT TELKOM AKSES\nNAMA: AHMAD FAUZAN\nNIK: 940218\nJABATAN: TEKNISI ASSURANCE PADANG\nSTATUS: AKTIF"
            }
            "EQ-14" -> { // One Click Cleanser
                "ALAT: ONE CLICK CLEANSER 2.5mm / 1.25mm\nSISA SIKLUS PEMBERSIH: 500+ CLICKS\nSTATUS PENA: BERSIH & PENUTUP TERPASANG"
            }
            "EQ-17" -> { // Toolkit Fo
                "ALAT: TOOLKIT FO\nCLEAVER SN: FC-6S-88319\nSTRIPPER: MILLER 3-HOLE PASS\nSTATUS: LENGKAP & TAJAM"
            }
            "EQ-18" -> { // Toolkit Set
                "ALAT: TOOLKIT SET LENGKAP\nITEM: TANG, OBENG (+/-), CUTTER, METERAN, ISOLASI\nSTATUS FISIK: LENGKAP & TERAWAT"
            }
            "EQ-20" -> { // Genset 1000 Watt & Lampu Penerangan
                "ALAT: GENSET 1000 WATT & LAMPU PENERANGAN\nOUTPUT VOLTAGE: 224V 50Hz STABIL\nLAMPU SOROT: 200W LED OPERASIONAL"
            }
            "EQ-22" -> { // Cable Console + Cable USB to RS232
                "ALAT: CABLE CONSOLE RJ45 + USB RS232\nCHIPSET: FTDI FT232R\nKONEKTOR: PIN UTUH & BERSIH"
            }
            "EQ-23" -> { // Crimping Tools RJ11 dan RJ45 Cat-5
                "ALAT: CRIMPING TOOLS RJ11 & RJ45 CAT-5\nPISAU PENGUPAS: TAJAM & PRESISI\nMEKANISME RATCHET: BERFUNGSI BAIK"
            }
            "EQ-24" -> { // Alat komunikasi
                "ALAT: ALAT KOMUNIKASI (HT / PHONE)\nCHANNEL: CH-04 ASSURANCE PADANG\nSTATUS SIGNAL: EXCELLENT / CONNECTED"
            }
            "EQ-25" -> { // Body Harness, Helm pengaman dan Kaus tangan
                "ALAT: BODY HARNESS, HELM & KAUS TANGAN\nSERTIFIKASI: EN 361 / SNI K3 VALID\nDOUBLE LANYARD & CARABINER: AMAN & UTUH"
            }
            "EQ-26" -> { // Net Book
                "ALAT: NET BOOK OPERASIONAL\nHOSTNAME: TA-PDG-TECH01\nTOOLS: PUTTY, WINBOX, PING TESTER READY\nSTATUS OS: ONLINE"
            }
            "EQ-27" -> { // Paket Internet 2GB
                "STATUS PAKET: INTERNET 2GB AKTIF\nOPERATOR: TELKOMSEL ENTERPRISE\nSISA KUOTA: 2.45 GB\nSTATUS: AKTIF & TERVERIFIKASI"
            }
            // Backward compatibility
            "EQ-13" -> {
                val km = Random.nextInt(38_000, 52_000)
                "ODOMETER: $km KM\nPLAT: BA 1948 ${listOf("QX", "TR", "AB").random()}\nSTATUS SPEEDOMETER: AKTIF & TERBACA"
            }
            else -> {
                if (equipmentName.contains("OTDR", ignoreCase = true)) {
                    "ALAT: OTDR\nSTATUS LAYAR: MENYALA (ACTIVE)\nWL: 1310/1550 nm\nTOTAL LOSS: 0.18 dB/km"
                } else if (equipmentName.contains("KBM", ignoreCase = true) || equipmentName.contains("Kendaraan", ignoreCase = true)) {
                    val km = Random.nextInt(38_000, 52_000)
                    "ODOMETER: $km KM\nPLAT: BA 1948 QX\nSTATUS SPEEDOMETER: AKTIF & TERBACA"
                } else {
                    "LABEL ALAT: $equipmentName\nKONDISI FISIK: BAIK\nKELENGKAPAN: LENGKAP & SESUAI SOP TELKOM"
                }
            }
        }

        val fullExtracted = """
[WATERMARK OVERLAY TERBACA]
Tanggal: $detectedDay
Bulan: $detectedMonth
Tahun: $detectedYear
Waktu: $detectedTime
Lokasi: $location
Status Watermark: VALID & TERVERIFIKASI
----------------------------------------
[DATA ALAT]
$equipmentDetail
        """.trimIndent()

        return OcrExtractionResult(
            extractedText = fullExtracted,
            confidence = confidence,
            hasValidTimestampWatermark = true,
            detectedDate = detectedDate,
            detectedDay = detectedDay,
            detectedMonth = detectedMonth,
            detectedYear = detectedYear,
            detectedTime = detectedTime,
            detectedLocation = location,
            warningMessage = null
        )
    }
}
