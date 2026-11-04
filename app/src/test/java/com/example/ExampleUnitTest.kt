package com.example

import com.example.data.local.MasterData
import com.example.data.model.EquipmentType
import com.example.service.ocr.OcrService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testMasterDataContains27Equipment() {
        val equipment = MasterData.equipmentList
        assertEquals(27, equipment.size)

        // Verify key equipment items from Tabel 2.7
        val splicer = equipment.find { it.id == "EQ-01" }
        assertNotNull(splicer)
        assertEquals("Splicer", splicer?.name)
        assertEquals("alat_ukur", splicer?.category)
        assertEquals(EquipmentType.PHOTO_OCR, splicer?.type)

        val otdr = equipment.find { it.id == "EQ-05" }
        assertNotNull(otdr)
        assertEquals("OTDR", otdr?.name)
        assertEquals("alat_ukur", otdr?.category)
        assertEquals(EquipmentType.PHOTO_OCR, otdr?.type)

        val kbmRoda2 = equipment.find { it.id == "EQ-07" }
        assertNotNull(kbmRoda2)
        assertEquals("KBM Roda 2", kbmRoda2?.name)
        assertEquals("kendaraan", kbmRoda2?.category)
        assertEquals(EquipmentType.PHOTO, kbmRoda2?.type)

        val genset = equipment.find { it.id == "EQ-20" }
        assertNotNull(genset)
        assertEquals("Genset 1000 Watt & Lampu Penerangan", genset?.name)
        assertEquals("genset", genset?.category)
        assertEquals(EquipmentType.PHOTO_OCR, genset?.type)

        val paketInternet = equipment.find { it.id == "EQ-27" }
        assertNotNull(paketInternet)
        assertEquals("Paket Internet 2GB", paketInternet?.name)
        assertEquals("komunikasi", paketInternet?.category)
        assertEquals(EquipmentType.STATUS, paketInternet?.type)
    }

    @Test
    fun testOcrSpeedometerExtraction() = runBlocking {
        val result = OcrService.processOcr("EQ-07", "KBM Roda 2")
        assertNotNull(result)
        assertTrue(result.confidence >= 90)
        assertTrue(result.extractedText.contains("ODOMETER"))
        assertTrue(result.hasValidTimestampWatermark)
    }

    @Test
    fun testOcrExtractsDayMonthYearWatermark() = runBlocking {
        val result = OcrService.processOcr("EQ-05", "OTDR")
        assertNotNull(result)
        assertTrue(result.detectedDay.isNotBlank())
        assertTrue(result.detectedMonth.isNotBlank())
        assertTrue(result.detectedYear.isNotBlank())
        assertTrue(result.extractedText.contains("Tanggal: ${result.detectedDay}"))
        assertTrue(result.extractedText.contains("Bulan: ${result.detectedMonth}"))
        assertTrue(result.extractedText.contains("Tahun: ${result.detectedYear}"))
    }

    @Test
    fun testReuploadDomainLogic() {
        val rejectedSub = com.example.data.model.InspectionSubmission(
            id = "SUB-TEST",
            periodId = "PRD-2026-10",
            technicianId = "TECH-01",
            technicianName = "Ahmad Fauzan",
            technicianSto = "STO Padang Baru",
            equipmentId = "EQ-15",
            equipmentName = "Tangga Dorong Aluminium 5.1 Meter",
            equipmentType = EquipmentType.PHOTO,
            status = com.example.data.model.InspectionStatus.REJECTED,
            adminFeedback = "Tali pengikat tidak tampak jelas."
        )

        // Simulate reupload
        val reuploaded = rejectedSub.copy(
            status = com.example.data.model.InspectionStatus.PENDING,
            adminFeedback = null,
            updatedAt = System.currentTimeMillis()
        )

        assertEquals(com.example.data.model.InspectionStatus.PENDING, reuploaded.status)
        assertEquals(null, reuploaded.adminFeedback)
    }

    @Test
    fun testUploadRejectedWhenFileExceeds10Mb() {
        val largeFileBytes = 11 * 1024 * 1024L // 11 MB > 10 MB
        val result = com.example.service.validation.UploadValidator.validateDirect(
            fileSizeBytes = largeFileBytes,
            fileExtensionOrMime = "jpg",
            currentPhotoCount = 1
        )
        assertTrue(result is com.example.service.validation.UploadValidationResult.Rejected)
        val rejected = result as com.example.service.validation.UploadValidationResult.Rejected
        assertEquals(com.example.service.validation.UploadValidationResult.ErrorCode.FILE_TOO_LARGE, rejected.code)
        assertTrue(rejected.reason.contains("melebihi batas maksimum 10 MB"))
    }

    @Test
    fun testUploadRejectedWhenFormatNotJpgPngWebp() {
        // Test PDF
        val pdfResult = com.example.service.validation.UploadValidator.validateDirect(
            fileSizeBytes = 2 * 1024 * 1024L,
            fileExtensionOrMime = "pdf",
            currentPhotoCount = 0
        )
        assertTrue(pdfResult is com.example.service.validation.UploadValidationResult.Rejected)
        val pdfRejected = pdfResult as com.example.service.validation.UploadValidationResult.Rejected
        assertEquals(com.example.service.validation.UploadValidationResult.ErrorCode.INVALID_FORMAT, pdfRejected.code)
        assertTrue(pdfRejected.reason.contains("Format berkas '.pdf' tidak didukung"))

        // Test GIF
        val gifResult = com.example.service.validation.UploadValidator.validateDirect(
            fileSizeBytes = 1 * 1024 * 1024L,
            fileExtensionOrMime = "gif",
            currentPhotoCount = 0
        )
        assertTrue(gifResult is com.example.service.validation.UploadValidationResult.Rejected)
    }

    @Test
    fun testUploadRejectedWhenUploadingFourthPhoto() {
        // Saat sudah ada 3 foto, coba unggah foto ke-4
        val result = com.example.service.validation.UploadValidator.validateDirect(
            fileSizeBytes = 1 * 1024 * 1024L,
            fileExtensionOrMime = "jpg",
            currentPhotoCount = 3
        )
        assertTrue(result is com.example.service.validation.UploadValidationResult.Rejected)
        val rejected = result as com.example.service.validation.UploadValidationResult.Rejected
        assertEquals(com.example.service.validation.UploadValidationResult.ErrorCode.MAX_PHOTOS_EXCEEDED, rejected.code)
        assertTrue(rejected.reason.contains("Batas maksimum 3 foto tercapai"))
        assertTrue(rejected.reason.contains("foto ke-4"))
    }

    @Test
    fun testUploadAcceptedWhenValid() {
        val validJpg = com.example.service.validation.UploadValidator.validateDirect(
            fileSizeBytes = 3 * 1024 * 1024L, // 3 MB
            fileExtensionOrMime = "jpg",
            currentPhotoCount = 0
        )
        assertTrue(validJpg is com.example.service.validation.UploadValidationResult.Success)

        val validPng = com.example.service.validation.UploadValidator.validateDirect(
            fileSizeBytes = 5 * 1024 * 1024L, // 5 MB
            fileExtensionOrMime = "png",
            currentPhotoCount = 1
        )
        assertTrue(validPng is com.example.service.validation.UploadValidationResult.Success)

        val validWebp = com.example.service.validation.UploadValidator.validateDirect(
            fileSizeBytes = 2 * 1024 * 1024L, // 2 MB
            fileExtensionOrMime = "webp",
            currentPhotoCount = 2
        )
        assertTrue(validWebp is com.example.service.validation.UploadValidationResult.Success)
    }
}
