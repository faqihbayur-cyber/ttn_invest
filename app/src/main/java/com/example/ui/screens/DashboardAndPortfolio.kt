package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.LightAccent
import com.example.ui.theme.SuccessColor
import com.example.ui.theme.WarningColor
import com.example.viewmodel.InvestorViewModel

@Composable
fun DashboardScreen(
    viewModel: InvestorViewModel,
    modifier: Modifier = Modifier
) {
    val investor by viewModel.currentInvestor.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isSkeletonLoading by viewModel.isLoadingDashboard.collectAsState()
    val notificationsList by viewModel.notifications.collectAsState()

    val selectedMetric by viewModel.selectedChartMetric.collectAsState()
    val selectedTimeline by viewModel.selectedTimelineFilter.collectAsState()

    var showNotifDropDown by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // 1. Premium Global App Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = investor.name.split(" ").map { it.take(1) }.joinToString(""),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Selamat Datang,",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = investor.name,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // Investor Tier Badge representation
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        when (investor.tier) {
                                            InvestorTier.DIAMOND -> Color(0xFFE5D5FF)
                                            InvestorTier.PLATINUM -> Color(0xFFDFEFFF)
                                            InvestorTier.GOLD -> Color(0xFFFFF6DF)
                                            else -> Color(0xFFF0F0F0)
                                        }
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = investor.tier.name,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (investor.tier) {
                                        InvestorTier.DIAMOND -> Color(0xFF6200EE)
                                        InvestorTier.PLATINUM -> Color(0xFF0066EE)
                                        InvestorTier.GOLD -> Color(0xFFEE8800)
                                        else -> Color(0xFF666666)
                                    }
                                )
                            }
                        }
                    }
                }

                // Notification Bell Icon button with drop down counter
                Box {
                    val unreadCount = notificationsList.count { !it.isRead }
                    IconButton(onClick = { showNotifDropDown = !showNotifDropDown }) {
                        Box {
                            Icon(Icons.Outlined.Notifications, contentDescription = "Sinyal")
                            if (unreadCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(Color.Red),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = unreadCount.toString(),
                                        fontSize = 8.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick pull down simulator bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.refreshDashboard() }
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isRefreshing) "Menyelaraskan data Firebase..." else "Pull to Refresh (Simulasi Tap)",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // 2. Primary Scroll Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                
                // Item A: Premium Asset Investment Card
                item {
                    PremiumCard(
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        borderColor = Color.White.copy(alpha = 0.35f)
                    ) {
                        Text(
                            text = "PORTFOLIO INVESTASI SAYA",
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 10.sp,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Nilai Investasi Berjalan",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = formatRupiah(investor.currentInvestmentValue),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        tint = SuccessColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "+${investor.roi}%",
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessColor,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Divider(
                            modifier = Modifier.padding(vertical = 14.dp),
                            color = Color.White.copy(alpha = 0.15f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Modal Awal",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = formatRupiah(investor.totalInvestment),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Keuntungan (Gain)",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "+" + formatRupiah(investor.totalProfit),
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessColor,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                // Item B: Chart section
                item {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Matriks Pertumbuhan Bisnis",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Selectable Metrics Filter Rows
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(listOf("ASET", "REVENUE", "PROFIT", "SALES")) { m ->
                                val active = (selectedMetric == m)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (active) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .clickable { viewModel.selectedChartMetric.value = m }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = m,
                                        color = if (active) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (isSkeletonLoading) {
                            SkeletonPlaceholder(width = 300.dp, height = 180.dp)
                        } else {
                            InteractiveDashboardChart(
                                metricType = selectedMetric,
                                filterType = selectedTimeline
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Selected Timeline buttons
                        Row(modifier = Modifier.fillMaxWidth()) {
                            listOf("MINGGUAN", "BULANAN", "TAHUNAN").forEach { t ->
                                val active = (selectedTimeline == t)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.selectedTimelineFilter.value = t }
                                        .padding(vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = t,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (active) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                                    )
                                }
                            }
                        }
                    }
                }

                // Item C: Metrics Dashboard Widgets Grid (2 columns)
                item {
                    Text(
                        text = "Statistik Operasional Nasional",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        WidgetCard(
                            title = "Total Outlet",
                            value = "1.240 Cabang",
                            icon = Icons.Default.Storefront,
                            desc = "34 Provinsi Aktif",
                            modifier = Modifier.weight(1f)
                        )
                        WidgetCard(
                            title = "Investor Global",
                            value = "4.150 Orang",
                            icon = Icons.Default.Groups,
                            desc = "+12% Bulan ini",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        WidgetCard(
                            title = "Revenue Q1",
                            value = "Rp 42.1 Milyar",
                            icon = Icons.Default.Paid,
                            desc = "Melebihi target 8.5%",
                            modifier = Modifier.weight(1f)
                        )
                        WidgetCard(
                            title = "Profit Bersih",
                            value = "Rp 5.2 Milyar",
                            icon = Icons.Default.MonetizationOn,
                            desc = "Bagi hasil dividen",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    PremiumCard(
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        borderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(WarningColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = WarningColor)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "Pembagian Dividen Berikutnya",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = "15 September 2026",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Estimasi Payout: Rp 45.000.000",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessColor
                                )
                            }
                        }
                    }
                }

                // Item D: National sales tracking ranking table mockup
                item {
                    Text(
                        text = "Ranking Produk Terbaik (Bulan Ini)",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        RankingRow(rank = "1", name = "Brown Sugar Teh Tarik", units = "240.230 Cup", percentage = "42%")
                        RankingRow(rank = "2", name = "Ice Cream Teh Tarik", units = "142.100 Cup", percentage = "28%")
                        RankingRow(rank = "3", name = "Nitro Cold Brew Tea", units = "89.410 Cup", percentage = "18%")
                        RankingRow(rank = "4", name = "Teh Tarik Ginger Warm", units = "52.090 Cup", percentage = "12%")
                    }
                }
            }
        }

        // Notification drop down overlay drawer
        if (showNotifDropDown) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { showNotifDropDown = false }
                    .background(Color.Black.copy(alpha = 0.4f))
            ) {
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 90.dp, end = 20.dp)
                        .width(280.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Notifikasi Pintar",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                "Read All",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { viewModel.clearAllNotifications() }
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        notificationsList.take(3).forEach { notif ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.readNotification(notif.id) }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (notif.isRead) Color.Transparent else Color.Red)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = notif.title,
                                        fontWeight = if (notif.isRead) FontWeight.Normal else FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (notif.isRead) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = notif.content,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                        lineHeight = 13.sp
                                    )
                                }
                            }
                            Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
                        }
                    }
                }
            }
        }
    }
}

// Layout padding multiplier
private fun gapValMultiplier(): Alignment.Vertical = Alignment.CenterVertically

// Helper Dashboard Widgets Card
@Composable
fun WidgetCard(
    title: String,
    value: String,
    icon: ImageVector,
    desc: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = desc,
                fontSize = 10.sp,
                color = SuccessColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Table row representation
@Composable
fun RankingRow(
    rank: String,
    name: String,
    units: String,
    percentage: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        when (rank) {
                            "1" -> Color(0xFFFFF1CC)
                            "2" -> Color(0xFFE2E2E2)
                            else -> Color(0xFFECE5DD)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = rank,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (rank) {
                        "1" -> Color(0xFFEE8800)
                        else -> Color.DarkGray
                    }
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(text = name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(text = units, fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = percentage,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
