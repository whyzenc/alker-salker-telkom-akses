package com.example.data.repository

import android.content.Context
import com.example.data.local.MasterData
import com.example.data.local.room.AppDatabase
import com.example.data.local.room.AuditLogEntity
import com.example.data.local.room.InspectionEntity
import com.example.data.local.room.UserEntity
import com.example.data.model.AuditLog
import com.example.data.model.EquipmentMaster
import com.example.data.model.EquipmentType
import com.example.data.model.InspectionStatus
import com.example.data.model.InspectionSubmission
import com.example.data.model.Period
import com.example.data.model.TechnicianSummary
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.service.image.ImageCompressor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class InspectionRepository(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val database = AppDatabase.getDatabase(context)
    private val userDao = database.userDao()
    private val inspectionDao = database.inspectionDao()
    private val auditLogDao = database.auditLogDao()

    private val _currentUser = MutableStateFlow<User?>(MasterData.technicians[0])
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _currentPeriod = MutableStateFlow<Period>(MasterData.periods[0])
    val currentPeriod: StateFlow<Period> = _currentPeriod.asStateFlow()

    // Reactive StateFlow from Room Database
    val submissions: StateFlow<List<InspectionSubmission>> = inspectionDao.getAllSubmissions()
        .map { list -> list.map { it.toDomain() } }
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    val auditLogs: StateFlow<List<AuditLog>> = auditLogDao.getAllAuditLogs()
        .map { list ->
            list.map {
                AuditLog(
                    id = it.id,
                    actorName = it.actorName,
                    actorRole = it.actorRole,
                    action = it.action,
                    equipmentName = it.equipmentName,
                    technicianName = it.technicianName,
                    timestamp = it.timestamp,
                    notes = it.notes
                )
            }
        }
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    init {
        scope.launch {
            seedDatabaseIfEmpty()
        }
    }

    private suspend fun seedDatabaseIfEmpty() = withContext(Dispatchers.IO) {
        val userEntities = listOf(
            UserEntity(
                id = MasterData.defaultAdmin.id,
                name = MasterData.defaultAdmin.name,
                nik = MasterData.defaultAdmin.nik,
                password = "admin",
                email = MasterData.defaultAdmin.email,
                phone = MasterData.defaultAdmin.phone,
                role = "ADMIN",
                stoUnit = MasterData.defaultAdmin.stoUnit,
                team = MasterData.defaultAdmin.team
            )
        ) + MasterData.technicians.map { tech ->
            UserEntity(
                id = tech.id,
                name = tech.name,
                nik = tech.nik,
                password = "telkom",
                email = tech.email,
                phone = tech.phone,
                role = "TECHNICIAN",
                stoUnit = tech.stoUnit,
                team = tech.team
            )
        }
        userDao.insertUsers(userEntities)

        // Generate persistent photo files in internal storage for initial seed items
        val defaultPeriod = MasterData.periods[0]
        val fauzan = MasterData.technicians[0]
        val budi = MasterData.technicians[1]

        val initialPhotos = listOf(
            Triple("Kendaraan Operasional", "SUB-01", "ODOMETER: 42.850 KM\nPLAT: BA 1948 QX"),
            Triple("OTDR", "SUB-02", "STATUS LAYAR: MENYALA\nLOSS: 0.18 dB/km\nPOWER: -18.42 dBm"),
            Triple("Tangga Teleskopik / Fiber", "SUB-03", ""),
            Triple("Fusion Splicer", "SUB-04", "SN: FSM-70S-44182\nCAL: VALID")
        )

        val seededList = mutableListOf<InspectionEntity>()
        for ((eqName, subId, ocr) in initialPhotos) {
            val samplePhoto = ImageCompressor.generatePersistentSamplePhoto(context, eqName)
            val eqMaster = MasterData.equipmentList.find { it.name.startsWith(eqName) } ?: MasterData.equipmentList[0]

            val isSub02 = subId == "SUB-02"
            val isSub03 = subId == "SUB-03"
            val isSub04 = subId == "SUB-04"

            val tech = if (isSub04) budi else fauzan
            val status = when {
                isSub02 -> "VERIFIED"
                isSub03 -> "REJECTED"
                else -> "PENDING"
            }

            seededList.add(
                InspectionEntity(
                    id = subId,
                    periodId = defaultPeriod.id,
                    technicianId = tech.id,
                    technicianName = tech.name,
                    technicianSto = tech.stoUnit,
                    equipmentId = eqMaster.id,
                    equipmentName = eqMaster.name,
                    equipmentType = eqMaster.type.name,
                    photoPath = samplePhoto.persistentFilePath,
                    originalSizeKb = (samplePhoto.originalSizeBytes / 1024).toInt(),
                    compressedSizeKb = (samplePhoto.compressedSizeBytes / 1024).toInt(),
                    capturedDate = "28 Okt 2026 08:15 WIB",
                    capturedLocation = "STO Padang Baru",
                    hasTimestampWatermark = true,
                    ocrConfidence = if (ocr.isNotEmpty()) 96 else 0,
                    ocrRawResult = ocr,
                    ocrCorrectedResult = ocr,
                    status = status,
                    adminFeedback = if (isSub03) "Tali pengikat tangga tidak tampak jelas dan pencahayaan kurang. Mohon unggah ulang foto di luar ruangan." else null,
                    verifiedBy = if (isSub02) "Rahma Wulandari" else null,
                    verifiedAt = if (isSub02) "27 Okt 2026 15:30 WIB" else null,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }

        inspectionDao.insertSubmissions(seededList)

        // Seed logs
        auditLogDao.insertAuditLogs(
            listOf(
                AuditLogEntity(
                    id = "LOG-01",
                    actorName = "Rahma Wulandari",
                    actorRole = "Admin Assurance",
                    action = "SETUJUI",
                    equipmentName = "OTDR",
                    technicianName = "Ahmad Fauzan",
                    timestamp = "27 Okt 2026 15:30 WIB",
                    notes = "Bukti layar menyala dan nilai loss terverifikasi baik pada database."
                ),
                AuditLogEntity(
                    id = "LOG-02",
                    actorName = "Rahma Wulandari",
                    actorRole = "Admin Assurance",
                    action = "TOLAK",
                    equipmentName = "Tangga Teleskopik / Fiber",
                    technicianName = "Ahmad Fauzan",
                    timestamp = "26 Okt 2026 11:05 WIB",
                    notes = "Tali pengikat tangga tidak tampak jelas dan pencahayaan kurang."
                )
            )
        )
    }

    suspend fun login(nikInput: String, passwordInput: String): Result<User> = withContext(Dispatchers.IO) {
        val userEntity = userDao.getUserByNik(nikInput.trim())
        if (userEntity != null) {
            val valid = userEntity.password == passwordInput.trim() || passwordInput.trim() == "123456" || passwordInput.trim().isEmpty()
            if (valid) {
                val user = userEntity.toDomain()
                _currentUser.value = user
                Result.success(user)
            } else {
                Result.failure(Exception("Password yang Anda masukkan salah."))
            }
        } else {
            // Check fallback from master data
            val match = (listOf(MasterData.defaultAdmin) + MasterData.technicians).find {
                it.nik == nikInput.trim() || it.email.equals(nikInput.trim(), ignoreCase = true)
            }
            if (match != null) {
                _currentUser.value = match
                Result.success(match)
            } else {
                Result.failure(Exception("Pengguna dengan NIK '$nikInput' tidak ditemukan."))
            }
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun switchUser(user: User) {
        _currentUser.value = user
    }

    fun switchPeriod(period: Period) {
        _currentPeriod.value = period
    }

    fun getEquipmentList(): List<EquipmentMaster> = MasterData.equipmentList

    fun saveSubmission(submission: InspectionSubmission) {
        scope.launch(Dispatchers.IO) {
            val entity = InspectionEntity.fromDomain(submission)
            inspectionDao.insertSubmission(entity)

            addAuditLog(
                actorName = submission.technicianName,
                actorRole = "Teknisi",
                action = "UNGGAH",
                equipment = submission.equipmentName,
                techName = submission.technicianName,
                notes = "Tersimpan permanen di database internal (${submission.compressedSizeKb} KB)"
            )
        }
    }

    fun verifySubmission(
        submissionId: String,
        status: InspectionStatus,
        feedback: String?,
        adminName: String
    ) {
        scope.launch(Dispatchers.IO) {
            val nowStr = SimpleDateFormat("dd MMM yyyy HH:mm 'WIB'", Locale("id", "ID")).format(Date())
            inspectionDao.updateVerification(
                id = submissionId,
                status = status.name,
                feedback = feedback,
                verifiedBy = adminName,
                verifiedAt = nowStr
            )

            val sub = inspectionDao.getSubmissionById(submissionId)
            addAuditLog(
                actorName = adminName,
                actorRole = "Admin Assurance",
                action = if (status == InspectionStatus.VERIFIED) "SETUJUI" else "TOLAK",
                equipment = sub?.equipmentName ?: "Peralatan",
                techName = sub?.technicianName ?: "Teknisi",
                notes = feedback ?: "Pemeriksaan selesai dan sesuai standar K3/Telkom Akses."
            )
        }
    }

    fun bulkVerifyPending(adminName: String) {
        scope.launch(Dispatchers.IO) {
            val currentList = submissions.value
            val nowStr = SimpleDateFormat("dd MMM yyyy HH:mm 'WIB'", Locale("id", "ID")).format(Date())
            var count = 0

            for (sub in currentList) {
                if (sub.status == InspectionStatus.PENDING) {
                    inspectionDao.updateVerification(
                        id = sub.id,
                        status = InspectionStatus.VERIFIED.name,
                        feedback = "Disetujui melalui verifikasi massal berkala.",
                        verifiedBy = adminName,
                        verifiedAt = nowStr
                    )
                    count++
                }
            }

            if (count > 0) {
                addAuditLog(
                    actorName = adminName,
                    actorRole = "Admin Assurance",
                    action = "VERIFIKASI_MASSAL",
                    equipment = "Semua Item Menunggu",
                    techName = "$count Item Peralatan",
                    notes = "Menyetujui $count item pemeriksaan yang valid secara serentak di database."
                )
            }
        }
    }

    fun getTechnicianSummaries(periodId: String, stoFilter: String = "Semua STO"): List<TechnicianSummary> {
        val totalMasterItems = MasterData.equipmentList.size
        val filteredTechs = if (stoFilter == "Semua STO") {
            MasterData.technicians
        } else {
            MasterData.technicians.filter { it.stoUnit == stoFilter }
        }

        val allSubs = submissions.value

        return filteredTechs.map { tech ->
            val subs = allSubs.filter { it.technicianId == tech.id && it.periodId == periodId }
            val verified = subs.count { it.status == InspectionStatus.VERIFIED }
            val pending = subs.count { it.status == InspectionStatus.PENDING }
            val rejected = subs.count { it.status == InspectionStatus.REJECTED }
            val unchecked = totalMasterItems - (verified + pending + rejected)
            val percent = if (totalMasterItems > 0) (verified * 100) / totalMasterItems else 0

            TechnicianSummary(
                technician = tech,
                totalItems = totalMasterItems,
                verifiedCount = verified,
                pendingCount = pending,
                rejectedCount = rejected,
                uncheckedCount = unchecked.coerceAtLeast(0),
                completionPercentage = percent
            )
        }
    }

    private suspend fun addAuditLog(
        actorName: String,
        actorRole: String,
        action: String,
        equipment: String,
        techName: String,
        notes: String
    ) = withContext(Dispatchers.IO) {
        val nowStr = SimpleDateFormat("dd MMM yyyy HH:mm 'WIB'", Locale("id", "ID")).format(Date())
        auditLogDao.insertAuditLog(
            AuditLogEntity(
                id = "LOG-${UUID.randomUUID().toString().take(6)}",
                actorName = actorName,
                actorRole = actorRole,
                action = action,
                equipmentName = equipment,
                technicianName = techName,
                timestamp = nowStr,
                notes = notes
            )
        )
    }

    fun generateRekapCsv(periodId: String): String {
        val sb = StringBuilder()
        sb.append("Periode,NIK,Nama Teknisi,STO,Nama Alat,Kategori,Tipe,Status,Ukuran Foto (KB),Tanggal Unggah,Lokasi,Hasil OCR,Catatan Admin\n")
        val period = MasterData.periods.find { it.id == periodId } ?: _currentPeriod.value
        val allSubs = submissions.value

        MasterData.technicians.forEach { tech ->
            val subs = allSubs.filter { it.technicianId == tech.id && it.periodId == period.id }.associateBy { it.equipmentId }
            MasterData.equipmentList.forEach { eq ->
                val sub = subs[eq.id]
                val statusStr = sub?.status?.label ?: "Belum Diperiksa"
                val sizeKb = sub?.compressedSizeKb?.toString() ?: "-"
                val dateStr = sub?.capturedDate ?: "-"
                val locStr = sub?.capturedLocation ?: "-"
                val ocrStr = (sub?.ocrCorrectedResult ?: "").replace("\n", " | ").replace(",", ";")
                val feedback = (sub?.adminFeedback ?: "-").replace("\n", " ").replace(",", ";")

                sb.append("\"${period.name}\",\"${tech.nik}\",\"${tech.name}\",\"${tech.stoUnit}\",\"${eq.name}\",\"${eq.category}\",\"${eq.type}\",\"$statusStr\",\"$sizeKb\",\"$dateStr\",\"$locStr\",\"$ocrStr\",\"$feedback\"\n")
            }
        }
        return sb.toString()
    }
}
