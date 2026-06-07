package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PremiumCard
import com.example.ui.components.formatRupiah
import com.example.ui.theme.SuccessColor
import com.example.ui.theme.WarningColor
import com.example.viewmodel.InvestorViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentSimulatorScreen(
    viewModel: InvestorViewModel,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // Simulation Input States
    var investmentAmount by remember { mutableStateOf(50000000.0) } // Default 50 Million IDR
    var durationYears by remember { mutableStateOf(3) } // Default 3 years
    var estimatedYield by remember { mutableStateOf(15.0) } // Default 15% p.a.
    var reinvestDividends by remember { mutableStateOf(false) } // Default Simple Payout (No compound)

    // Raw text field representation for typing amount
    var amountInputText by remember { mutableStateOf("50.000.000") }

    // Synchronize slider changes back to text input text formatted
    LaunchedEffect(investmentAmount) {
        val formatted = formatNumberId(investmentAmount.toLong())
        if (amountInputText.replace(".", "") != formatted.replace(".", "")) {
            amountInputText = formatted
        }
    }

    // Helper to parse typed text
    fun updateAmountFromText(text: String) {
        val sanitized = text.replace(".", "").replace(",", "")
        val parsed = sanitized.toDoubleOrNull()
        if (parsed != null) {
            investmentAmount = parsed.coerceIn(1000000.0, 1000000000.0) // 1 Juta to 1 Milyar bounds
        }
    }

    // Calculation results
    val results = remember(investmentAmount, durationYears, estimatedYield, reinvestDividends) {
        val rate = estimatedYield / 100.0
        val list = mutableListOf<Pair<Int, Double>>()
        
        // Populate year-by-year value curve
        for (year in 0..durationYears) {
            val value = if (reinvestDividends) {
                investmentAmount * Math.pow(1.0 + rate, year.toDouble())
            } else {
                investmentAmount + (investmentAmount * rate * year)
            }
            list.add(year to value)
        }

        val endingBalance = list.last().second
        val totalProfit = endingBalance - investmentAmount
        val roiPercentage = (totalProfit / investmentAmount) * 100.0
        val avgMonthlyDividend = totalProfit / (durationYears * 12)

        SimulationResults(
            yearCurve = list,
            endingBalance = endingBalance,
            totalProfit = totalProfit,
            roiPercentage = roiPercentage,
            avgMonthlyDividend = avgMonthlyDividend
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Welcome Header Inside Tab
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Proyeksi Kesuksesan Modal",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = "Simulasikan modal unit Anda secara transparan bersama unit logistik Teh Tarik Nusantara.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    lineHeight = 15.sp
                )
            }
        }

        // Section: Preset Templates
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                Text(
                    text = "Gunakan Paket Kemitraan Standar:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val presets = listOf(
                        PresetConfig("Kiosk", 15000000.0, 3, 14.0, false, "14% p.a."),
                        PresetConfig("Premium", 75000000.0, 5, 16.0, true, "16% p.a. (Compound)"),
                        PresetConfig("Regional", 250000000.0, 5, 18.0, true, "18% p.a. (Compound)")
                    )

                    presets.forEach { preset ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    RoundedCornerShape(10.dp)
                                )
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                .clickable {
                                    investmentAmount = preset.amount
                                    durationYears = preset.duration
                                    estimatedYield = preset.yield
                                    reinvestDividends = preset.reinvest
                                    amountInputText = formatNumberId(preset.amount.toLong())
                                    focusManager.clearFocus()
                                }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = preset.label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = formatRupiahMini(preset.amount),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Configurator Board
        item {
            PremiumCard(
                modifier = Modifier.padding(horizontal = 20.dp),
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Title Config
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Konfigurasi Parameter Investasi",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))

                    // Input 1: Investment Amount
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Jumlah Investasi (Principal):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "Min: 1 Juta | Max: 1 Milyar",
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Custom Numeric Input Field with Indonesian Thousand Separators
                        OutlinedTextField(
                            value = amountInputText,
                            onValueChange = { input ->
                                val filtered = input.filter { it.isDigit() }
                                amountInputText = if (filtered.isEmpty()) "" else {
                                    val parsedLong = filtered.toLongOrNull()
                                    if (parsedLong != null) formatNumberId(parsedLong) else amountInputText
                                }
                                updateAmountFromText(filtered)
                            },
                            prefix = { Text("Rp ", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)) },
                            suffix = { 
                                Text(
                                    text = "(${formatRupiahMini(investmentAmount)})",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            placeholder = { Text("Masukkan nominal modal unit...") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                }
                            ),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Increments Shortcut Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                5000000.0 to "+5 Jt",
                                25000000.0 to "+25 Jt",
                                100000000.0 to "+100 Jt"
                            ).forEach { (valAdd, label) ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                        .clickable {
                                            val nextVal = (investmentAmount + valAdd).coerceAtMost(1000000000.0)
                                            investmentAmount = nextVal
                                            amountInputText = formatNumberId(nextVal.toLong())
                                            focusManager.clearFocus()
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            // Reset to default button
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.08f))
                                    .clickable {
                                        investmentAmount = 10000000.0
                                        amountInputText = "10.000.000"
                                        focusManager.clearFocus()
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Reset 10 Jt",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    // Input 2: Yield Rate Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Estimasi Tingkat Pengembalian (Yield Annual):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "%.1f%% p.a.".format(estimatedYield),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Slider(
                            value = estimatedYield.toFloat(),
                            onValueChange = { 
                                estimatedYield = Math.round(it * 10f) / 10.0 // Round to 0.1 decimal point
                            },
                            valueRange = 5.0f..35.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    // Input 3: Jangka Waktu Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Jangka Waktu Simpan (Tenor):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "$durationYears Tahun",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Slider(
                            value = durationYears.toFloat(),
                            onValueChange = { durationYears = it.toInt() },
                            valueRange = 1.0f..10.0f,
                            steps = 8, // Integers between 1 and 10
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    // Input 4: Reinvestment Method Toggle
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.04f))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(
                                        imageVector = if (reinvestDividends) Icons.Default.Autorenew else Icons.Default.Payments,
                                        contentDescription = null,
                                        tint = if (reinvestDividends) MaterialTheme.colorScheme.primary else SuccessColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (reinvestDividends) "Metode Berbunga (Compound)" else "Metode Tunai Rutin (Simple p.a.)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (reinvestDividends) 
                                        "Hasil bagi dividen otomatis disetor balik sebagai penambah modal unit utama (Compounding)."
                                        else "Dividen ditarik tunai berkala setiap kuartal ke rek investor sementara modal utama flat.",
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                    lineHeight = 12.sp
                                )
                            }
                            
                            Switch(
                                checked = reinvestDividends,
                                onCheckedChange = { reinvestDividends = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Section: Projection visual dynamic chart drawing card
        item {
            PremiumCard(
                modifier = Modifier.padding(horizontal = 20.dp),
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "KURVA PERUMBUHAN INVESTASI",
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Perbandingan Principal vs Akumulasi Saldo Total",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }

                        // ROI Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SuccessColor.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "ROI: +%.1f%%".format(results.roiPercentage),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Draw our custom high-fidelity projection graph on Jetpack Compose Canvas!
                    ProjectionInteractiveGraph(
                        curveData = results.yearCurve,
                        primaryColor = MaterialTheme.colorScheme.primary,
                        profitColor = SuccessColor,
                        principalValue = investmentAmount
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Legenda keterangan warna
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                            )
                            Text("Principal (Modal Tetap)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(SuccessColor)
                            )
                            Text("Akumulasi Keuntungan", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                        }
                    }
                }
            }
        }

        // Section: Summary Numeric Output Metrics Grid
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Proyeksi Angka Hasil Simulasi:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )

                // Grid 1st Row: Initial principal & Net profit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricBox(
                        modifier = Modifier.weight(1f),
                        title = "Modal Unit Awal",
                        value = formatRupiah(investmentAmount),
                        color = MaterialTheme.colorScheme.onBackground,
                        icon = Icons.Default.AccountBalanceWallet
                    )

                    MetricBox(
                        modifier = Modifier.weight(1f),
                        title = "Estimasi Laba Bersih",
                        value = formatRupiah(results.totalProfit),
                        color = SuccessColor,
                        icon = Icons.Default.TrendingUp
                    )
                }

                // Grid 2nd Row: Ending Balance & Avg Monthly Dividend Revenue
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricBox(
                        modifier = Modifier.weight(1f),
                        title = "Total Nilai Akhir",
                        value = formatRupiah(results.endingBalance),
                        color = MaterialTheme.colorScheme.primary,
                        icon = Icons.Default.WorkspacePremium
                    )

                    MetricBox(
                        modifier = Modifier.weight(1f),
                        title = "Rata-Rata Dividen Bulanan",
                        value = formatRupiah(results.avgMonthlyDividend),
                        color = WarningColor,
                        icon = Icons.Default.MonetizationOn
                    )
                }

                // Final Notice Safety Clause matching financial regulations in ID
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Simulasi di atas menggunakan data empiris performa kargo logistik Teh Tarik Nusantara secara historis p.a. Nilai riil di lapangan dapat sedikit bergeser sesuai fluktuasi panen teh perkebunan lokal Jawa Barat & Sumatra.",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            lineHeight = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

// Custom interactive visual layout component for drawing year curve projection
@Composable
fun ProjectionInteractiveGraph(
    curveData: List<Pair<Int, Double>>,
    primaryColor: Color,
    profitColor: Color,
    principalValue: Double,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(Color.Black.copy(alpha = 0.02f), RoundedCornerShape(12.dp))
            .border(1.dp, Color.LightGray.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .padding(top = 16.dp, bottom = 24.dp, start = 14.dp, end = 20.dp)
    ) {
        val maxValInCurve = curveData.maxOfOrNull { it.second } ?: 1.0
        val minValInCurve = principalValue

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val pointsCount = curveData.size

            val spaceX = if (pointsCount > 1) width / (pointsCount - 1) else width
            val maxValueRatio = maxValInCurve * 1.15 // 15% slack space on top for neat rendering

            // 1. Draw horizontal guidelines of cash grids
            val gridLines = 4
            val gridStepValue = maxValueRatio / gridLines
            for (i in 1..gridLines) {
                val yCoord = height - (height * (i.toFloat() / gridLines))
                
                // Grid horizontal thin guideline
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.15f),
                    start = Offset(0f, yCoord),
                    end = Offset(width, yCoord),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
            }

            // 2. Draw Principal baseline shadow bar (showing the initial investment)
            val principalHeight = height * (principalValue.toFloat() / maxValueRatio.toFloat())
            val principalY = height - principalHeight
            drawRect(
                color = primaryColor.copy(alpha = 0.08f),
                topLeft = Offset(0f, principalY),
                size = Size(width, principalHeight)
            )
            drawLine(
                color = primaryColor.copy(alpha = 0.25f),
                start = Offset(0f, principalY),
                end = Offset(width, principalY),
                strokeWidth = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
            )

            // 3. Formulate and draw the Growth path
            val path = Path()
            val gradientPath = Path()

            curveData.forEachIndexed { idx, pair ->
                val x = idx * spaceX
                val valueRatio = pair.second / maxValueRatio
                val y = height - (height * valueRatio).toFloat()

                if (idx == 0) {
                    path.moveTo(x, y)
                    gradientPath.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                    gradientPath.lineTo(x, y)
                }
            }

            // Completes gradient fill path from growth line to the bottom
            if (curveData.isNotEmpty()) {
                val lastIdx = curveData.size - 1
                val lastX = lastIdx * spaceX
                gradientPath.lineTo(lastX, height)
                gradientPath.lineTo(0f, height)
                gradientPath.close()

                // Draw gorgeous smooth organic projection gradient!
                drawPath(
                    path = gradientPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            profitColor.copy(alpha = 0.35f),
                            profitColor.copy(alpha = 0.0f)
                        )
                    )
                )

                // Draw the actual growth neon-style line
                drawPath(
                    path = path,
                    color = profitColor,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // 4. Paint markers for each year point
                curveData.forEachIndexed { idx, pair ->
                    val x = idx * spaceX
                    val valueRatio = pair.second / maxValueRatio
                    val y = height - (height * valueRatio).toFloat()

                    // Pulse outer circle glow
                    drawCircle(
                        color = profitColor.copy(alpha = 0.3f),
                        radius = 8.dp.toPx(),
                        center = Offset(x, y)
                    )

                    // Clear inner center dot
                    drawCircle(
                        color = profitColor,
                        radius = 4.dp.toPx(),
                        center = Offset(x, y)
                    )

                    drawCircle(
                        color = Color.White,
                        radius = 2.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
            }
        }

        // 5. Draw Year X-Labels overlay outside canvas clip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .offset(y = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            curveData.forEachIndexed { idx, pair ->
                Text(
                    text = "Th-${pair.first}",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    modifier = Modifier.width(36.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// Metric block dashboard cell
@Composable
fun MetricBox(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = modifier.border(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
            RoundedCornerShape(14.dp)
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color.copy(alpha = 0.7f),
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1
            )
        }
    }
}

// High performance formatting helper for Rupiah Mini representing jutaan/milyaran clean
fun formatRupiahMini(amount: Double): String {
    return when {
        amount >= 1_000_000_000.0 -> {
            val bills = amount / 1_000_000_000.0
            if (bills % 1.0 == 0.0) "Rp %.0f Milyar".format(bills) else "Rp %.1f M.".format(bills)
        }
        amount >= 1_000_000.0 -> {
            val mills = amount / 1_000_000.0
            if (mills % 1.0 == 0.0) "Rp %.0f Juta".format(mills) else "Rp %.1f Juta".format(mills)
        }
        else -> formatRupiah(amount)
    }
}

// Clean thousand dot formatting helper
fun formatNumberId(number: Long): String {
    return try {
        val formatter = NumberFormat.getInstance(Locale("in", "ID"))
        formatter.format(number)
    } catch (e: Exception) {
        number.toString()
    }
}

// Preset and projection models
data class PresetConfig(
    val label: String,
    val amount: Double,
    val duration: Int,
    val yield: Double,
    val reinvest: Boolean,
    val subtitle: String
)

data class SimulationResults(
    val yearCurve: List<Pair<Int, Double>>,
    val endingBalance: Double,
    val totalProfit: Double,
    val roiPercentage: Double,
    val avgMonthlyDividend: Double
)
