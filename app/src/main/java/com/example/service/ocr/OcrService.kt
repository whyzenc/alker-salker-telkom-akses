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
    val detectedDate: String,
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
        delay(1200)

        val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm 'WIB'", Locale("id", "ID"))
        val now = dateFormat.format(Date())
        val location = padangLocations.random()
        val confidence = Random.nextInt(92, 99)

        val extracted = when (equipmentId) {
            "EQ-13" -> { // Kendaraan Operasional
                val km = Random.nextInt(38_000, 52_000)
                "ODOMETER: $km KM\nPLAT: BA 1948 ${listOf("QX", "TR", "AB").random()}\nSTATUS SPEEDOMETER: AKTIF & TERBACA"
            }
            "EQ-04" -> { // OTDR
                val loss = String.format(Locale.US, "%.2f", Random.nextDouble(0.15, 0.25))
                val power = String.format(Locale.US, "%.2f", Random.nextDouble(-22.0, -17.0))
                "STATUS LAYAR: MENYALA (ACTIVE)\nWL: 1310/1550 nm\nTOTAL LOSS: $loss dB/km\nPOWER LEVEL: $power dBm"
            }
            "EQ-01" -> { // Fusion Splicer
                val sn = "FSM-70S-${Random.nextInt(10000, 99999)}"
                val arc = Random.nextInt(1200, 3400)
                "SN: $sn\nCALIBRATION: VALID\nARC COUNT: $arc\nSTATUS: CLEAN & OPERATIONAL"
            }
            "EQ-07" -> { // Optical Power Meter
                val dbm = String.format(Locale.US, "%.2f", Random.nextDouble(-19.5, -15.0))
                "DISPLAY: MENYALA\nWAVELENGTH: 1490 nm\nPOWER MEASUREMENT: $dbm dBm\nBATTERY: 85%"
            }
            "EQ-06" -> { // LAN Tester
                "STATUS: ACTIVE\nPINS 1-2-3-4-5-6-7-8: ALL PASS / GREEN\nGROUND: OK"
            }
            "EQ-11" -> { // HP Android
                "PERANGKAT: SAMSUNG Galaxy A34 5G\nRAM: 8GB / STORAGE: 128GB\nAPP MYTA: v4.8.2 AKTIF\nBATTERY: 92%"
            }
            "EQ-14" -> { // ID Card
                "PT TELKOM AKSES\nNAMA: AHMAD FAUZAN\nNIK: 940218\nJABATAN: TEKNISI ASSURANCE PADANG"
            }
            "EQ-15" -> { // Mini OTDR
                val dist = Random.nextInt(120, 850)
                "EVENT DETECTED: FAULT DISTANCE $dist METER\nREFLECTANCE: -45.2 dB\nSTATUS: RUNNING"
            }
            else -> {
                "LABEL ALAT: $equipmentName\nKONDISI FISIK: BAIK\nWATERMARK TIMESTAMP: TERVERIFIKASI"
            }
        }

        return OcrExtractionResult(
            extractedText = extracted,
            confidence = confidence,
            hasValidTimestampWatermark = true,
            detectedDate = now,
            detectedLocation = location,
            warningMessage = null
        )
    }
}
