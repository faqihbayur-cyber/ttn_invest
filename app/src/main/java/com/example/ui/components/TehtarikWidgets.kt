package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SuccessColor
import com.example.ui.theme.WarningColor
import java.text.NumberFormat
import java.util.Locale

// 1. Premium Glassmorphic Card
@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
    borderColor: Color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.25f),
    elevation: Dp = 6.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            content()
        }
    }
}

// Helper currency string formatter
fun formatRupiah(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    return formatter.format(amount).replace("Rp", "Rp ").substringBefore(",")
}

// 2. Premium Interactive Dashboard Chart (Custom-Canvas Painted)
@Composable
fun InteractiveDashboardChart(
    metricType: String, // "ASET", "REVENUE", "PROFIT", "SALES"
    filterType: String,  // "MINGGUAN", "BULANAN", "TAHUNAN"
    modifier: Modifier = Modifier
) {
    // Generate simulated data based on selected metric and filter
    val rawPrices = remember(metricType, filterType) {
        when (metricType) {
            "ASET" -> {
                when (filterType) {
                    "MINGGUAN" -> listOf(250.0, 258.0, 269.0, 281.0, 290.0, 310.0, 327.0)
                    "BULANAN" -> listOf(250.0, 262.0, 275.0, 280.0, 298.0, 315.0, 327.0)
                    else -> listOf(112.0, 180.0, 250.0, 327.0) // TAHUNAN
                }
            }
            "REVENUE" -> {
                when (filterType) {
                    "MINGGUAN" -> listOf(31.0, 35.0, 42.0, 38.0, 45.0, 48.0, 52.0)
                    "BULANAN" -> listOf(31.0, 32.5, 34.0, 36.8, 38.5, 40.2, 42.1)
                    else -> listOf(12.0, 28.0, 38.5, 42.1)
                }
            }
            "PROFIT" -> {
                when (filterType) {
                    "MINGGUAN" -> listOf(3.8, 4.2, 5.1, 4.6, 5.5, 5.8, 6.2)
                    "BULANAN" -> listOf(3.6, 3.9, 4.2, 4.5, 4.8, 5.0, 5.2)
                    else -> listOf(1.2, 2.9, 4.8, 5.2)
                }
            }
            else -> { // SALES TRADING TREN
                when (filterType) {
                    "MINGGUAN" -> listOf(12.0, 14.2, 11.5, 15.1, 16.8, 18.2, 18.5)
                    "BULANAN" -> listOf(10.5, 12.0, 13.5, 15.0, 16.5, 17.8, 18.5)
                    else -> listOf(4.2, 8.5, 14.5, 18.5)
                }
            }
        }
    }

    val labels = remember(metricType, filterType) {
        when (filterType) {
            "MINGGUAN" -> listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
            "BULANAN" -> listOf("Nov", "Des", "Jan", "Feb", "Mar", "Apr", "Mei")
            else -> listOf("2023", "2024", "2025", "2026")
        }
    }

    var selectedIndex by remember { mutableStateOf(-1) }
    val primaryColor = MaterialTheme.colorScheme.primary
    val accentColor = MaterialTheme.colorScheme.tertiary
    val textColor = MaterialTheme.colorScheme.onBackground

    val animateProgress = remember { Animatable(0f) }
    LaunchedEffect(metricType, filterType) {
        selectedIndex = -1
        animateProgress.snapTo(0f)
        animateProgress.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)
        )
    }

    Column(modifier = modifier) {
        // Dynamic stats info banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val displayIndex = if (selectedIndex >= 0) selectedIndex else rawPrices.lastIndex
            val value = rawPrices[displayIndex]
            val labelStr = labels[displayIndex]

            Column {
                Text(
                    text = "Nilai Tertunjuk ($labelStr)",
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor.copy(alpha = 0.6f)
                )
                Text(
                    text = if (metricType == "ASET") {
                        "Rp ${value.toInt()} Juta"
                    } else {
                        "Rp ${value} Milyar"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SuccessColor.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "+${if (metricType == "ASET") "30.8%" else "14.5%"} YoY",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = SuccessColor
                )
            }
        }

        // Custom drawn Canvas Curve
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .pointerInput(rawPrices) {
                    detectTapGestures { offset ->
                        val widthSlice = size.width / (rawPrices.size - 1).toFloat()
                        val clickedIndex = (offset.x / widthSlice + 0.5f).toInt()
                        if (clickedIndex in rawPrices.indices) {
                            selectedIndex = clickedIndex
                        }
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val paddingL = 20f
                val paddingB = 40f
                
                val usableW = width - paddingL * 2
                val usableH = height - paddingB

                val maxVal = (rawPrices.maxOrNull() ?: 1.0) * 1.1f
                val minVal = (rawPrices.minOrNull() ?: 0.0) * 0.9f
                val valRange = maxVal - minVal

                // Grid background lines
                val gridLines = 4
                for (i in 0..gridLines) {
                    val y = usableH - (usableH / gridLines) * i
                    drawLine(
                        color = textColor.copy(alpha = 0.08f),
                        start = Offset(paddingL, y),
                        end = Offset(width - paddingL, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Points path mapping
                val points = rawPrices.mapIndexed { idx, price ->
                    val x = paddingL + (usableW / (rawPrices.size - 1)) * idx
                    val ratio = (price - minVal) / valRange
                    val y = usableH - (usableH * ratio * animateProgress.value).toFloat()
                    Offset(x, y)
                }

                // Draw linear gradient under the curve
                val gradientPath = Path().apply {
                    moveTo(points.first().x, usableH)
                    points.forEach { lineTo(it.x, it.y) }
                    lineTo(points.last().x, usableH)
                    close()
                }

                drawPath(
                    path = gradientPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(primaryColor.copy(alpha = 0.35f), Color.Transparent),
                        startY = 0f,
                        endY = usableH
                    )
                )

                // Draw Main Curve Line
                val strokePath = Path().apply {
                    moveTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        lineTo(points[i].x, points[i].y)
                    }
                }

                drawPath(
                    path = strokePath,
                    color = primaryColor,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Highlight Selected Node or standard pulses
                points.forEachIndexed { i, offset ->
                    val isSelected = (i == selectedIndex) || (selectedIndex == -1 && i == points.lastIndex)
                    val r = if (isSelected) 8.dp.toPx() else 4.dp.toPx()
                    val color = if (isSelected) accentColor else primaryColor

                    drawCircle(
                        color = color,
                        radius = r,
                        center = offset
                    )

                    if (isSelected) {
                        drawCircle(
                            color = color.copy(alpha = 0.3f),
                            radius = r + 5.dp.toPx(),
                            center = offset
                        )

                        // Draw virtual dotted alignment line
                        drawLine(
                            color = textColor.copy(alpha = 0.25f),
                            start = Offset(offset.x, 0f),
                            end = Offset(offset.x, usableH),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )
                    }
                }
            }
        }

        // Horizontal bottom labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEachIndexed { i, l ->
                Text(
                    text = l,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    fontWeight = if (i == selectedIndex) FontWeight.Bold else FontWeight.Normal,
                    color = if (i == selectedIndex) accentColor else textColor.copy(alpha = 0.5f)
                )
            }
        }
    }
}

// 3. Simulated Mobile Interactive Indonesia Branch Hotspot Node Map
@Composable
fun InteractiveBranchMap(
    selectedRegion: String,
    onRegionSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val regionsList = listOf(
        Pair("SUMATERA", Offset(50f, 60f)),
        Pair("DKI JAKARTA", Offset(140f, 145f)),
        Pair("JAWA BARAT", Offset(185f, 155f)),
        Pair("JAWA TIMUR", Offset(280f, 160f)),
        Pair("SULAWESI", Offset(350f, 90f))
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Peta Sebaran Outlet",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Tap daerah pada peta",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Vector map canvas mockup representation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Draw general simulated Indonesian archipelago shape lanes
                val archipelagoColor = Color(0xFFE2DDD5)
                val oceanColor = Color(0xFFEFF4F9)

                // Background ocean
                drawRect(color = oceanColor, size = size)

                // Mock Sumatra lane
                drawPath(
                    path = Path().apply {
                        moveTo(20f, 30f)
                        lineTo(110f, 90f)
                        lineTo(130f, 110f)
                        lineTo(90f, 130f)
                        lineTo(10f, 50f)
                        close()
                    },
                    color = archipelagoColor
                )

                // Mock Java lane
                drawPath(
                    path = Path().apply {
                        moveTo(100f, 140f)
                        lineTo(320f, 160f)
                        lineTo(315f, 175f)
                        lineTo(95f, 155f)
                        close()
                    },
                    color = archipelagoColor
                )

                // Mock Kalimantan lane
                drawPath(
                    path = Path().apply {
                        moveTo(170f, 40f)
                        lineTo(250f, 35f)
                        lineTo(270f, 95f)
                        lineTo(185f, 100f)
                        close()
                    },
                    color = archipelagoColor
                )

                // Mock Sulawesi lane
                drawPath(
                    path = Path().apply {
                        moveTo(290f, 60f)
                        lineTo(340f, 55f)
                        lineTo(370f, 110f)
                        lineTo(320f, 120f)
                        close()
                    },
                    color = archipelagoColor
                )
            }

            // Overlay click hotspots
            regionsList.forEach { (name, point) ->
                val active = (selectedRegion == name)

                Box(
                    modifier = Modifier
                        .offset(dpAtPointMultiplier(point.x), dpAtPointMultiplier(point.y))
                        .clickable { onRegionSelect(name) }
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                            else Color.Transparent
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = name,
                            tint = if (active) MaterialTheme.colorScheme.primary else WarningColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = name.substringBefore(" "),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Selector Row Button Filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val filters = listOf("SEMUA", "DKI JAKARTA", "JAWA BARAT", "JAWA TIMUR", "SUMATERA")
            filters.forEach { f ->
                val active = (selectedRegion == f)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (active) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable { onRegionSelect(f) }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (f.contains(" ")) f.substringAfter(" ") else f,
                        color = if (active) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// Helper multiplier to align responsive maps slightly
private fun dpAtPointMultiplier(coord: Float): Dp {
    return coord.coerceAtLeast(10f).dp
}

// 4. Skeleton Loading placeholder mockup
@Composable
fun SkeletonPlaceholder(
    width: Dp,
    height: Dp,
    cornerRadius: Dp = 8.dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = modifier
            .size(width, height)
            .alpha(alphaAnim)
            .background(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f),
                shape = RoundedCornerShape(cornerRadius)
            )
    )
}
