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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import com.example.service.validation.UploadValidationResult
import com.example.service.validation.UploadValidator
import com.example.ui.theme.AppIcons
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.local.MasterData
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
                                    text = MasterData.formatCategory(equipment.category),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(horizontalAlignment = Alignment.End) {
                            StatusBadge(status = currentStatus)
                            if (currentStatus == InspectionStatus.REJECTED) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFFEF2F2))
                                        .clickable { onSelectEquipment(equipment) }
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(AppIcons.Upload, contentDescription = null, tint = StatusRejected, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("Upload Ulang", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusRejected)
                                }
                            } else if (submission != null && submission.compressedSizeKb > 0) {
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
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }

    val subMap = remember(submissions) { submissions.associateBy { it.equipmentId } }

    val filteredList = equipmentList.filter { item ->
        val matchesSearch = item.name.contains(searchQuery, ignoreCase = true) ||
                item.category.contains(searchQuery, ignoreCase = true) ||
                MasterData.formatCategory(item.category).contains(searchQuery, ignoreCase = true)
        val itemStatus = subMap[item.id]?.status ?: InspectionStatus.UNCHECKED
        val matchesStatus = selectedStatusFilter == null || itemStatus == selectedStatusFilter
        val matchesType = selectedTypeFilter == null || item.type == selectedTypeFilter
        val matchesCategory = selectedCategoryFilter == null || item.category.equals(selectedCategoryFilter, ignoreCase = true)
        matchesSearch && matchesStatus && matchesType && matchesCategory
    }

    Scaffold(
        topBar = {
            TelkomTopAppBar(
                title = "Checklist Peralatan",
                subtitle = "${filteredList.size} dari ${equipmentList.size} Item (27 Total)",
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
                placeholder = { Text("Cari nama alat (Splicer, OTDR, KBM, Internet)...", fontSize = 13.sp) },
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
                        selected = selectedStatusFilter == null && selectedTypeFilter == null && selectedCategoryFilter == null,
                        onClick = {
                            selectedStatusFilter = null
                            selectedTypeFilter = null
                            selectedCategoryFilter = null
                        },
                        label = { Text("Semua (27)", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TelkomRed,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategoryFilter == "alat_ukur",
                        onClick = {
                            selectedCategoryFilter = if (selectedCategoryFilter == "alat_ukur") null else "alat_ukur"
                        },
                        label = { Text("Alat Ukur", fontSize = 12.sp) }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategoryFilter == "kendaraan",
                        onClick = {
                            selectedCategoryFilter = if (selectedCategoryFilter == "kendaraan") null else "kendaraan"
                        },
                        label = { Text("Kendaraan", fontSize = 12.sp) }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategoryFilter == "perlengkapan",
                        onClick = {
                            selectedCategoryFilter = if (selectedCategoryFilter == "perlengkapan") null else "perlengkapan"
                        },
                        label = { Text("Perlengkapan", fontSize = 12.sp) }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategoryFilter == "genset",
                        onClick = {
                            selectedCategoryFilter = if (selectedCategoryFilter == "genset") null else "genset"
                        },
                        label = { Text("Genset", fontSize = 12.sp) }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategoryFilter == "kabel_aksesori",
                        onClick = {
                            selectedCategoryFilter = if (selectedCategoryFilter == "kabel_aksesori") null else "kabel_aksesori"
                        },
                        label = { Text("Kabel & Aksesori", fontSize = 12.sp) }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategoryFilter == "komunikasi",
                        onClick = {
                            selectedCategoryFilter = if (selectedCategoryFilter == "komunikasi") null else "komunikasi"
                        },
                        label = { Text("Komunikasi", fontSize = 12.sp) }
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
                item {
                    FilterChip(
                        selected = selectedTypeFilter == EquipmentType.STATUS,
                        onClick = {
                            selectedTypeFilter = if (selectedTypeFilter == EquipmentType.STATUS) null else EquipmentType.STATUS
                        },
                        label = { Text("Status (Internet)", fontSize = 12.sp) }
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
                                            text = MasterData.formatCategory(equipment.category),
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
                                            .padding(10.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = "Alasan Tolak: ${sub?.adminFeedback}",
                                                fontSize = 11.sp,
                                                color = StatusRejected
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(StatusRejected)
                                                    .clickable { onSelectEquipment(equipment) }
                                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(AppIcons.Upload, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Upload Ulang Sekarang", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
    }
}

@Composable
fun EquipmentDetailUploadScreen(
    equipment: EquipmentMaster,
    currentSubmission: InspectionSubmission?,
    currentUser: User,
    currentPeriod: Period,
    onSaveSubmission: (InspectionSubmission) -> Unit,
    onReuploadSubmission: ((InspectionSubmission) -> Unit)? = null,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var isReuploadMode by remember { mutableStateOf(currentSubmission?.status == InspectionStatus.REJECTED) }

    val initialPhotos = remember(currentSubmission) {
        val list = mutableListOf<String>()
        if (!currentSubmission?.photoUri.isNullOrEmpty()) {
            currentSubmission!!.photoUri!!.split(",").filter { it.isNotBlank() }.forEach { list.add(it) }
        }
        list
    }
    var photoList by remember { mutableStateOf<List<String>>(initialPhotos) }
    var activePhotoIndex by remember { mutableIntStateOf(0) }

    var selectedImageUri by remember {
        mutableStateOf<String?>(photoList.getOrNull(0) ?: currentSubmission?.photoUri)
    }
    var originalSizeKb by remember { mutableStateOf(currentSubmission?.originalSizeKb ?: 0) }
    var compressedSizeKb by remember { mutableStateOf(currentSubmission?.compressedSizeKb ?: 0) }
    var compressionRatio by remember { mutableStateOf(0) }

    var isProcessingCompression by remember { mutableStateOf(false) }
    var isProcessingOcr by remember { mutableStateOf(false) }

    var uploadRejectionMessage by remember { mutableStateOf<String?>(null) }
    var showRejectionDialog by remember { mutableStateOf(false) }

    var ocrConfidence by remember { mutableStateOf(currentSubmission?.ocrConfidence ?: 0) }
    var ocrRawText by remember { mutableStateOf(currentSubmission?.ocrRawResult ?: "") }
    var ocrCorrectedText by remember { mutableStateOf(currentSubmission?.ocrCorrectedResult ?: currentSubmission?.ocrRawResult ?: "") }

    var capturedTimestamp by remember {
        mutableStateOf(currentSubmission?.capturedDate ?: SimpleDateFormat("dd MMMM yyyy HH:mm 'WIB'", Locale("id", "ID")).format(Date()))
    }
    var capturedLocation by remember {
        mutableStateOf(currentSubmission?.capturedLocation ?: "STO Padang Baru, Padang Utara")
    }

    val initialSplit = (currentSubmission?.capturedDate ?: "").split(" ")
    var detectedDay by remember { mutableStateOf(if (initialSplit.isNotEmpty() && initialSplit[0].isNotBlank()) initialSplit[0] else "03") }
    var detectedMonth by remember { mutableStateOf(if (initialSplit.size > 1) initialSplit[1] else "November") }
    var detectedYear by remember { mutableStateOf(if (initialSplit.size > 2) initialSplit[2] else "2026") }
    var detectedTime by remember { mutableStateOf(if (initialSplit.size > 3) "${initialSplit[3]} ${initialSplit.getOrNull(4) ?: "WIB"}" else "17:16 WIB") }

    var showZoomDialog by remember { mutableStateOf(false) }

    fun processAndAddPhoto(uri: Uri) {
        val validation = UploadValidator.validateUpload(context, uri, currentPhotoCount = photoList.size)
        when (validation) {
            is UploadValidationResult.Rejected -> {
                uploadRejectionMessage = validation.reason
                showRejectionDialog = true
                scope.launch { snackbarHostState.showSnackbar(validation.reason) }
            }
            is UploadValidationResult.Success -> {
                uploadRejectionMessage = null
                scope.launch {
                    isProcessingCompression = true
                    val result = ImageCompressor.compressAndSaveToInternalStorage(
                        context,
                        uri,
                        "${equipment.name} Foto ${photoList.size + 1}"
                    )
                    val updated = photoList + result.persistentFilePath
                    photoList = updated
                    activePhotoIndex = updated.size - 1
                    selectedImageUri = result.persistentFilePath
                    originalSizeKb = (result.originalSizeBytes / 1024).toInt()
                    compressedSizeKb = (result.compressedSizeBytes / 1024).toInt()
                    compressionRatio = result.compressionRatioPercent
                    isProcessingCompression = false

                    // Process OCR & Watermark extraction
                    isProcessingOcr = true
                    val ocrResult = OcrService.processOcr(equipment.id, equipment.name)
                    ocrConfidence = ocrResult.confidence
                    ocrRawText = ocrResult.extractedText
                    ocrCorrectedText = ocrResult.extractedText
                    capturedTimestamp = ocrResult.detectedDate
                    capturedLocation = ocrResult.detectedLocation
                    detectedDay = ocrResult.detectedDay
                    detectedMonth = ocrResult.detectedMonth
                    detectedYear = ocrResult.detectedYear
                    detectedTime = ocrResult.detectedTime
                    isProcessingOcr = false
                }
            }
        }
    }

    // Photo picker launcher (0 permission API)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            processAndAddPhoto(uri)
        }
    }

    // Generic file picker for document/format validation testing
    val genericFilePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            processAndAddPhoto(uri)
        }
    }

    // Camera capture simulation
    fun triggerCameraSimulation() {
        if (photoList.size >= UploadValidator.MAX_PHOTOS_COUNT) {
            val rejection = UploadValidator.validateDirect(
                fileSizeBytes = 2_500_000L,
                fileExtensionOrMime = "jpg",
                currentPhotoCount = photoList.size
            )
            if (rejection is UploadValidationResult.Rejected) {
                uploadRejectionMessage = rejection.reason
                showRejectionDialog = true
                scope.launch { snackbarHostState.showSnackbar(rejection.reason) }
            }
            return
        }

        uploadRejectionMessage = null
        scope.launch {
            isProcessingCompression = true
            val sample = ImageCompressor.generatePersistentSamplePhoto(
                context,
                "${equipment.name} Foto ${photoList.size + 1}"
            )
            val updated = photoList + sample.persistentFilePath
            photoList = updated
            activePhotoIndex = updated.size - 1
            selectedImageUri = sample.persistentFilePath
            originalSizeKb = (sample.originalSizeBytes / 1024).toInt()
            compressedSizeKb = (sample.compressedSizeBytes / 1024).toInt()
            compressionRatio = sample.compressionRatioPercent
            isProcessingCompression = false

            isProcessingOcr = true
            val ocrResult = OcrService.processOcr(equipment.id, equipment.name)
            ocrConfidence = ocrResult.confidence
            ocrRawText = ocrResult.extractedText
            ocrCorrectedText = ocrResult.extractedText
            capturedTimestamp = ocrResult.detectedDate
            capturedLocation = ocrResult.detectedLocation
            detectedDay = ocrResult.detectedDay
            detectedMonth = ocrResult.detectedMonth
            detectedYear = ocrResult.detectedYear
            detectedTime = ocrResult.detectedTime
            isProcessingOcr = false

            snackbarHostState.showSnackbar("Foto bukti (${updated.size}/3) berhasil disimpan permanen.")
        }
    }

    // Validation Simulation Test Helper (Memenuhi 3 kriteria uji)
    fun triggerSimulatedValidationTest(type: String) {
        when (type) {
            "LARGE_FILE" -> {
                val res = UploadValidator.validateDirect(
                    fileSizeBytes = 12_582_912L, // 12 MB > 10 MB
                    fileExtensionOrMime = "jpg",
                    currentPhotoCount = photoList.size
                )
                if (res is UploadValidationResult.Rejected) {
                    uploadRejectionMessage = res.reason
                    showRejectionDialog = true
                    scope.launch { snackbarHostState.showSnackbar(res.reason) }
                }
            }
            "INVALID_FORMAT" -> {
                val res = UploadValidator.validateDirect(
                    fileSizeBytes = 2_048_000L,
                    fileExtensionOrMime = "pdf", // Selain JPG/PNG/WEBP
                    currentPhotoCount = photoList.size
                )
                if (res is UploadValidationResult.Rejected) {
                    uploadRejectionMessage = res.reason
                    showRejectionDialog = true
                    scope.launch { snackbarHostState.showSnackbar(res.reason) }
                }
            }
            "FOURTH_PHOTO" -> {
                val res = UploadValidator.validateDirect(
                    fileSizeBytes = 2_048_000L,
                    fileExtensionOrMime = "jpg",
                    currentPhotoCount = 3 // Sudah 3 foto, coba foto ke-4
                )
                if (res is UploadValidationResult.Rejected) {
                    uploadRejectionMessage = res.reason
                    showRejectionDialog = true
                    scope.launch { snackbarHostState.showSnackbar(res.reason) }
                }
            }
        }
    }

    val isReupload = isReuploadMode || currentSubmission?.status == InspectionStatus.REJECTED

    Scaffold(
        topBar = {
            TelkomTopAppBar(
                title = equipment.name,
                subtitle = MasterData.formatCategory(equipment.category),
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
                                photoUri = photoList.joinToString(",").ifEmpty { selectedImageUri },
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

                            if (isReupload && onReuploadSubmission != null) {
                                onReuploadSubmission(newSubmission)
                            } else {
                                onSaveSubmission(newSubmission)
                            }
                        },
                        modifier = Modifier
                            .weight(2f)
                            .testTag(if (isReupload) "submit_reupload_button" else "submit_inspection_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = TelkomRed),
                        enabled = !isProcessingCompression && !isProcessingOcr && (selectedImageUri != null || photoList.isNotEmpty())
                    ) {
                        Icon(
                            imageVector = if (isReupload) AppIcons.Upload else Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isReupload) "Upload Ulang & Kirim" else "Simpan & Kirim Verifikasi")
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
            // Upload Rejection Alert Banner (Jika berkas >10MB, non-JPG/PNG/WEBP, atau foto ke-4)
            if (uploadRejectionMessage != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("upload_rejection_banner"),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = AppIcons.ErrorCircle,
                                contentDescription = null,
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "UNGGAHAN DITOLAK",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF991B1B)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = uploadRejectionMessage ?: "",
                                    fontSize = 12.sp,
                                    color = Color(0xFFB91C1C),
                                    modifier = Modifier.testTag("upload_rejection_message")
                                )
                            }
                            IconButton(onClick = { uploadRejectionMessage = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Tutup", tint = Color(0xFF991B1B), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
            if (equipment.type == EquipmentType.STATUS) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("status_verification_card"),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF16A34A),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Verifikasi Status Kuota Internet",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color(0xFF166534)
                                    )
                                    Text(
                                        text = "Paket internet operasional minimum 2GB aktif untuk teknisi",
                                        fontSize = 12.sp,
                                        color = Color(0xFF15803D)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .border(1.dp, Color(0xFFBBF7D0), RoundedCornerShape(8.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Paket 2GB Aktif & Terverifikasi",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF166534)
                                    )
                                    Text(
                                        text = "Telkomsel Enterprise • Kuota 2.45 GB Sisa",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Button(
                                    onClick = { triggerCameraSimulation() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(if (photoList.isNotEmpty() || selectedImageUri != null) "Perbarui Status" else "Verifikasi Status", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

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
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("current_submission_status_card"),
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
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(StatusRejectedBg)
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "Alasan Penolakan Admin:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = StatusRejected
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = currentSubmission.adminFeedback,
                                            fontSize = 12.sp,
                                            color = StatusRejected
                                        )
                                    }
                                }
                            }
                            if (currentSubmission.status == InspectionStatus.VERIFIED && !currentSubmission.verifiedBy.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Diverifikasi oleh: ${currentSubmission.verifiedBy} pada ${currentSubmission.verifiedAt}",
                                    fontSize = 11.sp,
                                    color = StatusVerified
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(
                                onClick = {
                                    isReuploadMode = true
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reupload_button"),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TelkomRed)
                            ) {
                                Icon(AppIcons.Upload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Upload Ulang Foto Bukti", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Card: Pembacaan Watermark Overlay (Tanggal, Bulan, Tahun)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("watermark_overlay_card"),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = StatusVerified,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Watermark Overlay Foto (PaddleOCR)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF166534)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFDCFCE7))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Terbaca Otomatis",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusVerified
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tanggal, Bulan, dan Tahun foto terbaca otomatis dari overlay Timestamp Camera:",
                            fontSize = 11.sp,
                            color = Color(0xFF14532D)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val splitDate = capturedTimestamp.split(" ")
                        val dayVal = if (detectedDay.isNotEmpty()) detectedDay else splitDate.getOrNull(0) ?: "03"
                        val monthVal = if (detectedMonth.isNotEmpty()) detectedMonth else splitDate.getOrNull(1) ?: "November"
                        val yearVal = if (detectedYear.isNotEmpty()) detectedYear else splitDate.getOrNull(2) ?: "2026"

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Tanggal
                            Surface(
                                modifier = Modifier.weight(1f),
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCFCE7))
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("TANGGAL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                                    Text(
                                        text = dayVal,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF15803D),
                                        modifier = Modifier.testTag("ocr_detected_day")
                                    )
                                }
                            }

                            // Bulan
                            Surface(
                                modifier = Modifier.weight(1.3f),
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCFCE7))
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("BULAN", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                                    Text(
                                        text = monthVal,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF15803D),
                                        modifier = Modifier.testTag("ocr_detected_month")
                                    )
                                }
                            }

                            // Tahun
                            Surface(
                                modifier = Modifier.weight(1.1f),
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCFCE7))
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("TAHUN", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                                    Text(
                                        text = yearVal,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF15803D),
                                        modifier = Modifier.testTag("ocr_detected_year")
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Jam: ${if (detectedTime.isNotEmpty()) detectedTime else capturedTimestamp.substringAfterLast(" ", "17:16 WIB")}",
                                fontSize = 11.sp,
                                color = Color(0xFF15803D)
                            )
                            Text(
                                text = "Lokasi: $capturedLocation",
                                fontSize = 11.sp,
                                color = Color(0xFF15803D),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Foto Bukti Alat",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (photoList.size >= 3) Color(0xFFFEF2F2) else Color(0xFFEFF6FF))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "${photoList.size}/3 Foto (Maks. 3)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (photoList.size >= 3) StatusRejected else Color(0xFF2563EB),
                                    modifier = Modifier.testTag("photo_count_indicator")
                                )
                            }
                        }

                        // Thumbnail strip if there are multiple photos
                        if (photoList.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(photoList.indices.toList()) { index ->
                                    val path = photoList[index]
                                    val isSelected = (activePhotoIndex == index) || (selectedImageUri == path)
                                    Box(
                                        modifier = Modifier
                                            .size(68.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(
                                                2.dp,
                                                if (isSelected) TelkomRed else Color(0xFFCBD5E1),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable {
                                                activePhotoIndex = index
                                                selectedImageUri = path
                                            }
                                    ) {
                                        AsyncImage(
                                            model = path,
                                            contentDescription = "Foto ${index + 1}",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                        // Delete button on thumbnail
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(2.dp)
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(Color.Black.copy(alpha = 0.7f))
                                                .clickable {
                                                    val updated = photoList.toMutableList().apply { removeAt(index) }
                                                    photoList = updated
                                                    activePhotoIndex = (activePhotoIndex - 1).coerceAtLeast(0)
                                                    selectedImageUri = updated.getOrNull(activePhotoIndex)
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Hapus",
                                                tint = Color.White,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomStart)
                                                .padding(2.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color.Black.copy(alpha = 0.65f))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = "#${index + 1}",
                                                fontSize = 9.sp,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

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
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = { triggerCameraSimulation() },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("camera_capture_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = TelkomRed),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                            ) {
                                Icon(AppIcons.Camera, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Kamera", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("gallery_pick_button"),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                            ) {
                                Icon(AppIcons.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Galeri", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    genericFilePickerLauncher.launch("*/*")
                                },
                                modifier = Modifier
                                    .weight(1.1f)
                                    .testTag("generic_file_pick_button"),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                            ) {
                                Icon(AppIcons.Upload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Pilih Berkas", fontSize = 11.sp)
                            }
                        }

                        // Simulation & Validation Testing Card (SOP Telkom Akses)
                        Spacer(modifier = Modifier.height(14.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("upload_validation_test_card"),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Uji Validasi Unggahan (Aturan SOP)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF92400E)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Mengunggah berkas >10 MB, format selain JPG/PNG/WEBP, atau foto ke-4 akan ditolak dengan pesan kesalahan resmi.",
                                    fontSize = 11.sp,
                                    color = Color(0xFFB45309),
                                    lineHeight = 15.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { triggerSimulatedValidationTest("LARGE_FILE") },
                                        modifier = Modifier.weight(1f).testTag("test_validation_large_file"),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB45309))
                                    ) {
                                        Text(">10 MB", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    OutlinedButton(
                                        onClick = { triggerSimulatedValidationTest("INVALID_FORMAT") },
                                        modifier = Modifier.weight(1.1f).testTag("test_validation_invalid_format"),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB45309))
                                    ) {
                                        Text("Non-JPG/PNG", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    OutlinedButton(
                                        onClick = { triggerSimulatedValidationTest("FOURTH_PHOTO") },
                                        modifier = Modifier.weight(1f).testTag("test_validation_fourth_photo"),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB45309))
                                    ) {
                                        Text("Foto ke-4", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
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

    // Rejection Alert Dialog
    if (showRejectionDialog && uploadRejectionMessage != null) {
        AlertDialog(
            onDismissRequest = { showRejectionDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = AppIcons.ErrorCircle,
                        contentDescription = null,
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Unggahan Ditolak",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF991B1B)
                    )
                }
            },
            text = {
                Text(
                    text = uploadRejectionMessage ?: "",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 18.sp,
                    modifier = Modifier.testTag("rejection_dialog_text")
                )
            },
            confirmButton = {
                Button(
                    onClick = { showRejectionDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = TelkomRed),
                    modifier = Modifier.testTag("dismiss_rejection_dialog_button")
                ) {
                    Text("Mengerti")
                }
            }
        )
    }
}
