package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import com.example.ui.theme.AppIcons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MasterData
import com.example.data.model.EquipmentMaster
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.repository.InspectionRepository
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.admin.AdminTechnicianDetailVerificationScreen
import com.example.ui.screens.admin.ExportCsvDialog
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.technician.EquipmentDetailUploadScreen
import com.example.ui.screens.technician.TechnicianChecklistScreen
import com.example.ui.screens.technician.TechnicianDashboardScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TelkomRed

enum class AppScreen {
    LOGIN,
    TECH_DASHBOARD,
    TECH_CHECKLIST,
    TECH_EQUIPMENT_DETAIL,
    ADMIN_DASHBOARD,
    ADMIN_TECH_DETAIL
}

class MainActivity : ComponentActivity() {

    private lateinit var repository: InspectionRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        repository = InspectionRepository(applicationContext)

        setContent {
            MyApplicationTheme {
                AlkerSarkerApp(repository = repository)
            }
        }
    }
}

@Composable
fun AlkerSarkerApp(repository: InspectionRepository) {
    val currentUser by repository.currentUser.collectAsState()
    val currentPeriod by repository.currentPeriod.collectAsState()
    val submissions by repository.submissions.collectAsState()
    val auditLogs by repository.auditLogs.collectAsState()

    var currentScreen by remember { mutableStateOf(if (currentUser != null) AppScreen.TECH_DASHBOARD else AppScreen.LOGIN) }
    var selectedEquipment by remember { mutableStateOf<EquipmentMaster?>(null) }
    var selectedTechnician by remember { mutableStateOf<User?>(null) }

    var showRoleSwitcherDialog by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }
    var showCsvExportDialog by remember { mutableStateOf(false) }
    var csvExportContent by remember { mutableStateOf("") }

    val equipmentList = remember { repository.getEquipmentList() }

    fun handleUserLogin(user: User) {
        repository.switchUser(user)
        currentScreen = if (user.role == UserRole.ADMIN) {
            AppScreen.ADMIN_DASHBOARD
        } else {
            AppScreen.TECH_DASHBOARD
        }
        selectedEquipment = null
        selectedTechnician = null
    }

    fun handleLogout() {
        repository.logout()
        currentScreen = AppScreen.LOGIN
        selectedEquipment = null
        selectedTechnician = null
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (currentUser == null || currentScreen == AppScreen.LOGIN) {
            LoginScreen(
                onLoginSuccess = { user ->
                    handleUserLogin(user)
                },
                onLoginAttempt = { nik, pass ->
                    repository.login(nik, pass)
                }
            )
        } else {
            val user = currentUser!!
            when (currentScreen) {
                AppScreen.LOGIN -> {
                    // Handled above
                }

                AppScreen.TECH_DASHBOARD -> {
                    val techSubmissions = submissions.filter {
                        it.technicianId == user.id && it.periodId == currentPeriod.id
                    }
                    TechnicianDashboardScreen(
                        currentUser = user,
                        currentPeriod = currentPeriod,
                        equipmentList = equipmentList,
                        submissions = techSubmissions,
                        onSelectEquipment = { eq ->
                            selectedEquipment = eq
                            currentScreen = AppScreen.TECH_EQUIPMENT_DETAIL
                        },
                        onViewChecklist = {
                            currentScreen = AppScreen.TECH_CHECKLIST
                        },
                        onSwitchRole = { showRoleSwitcherDialog = true },
                        onLogout = { showLogoutConfirmDialog = true }
                    )
                }

                AppScreen.TECH_CHECKLIST -> {
                    BackHandler {
                        currentScreen = AppScreen.TECH_DASHBOARD
                    }
                    val techSubmissions = submissions.filter {
                        it.technicianId == user.id && it.periodId == currentPeriod.id
                    }
                    TechnicianChecklistScreen(
                        equipmentList = equipmentList,
                        submissions = techSubmissions,
                        onSelectEquipment = { eq ->
                            selectedEquipment = eq
                            currentScreen = AppScreen.TECH_EQUIPMENT_DETAIL
                        },
                        onBack = { currentScreen = AppScreen.TECH_DASHBOARD }
                    )
                }

                AppScreen.TECH_EQUIPMENT_DETAIL -> {
                    BackHandler {
                        currentScreen = AppScreen.TECH_DASHBOARD
                    }
                    val eq = selectedEquipment ?: equipmentList[0]
                    val currentSub = submissions.find {
                        it.technicianId == user.id &&
                        it.periodId == currentPeriod.id &&
                        it.equipmentId == eq.id
                    }

                    EquipmentDetailUploadScreen(
                        equipment = eq,
                        currentSubmission = currentSub,
                        currentUser = user,
                        currentPeriod = currentPeriod,
                        onSaveSubmission = { sub ->
                            repository.saveSubmission(sub)
                            currentScreen = AppScreen.TECH_DASHBOARD
                        },
                        onBack = { currentScreen = AppScreen.TECH_DASHBOARD }
                    )
                }

                AppScreen.ADMIN_DASHBOARD -> {
                    val summaries = repository.getTechnicianSummaries(currentPeriod.id)
                    AdminDashboardScreen(
                        currentAdmin = user,
                        currentPeriod = currentPeriod,
                        technicianSummaries = summaries,
                        allSubmissions = submissions.filter { it.periodId == currentPeriod.id },
                        auditLogs = auditLogs,
                        onSelectTechnician = { tech ->
                            selectedTechnician = tech
                            currentScreen = AppScreen.ADMIN_TECH_DETAIL
                        },
                        onBulkVerify = {
                            repository.bulkVerifyPending(user.name)
                        },
                        onExportCsv = {
                            csvExportContent = repository.generateRekapCsv(currentPeriod.id)
                            showCsvExportDialog = true
                        },
                        onSwitchRole = { showRoleSwitcherDialog = true },
                        onLogout = { showLogoutConfirmDialog = true }
                    )
                }

                AppScreen.ADMIN_TECH_DETAIL -> {
                    BackHandler {
                        currentScreen = AppScreen.ADMIN_DASHBOARD
                    }
                    val tech = selectedTechnician ?: MasterData.technicians[0]
                    val techSubmissions = submissions.filter {
                        it.technicianId == tech.id && it.periodId == currentPeriod.id
                    }

                    AdminTechnicianDetailVerificationScreen(
                        technician = tech,
                        currentPeriod = currentPeriod,
                        equipmentList = equipmentList,
                        submissions = techSubmissions,
                        onVerify = { subId, status, feedback ->
                            repository.verifySubmission(subId, status, feedback, user.name)
                        },
                        onBack = { currentScreen = AppScreen.ADMIN_DASHBOARD }
                    )
                }
            }
        }

        // Role Switcher Dialog for rapid testing
        if (showRoleSwitcherDialog && currentUser != null) {
            RoleSwitcherDialog(
                currentUser = currentUser!!,
                onSelectUser = { newUser ->
                    handleUserLogin(newUser)
                    showRoleSwitcherDialog = false
                },
                onDismiss = { showRoleSwitcherDialog = false }
            )
        }

        // Logout Confirmation Dialog
        if (showLogoutConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutConfirmDialog = false },
                title = { Text("Konfirmasi Logout") },
                text = {
                    Text("Apakah Anda yakin ingin keluar dari akun ${currentUser?.name}?")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showLogoutConfirmDialog = false
                            handleLogout()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TelkomRed),
                        modifier = Modifier.testTag("confirm_logout_button")
                    ) {
                        Text("Ya, Keluar")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showLogoutConfirmDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }

        // CSV Export Dialog
        if (showCsvExportDialog) {
            ExportCsvDialog(
                csvData = csvExportContent,
                periodName = currentPeriod.name,
                onDismiss = { showCsvExportDialog = false }
            )
        }
    }
}

@Composable
fun RoleSwitcherDialog(
    currentUser: User,
    onSelectUser: (User) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Ganti Peran Pengguna (Demo/Testing)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Pilih akun untuk menguji fitur sisi Teknisi Lapangan atau sisi Admin Assurance PT Telkom Akses Padang:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Admin Option
                    item {
                        val admin = MasterData.defaultAdmin
                        val isSelected = currentUser.id == admin.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectUser(admin) }
                                .testTag("select_admin_role_card"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFFFEF2F2) else MaterialTheme.colorScheme.surface
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) TelkomRed else Color(0xFFE2E8F0)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(TelkomRed),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        AppIcons.AdminShield,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${admin.name} (ADMIN)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = admin.stoUnit,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = TelkomRed)
                                }
                            }
                        }
                    }

                    // Technician Options
                    items(MasterData.technicians) { tech ->
                        val isSelected = currentUser.id == tech.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectUser(tech) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFFEFF6FF) else MaterialTheme.colorScheme.surface
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) Color(0xFF2563EB) else Color(0xFFE2E8F0)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2563EB)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        AppIcons.Engineering,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${tech.name} (TEKNISI)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${tech.stoUnit} • NIK: ${tech.nik}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF2563EB))
                                }
                            }
                        }
                    }
                }
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
