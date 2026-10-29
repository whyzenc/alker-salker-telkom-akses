package com.example.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.EquipmentType
import com.example.data.model.InspectionStatus
import com.example.data.model.InspectionSubmission
import com.example.data.model.User
import com.example.data.model.UserRole

@Entity(tableName = "app_users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val nik: String,
    val password: String,
    val email: String,
    val phone: String,
    val role: String, // "TECHNICIAN" or "ADMIN"
    val stoUnit: String,
    val team: String
) {
    fun toDomain(): User = User(
        id = id,
        name = name,
        nik = nik,
        email = email,
        phone = phone,
        role = if (role == "ADMIN") UserRole.ADMIN else UserRole.TECHNICIAN,
        stoUnit = stoUnit,
        team = team
    )
}

@Entity(tableName = "inspection_submissions")
data class InspectionEntity(
    @PrimaryKey val id: String,
    val periodId: String,
    val technicianId: String,
    val technicianName: String,
    val technicianSto: String,
    val equipmentId: String,
    val equipmentName: String,
    val equipmentType: String,
    val photoPath: String?,
    val originalSizeKb: Int,
    val compressedSizeKb: Int,
    val capturedDate: String,
    val capturedLocation: String,
    val hasTimestampWatermark: Boolean,
    val ocrConfidence: Int,
    val ocrRawResult: String,
    val ocrCorrectedResult: String,
    val status: String, // "UNCHECKED", "PENDING", "VERIFIED", "REJECTED"
    val adminFeedback: String?,
    val verifiedBy: String?,
    val verifiedAt: String?,
    val updatedAt: Long
) {
    fun toDomain(): InspectionSubmission = InspectionSubmission(
        id = id,
        periodId = periodId,
        technicianId = technicianId,
        technicianName = technicianName,
        technicianSto = technicianSto,
        equipmentId = equipmentId,
        equipmentName = equipmentName,
        equipmentType = if (equipmentType == "PHOTO_OCR") EquipmentType.PHOTO_OCR else EquipmentType.PHOTO,
        photoUri = photoPath,
        originalSizeKb = originalSizeKb,
        compressedSizeKb = compressedSizeKb,
        capturedDate = capturedDate,
        capturedLocation = capturedLocation,
        hasTimestampWatermark = hasTimestampWatermark,
        ocrConfidence = ocrConfidence,
        ocrRawResult = ocrRawResult,
        ocrCorrectedResult = ocrCorrectedResult,
        status = when (status) {
            "VERIFIED" -> InspectionStatus.VERIFIED
            "PENDING" -> InspectionStatus.PENDING
            "REJECTED" -> InspectionStatus.REJECTED
            else -> InspectionStatus.UNCHECKED
        },
        adminFeedback = adminFeedback,
        verifiedBy = verifiedBy,
        verifiedAt = verifiedAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(sub: InspectionSubmission): InspectionEntity = InspectionEntity(
            id = sub.id,
            periodId = sub.periodId,
            technicianId = sub.technicianId,
            technicianName = sub.technicianName,
            technicianSto = sub.technicianSto,
            equipmentId = sub.equipmentId,
            equipmentName = sub.equipmentName,
            equipmentType = sub.equipmentType.name,
            photoPath = sub.photoUri,
            originalSizeKb = sub.originalSizeKb,
            compressedSizeKb = sub.compressedSizeKb,
            capturedDate = sub.capturedDate,
            capturedLocation = sub.capturedLocation,
            hasTimestampWatermark = sub.hasTimestampWatermark,
            ocrConfidence = sub.ocrConfidence,
            ocrRawResult = sub.ocrRawResult,
            ocrCorrectedResult = sub.ocrCorrectedResult,
            status = sub.status.name,
            adminFeedback = sub.adminFeedback,
            verifiedBy = sub.verifiedBy,
            verifiedAt = sub.verifiedAt,
            updatedAt = sub.updatedAt
        )
    }
}

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String,
    val actorName: String,
    val actorRole: String,
    val action: String,
    val equipmentName: String,
    val technicianName: String,
    val timestamp: String,
    val notes: String
)
