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
    fun testMasterDataContains15Equipment() {
        val equipment = MasterData.equipmentList
        assertEquals(15, equipment.size)

        // Verify key equipment types
        val otdr = equipment.find { it.id == "EQ-04" }
        assertNotNull(otdr)
        assertEquals("OTDR", otdr?.name)
        assertEquals(EquipmentType.PHOTO_OCR, otdr?.type)

        val tangga = equipment.find { it.id == "EQ-09" }
        assertNotNull(tangga)
        assertEquals(EquipmentType.PHOTO, tangga?.type)

        val kendaraan = equipment.find { it.id == "EQ-13" }
        assertNotNull(kendaraan)
        assertEquals(EquipmentType.PHOTO_OCR, kendaraan?.type)
    }

    @Test
    fun testOcrSpeedometerExtraction() = runBlocking {
        val result = OcrService.processOcr("EQ-13", "Kendaraan Operasional")
        assertNotNull(result)
        assertTrue(result.confidence >= 90)
        assertTrue(result.extractedText.contains("ODOMETER"))
        assertTrue(result.hasValidTimestampWatermark)
    }
}
