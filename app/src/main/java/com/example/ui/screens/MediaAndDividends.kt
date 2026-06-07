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
fun MediaAndDividendsScreen(
    viewModel: InvestorViewModel,
    modifier: Modifier = Modifier
) {
    // Media menu routing indices: NEWS, DIVIDENDS, DOCUMENTS
    var selectedSubTab by remember { mutableStateOf("NEWS") } // "NEWS", "DIVS", "DOCS"

    val announcementsList by viewModel.announcements.collectAsState()
    val dividendsList by viewModel.dividends.collectAsState()
    val documentsList = viewModel.documents

    val activeNewsCategory by viewModel.selectedNewsCategory.collectAsState()
    val activeDocViewer by viewModel.activeViewerDoc.collectAsState()

    val filteredNews = remember(announcementsList, activeNewsCategory) {
        if (activeNewsCategory == null) announcementsList
        else announcementsList.filter { it.category == activeNewsCategory }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // Sub-Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Laporan & Media",
                    fontSize = 22.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Hasil investasi dan kabar utama perseroan",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Triple Segmented Tab Controllers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        "NEWS" to "Kabar Pers",
                        "DIVS" to "Dividen",
                        "DOCS" to "Dokumen"
                    ).forEach { (key, display) ->
                        val active = (selectedSubTab == key)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (active) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .clickable { selectedSubTab = key }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = display,
                                color = if (active) Color.White else MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Centralized Body Renderers
            when (selectedSubTab) {
                "NEWS" -> {
                    // Cabinet 1: News Catalog
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Category tag horizontal slider
                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                item {
                                    val active = (activeNewsCategory == null)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (active) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surfaceVariant)
                                            .clickable { viewModel.selectedNewsCategory.value = null }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text("Semua", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (active) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                                    }
                                }

                                items(NewsCategory.values()) { cat ->
                                    val active = (activeNewsCategory == cat)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (active) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surfaceVariant)
                                            .clickable { viewModel.selectedNewsCategory.value = cat }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = cat.name.replace("_", " "),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (active) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }

                        items(filteredNews) { news ->
                            NewsCardRow(
                                news = news,
                                onLike = { viewModel.toggleNewsLike(news.id) },
                                onBookmark = { viewModel.toggleNewsBookmark(news.id) }
                            )
                        }
                    }
                }

                "DIVS" -> {
                    // Cabinet 2: Dividends tracking ledger
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            // Dividend summary widgets
                            PremiumCard(
                                backgroundColor = MaterialTheme.colorScheme.primary,
                                borderColor = Color.White.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "SUMMARY DIVIDEN DITERIMA",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.7f),
                                    letterSpacing = 1.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Akumulasi Dividen Masuk", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                                        Text(formatRupiah(56250000.0), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.White.copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("Rata-rata Yield: 11.2%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                text = "Riwayat Payout Distribusi",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        items(dividendsList) { div ->
                            DividendItemRow(div)
                        }
                    }
                }

                else -> {
                    // Cabinet 3: Prospectus Document Center vault selection list
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text(
                                "Berkas & Buku Legalitas",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        items(documentsList) { doc ->
                            DocumentListRow(
                                doc = doc,
                                onClick = { viewModel.activeViewerDoc.value = doc }
                            )
                        }
                    }
                }
            }
        }

        // Fullscreen dynamic simulated PDF Document Reader Modal overlay
        activeDocViewer?.let { doc ->
            SimulatedPdfViewerOverlay(
                doc = doc,
                onClose = { viewModel.activeViewerDoc.value = null }
            )
        }
    }
}

// 4. News Feed Card Item
@Composable
fun NewsCardRow(
    news: Announcement,
    onLike: () -> Unit,
    onBookmark: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // News Category tag badge string
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = news.category.name,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = news.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = news.content,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Diterbitkan: ${news.publishDate}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )

                // Interactions bar buttons
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Like button
                    Row(
                        modifier = Modifier.clickable { onLike() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (news.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Suka",
                            tint = if (news.isLiked) Color.Red else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = news.likesCount.toString(), fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                    }

                    // Bookmark button
                    Icon(
                        imageVector = if (news.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Simpan",
                        tint = if (news.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { onBookmark() }
                    )

                    // Share button stub representing direct Native Intent triggering
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Bagikan",
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { /* Simulate Share pop */ }
                    )
                }
            }
        }
    }
}

// 5. Dividend Item Card
@Composable
fun DividendItemRow(div: Dividend) {
    val isPaid = (div.status == "PAID")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isPaid) SuccessColor.copy(alpha = 0.15f) else WarningColor.copy(alpha = 0.15f),
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
                Column {
                    Text(text = "Rasio Dividen Kuartal", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                    Text(text = div.id, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                // Paid label representation status
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isPaid) SuccessColor.copy(alpha = 0.15f) else WarningColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isPaid) "BERHASIL DIKIRIM" else "MENUNGGU JADWAL",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPaid) SuccessColor else WarningColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Spread details Pph taxes deductions
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Nominal Kotor", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                    Text(formatRupiah(div.nominal), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Potongan PPh 10%", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                    Text(formatRupiah(div.taxDeduction), fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color.Red.copy(alpha = 0.7f))
                }
            }

            Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Jumlah Bersih Diterima (Net Payout)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                    Text(formatRupiah(div.netReceived), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SuccessColor)
                }

                Text(
                    text = "Yield: ${div.yieldPercentage}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// 6. Documents list drawer representation
@Composable
fun DocumentListRow(
    doc: DocumentInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PictureAsPdf,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = doc.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    lineHeight = 16.sp
                )
                Text(
                    text = "${doc.category.name} | Ukuran: ${doc.size} | Rilis: ${doc.datePublished}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            )
        }
    }
}

// 7. Fullscreen Interactive Simulated Native PDF Reader Overlay
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimulatedPdfViewerOverlay(
    doc: DocumentInfo,
    onClose: () -> Unit
) {
    var currentPage by remember { mutableStateOf(1) }
    val totalPages = 18
    var pdfSearchQuery by remember { mutableStateOf("") }
    var scaleZoom by remember { mutableStateOf(100) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF151312))
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // PDF Reader Top Bar Header controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1A18))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Tutup", tint = Color.White)
                }

                Column(modifier = Modifier.weight(1f).padding(horizontal = 10.dp)) {
                    Text(
                        text = doc.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White,
                        maxLines = 1
                    )
                    Text(
                        text = "Viewer PDF Bawaan Terenkripsi • Halaman $currentPage dari $totalPages",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }

                // PDF Zoom buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = { if (scaleZoom > 50) scaleZoom -= 25 }) {
                        Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    Text(text = "$scaleZoom%", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { if (scaleZoom < 200) scaleZoom += 25 }) {
                        Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // PDF Local Document Search In-file bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF25201D))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(10.dp))
                BasicTextFieldMockUp(
                    value = pdfSearchQuery,
                    onValueChange = { pdfSearchQuery = it },
                    placeholder = "Cari kata di prospektus...",
                    modifier = Modifier.weight(1f)
                )
                if (pdfSearchQuery.isNotEmpty()) {
                    Icon(
                        Icons.Default.Close, 
                        contentDescription = null, 
                        tint = Color.Gray, 
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { pdfSearchQuery = "" }
                    )
                }
            }

            // Centralized PDF White Paper container showing content text matching selected doc
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFF151312))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            1.dp,
                            Color.Gray.copy(alpha = 0.2f),
                            RoundedCornerShape(8.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        // PDF Header content representation
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "TEH TARIK NUSANTARA",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFF8B5E3C)
                            )
                            Icon(
                                Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color.LightGray)

                        Spacer(modifier = Modifier.height(10.dp))

                        // Text content corresponding with selected Doc
                        Text(
                            text = "DOKUMEN UTAMA NEGARA: ${doc.category.name}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.DarkGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = when (doc.category) {
                                DocumentCategory.PROSPECTUS -> {
                                    "Isi Prospektus Terkonsolidasi (Halaman $currentPage)\n\n" +
                                            "Berdasarkan ulasan kelayakan bisnis bersama tim penilai, Teh Tarik Nusantara menawarkan " +
                                            "dividen berbobot 10-15% per tahun dari laba bersih. Modal unit digunakan penuh untuk " +
                                            "ekspansi Smart Automation Kiosk di regional perkantoran Jabodetabek dan modernisasi " +
                                            "rantai logistik teh asli Jawa Barat.\n\n" +
                                            "Target ROI kami tercapai konsisten sebesar 30.8% selama kurun waktu 3 tahun terakhir " +
                                            "dan kami berkomitmen melanjutkan transparansi modal aman."
                                }
                                DocumentCategory.ANNUAL -> {
                                    "Konten Laporan Tahunan Perseroan (Halaman $currentPage)\n\n" +
                                            "Evaluasi Kinerja FY2025:\n" +
                                            "• Penambahan 140 outlet di Sumatra & Jawa.\n" +
                                            "• Penghematan beban logistik sebesar 8.5% karena kemitraan kebun teh organik baru.\n" +
                                            "• Pembayaran dividen tepat waktu triwulanan.\n\n" +
                                            "Kami mengucapkan terima kasih kepada Ibu Sofia Hadiningrat atas kontribusi modal unit " +
                                            "Platinum setara 250,000 unit hak suara."
                                }
                                else -> {
                                    "Berkas Legalitas Diaudit OJK (Halaman $currentPage)\n\n" +
                                            "Akta Pendirian No. 42 / Kemenkumham RI.\n" +
                                            "Sertifikat Blockchain Hak Paten Formula Rasa Teh Tarik Premium.\n" +
                                            "Semua aset fisik outlet diasuransikan terhadap risiko kebakaran dan force majeure " +
                                            "sehingga modal investor terlindung 100% secara aman."
                                }
                            },
                            fontSize = 11.sp,
                            color = Color.Black,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // PDF Page Footer
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Rahasia & Terbatas • Investor Hub",
                                fontSize = 8.sp,
                                color = Color.Gray
                            )
                            Text(
                                "Halaman $currentPage dari $totalPages",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }

            // PDF Bottom Pagination Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1A18))
                    .padding(vertical = 12.dp, horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { if (currentPage > 1) currentPage -= 1 },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25201D)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Halaman Sblm", fontSize = 11.sp, color = Color.White)
                }

                Text(
                    text = "$currentPage / $totalPages",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Button(
                    onClick = { if (currentPage < totalPages) currentPage += 1 },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25201D)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Halaman Brkt", fontSize = 11.sp, color = Color.White)
                }
            }
        }
    }
}

// Simulated Basic Text Field mock input box
@Composable
fun BasicTextFieldMockUp(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        if (value.isEmpty()) {
            Text(text = placeholder, color = Color.Gray, fontSize = 12.sp)
        }
        androidx.compose.foundation.text.BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontSize = 12.sp)
        )
    }
}
