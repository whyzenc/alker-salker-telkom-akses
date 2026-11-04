package com.example.service.validation

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.util.Locale

sealed class UploadValidationResult {
    object Success : UploadValidationResult()
    data class Rejected(val reason: String, val code: ErrorCode) : UploadValidationResult()

    enum class ErrorCode {
        FILE_TOO_LARGE,
        INVALID_FORMAT,
        MAX_PHOTOS_EXCEEDED
    }
}

object UploadValidator {
    const val MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024L // 10 MB
    const val MAX_PHOTOS_COUNT = 3

    val ALLOWED_FORMATS = listOf("jpg", "jpeg", "png", "webp")
    val ALLOWED_MIME_TYPES = listOf(
        "image/jpeg",
        "image/jpg",
        "image/png",
        "image/webp"
    )

    fun validateUpload(
        context: Context,
        uri: Uri,
        currentPhotoCount: Int
    ): UploadValidationResult {
        // 1. Validasi batas jumlah foto (Maksimal 3 foto, foto ke-4 ditolak)
        if (currentPhotoCount >= MAX_PHOTOS_COUNT) {
            return UploadValidationResult.Rejected(
                reason = "Unggahan ditolak: Batas maksimum 3 foto tercapai. Anda tidak dapat menambahkan foto ke-4. Silakan hapus salah satu foto terlebih dahulu sebelum menambah foto baru.",
                code = UploadValidationResult.ErrorCode.MAX_PHOTOS_EXCEEDED
            )
        }

        // 2. Validasi format berkas (Selain JPG/PNG/WEBP ditolak)
        val mimeType = context.contentResolver.getType(uri)?.lowercase(Locale.ROOT)
        val fileName = getFileName(context, uri).lowercase(Locale.ROOT)
        val fileExtension = fileName.substringAfterLast(".", "")

        val isMimeValid = mimeType != null && ALLOWED_MIME_TYPES.any { mimeType.contains(it.substringAfter("/")) }
        val isExtValid = fileExtension.isNotEmpty() && ALLOWED_FORMATS.contains(fileExtension)

        if (mimeType != null && !isMimeValid && !isExtValid) {
            val displayFormat = if (fileExtension.isNotEmpty()) ".$fileExtension" else mimeType
            return UploadValidationResult.Rejected(
                reason = "Unggahan ditolak: Format berkas '$displayFormat' tidak didukung. Harap unggah foto dengan format JPG, PNG, atau WEBP.",
                code = UploadValidationResult.ErrorCode.INVALID_FORMAT
            )
        }

        // 3. Validasi ukuran berkas (>10 MB ditolak)
        val fileSizeBytes = getFileSizeBytes(context, uri)
        if (fileSizeBytes > MAX_FILE_SIZE_BYTES) {
            val sizeMb = String.format(Locale.US, "%.2f", fileSizeBytes / (1024.0 * 1024.0))
            return UploadValidationResult.Rejected(
                reason = "Unggahan ditolak: Ukuran berkas ($sizeMb MB) melebihi batas maksimum 10 MB. Silakan pilih foto dengan ukuran lebih kecil.",
                code = UploadValidationResult.ErrorCode.FILE_TOO_LARGE
            )
        }

        return UploadValidationResult.Success
    }

    fun validateDirect(
        fileSizeBytes: Long,
        fileExtensionOrMime: String,
        currentPhotoCount: Int
    ): UploadValidationResult {
        // 1. Validasi foto ke-4
        if (currentPhotoCount >= MAX_PHOTOS_COUNT) {
            return UploadValidationResult.Rejected(
                reason = "Unggahan ditolak: Batas maksimum 3 foto tercapai. Anda tidak dapat menambahkan foto ke-4. Silakan hapus salah satu foto terlebih dahulu sebelum menambah foto baru.",
                code = UploadValidationResult.ErrorCode.MAX_PHOTOS_EXCEEDED
            )
        }

        // 2. Validasi format selain JPG/PNG/WEBP
        val cleanExt = fileExtensionOrMime.removePrefix(".").lowercase(Locale.ROOT).trim()
        val isFormatAllowed = ALLOWED_FORMATS.contains(cleanExt) ||
                ALLOWED_MIME_TYPES.any { cleanExt.contains(it.substringAfter("/")) }

        if (!isFormatAllowed) {
            return UploadValidationResult.Rejected(
                reason = "Unggahan ditolak: Format berkas '.$cleanExt' tidak didukung. Harap unggah foto dengan format JPG, PNG, atau WEBP.",
                code = UploadValidationResult.ErrorCode.INVALID_FORMAT
            )
        }

        // 3. Validasi ukuran berkas > 10 MB
        if (fileSizeBytes > MAX_FILE_SIZE_BYTES) {
            val sizeMb = String.format(Locale.US, "%.2f", fileSizeBytes / (1024.0 * 1024.0))
            return UploadValidationResult.Rejected(
                reason = "Unggahan ditolak: Ukuran berkas ($sizeMb MB) melebihi batas maksimum 10 MB. Silakan pilih foto dengan ukuran lebih kecil.",
                code = UploadValidationResult.ErrorCode.FILE_TOO_LARGE
            )
        }

        return UploadValidationResult.Success
    }

    private fun getFileSizeBytes(context: Context, uri: Uri): Long {
        var size = 0L
        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex != -1 && cursor.moveToFirst()) {
                    size = cursor.getLong(sizeIndex)
                }
            }
        } catch (_: Exception) {}

        if (size <= 0L) {
            try {
                if (uri.scheme == "file") {
                    val file = File(uri.path ?: "")
                    if (file.exists()) size = file.length()
                } else {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        size = stream.available().toLong()
                    }
                }
            } catch (_: Exception) {}
        }
        return size
    }

    private fun getFileName(context: Context, uri: Uri): String {
        var name = ""
        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    name = cursor.getString(nameIndex) ?: ""
                }
            }
        } catch (_: Exception) {}
        if (name.isEmpty()) {
            name = uri.lastPathSegment ?: ""
        }
        return name
    }
}
