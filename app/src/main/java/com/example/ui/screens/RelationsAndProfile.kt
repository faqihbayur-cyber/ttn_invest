package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.SuccessColor
import com.example.ui.theme.WarningColor
import com.example.viewmodel.InvestorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelationsAndProfileScreen(
    viewModel: InvestorViewModel,
    modifier: Modifier = Modifier
) {
    // Top-routing indices: SUPPORT or PROFILE
    var isShowingSupport by remember { mutableStateOf(false) }

    val investor by viewModel.currentInvestor.collectAsState()
    val ticketsList by viewModel.tickets.collectAsState()
    val logsList by viewModel.logs.collectAsState()

    val activeChatId by viewModel.activeChatTicketId.collectAsState()
    val supportChatMsgInput by viewModel.supportChatInputText.collectAsState()

    val ticketSubjectState by viewModel.ticketSubject.collectAsState()
    val ticketDeptState by viewModel.ticketCategory.collectAsState()
    val ticketPriorityState by viewModel.ticketPriority.collectAsState()

    var showCreateTicketForm by remember { mutableStateOf(false) }

    val isSslActive by viewModel.isSslPinned.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // Sub-nav Dual segments header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Layanan & Pengaturan",
                    fontSize = 22.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Otentikasi biometrik dan jalur bantuan khusus",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isShowingSupport) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { isShowingSupport = true }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.ConnectWithoutContact,
                                contentDescription = null,
                                tint = if (isShowingSupport) Color.White else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Hubung Relations",
                                color = if (isShowingSupport) Color.White else MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (!isShowingSupport) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { isShowingSupport = false }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = if (!isShowingSupport) Color.White else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Profil & Keamanan",
                                color = if (!isShowingSupport) Color.White else MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Body Display switcher switchers
            if (isShowingSupport) {
                // Section A: Help Desk Desk Live Ticket system
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(SuccessColor.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.SupportAgent, contentDescription = null, tint = SuccessColor)
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Investor Relations Chat", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Rata-rata respon secepat 2 menit", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                                }

                                Button(
                                    onClick = { /* Redirection WhatsApp mockup */ },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessColor)
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("WhatsApp", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tiket Bantuan Aktif (${ticketsList.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Button(
                                onClick = { showCreateTicketForm = true },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(34.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Tiket Baru", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    items(ticketsList) { ticket ->
                        TicketListRow(
                            ticket = ticket,
                            onClickChat = { viewModel.activeChatTicketId.value = ticket.id }
                        )
                    }
                }
            } else {
                // Section B: Biometric switches, Handshakes, and device audit logs
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        // User tier profile visual label
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f),
                                    RoundedCornerShape(20.dp)
                                ),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = investor.name.split(" ").map { it.take(1) }.joinToString(""),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column {
                                        Text(text = investor.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Text(text = "ID: ${investor.id}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                                        
                                        Spacer(modifier = Modifier.height(4.dp))

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f))
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "${investor.tier} INVESTOR",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp,
                                                color = MaterialTheme.colorScheme.tertiary
                                            )
                                        }
                                    }
                                }

                                Divider(modifier = Modifier.padding(vertical = 14.dp), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))

                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Total Lembar", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                                        Text("${investor.shareUnits} Unit", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Ownership Rasio", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                                        Text("${investor.ownershipPercentage}% Modal", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Konfigurasi Keamanan Kripto",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Biometrics switch
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f))
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Fingerprint, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Biometric Login", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Verifikasi sidik jari saat startup", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                                }
                            }
                            Switch(
                                checked = investor.biometricEnabled,
                                onCheckedChange = { viewModel.toggleBiometricSetting(it) }
                            )
                        }
                    }

                    // SSL Pinning switch
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f))
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VpnKey, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("SSL Pinning Enforcer", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Bypass anti-tampering proxy MITM", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                                }
                            }
                            Switch(
                                checked = isSslActive,
                                onCheckedChange = { viewModel.toggleSslPinning() }
                            )
                        }
                    }

                    // Show active crypt token
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SettingsEthernet, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Token Sesi JWT Aktif", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.tertiary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = viewModel.jwtToken.collectAsState().value,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }

                    // Timeline audit device session logs
                    item {
                        Text(
                            text = "Log Aktifitas Sesi Gawai",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    items(logsList) { log ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Circle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(8.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = log.action, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(text = "${log.deviceName} • IP: ${log.ipAddress} • ${log.timestamp}", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }

                    // Perform Sign out button
                    item {
                        Button(
                            onClick = { viewModel.performSelfLogout() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.ExitToApp, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Keluar Sesi Aman", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // 3. Float Creator Ticket Form Popup Bottom Sheet dialog
        if (showCreateTicketForm) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { showCreateTicketForm = false }
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(20.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .clickable(enabled = false) {}
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Buat Tiket Bantuan", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            IconButton(onClick = { showCreateTicketForm = false }) {
                                Icon(Icons.Default.Close, contentDescription = null)
                            }
                        }

                        // Subject input
                        OutlinedTextField(
                            value = ticketSubjectState,
                            onValueChange = { viewModel.ticketSubject.value = it },
                            placeholder = { Text("Tulis subjek laporan bantuan...") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        // Dept selector button
                        Text("Pilih Divisi Departemen:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Finance & Dividend", "Legal & Compliance").forEach { dept ->
                                val selected = (ticketDeptState == dept)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                        .clickable { viewModel.ticketCategory.value = dept }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = dept,
                                        fontSize = 11.sp,
                                        color = if (selected) Color.White else MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }

                        // Submission triggers
                        Button(
                            onClick = {
                                viewModel.createSupportTicket()
                                showCreateTicketForm = false
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            enabled = ticketSubjectState.isNotBlank()
                        ) {
                            Text("Kirim Tiket Resmi", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 4. Live Chat Messaging Overlay Layout
        activeChatId?.let { tckId ->
            val activeTicket = ticketsList.find { it.id == tckId }
            activeTicket?.let { ticket ->
                LiveChatThreadOverlay(
                    ticket = ticket,
                    msgInput = supportChatMsgInput,
                    onMsgInputChange = { viewModel.supportChatInputText.value = it },
                    onSend = { viewModel.sendTicketMessage() },
                    onClose = { viewModel.activeChatTicketId.value = null }
                )
            }
        }
    }
}

// Helper Card lists cell
@Composable
fun TicketListRow(
    ticket: SupportTicket,
    onClickChat: () -> Unit
) {
    val isOpen = (ticket.status == TicketStatus.OPEN)
    val isInProgress = (ticket.status == TicketStatus.IN_PROGRESS)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Tiket #${ticket.id}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                
                // StatusBadge representation
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when {
                                isOpen -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                                isInProgress -> WarningColor.copy(alpha = 0.15f)
                                else -> SuccessColor.copy(alpha = 0.15f)
                            }
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = ticket.status.name,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isOpen -> MaterialTheme.colorScheme.tertiary
                            isInProgress -> WarningColor
                            else -> SuccessColor
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = ticket.subject, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = "Departemen: ${ticket.department} • Prioritas: ${ticket.priority}", fontSize = 11.sp, color = Color.Gray)

            Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Diuari: ${ticket.lastUpdate}", fontSize = 10.sp, color = Color.Gray)
                
                Button(
                    onClick = onClickChat,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Masuk Chatting", fontSize = 10.sp)
                }
            }
        }
    }
}

// Live Chat Messaging Panel Overlay Modal drawer
@Composable
fun LiveChatThreadOverlay(
    ticket: SupportTicket,
    msgInput: String,
    onMsgInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Live Chat Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column {
                    Text(text = "Tiket Hub #${ticket.id}", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                    Text(text = ticket.subject, fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f), maxLines = 1)
                }
            }

            // Chat lists scrolling viewport
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(ticket.messages) { msg ->
                    val isUser = (msg.senderId == "INVESTOR")
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 14.dp,
                                        topEnd = 14.dp,
                                        bottomStart = if (isUser) 14.dp else 0.dp,
                                        bottomEnd = if (isUser) 0.dp else 14.dp
                                    )
                                )
                                .background(
                                    if (isUser) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .padding(12.dp)
                        ) {
                            Text(
                                text = msg.text,
                                color = if (isUser) Color.White else MaterialTheme.colorScheme.onBackground,
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = "${msg.senderName} • ${msg.timestamp}",
                            fontSize = 9.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
                        )
                    }
                }
            }

            // Input Send Row panel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = msgInput,
                    onValueChange = onMsgInputChange,
                    placeholder = { Text("Ketik pesan Anda ke dukungan...", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                IconButton(
                    onClick = onSend,
                    enabled = msgInput.isNotBlank(),
                    colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Kirim", tint = Color.White)
                }
            }
        }
    }
}
