package com.example.ui.screens.technician

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import com.example.ui.theme.AppIcons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.EquipmentMaster
import com.example.data.model.EquipmentType
import com.example.data.model.InspectionStatus
import com.example.data.model.InspectionSubmission
import com.example.data.model.Period
import com.example.data.model.User
import com.example.service.image.ImageCompressor
import com.example.service.ocr.OcrService
import com.example.ui.components.EmptyStateView
import com.example.ui.components.EquipmentTypeBadge
import com.example.ui.components.PhotoZoomDialog
import com.example.ui.components.StatCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.TelkomTopAppBar
import com.example.ui.theme.StatusPending
import com.example.ui.theme.StatusPendingBg
import com.example.ui.theme.StatusRejected
import com.example.ui.theme.StatusRejectedBg
import com.example.ui.theme.StatusUnchecked
import com.example.ui.theme.StatusUncheckedBg
import com.example.ui.theme.StatusVerified
import com.example.ui.theme.StatusVerifiedBg
import com.example.ui.theme.TelkomRed
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun TechnicianDashboardScreen(
    currentUser: User,
    currentPeriod: Period,
    equipmentList: List<EquipmentMaster>,
    submissions: List<InspectionSubmission>,
    onSelectEquipment: (EquipmentMaster) -> Unit,
    onViewChecklist: () -> Unit,
    onSwitchRole: () -> Unit,
    onLogout: () -> Unit
) {
    val totalItems = equipmentList.size
    val subMap = remember(submissions) { submissions.associateBy { it.equipmentId } }

    val verifiedCount = subMap.values.count { it.status == InspectionStatus.VERIFIED }
    val pendingCount = subMap.values.count { it.status == InspectionStatus.PENDING }
    val rejectedCount = subMap.values.count { it.status == InspectionStatus.REJECTED }
    val uncheckedCount = (totalItems - (verifiedCount + pendingCount + rejectedCount)).coerceAtLeast(0)
    val percentage = if (totalItems > 0) (verifiedCount * 100) / totalItems else 0

    Scaffold(
        topBar = {
            TelkomTopAppBar(
                title = "Alker Sarker Assurance",
                subtitle = "${currentUser.name} • ${currentUser.stoUnit}",
                currentUser = currentUser,
                onSwitchRole = onSwitchRole,
                onLogout = onLogout
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Period Banner & Progress Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "PERIODE PEMERIKSAAN AKTIF",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TelkomRed,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = currentPeriod.name,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFEF2F2))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Sisa ${currentPeriod.daysRemaining} Hari",
                                    color = TelkomRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Progress circle & stats
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(80.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    progress = { percentage / 100f },
                                    modifier = Modifier.fillMaxSize(),
                                    color = if (percentage == 100) StatusVerified else TelkomRed,
                                    trackColor = Color(0xFFE2E8F0),
                                    strokeWidth = 8.dp
                                )
                                Text(
                                    text = "$percentage%",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = "$verifiedCount dari $totalItems Alat Terverifikasi",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (percentage == 100) "Semua perlengkapan telah siap operasi!"
                                    else if (rejectedCount > 0) "Ada $rejectedCount bukti ditolak, perbaiki segera."
                                    else "Lengkapi foto alat sebelum batas waktu.",
                                    fontSize = 12.sp,
                                    color = if (rejectedCount > 0) StatusRejected else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Stat Counter Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "Terverifikasi",
                        count = verifiedCount,
                        color = StatusVerified,
                        bgColor = StatusVerifiedBg,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Menunggu",
                        count = pendingCount,
                        color = StatusPending,
                        bgColor = StatusPendingBg,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Ditolak",
                        count = rejectedCount,
                        color = StatusRejected,
                        bgColor = StatusRejectedBg,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Belum",
                        count = uncheckedCount,
                        color = StatusUnchecked,
                        bgColor = StatusUncheckedBg,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Rejection Alert Card if any
            if (rejectedCount > 0) {
                item {
                    val rejectedSub = subMap.values.firstOrNull { it.status == InspectionStatus.REJECTED }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = StatusRejected,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Perhatian: Catatan Dari Admin Assurance",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusRejected
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${rejectedSub?.equipmentName ?: "Peralatan"}: \"${rejectedSub?.adminFeedback ?: "Foto tidak jelas, mohon ulangi."}\"",
                                    fontSize = 12.sp,
                                    color = Color(0xFF7F1D1D)
                                )
                            }
                        }
                    }
                }
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daftar Alker & Sarker Lapangan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Lihat Semua",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TelkomRed,
                        modifier = Modifier
                            .clickable { onViewChecklist() }
                            .padding(4.dp)
                    )
                }
            }

            // Equipment List Preview
            items(equipmentList) { equipment ->
                val submission = subMap[equipment.id]
                val currentStatus = submission?.status ?: InspectionStatus.UNCHECKED

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectEquipment(equipment) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (equipment.type == EquipmentType.PHOTO_OCR) Color(0xFFEFF6FF)
                                    else Color(0xFFF1F5F9)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (equipment.type == EquipmentType.PHOTO_OCR) AppIcons.Analytics else AppIcons.Camera,
                                contentDescription = null,
                                tint = if (equipment.type == EquipmentType.PHOTO_OCR) Color(0xFF2563EB) else TelkomRed,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = equipment.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                EquipmentTypeBadge(type = equipment.type)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = equipment.category,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(horizontalAlignment = Alignment.End) {
                            StatusBadge(status = currentStatus)
                            if (submission != null && submission.compressedSizeKb > 0) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${submission.compressedSizeKb} KB",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TechnicianChecklistScreen(
    equipmentList: List<EquipmentMaster>,
    submissions: List<InspectionSubmission>,
    onSelectEquipment: (EquipmentMaster) -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf<InspectionStatus?>(null) }
    var selectedTypeFilter by remember { mutableStateOf<EquipmentType?>(null) }

    val subMap = remember(submissions) { submissions.associateBy { it.equipmentId } }

    val filteredList = equipmentList.filter { item ->
        val matchesSearch = item.name.contains(searchQuery, ignoreCase = true) ||
                item.category.contains(searchQuery, ignoreCase = true)
        val itemStatus = subMap[item.id]?.status ?: InspectionStatus.UNCHECKED
        val matchesStatus = selectedStatusFilter == null || itemStatus == selectedStatusFilter
        val matchesType = selectedTypeFilter == null || item.type == selectedTypeFilter
        matchesSearch && matchesStatus && matchesType
    }

    Scaffold(
        topBar = {
            TelkomTopAppBar(
                title = "Checklist Peralatan",
                subtitle = "${filteredList.size} dari ${equipmentList.size} Item",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("search_equipment_input"),
                placeholder = { Text("Cari nama alat (OTDR, Splicer, Tangga)...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Cari") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedStatusFilter == null && selectedTypeFilter == null,
                        onClick = {
                            selectedStatusFilter = null
                            selectedTypeFilter = null
                        },
                        label = { Text("Semua", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TelkomRed,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedStatusFilter == InspectionStatus.UNCHECKED,
                        onClick = {
                            selectedStatusFilter = if (selectedStatusFilter == InspectionStatus.UNCHECKED) null else InspectionStatus.UNCHECKED
                        },
                        label = { Text("Belum Diperiksa", fontSize = 12.sp) }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedStatusFilter == InspectionStatus.PENDING,
                        onClick = {
                            selectedStatusFilter = if (selectedStatusFilter == InspectionStatus.PENDING) null else InspectionStatus.PENDING
                        },
                        label = { Text("Menunggu", fontSize = 12.sp) }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedStatusFilter == InspectionStatus.VERIFIED,
                        onClick = {
                            selectedStatusFilter = if (selectedStatusFilter == InspectionStatus.VERIFIED) null else InspectionStatus.VERIFIED
                        },
                        label = { Text("Terverifikasi", fontSize = 12.sp) }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedStatusFilter == InspectionStatus.REJECTED,
                        onClick = {
                            selectedStatusFilter = if (selectedStatusFilter == InspectionStatus.REJECTED) null else InspectionStatus.REJECTED
                        },
                        label = { Text("Ditolak", fontSize = 12.sp) }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedTypeFilter == EquipmentType.PHOTO_OCR,
                        onClick = {
                            selectedTypeFilter = if (selectedTypeFilter == EquipmentType.PHOTO_OCR) null else EquipmentType.PHOTO_OCR
                        },
                        label = { Text("Hanya OCR", fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredList.isEmpty()) {
                EmptyStateView(
                    title = "Tidak Ada Peralatan",
                    message = "Tidak ada alat kerja yang cocok dengan filter atau pencarian Anda.",
                    actionLabel = "Reset Filter",
                    onAction = {
                        searchQuery = ""
                        selectedStatusFilter = null
                        selectedTypeFilter = null
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredList) { equipment ->
                        val sub = subMap[equipment.id]
                        val status = sub?.status ?: InspectionStatus.UNCHECKED

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectEquipment(equipment) },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = equipment.name,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = equipment.category,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    StatusBadge(status = status)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    EquipmentTypeBadge(type = equipment.type)

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (status == InspectionStatus.UNCHECKED) "Mulai Periksa" else "Lihat Detail",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TelkomRed
                                        )
                                        Icon(
                                            imageVector = Icons.Default.ArrowForward,
                                            contentDescription = null,
                                            tint = TelkomRed,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                if (status == InspectionStatus.REJECTED && !sub?.adminFeedback.isNullOrEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFFEF2F2))
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            text = "Alasan Tolak: ${sub?.adminFeedback}",
                                            fontSize = 11.sp,
                                            color = StatusRejected
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EquipmentDetailUploadScreen(
    equipment: EquipmentMaster,
    currentSubmission: InspectionSubmission?,
    currentUser: User,
    currentPeriod: Period,
    onSaveSubmission: (InspectionSubmission) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedImageUri by remember { mutableStateOf<String?>(currentSubmission?.photoUri) }
    var originalSizeKb by remember { mutableStateOf(currentSubmission?.originalSizeKb ?: 0) }
    var compressedSizeKb by remember { mutableStateOf(currentSubmission?.compressedSizeKb ?: 0) }
    var compressionRatio by remember { mutableStateOf(0) }

    var isProcessingCompression by remember { mutableStateOf(false) }
    var isProcessingOcr by remember { mutableStateOf(false) }

    var ocrConfidence by remember { mutableStateOf(currentSubmission?.ocrConfidence ?: 0) }
    var ocrRawText by remember { mutableStateOf(currentSubmission?.ocrRawResult ?: "") }
    var ocrCorrectedText by remember { mutableStateOf(currentSubmission?.ocrCorrectedResult ?: currentSubmission?.ocrRawResult ?: "") }

    var capturedTimestamp by remember {
        mutableStateOf(currentSubmission?.capturedDate ?: SimpleDateFormat("dd MMM yyyy HH:mm 'WIB'", Locale("id", "ID")).format(Date()))
    }
    var capturedLocation by remember {
        mutableStateOf(currentSubmission?.capturedLocation ?: "Padang Barat, Kota Padang")
    }

    var showZoomDialog by remember { mutableStateOf(false) }

    // Photo picker launcher (0 permission API)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                isProcessingCompression = true
                val result = ImageCompressor.compressAndSaveToInternalStorage(context, uri, equipment.name)
                selectedImageUri = result.persistentFilePath
                originalSizeKb = (result.originalSizeBytes / 1024).toInt()
                compressedSizeKb = (result.compressedSizeBytes / 1024).toInt()
                compressionRatio = result.compressionRatioPercent
                isProcessingCompression = false

                // If PHOTO_OCR, execute OCR engine
                if (equipment.type == EquipmentType.PHOTO_OCR) {
                    isProcessingOcr = true
                    val ocrResult = OcrService.processOcr(equipment.id, equipment.name)
                    ocrConfidence = ocrResult.confidence
                    ocrRawText = ocrResult.extractedText
                    ocrCorrectedText = ocrResult.extractedText
                    capturedTimestamp = ocrResult.detectedDate
                    capturedLocation = ocrResult.detectedLocation
                    isProcessingOcr = false
                }
            }
        }
    }

    // Camera capture simulation
    fun triggerCameraSimulation() {
        scope.launch {
            isProcessingCompression = true
            val sample = ImageCompressor.generatePersistentSamplePhoto(context, equipment.name)
            selectedImageUri = sample.persistentFilePath
            originalSizeKb = (sample.originalSizeBytes / 1024).toInt()
            compressedSizeKb = (sample.compressedSizeBytes / 1024).toInt()
            compressionRatio = sample.compressionRatioPercent
            isProcessingCompression = false

            if (equipment.type == EquipmentType.PHOTO_OCR) {
                isProcessingOcr = true
                val ocrResult = OcrService.processOcr(equipment.id, equipment.name)
                ocrConfidence = ocrResult.confidence
                ocrRawText = ocrResult.extractedText
                ocrCorrectedText = ocrResult.extractedText
                capturedTimestamp = ocrResult.detectedDate
                capturedLocation = ocrResult.detectedLocation
                isProcessingOcr = false
            }
            snackbarHostState.showSnackbar("Foto bukti berhasil disimpan permanen ke database.")
        }
    }

    Scaffold(
        topBar = {
            TelkomTopAppBar(
                title = equipment.name,
                subtitle = equipment.category,
                onBack = onBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Batal")
                    }
                    Button(
                        onClick = {
                            if (selectedImageUri == null) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Silakan ambil atau pilih foto bukti terlebih dahulu.")
                                }
                                return@Button
                            }

                            val newSubmission = InspectionSubmission(
                                id = currentSubmission?.id ?: "SUB-${UUID.randomUUID().toString().take(6)}",
                                periodId = currentPeriod.id,
                                technicianId = currentUser.id,
                                technicianName = currentUser.name,
                                technicianSto = currentUser.stoUnit,
                                equipmentId = equipment.id,
                                equipmentName = equipment.name,
                                equipmentType = equipment.type,
                                photoUri = selectedImageUri,
                                originalSizeKb = originalSizeKb,
                                compressedSizeKb = compressedSizeKb,
                                capturedDate = capturedTimestamp,
                                capturedLocation = capturedLocation,
                                hasTimestampWatermark = true,
                                ocrConfidence = ocrConfidence,
                                ocrRawResult = ocrRawText,
                                ocrCorrectedResult = ocrCorrectedText,
                                status = InspectionStatus.PENDING,
                                adminFeedback = null,
                                updatedAt = System.currentTimeMillis()
                            )

                            onSaveSubmission(newSubmission)
                        },
                        modifier = Modifier
                            .weight(2f)
                            .testTag("submit_inspection_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = TelkomRed),
                        enabled = !isProcessingCompression && !isProcessingOcr && selectedImageUri != null
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simpan & Kirim Verifikasi")
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Photo Guideline Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFF1D4ED8),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ketentuan Foto Bukti Lapangan",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D4ED8)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = equipment.photoGuide,
                            fontSize = 13.sp,
                            color = Color(0xFF1E3A8A),
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "💡 Tips: ${equipment.sampleTip}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF2563EB)
                        )
                    }
                }
            }

            // Current Status if already checked
            if (currentSubmission != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Status Saat Ini",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                StatusBadge(status = currentSubmission.status)
                            }
                            if (currentSubmission.status == InspectionStatus.REJECTED && !currentSubmission.adminFeedback.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Alasan Penolakan Admin: ${currentSubmission.adminFeedback}",
                                    fontSize = 12.sp,
                                    color = StatusRejected,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            if (currentSubmission.status == InspectionStatus.VERIFIED && !currentSubmission.verifiedBy.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Diverifikasi oleh: ${currentSubmission.verifiedBy} pada ${currentSubmission.verifiedAt}",
                                    fontSize = 11.sp,
                                    color = StatusVerified
                                )
                            }
                        }
                    }
                }
            }

            // Photo Capture & Preview Area
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Foto Bukti Alat",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Preview Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0F172A))
                                .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedImageUri != null) {
                                AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = "Bukti Foto",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                // Watermark banner on preview
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(8.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.Black.copy(alpha = 0.7f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "TIMESTAMP CAMERA PADANG",
                                            color = Color(0xFF22C55E),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "$capturedTimestamp • $capturedLocation",
                                            color = Color.White,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                // Zoom button
                                IconButton(
                                    onClick = { showZoomDialog = true },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.6f))
                                ) {
                                    Icon(
                                        imageVector = AppIcons.ZoomIn,
                                        contentDescription = "Perbesar",
                                        tint = Color.White
                                    )
                                }
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = AppIcons.Camera,
                                        contentDescription = null,
                                        tint = Color(0xFF64748B),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Belum ada foto yang diambil",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "Gunakan tombol kamera atau pilih dari galeri",
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            if (isProcessingCompression || isProcessingOcr) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.65f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        CircularProgressIndicator(color = TelkomRed)
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = if (isProcessingCompression) "Mengompresi foto..." else "Membaca teks OCR...",
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Capture buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { triggerCameraSimulation() },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("camera_capture_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = TelkomRed)
                            ) {
                                Icon(AppIcons.Camera, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ambil Foto", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("gallery_pick_button")
                            ) {
                                Icon(AppIcons.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Dari Galeri", fontSize = 12.sp)
                            }
                        }

                        // Compression stats indicator
                        if (compressedSizeKb > 0) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = AppIcons.Compress,
                                            contentDescription = null,
                                            tint = StatusVerified,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Kompresi Berhasil:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Text(
                                        text = "$originalSizeKb KB ➔ $compressedSizeKb KB (Hemat ${if (compressionRatio > 0) compressionRatio else 85}%)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = StatusVerified
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // OCR Extraction & Correction Form (If PHOTO_OCR)
            if (equipment.type == EquipmentType.PHOTO_OCR) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = AppIcons.Analytics,
                                        contentDescription = null,
                                        tint = Color(0xFF2563EB),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Hasil Ekstraksi OCR Otomatis",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                if (ocrConfidence > 0) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFEFF6FF))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Keyakinan $ocrConfidence%",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2563EB)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Target pembacaan: ${equipment.ocrFieldLabel ?: "Teks pada alat"}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = ocrCorrectedText,
                                onValueChange = { ocrCorrectedText = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("ocr_correction_input"),
                                label = { Text("Koreksi Hasil Teks (Bisa diedit bila ada salah baca)") },
                                minLines = 3,
                                maxLines = 5,
                                shape = RoundedCornerShape(8.dp)
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Pastikan angka/informasi sesuai dengan yang tertera di foto fisik sebelum disimpan.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    // Fullscreen Zoom Dialog
    if (showZoomDialog && selectedImageUri != null) {
        PhotoZoomDialog(
            photoUri = selectedImageUri,
            title = equipment.name,
            timestampText = capturedTimestamp,
            locationText = capturedLocation,
            onDismiss = { showZoomDialog = false }
        )
    }
}
