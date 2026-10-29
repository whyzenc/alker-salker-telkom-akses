package com.example.ui.screens.admin

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import com.example.ui.theme.AppIcons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.MasterData
import com.example.data.model.AuditLog
import com.example.data.model.EquipmentMaster
import com.example.data.model.EquipmentType
import com.example.data.model.InspectionStatus
import com.example.data.model.InspectionSubmission
import com.example.data.model.Period
import com.example.data.model.TechnicianSummary
import com.example.data.model.User
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

@Composable
fun AdminDashboardScreen(
    currentAdmin: User,
    currentPeriod: Period,
    technicianSummaries: List<TechnicianSummary>,
    allSubmissions: List<InspectionSubmission>,
    auditLogs: List<AuditLog>,
    onSelectTechnician: (User) -> Unit,
    onBulkVerify: () -> Unit,
    onExportCsv: () -> Unit,
    onSwitchRole: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var selectedStoFilter by remember { mutableStateOf("Semua STO") }
    var searchQuery by remember { mutableStateOf("") }
    var showBulkConfirmDialog by remember { mutableStateOf(false) }

    val pendingCount = allSubmissions.count { it.status == InspectionStatus.PENDING }
    val verifiedCount = allSubmissions.count { it.status == InspectionStatus.VERIFIED }
    val rejectedCount = allSubmissions.count { it.status == InspectionStatus.REJECTED }
    val totalSubmissions = allSubmissions.size

    val filteredTechs = technicianSummaries.filter { summary ->
        val matchesSto = selectedStoFilter == "Semua STO" || summary.technician.stoUnit == selectedStoFilter
        val matchesSearch = summary.technician.name.contains(searchQuery, ignoreCase = true) ||
                summary.technician.nik.contains(searchQuery, ignoreCase = true) ||
                summary.technician.team.contains(searchQuery, ignoreCase = true)
        matchesSto && matchesSearch
    }

    Scaffold(
        topBar = {
            TelkomTopAppBar(
                title = "Admin Assurance Dashboard",
                subtitle = "Wilayah PT Telkom Akses Padang",
                currentUser = currentAdmin,
                onSwitchRole = onSwitchRole,
                onLogout = onLogout,
                actions = {
                    IconButton(onClick = onExportCsv, modifier = Modifier.testTag("export_csv_top_button")) {
                        Icon(AppIcons.Download, contentDescription = "Ekspor CSV", tint = Color.White)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = TelkomRed,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = TelkomRed
                    )
                }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Rekap Teknisi", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                    icon = { Icon(AppIcons.People, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Antrean Verifikasi", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            if (pendingCount > 0) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(StatusPending)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "$pendingCount",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    },
                    icon = { Icon(AppIcons.Hourglass, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = { Text("Log Audit", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                    icon = { Icon(AppIcons.History, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }

            when (selectedTabIndex) {
                0 -> {
                    // TAB 1: Rekap Teknisi
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Summary Metrics
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "MONITORING PERIODE BERJALAN",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TelkomRed
                                            )
                                            Text(
                                                text = currentPeriod.name,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        OutlinedButton(
                                            onClick = onExportCsv,
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(AppIcons.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Ekspor CSV", fontSize = 11.sp)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        StatCard(
                                            title = "Menunggu",
                                            count = pendingCount,
                                            color = StatusPending,
                                            bgColor = StatusPendingBg,
                                            modifier = Modifier.weight(1f)
                                        )
                                        StatCard(
                                            title = "Terverifikasi",
                                            count = verifiedCount,
                                            color = StatusVerified,
                                            bgColor = StatusVerifiedBg,
                                            modifier = Modifier.weight(1f)
                                        )
                                        StatCard(
                                            title = "Ditolak",
                                            count = rejectedCount,
                                            color = StatusRejected,
                                            bgColor = StatusRejectedBg,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    if (pendingCount > 0) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Button(
                                            onClick = { showBulkConfirmDialog = true },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("bulk_verify_trigger_button"),
                                            colors = ButtonDefaults.buttonColors(containerColor = TelkomRed),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(AppIcons.AssignmentCheck, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Verifikasi Massal ($pendingCount Item Menunggu)", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }

                        // Search & Filter STO
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("Cari nama teknisi atau NIK...", fontSize = 13.sp) },
                                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Cari") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(MasterData.stoList) { sto ->
                                        FilterChip(
                                            selected = selectedStoFilter == sto,
                                            onClick = { selectedStoFilter = sto },
                                            label = { Text(sto, fontSize = 12.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = TelkomRed,
                                                selectedLabelColor = Color.White
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // Technician List
                        items(filteredTechs) { summary ->
                            val tech = summary.technician
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectTechnician(tech) },
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
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(CircleShape)
                                                    .background(TelkomRed),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = tech.name.take(2).uppercase(),
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = tech.name,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = "NIK: ${tech.nik} • ${tech.stoUnit}",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "${summary.completionPercentage}%",
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 15.sp,
                                                color = if (summary.completionPercentage == 100) StatusVerified else TelkomRed
                                            )
                                            Text(
                                                text = "${summary.verifiedCount}/${summary.totalItems} Alat",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Linear Progress Bar
                                    LinearProgressIndicator(
                                        progress = { summary.completionPercentage / 100f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = if (summary.completionPercentage == 100) StatusVerified else TelkomRed,
                                        trackColor = Color(0xFFE2E8F0)
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            if (summary.pendingCount > 0) {
                                                Text(
                                                    text = "${summary.pendingCount} Menunggu",
                                                    color = StatusPending,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                            if (summary.rejectedCount > 0) {
                                                Text(
                                                    text = "${summary.rejectedCount} Ditolak",
                                                    color = StatusRejected,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "Periksa Alker",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = TelkomRed
                                            )
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = null,
                                                tint = TelkomRed,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 2: Antrean Verifikasi (Pending Submissions)
                    val pendingList = allSubmissions.filter { it.status == InspectionStatus.PENDING }
                    if (pendingList.isEmpty()) {
                        EmptyStateView(
                            title = "Tidak Ada Antrean",
                            message = "Semua foto alat kerja pada periode ini telah diverifikasi atau belum ada pengiriman baru."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${pendingList.size} Pengiriman Menunggu Verifikasi",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Button(
                                        onClick = { showBulkConfirmDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = TelkomRed),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Setujui Semua", fontSize = 11.sp)
                                    }
                                }
                            }

                            items(pendingList) { sub ->
                                val tech = MasterData.technicians.find { it.id == sub.technicianId }
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (tech != null) onSelectTechnician(tech)
                                        },
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
                                                .size(56.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFF0F172A)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            AsyncImage(
                                                model = sub.photoUri,
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = sub.equipmentName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "${sub.technicianName} • ${sub.technicianSto}",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            EquipmentTypeBadge(type = sub.equipmentType)
                                        }

                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = "Buka",
                                            tint = TelkomRed
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 3: Log Audit
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(auditLogs) { log ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(
                                                        if (log.action.contains("SETUJUI")) StatusVerifiedBg
                                                        else if (log.action.contains("TOLAK")) StatusRejectedBg
                                                        else Color(0xFFEFF6FF)
                                                    )
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = log.action,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (log.action.contains("SETUJUI")) StatusVerified
                                                    else if (log.action.contains("TOLAK")) StatusRejected
                                                    else Color(0xFF2563EB)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = log.equipmentName,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        Text(
                                            text = log.timestamp,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Aktor: ${log.actorName} (${log.actorRole}) • Teknisi: ${log.technicianName}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (log.notes.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "\"${log.notes}\"",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontFamily = FontFamily.Default
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

    // Bulk verification confirmation dialog
    if (showBulkConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showBulkConfirmDialog = false },
            title = { Text("Konfirmasi Verifikasi Massal") },
            text = {
                Text("Apakah Anda yakin ingin menyetujui seluruh $pendingCount item peralatan yang berstatus Menunggu secara sekaligus?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onBulkVerify()
                        showBulkConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TelkomRed)
                ) {
                    Text("Ya, Setujui Semua")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showBulkConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun AdminTechnicianDetailVerificationScreen(
    technician: User,
    currentPeriod: Period,
    equipmentList: List<EquipmentMaster>,
    submissions: List<InspectionSubmission>,
    onVerify: (submissionId: String, status: InspectionStatus, feedback: String?) -> Unit,
    onBack: () -> Unit
) {
    val subMap = remember(submissions) { submissions.associateBy { it.equipmentId } }

    var selectedItemForAction by remember { mutableStateOf<Pair<EquipmentMaster, InspectionSubmission?>?>(null) }
    var isRejectAction by remember { mutableStateOf(false) }
    var rejectionReasonInput by remember { mutableStateOf("") }
    var reasonError by remember { mutableStateOf(false) }

    var zoomPhotoData by remember { mutableStateOf<InspectionSubmission?>(null) }

    Scaffold(
        topBar = {
            TelkomTopAppBar(
                title = technician.name,
                subtitle = "NIK: ${technician.nik} • ${technician.stoUnit}",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header summary
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
                            Column {
                                Text(
                                    text = technician.team,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TelkomRed
                                )
                                Text(
                                    text = "Telp: ${technician.phone}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = currentPeriod.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // List of equipment
            items(equipmentList) { equipment ->
                val sub = subMap[equipment.id]
                val status = sub?.status ?: InspectionStatus.UNCHECKED

                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                            StatusBadge(status = status)
                        }

                        // Photo & OCR Display if uploaded
                        if (sub != null && sub.photoUri != null) {
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Photo thumbnail with zoom button
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF0F172A))
                                        .clickable { zoomPhotoData = sub },
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = sub.photoUri,
                                        contentDescription = "Bukti Foto",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(4.dp)
                                            .clip(CircleShape)
                                            .background(Color.Black.copy(alpha = 0.6f))
                                            .padding(2.dp)
                                    ) {
                                        Icon(
                                            imageVector = AppIcons.ZoomIn,
                                            contentDescription = "Zoom",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                // Details: size, timestamp, OCR
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Waktu: ${sub.capturedDate}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Lokasi: ${sub.capturedLocation}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Ukuran: ${sub.compressedSizeKb} KB (Asli: ${sub.originalSizeKb} KB)",
                                        fontSize = 10.sp,
                                        color = StatusVerified
                                    )

                                    if (equipment.type == EquipmentType.PHOTO_OCR && sub.ocrCorrectedResult.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFEFF6FF))
                                                .padding(6.dp)
                                        ) {
                                            Column {
                                                Text(
                                                    text = "Hasil OCR (${sub.ocrConfidence}%):",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF2563EB)
                                                )
                                                Text(
                                                    text = sub.ocrCorrectedResult,
                                                    fontSize = 10.sp,
                                                    color = Color(0xFF1E3A8A),
                                                    maxLines = 3,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Rejection reason if already rejected
                            if (status == InspectionStatus.REJECTED && !sub.adminFeedback.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFFEF2F2))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "Catatan Penolakan: ${sub.adminFeedback}",
                                        fontSize = 11.sp,
                                        color = StatusRejected
                                    )
                                }
                            }

                            // Verification Action Buttons
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        selectedItemForAction = Pair(equipment, sub)
                                        isRejectAction = true
                                        rejectionReasonInput = ""
                                        reasonError = false
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRejected)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Tolak", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        onVerify(sub.id, InspectionStatus.VERIFIED, null)
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusVerified)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Setujui", fontSize = 12.sp)
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Teknisi belum mengunggah foto bukti untuk alat ini.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    // Zoom Dialog
    if (zoomPhotoData != null) {
        PhotoZoomDialog(
            photoUri = zoomPhotoData?.photoUri,
            title = zoomPhotoData?.equipmentName ?: "Foto Bukti",
            timestampText = zoomPhotoData?.capturedDate ?: "-",
            locationText = zoomPhotoData?.capturedLocation ?: "-",
            onDismiss = { zoomPhotoData = null }
        )
    }

    // Rejection Reason Modal Dialog (Wajib Alasan)
    if (isRejectAction && selectedItemForAction != null) {
        val (eq, sub) = selectedItemForAction!!
        AlertDialog(
            onDismissRequest = { isRejectAction = false },
            title = { Text("Tolak Bukti ${eq.name}", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Sesuai SOP Assurance, penolakan WAJIB menyertakan alasan yang jelas agar teknisi dapat memperbaikinya:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = rejectionReasonInput,
                        onValueChange = {
                            rejectionReasonInput = it
                            if (it.isNotBlank()) reasonError = false
                        },
                        label = { Text("Alasan Penolakan (Wajib)") },
                        placeholder = { Text("Contoh: Layar OTDR belum menyala / Foto speedometer silau") },
                        isError = reasonError,
                        minLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("rejection_reason_input")
                    )
                    if (reasonError) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Alasan penolakan tidak boleh kosong!",
                            color = StatusRejected,
                            fontSize = 11.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (rejectionReasonInput.isBlank()) {
                            reasonError = true
                        } else {
                            if (sub != null) {
                                onVerify(sub.id, InspectionStatus.REJECTED, rejectionReasonInput.trim())
                            }
                            isRejectAction = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRejected),
                    modifier = Modifier.testTag("confirm_rejection_button")
                ) {
                    Text("Kirim Penolakan")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { isRejectAction = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun ExportCsvDialog(
    csvData: String,
    periodName: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(AppIcons.Download, contentDescription = null, tint = TelkomRed)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ekspor Rekapitulasi ($periodName)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text(
                    text = "Pratinjau Data CSV (Siap diimpor ke Excel/Google Sheets):",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F172A))
                        .padding(10.dp)
                ) {
                    LazyColumn {
                        item {
                            Text(
                                text = csvData,
                                color = Color(0xFF38BDF8),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "✓ Berisi seluruh data 15 Alker/Sarker per teknisi Padang, status verifikasi, hasil OCR, dan riwayat revisi.",
                    fontSize = 11.sp,
                    color = StatusVerified
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = TelkomRed)
            ) {
                Text("Tutup")
            }
        }
    )
}
