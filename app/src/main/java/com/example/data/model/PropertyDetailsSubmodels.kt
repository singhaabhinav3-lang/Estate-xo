package com.example.data.model

data class VirtualTourRoom(
    val roomId: String,
    val name: String,
    val description: String,
    val squareFootage: Int,
    val keyFeatures: List<String>,
    val panoramaRotation: Float = 0f
)

data class PropertyPassport(
    val titleDeedNumber: String,
    val landRegistryHash: String,
    val cadastreId: String,
    val energyRating: String, // e.g. "EPC A+", "LEED Platinum"
    val carbonFootprintKgPerYear: Int,
    val structuralInspectionRating: Int, // e.g. 98 / 100
    val escrowAccountId: String,
    val isDeedVerified: Boolean = true,
    val isIdentityVerified: Boolean = true,
    val isTaxCompliant: Boolean = true,
    val lastValuationDate: String = "September 2026",
    val municipalPermitStatus: String = "Fully Certified & Permitted"
)

data class InvestmentMetrics(
    val estimatedMonthlyRentUsd: Double,
    val annualPropertyTaxUsd: Double,
    val annualHoaFeesUsd: Double,
    val annualInsuranceUsd: Double,
    val annualMaintenanceBudgetUsd: Double,
    val managementFeePercent: Double = 8.0,
    val projectedAppreciationRateAnnual: Double = 6.4,
    val historicalOccupancyRate: Double = 94.5
) {
    fun calculateGrossYield(purchasePriceUsd: Double): Double {
        if (purchasePriceUsd <= 0.0) return 0.0
        val annualRent = estimatedMonthlyRentUsd * 12
        return (annualRent / purchasePriceUsd) * 100.0
    }

    fun calculateNetYield(purchasePriceUsd: Double): Double {
        if (purchasePriceUsd <= 0.0) return 0.0
        val annualRent = estimatedMonthlyRentUsd * 12
        val annualExpenses = annualPropertyTaxUsd + annualHoaFeesUsd + annualInsuranceUsd +
                annualMaintenanceBudgetUsd + (annualRent * (managementFeePercent / 100.0))
        val netIncome = annualRent - annualExpenses
        return (netIncome / purchasePriceUsd) * 100.0
    }

    fun calculateMonthlyNetCashFlow(purchasePriceUsd: Double, downPaymentPercent: Double = 30.0, interestRateAnnual: Double = 5.5): Double {
        val loanAmount = purchasePriceUsd * (1.0 - (downPaymentPercent / 100.0))
        val monthlyInterestRate = (interestRateAnnual / 100.0) / 12.0
        val totalMonths = 30 * 12
        val monthlyMortgage = if (loanAmount > 0) {
            val factor = Math.pow(1.0 + monthlyInterestRate, totalMonths.toDouble())
            (loanAmount * monthlyInterestRate * factor) / (factor - 1.0)
        } else 0.0

        val annualRent = estimatedMonthlyRentUsd * 12
        val annualExpenses = annualPropertyTaxUsd + annualHoaFeesUsd + annualInsuranceUsd +
                annualMaintenanceBudgetUsd + (annualRent * (managementFeePercent / 100.0))
        val monthlyOperatingExpenses = annualExpenses / 12.0

        return estimatedMonthlyRentUsd - (monthlyMortgage + monthlyOperatingExpenses)
    }
}

enum class AmenityCategory(val label: String, val iconName: String) {
    SCHOOL("Education", "School"),
    HOSPITAL("Healthcare", "LocalHospital"),
    TRANSIT("Metro & Transit", "Train"),
    DINING("Fine Dining", "Restaurant"),
    LEISURE("Leisure & Clubs", "SportsGolf"),
    AIRPORT("Int'l Airport", "Flight")
}

data class NearbyAmenity(
    val category: AmenityCategory,
    val name: String,
    val distanceKm: Double,
    val travelTimeMinutes: Int,
    val rating: Double
)

enum class DocumentStatus {
    VERIFIED,
    IN_REVIEW,
    OPTIONAL
}

data class PropertyDocument(
    val id: String,
    val title: String,
    val category: String, // e.g. "Title Deed", "Floor Plan", "HOA Bylaws", "Tax Clearance"
    val fileSize: String,
    val status: DocumentStatus,
    val summary: String,
    val lawyerReviewed: Boolean = true
)
