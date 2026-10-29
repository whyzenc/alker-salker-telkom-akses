package com.example.data.model

enum class EquipmentType {
    PHOTO,
    PHOTO_OCR
}

enum class InspectionStatus(val label: String) {
    UNCHECKED("Belum Diperiksa"),
    PENDING("Menunggu Verifikasi"),
    VERIFIED("Terverifikasi"),
    REJECTED("Perlu Perbaikan")
}

enum class UserRole(val label: String) {
    TECHNICIAN("Teknisi Lapangan"),
    ADMIN("Admin Assurance")
}

data class User(
    val id: String,
    val name: String,
    val nik: String,
    val email: String,
    val phone: String,
    val role: UserRole,
    val stoUnit: String,
    val team: String
)

data class Period(
    val id: String,
    val name: String,
    val startDate: String,
    val endDate: String,
    val isActive: Boolean,
    val daysRemaining: Int
)

data class EquipmentMaster(
    val id: String,
    val name: String,
    val category: String, // "Alat Kerja (Alker)" atau "Sarana Kerja (Sarker)"
    val type: EquipmentType,
    val photoGuide: String,
    val sampleTip: String,
    val ocrFieldLabel: String? = null,
    val iconName: String = "build"
)

data class InspectionSubmission(
    val id: String,
    val periodId: String,
    val technicianId: String,
    val technicianName: String,
    val technicianSto: String,
    val equipmentId: String,
    val equipmentName: String,
    val equipmentType: EquipmentType,
    val photoUri: String? = null,
    val originalSizeKb: Int = 0,
    val compressedSizeKb: Int = 0,
    val capturedDate: String = "",
    val capturedLocation: String = "",
    val hasTimestampWatermark: Boolean = true,
    val ocrConfidence: Int = 0, // Misal 94%
    val ocrRawResult: String = "",
    val ocrCorrectedResult: String = "",
    val status: InspectionStatus = InspectionStatus.UNCHECKED,
    val adminFeedback: String? = null,
    val verifiedBy: String? = null,
    val verifiedAt: String? = null,
    val updatedAt: Long = System.currentTimeMillis(),
    val isDraft: Boolean = false
)

data class AuditLog(
    val id: String,
    val actorName: String,
    val actorRole: String,
    val action: String, // "SETUJUI", "TOLAK", "KOREKSI_OCR", "UNGGAH"
    val equipmentName: String,
    val technicianName: String,
    val timestamp: String,
    val notes: String = ""
)

data class TechnicianSummary(
    val technician: User,
    val totalItems: Int,
    val verifiedCount: Int,
    val pendingCount: Int,
    val rejectedCount: Int,
    val uncheckedCount: Int,
    val completionPercentage: Int
)
