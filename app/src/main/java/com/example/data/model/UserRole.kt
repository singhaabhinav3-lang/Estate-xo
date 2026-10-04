package com.example.data.model

enum class UserRole(val displayName: String, val badge: String, val description: String) {
    BUYER("Buyer", "Elite Buyer", "Discover, tour, and acquire global luxury properties"),
    AGENT("Real-Estate Agent", "Licensed Partner", "List properties, manage leads, and close high-value transactions"),
    OWNER("Owner / Developer", "Property Asset Owner", "List private estates, multi-unit developments, and track valuation"),
    INVESTOR("Global Investor", "Portfolio Capitalist", "Analyze cap rates, rental yield models, and cash flow projections")
}

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: UserRole,
    val avatarUrl: String = "",
    val agency: String = "Sotheby's International Realty",
    val licenseNumber: String = "DXB-ORN-89241",
    val verifiedIdentity: Boolean = true,
    val memberSince: String = "2023",
    val totalTransactionsUsd: Long = 48500000L
)
