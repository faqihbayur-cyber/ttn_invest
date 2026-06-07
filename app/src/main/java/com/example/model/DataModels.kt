package com.example.model

import java.util.Date

enum class InvestorTier {
    SILVER, GOLD, PLATINUM, DIAMOND
}

enum class OutletStatus {
    ACTIVE, PLANNED, EXPANDING, MAINTENANCE
}

enum class DocumentCategory {
    PROSPECTUS, ANNUAL, FINANCIAL, LEGALITY
}

enum class NewsCategory {
    EKSPANSI, OUTLET_BARU, KERJASAMA, DIVIDEN, EVENT
}

enum class NotificationCategory {
    DIVIDEN_CAIR, REVENUE_UP, OUTLET_BARU, LAPORAN_BARU, EVENT_INVESTOR
}

enum class TicketStatus {
    OPEN, IN_PROGRESS, RESOLVED
}

data class Investor(
    val id: String = "INV-084920",
    val name: String = "Sofia Hadiningrat",
    val email: String = "sofia.hadiningrat@nusantara.com",
    val phone: String = "+62 811-2345-6789",
    val avatarUrl: String = "",
    val tier: InvestorTier = InvestorTier.PLATINUM,
    val totalInvestment: Double = 250000000.0,
    val currentInvestmentValue: Double = 327000000.0,
    val totalProfit: Double = 77000000.0,
    val roi: Double = 30.8,
    val shareUnits: Long = 250000,
    val ownershipPercentage: Double = 1.25,
    val lastLoginDevice: String = "iPhone 15 Pro Max",
    val biometricEnabled: Boolean = true,
    val deviceVerified: Boolean = true
)

data class Investment(
    val id: String,
    val investorId: String,
    val amount: Double,
    val currentValue: Double,
    val dateInvested: Date,
    val roi: Double,
    val sharesCount: Long,
    val ownershipPercentage: Double
)

data class Dividend(
    val id: String,
    val date: String,
    val nominal: Double,
    val yieldPercentage: Double,
    val status: String, // "PAID" or "SCHEDULED"
    val taxDeduction: Double,
    val netReceived: Double
)

data class RevenueStatement(
    val period: String, // Month-Year
    val revenue: Double,
    val cost: Double,
    val profit: Double,
    val ebitda: Double,
    val cashFlow: Double,
    val asset: Double,
    val liability: Double,
    val equity: Double
)

data class Outlet(
    val id: String,
    val name: String,
    val city: String,
    val province: String,
    val status: OutletStatus,
    val dailyRevenue: Double,
    val monthlyRevenue: Double,
    val activeSince: String,
    val imageUrl: String,
    val isStarOutlet: Boolean = false
)

data class Branch(
    val id: String,
    val name: String,
    val manager: String,
    val locationName: String,
    val outletsCount: Int,
    val activeOutletsCount: Int,
    val salesGrowthPercentage: Double
)

data class DocumentInfo(
    val id: String,
    val name: String,
    val category: DocumentCategory,
    val size: String,
    val datePublished: String,
    val fileUrl: String,
    val description: String
)

data class Announcement(
    val id: String,
    val title: String,
    val content: String,
    val category: NewsCategory,
    val likesCount: Int,
    val bookmarkCount: Int,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val publishDate: String,
    val imageUrl: String
)

data class SmartNotification(
    val id: String,
    val title: String,
    val content: String,
    val category: NotificationCategory,
    val isRead: Boolean = false,
    val timeLabel: String
)

data class ChatMessage(
    val senderId: String, // "INVESTOR" or "SUPPORT"
    val senderName: String,
    val text: String,
    val timestamp: String
)

data class SupportTicket(
    val id: String,
    val subject: String,
    val department: String,
    val status: TicketStatus,
    val priority: String, // "LOW", "MEDIUM", "HIGH"
    val dateCreated: String,
    val lastUpdate: String,
    val messages: List<ChatMessage> = emptyList()
)

data class TransactionActivity(
    val id: String,
    val type: String, // "INVESTMENT", "DIVIDEND", "BONUS"
    val amount: Double,
    val date: String,
    val status: String // "SUCCESS", "PENDING", "FAILED"
)

data class ActivityLog(
    val id: String,
    val action: String,
    val ipAddress: String,
    val deviceName: String,
    val timestamp: String
)
