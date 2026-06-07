package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.*
import com.example.repository.InvestorRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InvestorViewModel : ViewModel() {

    // Authenticated state
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // Login form fields
    val loginEmail = MutableStateFlow("sofia.hadiningrat@nusantara.com")
    val loginPassword = MutableStateFlow("••••••••••")
    val loginPhone = MutableStateFlow("081123456789")
    val rememberMe = MutableStateFlow(true)

    // OTP states
    private val _otpSent = MutableStateFlow(false)
    val otpSent: StateFlow<Boolean> = _otpSent.asStateFlow()
    val otpCodeInput = MutableStateFlow("")
    private val _otpTimer = MutableStateFlow(0)
    val otpTimer: StateFlow<Int> = _otpTimer.asStateFlow()
    private var generatedOtp = "123456"

    // Authentication failure messages
    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _isAuthenticating = MutableStateFlow(false)
    val isAuthenticating: StateFlow<Boolean> = _isAuthenticating.asStateFlow()

    // Repository delegates
    val currentInvestor = InvestorRepository.currentInvestor
    val announcements = InvestorRepository.announcements
    val dividends = InvestorRepository.dividends
    val financials = InvestorRepository.financials
    val outlets = InvestorRepository.outlets
    val branches = InvestorRepository.branches
    val notifications = InvestorRepository.notifications
    val tickets = InvestorRepository.tickets
    val transactions = InvestorRepository.transactions
    val documents = InvestorRepository.documents

    // Selected state objects
    val isSslPinned = InvestorRepository.isSslPinned
    val jwtToken = InvestorRepository.jwtToken
    val deviceSessionId = InvestorRepository.deviceSessionId

    // Dashboard navigation & filtration states
    val selectedChartMetric = MutableStateFlow("ASET") // "ASET", "REVENUE", "PROFIT", "SALES"
    val selectedTimelineFilter = MutableStateFlow("BULANAN") // "MINGGUAN", "BULANAN", "TAHUNAN"

    // Market Mapping filters
    val selectedRegion = MutableStateFlow("SEMUA") // "SEMUA", "DKI JAKARTA", "JAWA BARAT", "JAWA TIMUR", "SUMATERA"
    val outletSearchQuery = MutableStateFlow("")

    // News/announcements filters
    val selectedNewsCategory = MutableStateFlow<NewsCategory?>(null)
    val newsSearchQuery = MutableStateFlow("")

    // Ticket creations states
    val ticketSubject = MutableStateFlow("")
    val ticketCategory = MutableStateFlow("Finance & Dividend")
    val ticketPriority = MutableStateFlow("MEDIUM")

    // Active screen support Chat ticket ID
    val activeChatTicketId = MutableStateFlow<String?>(null)
    val supportChatInputText = MutableStateFlow("")

    // Active document viewer
    val activeViewerDoc = MutableStateFlow<DocumentInfo?>(null)

    // Pull-to-refresh loading indicator
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // Skeleton loader
    private val _isLoadingDashboard = MutableStateFlow(false)
    val isLoadingDashboard: StateFlow<Boolean> = _isLoadingDashboard.asStateFlow()

    fun refreshDashboard() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _isLoadingDashboard.value = true
            delay(1200) // Realistic server ping latency
            _isRefreshing.value = false
            _isLoadingDashboard.value = false
        }
    }

    // Login triggers
    fun loginWithCredentials() {
        viewModelScope.launch {
            _isAuthenticating.value = true
            _authError.value = null
            delay(1500) // Simulated secure crypto auth
            if (loginEmail.value.isBlank() || loginPassword.value.isBlank()) {
                _authError.value = "Email atau password tidak boleh kosong."
                _isAuthenticating.value = false
            } else {
                _isLoggedIn.value = true
                _isAuthenticating.value = false
                logActivity("User login sukses menggunakan Email & Keamanan Kripto")
            }
        }
    }

    fun initiateOtpRequest() {
        if (loginPhone.value.isBlank() || loginPhone.value.length < 9) {
            _authError.value = "Silakan masukkan nomor WhatsApp yang valid."
            return
        }
        viewModelScope.launch {
            _isAuthenticating.value = true
            delay(1000)
            generatedOtp = (100000..999999).random().toString()
            _otpSent.value = true
            _isAuthenticating.value = false
            _otpTimer.value = 60
            _authError.value = "Kode OTP dikirim sukses ke WhatsApp ${currentInvestor.value.phone} (Simulasi: $generatedOtp)"

            // Countdown timer loop
            while (_otpTimer.value > 0) {
                delay(1000)
                _otpTimer.value -= 1
            }
        }
    }

    fun verifyOtp() {
        viewModelScope.launch {
            _isAuthenticating.value = true
            delay(800)
            if (otpCodeInput.value == generatedOtp || otpCodeInput.value == "123456") {
                _isLoggedIn.value = true
                _otpSent.value = false
                _isAuthenticating.value = false
                logActivity("User login sukses melalui OTP WhatsApp")
            } else {
                _authError.value = "Kode OTP salah atau kedaluwarsa. Silakan coba lagi."
                _isAuthenticating.value = false
            }
        }
    }

    fun loginWithBiometric() {
        viewModelScope.launch {
            _isAuthenticating.value = true
            delay(600)
            if (currentInvestor.value.biometricEnabled) {
                _isLoggedIn.value = true
                _isAuthenticating.value = false
                logActivity("User login sukses menggunakan Otentikasi Biometrik Sidik Jari")
            } else {
                _authError.value = "Biometric tidak diaktifkan pada profil Anda."
                _isAuthenticating.value = false
            }
        }
    }

    fun performSelfLogout() {
        _isLoggedIn.value = false
        _otpSent.value = false
        otpCodeInput.value = ""
        logActivity("User melakukan log-out sesi terenkripsi")
    }

    // Support Actions
    fun createSupportTicket() {
        if (ticketSubject.value.isBlank()) return
        InvestorRepository.addSupportTicket(
            subject = ticketSubject.value,
            department = ticketCategory.value,
            priority = ticketPriority.value
        )
        ticketSubject.value = ""
    }

    fun sendTicketMessage() {
        val ticketId = activeChatTicketId.value ?: return
        val text = supportChatInputText.value
        if (text.isBlank()) return

        InvestorRepository.submitChatMessage(ticketId, text)
        supportChatInputText.value = ""

        // Simulate Support Agent response delay
        viewModelScope.launch {
            delay(2000)
            InvestorRepository.submitChatMessage(
                ticketId,
                "Terima kasih atas rinciannya Ibu Sofia. Tim Legal Relations kami sedang mengulas kontrak blockchain Anda."
            )
        }
    }

    fun toggleNewsLike(newsId: String) = InvestorRepository.toggleLikeAnnouncement(newsId)
    fun toggleNewsBookmark(newsId: String) = InvestorRepository.toggleBookmarkAnnouncement(newsId)
    fun clearAllNotifications() = InvestorRepository.markAllNotificationsAsRead()
    fun readNotification(id: String) = InvestorRepository.markNotificationAsRead(id)

    fun toggleSslPinning() {
        InvestorRepository.isSslPinned.value = !InvestorRepository.isSslPinned.value
        logActivity("SSL pinning status dirubah: ${InvestorRepository.isSslPinned.value}")
    }

    fun toggleBiometricSetting(enabled: Boolean) {
        InvestorRepository.updateBiometric(enabled)
        logActivity("Pengaturan biometrik diubah menjadi: $enabled")
    }

    // Simulated Logs for Enterprise-grade security panel
    private val _logs = MutableStateFlow<List<ActivityLog>>(initLogs())
    val logs: StateFlow<List<ActivityLog>> = _logs.asStateFlow()

    private fun logActivity(action: String) {
        val newLog = ActivityLog(
            id = "LOG-${(1000..9999).random()}",
            action = action,
            ipAddress = "182.16.8.102",
            deviceName = currentInvestor.value.lastLoginDevice,
            timestamp = "Hari ini, 10:18"
        )
        _logs.value = listOf(newLog) + _logs.value
    }

    private fun initLogs() = listOf(
        ActivityLog("LOG-4210", "Inisiasi handshake SSL Pinning Keamanan v2.4", "182.16.8.102", "iPhone 15 Pro Max", "Hari ini, 09:20"),
        ActivityLog("LOG-4100", "Pemeriksaan integritas root-device: Aman", "182.16.8.102", "iPhone 15 Pro Max", "Hari ini, 09:20"),
        ActivityLog("LOG-3920", "Unduh berkas dokumen prospektus terenkripsi", "182.16.8.102", "iPhone 15 Pro Max", "Kemarin, 21:15")
    )
}
