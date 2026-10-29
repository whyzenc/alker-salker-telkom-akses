package com.example.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM app_users WHERE nik = :nik LIMIT 1")
    suspend fun getUserByNik(nik: String): UserEntity?

    @Query("SELECT * FROM app_users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM app_users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)
}

@Dao
interface InspectionDao {
    @Query("SELECT * FROM inspection_submissions ORDER BY updatedAt DESC")
    fun getAllSubmissions(): Flow<List<InspectionEntity>>

    @Query("SELECT * FROM inspection_submissions WHERE technicianId = :techId AND periodId = :periodId")
    fun getSubmissionsForTechnician(techId: String, periodId: String): Flow<List<InspectionEntity>>

    @Query("SELECT * FROM inspection_submissions WHERE id = :id LIMIT 1")
    suspend fun getSubmissionById(id: String): InspectionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(submission: InspectionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmissions(submissions: List<InspectionEntity>)

    @Query("UPDATE inspection_submissions SET status = :status, adminFeedback = :feedback, verifiedBy = :verifiedBy, verifiedAt = :verifiedAt WHERE id = :id")
    suspend fun updateVerification(id: String, status: String, feedback: String?, verifiedBy: String, verifiedAt: String)
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY id DESC")
    fun getAllAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLogs(logs: List<AuditLogEntity>)
}
