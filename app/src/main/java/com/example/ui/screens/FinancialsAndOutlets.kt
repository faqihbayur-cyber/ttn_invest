package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.SuccessColor
import com.example.ui.theme.WarningColor
import com.example.viewmodel.InvestorViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialsAndOutletsScreen(
    viewModel: InvestorViewModel,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    // Screen navigation layout sub-tabs: "FINANCIALS", "EXPANSION", or "SIMULATOR"
    var selectedTab by remember { mutableStateOf("FINANCIALS") }

    // Table state trackers
    val financialsList = viewModel.financials
    var selectedReportPeriodIdx by remember { mutableStateOf(0) }
    val report = financialsList[selectedReportPeriodIdx]

    // Outlets state trackers
    val outletsList by viewModel.outlets.collectAsState()
    val rawBranches = viewModel.branches
    val mapRegionFilter by viewModel.selectedRegion.collectAsState()
    val searchQuery by viewModel.outletSearchQuery.collectAsState()

    // Export state modal tracker
    var isExportingExcel by remember { mutableStateOf(false) }
    var isDownloadingPdf by remember { mutableStateOf(false) }
    var operationResultMessage by remember { mutableStateOf<String?>(null) }

    val filteredOutlets = remember(outletsList, mapRegionFilter, searchQuery) {
        outletsList.filter { outlet ->
            val matchesRegion = when (mapRegionFilter) {
                "SEMUA" -> true
                "DKI JAKARTA" -> outlet.province == "DKI Jakarta"
                "JAWA BARAT" -> outlet.province == "Jawa Barat"
                "JAWA TIMUR" -> outlet.province == "Jawa Timur"
                "SUMATERA" -> outlet.province == "Riau" || outlet.province == "Sumatera Utara"
                else -> true
            }
            val matchesSearch = outlet.name.contains(searchQuery, ignoreCase = true) ||
                    outlet.city.contains(searchQuery, ignoreCase = true)

            matchesRegion && matchesSearch
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // Sub-nav Header selector bar with dual tabs
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Laporan & Ekspansi",
                    fontSize = 22.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Akurasi data operasional terpusat",
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
                            .background(if (selectedTab == "FINANCIALS") MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { selectedTab = "FINANCIALS" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Assessment,
                                contentDescription = null,
                                tint = if (selectedTab == "FINANCIALS") Color.White else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Laporan",
                                color = if (selectedTab == "FINANCIALS") Color.White else MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedTab == "EXPANSION") MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { selectedTab = "EXPANSION" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Map,
                                contentDescription = null,
                                tint = if (selectedTab == "EXPANSION") Color.White else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Cabang",
                                color = if (selectedTab == "EXPANSION") Color.White else MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedTab == "SIMULATOR") MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { selectedTab = "SIMULATOR" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = if (selectedTab == "SIMULATOR") Color.White else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Simulasi ROI",
                                color = if (selectedTab == "SIMULATOR") Color.White else MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Body Display switcher
            when (selectedTab) {
                "FINANCIALS" -> {
                // Section 1: Financial Statement ledger spreadsheet
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text(
                            text = "Neraca & Laba Rugi Buku",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Period Selection Pill rows
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            financialsList.forEachIndexed { idx, item ->
                                val selected = (selectedReportPeriodIdx == idx)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (selected) MaterialTheme.colorScheme.tertiary
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .clickable { selectedReportPeriodIdx = idx }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = item.period,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }

                    // Numeric spreadsheet elements
                    item {
                        PremiumCard(
                            backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            FinancialReportRow(label = "Pendapatan Kotor (Revenue)", value = report.revenue)
                            FinancialReportRow(label = "Beban HPP (Cost of Goods)", value = report.cost, prefixSign = "- ")
                            FinancialReportRow(label = "Laba Kotor Terkonsolidasi", value = report.profit, textBold = true, highlighted = true)
                            
                            Divider(modifier = Modifier.padding(vertical = 10.dp))
                            
                            FinancialReportRow(label = "Margin EBITDA Operasional", value = report.ebitda)
                            FinancialReportRow(label = "Aliran Arus Kas (Cash Flow)", value = report.cashFlow)
                            
                            Divider(modifier = Modifier.padding(vertical = 10.dp))

                            FinancialReportRow(label = "Aset Korporasi Nasional", value = report.asset, textBold = true)
                            FinancialReportRow(label = "Kewajiban Kewajiban (Liability)", value = report.liability, prefixSign = "- ")
                            FinancialReportRow(label = "Modal Bersih (Equity)", value = report.equity, textBold = true, highlighted = true)
                        }
                    }

                    // Excel & PDF action triggers
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // PDF Trigger
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        isDownloadingPdf = true
                                        operationResultMessage = null
                                        delay(2000)
                                        isDownloadingPdf = false
                                        operationResultMessage = "Dokumen PDF Audited ${report.period} berhasil diunduh ke folder Downloads."
                                    }
                                },
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                if (isDownloadingPdf) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                } else {
                                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Download PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Excel Trigger
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        isExportingExcel = true
                                        operationResultMessage = null
                                        delay(1800)
                                        isExportingExcel = false
                                        operationResultMessage = "Export buku kas Excel selesai. Berkas disimpan di penyimpanan lokal."
                                    }
                                },
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                            ) {
                                if (isExportingExcel) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                } else {
                                    Icon(Icons.Default.Output, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Ekspor Excel", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // KAP Auditors Stamp representation
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SuccessColor.copy(alpha = 0.08f)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = SuccessColor)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        "Laporan Terverifikasi Wajar Tanpa Pengecualian",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = SuccessColor
                                    )
                                    Text(
                                        "Diaudit independen oleh KAP Hananta & Rekan (OJK Registered)",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
                "EXPANSION" -> {
                // Section 2: Interactive map & and regional branches list indices
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Hotspots map
                    item {
                        InteractiveBranchMap(
                            selectedRegion = mapRegionFilter,
                            onRegionSelect = { viewModel.selectedRegion.value = it }
                        )
                    }

                    // Search input fields
                    item {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.outletSearchQuery.value = it },
                            placeholder = { Text("Cari nama outlet, kota atau cabang...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Daftar Outlet Beroperasi (${filteredOutlets.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )

                            if (mapRegionFilter != "SEMUA") {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                        .clickable { viewModel.selectedRegion.value = "SEMUA" }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Reset Filter", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }

                    // Outlets cards list grid
                    if (filteredOutlets.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                                        modifier = Modifier.size(38.dp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        "Tidak ada outlet ditemukan cocok dengan kriteria.",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    } else {
                        items(filteredOutlets) { outlet ->
                            OutletItemCard(outlet)
                        }
                    }
                }
            }
                "SIMULATOR" -> {
                    InvestmentSimulatorScreen(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    )
                }
            }
        }

        // Animated action popup message
        operationResultMessage?.let { msg ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 90.dp, start = 20.dp, end = 20.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = msg, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { operationResultMessage = null }
                    )
                }
            }
        }
    }
}

// 3. Spreadsheet Data Row
@Composable
fun FinancialReportRow(
    label: String,
    value: Double,
    textBold: Boolean = false,
    highlighted: Boolean = false,
    prefixSign: String = ""
) {
    val styledColor = if (highlighted) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onBackground
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (highlighted) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (textBold) FontWeight.Bold else FontWeight.Normal,
            color = if (textBold) styledColor else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )
        Text(
            text = prefixSign + formatRupiah(value),
            fontSize = 13.sp,
            fontWeight = if (textBold) FontWeight.Bold else FontWeight.Normal,
            color = styledColor
        )
    }
}

// 4. Clean list item representation of Outlet Cards
@Composable
fun OutletItemCard(outlet: Outlet) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Store,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(text = outlet.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(
                            text = "${outlet.city}, ${outlet.province}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }

                // Render badge matching state of expansion
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when (outlet.status) {
                                OutletStatus.ACTIVE -> SuccessColor.copy(alpha = 0.15f)
                                OutletStatus.EXPANDING -> WarningColor.copy(alpha = 0.15f)
                                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = outlet.status.name,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (outlet.status) {
                            OutletStatus.ACTIVE -> SuccessColor
                            OutletStatus.EXPANDING -> WarningColor
                            else -> MaterialTheme.colorScheme.outline
                        }
                    )
                }
            }

            if (outlet.status == OutletStatus.ACTIVE) {
                Divider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Omset Hari Ini", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                        Text(formatRupiah(outlet.dailyRevenue), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SuccessColor)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Akumulasi Bulanan", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                        Text(formatRupiah(outlet.monthlyRevenue), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
