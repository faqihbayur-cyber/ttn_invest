package com.example.repository

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

object InvestorRepository {

    // Current logged in investor state
    private val _currentInvestor = MutableStateFlow<Investor>(Investor())
    val currentInvestor: StateFlow<Investor> = _currentInvestor.asStateFlow()

    // Announcements state
    private val _announcements = MutableStateFlow<List<Announcement>>(initAnnouncements())
    val announcements: StateFlow<List<Announcement>> = _announcements.asStateFlow()

    // Dividends state
    private val _dividends = MutableStateFlow<List<Dividend>>(initDividends())
    val dividends: StateFlow<List<Dividend>> = _dividends.asStateFlow()

    // Financial Reports
    val financials: List<RevenueStatement> = initFinancials()

    // Outlets state
    private val _outlets = MutableStateFlow<List<Outlet>>(initOutlets())
    val outlets: StateFlow<List<Outlet>> = _outlets.asStateFlow()

    // Branches state
    val branches: List<Branch> = initBranches()

    // Notifications state
    private val _notifications = MutableStateFlow<List<SmartNotification>>(initNotifications())
    val notifications: StateFlow<List<SmartNotification>> = _notifications.asStateFlow()

    // Support Tickets state
    private val _tickets = MutableStateFlow<List<SupportTicket>>(initTickets())
    val tickets: StateFlow<List<SupportTicket>> = _tickets.asStateFlow()

    // Transactions
    val transactions: List<TransactionActivity> = initTransactions()

    // Documents
    val documents: List<DocumentInfo> = initDocuments()

    // Mock Security State variables
    val isSslPinned = MutableStateFlow(true)
    val jwtToken = MutableStateFlow("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.sfia_hadiningrat_ttn")
    val deviceSessionId = MutableStateFlow("SES-NUSAN-9482X")

    fun updateBiometric(enabled: Boolean) {
        _currentInvestor.value = _currentInvestor.value.copy(biometricEnabled = enabled)
    }

    fun toggleLikeAnnouncement(id: String) {
        _announcements.value = _announcements.value.map {
            if (it.id == id) {
                val nextLike = !it.isLiked
                val countChange = if (nextLike) 1 else -1
                it.copy(isLiked = nextLike, likesCount = it.likesCount + countChange)
            } else it
        }
    }

    fun toggleBookmarkAnnouncement(id: String) {
        _announcements.value = _announcements.value.map {
            if (it.id == id) {
                it.copy(isBookmarked = !it.isBookmarked, bookmarkCount = it.bookmarkCount + (if (it.isBookmarked) -1 else 1))
            } else it
        }
    }

    fun markAllNotificationsAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun markNotificationAsRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun addSupportTicket(subject: String, department: String, priority: String) {
        val newId = "TCK-${(1000..9900).random()}"
        val newTicket = SupportTicket(
            id = newId,
            subject = subject,
            department = department,
            status = TicketStatus.OPEN,
            priority = priority,
            dateCreated = "Hari ini, 10:00",
            lastUpdate = "Hari ini, 10:00",
            messages = listOf(
                ChatMessage(
                    senderId = "INVESTOR",
                    senderName = _currentInvestor.value.name,
                    text = "Saya ingin menanyakan detail laporan audit triwulan II daerah Jawa Barat.",
                    timestamp = "10:00"
                )
            )
        )
        _tickets.value = listOf(newTicket) + _tickets.value

        // Simulate automatic customer agent reply in 3 seconds
        _notifications.value = listOf(
            SmartNotification(
                id = "NOTIF-${(10000..99999).random()}",
                title = "Tiket Baru Dibuat",
                content = "Tiket bantuan $newId berhasil dibuat. Customer Relation kami akan segera membalas.",
                category = NotificationCategory.LAPORAN_BARU,
                timeLabel = "Baru saja"
            )
        ) + _notifications.value
    }

    fun submitChatMessage(ticketId: String, text: String) {
        _tickets.value = _tickets.value.map { ticket ->
            if (ticket.id == ticketId) {
                val updatedMessages = ticket.messages + ChatMessage(
                    senderId = "INVESTOR",
                    senderName = _currentInvestor.value.name,
                    text = text,
                    timestamp = "10:15"
                )
                ticket.copy(messages = updatedMessages, lastUpdate = "Hari ini, 10:15")
            } else ticket
        }
    }

    // Dynamic database fillers
    private fun initAnnouncements() = listOf(
        Announcement(
            id = "NEWS-001",
            title = "Ekspansi Masif: Teh Tarik Nusantara Resmikan 50 Outlet Baru di Sumatra",
            content = "Teh Tarik Nusantara kembali memantapkan posisinya sebagai pionir minuman tradisional modern dengan membuka 50 outlet baru di kota Medan, Palembang, dan Pekanbaru. Langkah strategis ini diharapkan mampu meningkatkan performa profit kuartal ini sebesar 12%. Dukungan penuh dari pasokan teh asli tanah jaya memberikan kesinambungan mutu rasa teh tarik premium.",
            category = NewsCategory.EKSPANSI,
            likesCount = 142,
            bookmarkCount = 45,
            publishDate = "05 Juni 2026",
            imageUrl = ""
        ),
        Announcement(
            id = "NEWS-002",
            title = "Pembagian Dividen Interim Fase II Sebesar Rp 150/Lembar Disetujui",
            content = "Rapat Direksi menyepakati rasio dividen interim Fase II yang siap dibagikan kepada para investor priority dan retail pada tanggal 15 September 2026. Transparansi keuangan tetap menjadi pegangan erat bagi Teh Tarik Nusantara dalam perjalanan berkembang bersama ratusan investor nasional.",
            category = NewsCategory.DIVIDEN,
            likesCount = 289,
            bookmarkCount = 98,
            publishDate = "02 Juni 2026",
            imageUrl = ""
        ),
        Announcement(
            id = "NEWS-003",
            title = "Kerjasama Strategis dengan Perkebunan Teh Organik Jawa Barat",
            content = "Guna menjaga konsistensi cita rasa premium yang sangat digemari penikmat Teh Tarik, manajemen resmi menandatangani kemitraan pasokan eksklusif teh berkualitas tinggi organik di Sukabumi. Hal ini mengefisiensi ongkos produksi (HPP) hingga 8.5% untuk margin keuntungan outlet yang lebih tinggi.",
            category = NewsCategory.KERJASAMA,
            likesCount = 88,
            bookmarkCount = 19,
            publishDate = "28 Mei 2026",
            imageUrl = ""
        ),
        Announcement(
            id = "NEWS-004",
            title = "Grand Launching Flagship Restaurant Kuningan - Jakarta Selatan",
            content = "Mengusung konsep glassmorphic premium lounge, dine-in Teh Tarik Nusantara Flagship kini resmi beroperasi. Melayani segmen pasar profesional perkantoran, outlet ini dilengkapi coworking space mini dan menyediakan menu teh tarik nitrogen.",
            category = NewsCategory.OUTLET_BARU,
            likesCount = 175,
            bookmarkCount = 54,
            publishDate = "15 Mei 2026",
            imageUrl = ""
        ),
        Announcement(
            id = "NEWS-005",
            title = "Konferensi Tahunan Investor Tehtarik Nusantara (Shareholder Meet 2026)",
            content = "Mengharap kehadiran seluruh pemegang unit modal dalam agenda ramah-tamah, review performa triwulan nasional, serta sosialisasi road-map ekspansi teknologi Smart Kiosk AI yang direncanakan meluncur pada tahun depan.",
            category = NewsCategory.EVENT,
            likesCount = 104,
            bookmarkCount = 33,
            publishDate = "10 Mei 2026",
            imageUrl = ""
        )
    )

    private fun initDividends() = listOf(
        Dividend("DIV-01", "15 Des 2025", 25000000.0, 10.0, "PAID", 2500000.0, 22500000.0),
        Dividend("DIV-02", "30 Mar 2026", 31250000.0, 12.5, "PAID", 3125000.0, 28125000.0),
        Dividend("DIV-03", "15 Sep 2026", 45000000.0, 18.0, "SCHEDULED", 4500000.0, 40500000.0)
    )

    private fun initFinancials() = listOf(
        RevenueStatement("Triwulan I 2026", 42100000000.0, 25260000000.0, 16840000000.0, 8900000000.0, 6200000000.0, 95000000000.0, 32000000000.0, 63000000000.0),
        RevenueStatement("Triwulan IV 2025", 38500000000.0, 23870000000.0, 14630000000.0, 7750000000.0, 5150000000.0, 88000000000.0, 30000000000.0, 58000000000.0),
        RevenueStatement("Triwulan III 2025", 35200000000.0, 21824000000.0, 13376000000.0, 7100000000.0, 4800000000.0, 82000000000.0, 28000000000.0, 54000000000.0),
        RevenueStatement("Triwulan II 2025", 31000000000.0, 19530000000.0, 11470000000.0, 6200000000.0, 3900000000.0, 75000000000.0, 25000000000.0, 50000000000.0)
    )

    private fun initOutlets() = listOf(
        Outlet("OUT-01", "Outlet Flagship Kemang", "Jakarta Selatan", "DKI Jakarta", OutletStatus.ACTIVE, 18500000.0, 450000000.0, "12 Des 2022", "", true),
        Outlet("OUT-02", "Outlet Dago Hills", "Bandung", "Jawa Barat", OutletStatus.ACTIVE, 14200000.0, 380000000.0, "24 Mar 2023", "", true),
        Outlet("OUT-03", "Outlet Tunjungan Plaza", "Surabaya", "Jawa Timur", OutletStatus.ACTIVE, 22000000.0, 510000000.0, "01 Feb 2024", "", true),
        Outlet("OUT-04", "Outlet Sudirman Walk", "Pekanbaru", "Riau", OutletStatus.ACTIVE, 9800000.0, 260000000.0, "15 Mei 2024", ""),
        Outlet("OUT-05", "Outlet Nipah Mall", "Makassar", "Sulawesi Selatan", OutletStatus.ACTIVE, 11500000.0, 310000000.0, "19 Nov 2024", ""),
        Outlet("OUT-06", "Outlet Diponegoro Premium", "Semarang", "Jawa Tengah", OutletStatus.EXPANDING, 12800000.0, 340000000.0, "10 Jan 2025", ""),
        Outlet("OUT-07", "Outlet Gajah Mada", "Medan", "Sumatera Utara", OutletStatus.ACTIVE, 16300000.0, 410000000.0, "05 Apr 2025", "", true),
        Outlet("OUT-08", "Outlet Smart Kiosk Ubud", "Gianyar", "Bali", OutletStatus.PLANNED, 0.0, 0.0, "Rencana Des 2026", "")
    )

    private fun initBranches() = listOf(
        Branch("BR-01", "Regional DKI Jakarta", "Budi Santoso", "Kopitiam Sudirman", 240, 225, 14.5),
        Branch("BR-02", "Regional Jawa Barat", "Asep Sunandar", "Dago Hub Office", 185, 170, 11.2),
        Branch("BR-03", "Regional Jawa Timur", "Heri Cahyono", "Surabaya Creative Room", 150, 145, 18.9),
        Branch("BR-04", "Regional Sumatera Utara", "Roni Siahaan", "Medan Central", 98, 92, 9.4),
        Branch("BR-05", "Regional Sulawesi Selatan", "Andi Yusuf", "Makassar Base", 65, 60, 12.1)
    )

    private fun initNotifications() = listOf(
        SmartNotification(
            "NTF-01",
            "Dividen Interim Cair!",
            "Halo Sofia, dividen triwulan Anda sebesar Rp 31.250.000 sudah disetorkan langsung ke rekening bank terdaftar.",
            NotificationCategory.DIVIDEN_CAIR,
            false,
            "1 hari lalu"
        ),
        SmartNotification(
            "NTF-02",
            "Suhu Penjualan Naik (+15.4%)",
            "Laporan closing harian menunjukkan total penjualan nasional menembus Rp 680 juta dalam 24 jam terakhir.",
            NotificationCategory.REVENUE_UP,
            false,
            "2 hari lalu"
        ),
        SmartNotification(
            "NTF-03",
            "Grand Opening Outlet Ubud, Bali",
            "Perluasan jangkauan ke kawasan pariwisata internasional. Dapatkan update margin dan volume penjualan outlet bali.",
            NotificationCategory.OUTLET_BARU,
            true,
            "3 hari lalu"
        ),
        SmartNotification(
            "NTF-04",
            "Laporan Keuangan Audit Q1 Rilis",
            "Buku laporan keuangan yang telah diaudit oleh Akuntan Publik KAP Hananta kini tersedia untuk di-download.",
            NotificationCategory.LAPORAN_BARU,
            true,
            "1 minggu lalu"
        )
    )

    private fun initTickets() = listOf(
        SupportTicket(
            id = "TCK-4819",
            subject = "Pertanyaan Mekanisme e-Sertifikat Saham",
            department = "Legal & Compliance",
            status = TicketStatus.RESOLVED,
            priority = "MEDIUM",
            dateCreated = "25 Mei 2026",
            lastUpdate = "26 Mei 2026",
            messages = listOf(
                ChatMessage("INVESTOR", "Sofia Hadiningrat", "Apakah sertifikat fisik akan dikirim ke alamat rumah?", "10:00"),
                ChatMessage("SUPPORT", "Customer Care Elite", "Selamat pagi Ibu Sofia, e-sertifikat terenkripsi sudah tersedia resmi di Tab Dokumen. Cetakan fisik dapat dikirimkan gratis jika status investasi mencapai Diamond level.", "11:30")
            )
        ),
        SupportTicket(
            id = "TCK-5102",
            subject = "Rekonsiliasi Pajak Dividen Triwulan I",
            department = "Finance & Tax",
            status = TicketStatus.IN_PROGRESS,
            priority = "HIGH",
            dateCreated = "01 Juni 2026",
            lastUpdate = "02 Juni 2026",
            messages = listOf(
                ChatMessage("INVESTOR", "Sofia Hadiningrat", "Potongan pajak PPh pasal 23 mengapa belum tercatat di FP01 milik kantor?", "15:00"),
                ChatMessage("SUPPORT", "Tax Specialist TTN", "Kami sedang memproses sinkronisasi e-bupot DJP Online. Mohon tunggu maksimal 3 hari kerja buku bupot diunggah.", "09:00")
            )
        )
    )

    private fun initTransactions() = listOf(
        TransactionActivity("TRX-940201", "INVESTMENT", 250000000.0, "15 Apr 2025", "SUCCESS"),
        TransactionActivity("TRX-948201", "DIVIDEND", 22500000.0, "15 Des 2025", "SUCCESS"),
        TransactionActivity("TRX-955201", "DIVIDEND", 28125000.0, "30 Mar 2026", "SUCCESS")
    )

    private fun initDocuments() = listOf(
        DocumentInfo(
            "DOC-01",
            "Prospektus Saham Teh Tarik Nusantara 2026",
            DocumentCategory.PROSPECTUS,
            "12.4 MB",
            "10 Jan 2026",
            "https://tehtariknusantara.co.id/docs/prospektus2026.pdf",
            "Prospektus penawaran umum terbatas, analisa kelayakan usaha, proyeksi ROI jangka 5 tahun."
        ),
        DocumentInfo(
            "DOC-02",
            "Laporan Tahunan Terkonsolidasi (Annual Report FY2025)",
            DocumentCategory.ANNUAL,
            "24.8 MB",
            "15 Mar 2026",
            "https://tehtariknusantara.co.id/docs/annual_report_2025.pdf",
            "Laporan evaluasi menyeluruh dari dewan komisaris, kinerja bisnis cabang, perkembangan CSR dan audit mutu."
        ),
        DocumentInfo(
            "DOC-03",
            "Laporan Keuangan Diaudit KAP Q1 2026",
            DocumentCategory.FINANCIAL,
            "4.2 MB",
            "15 Mei 2026",
            "https://tehtariknusantara.co.id/docs/financial_q1_2026.pdf",
            "Neraca keuangan, laporan laba-rugi komprehensif, laporan arus kas yang telah diaudit oleh KAP terdaftar OJK."
        ),
        DocumentInfo(
            "DOC-04",
            "Sertifikat Saham Terverifikasi Blockchain & DJP",
            DocumentCategory.LEGALITY,
            "1.8 MB",
            "16 Apr 2025",
            "https://tehtariknusantara.co.id/docs/cert_blockchain_sh.pdf",
            "Dokumen kepemilikan saham legal Ibu Sofia Hadiningrat tercatat digital dan didukung tanda tangan elektronik Balai Sertifikasi RI."
        )
    )
}
