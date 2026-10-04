package com.example.data.model

enum class ListingType(val label: String) {
    BUY("For Sale"),
    RENT("For Rent")
}

enum class PropertyType(val label: String) {
    PENTHOUSE("Penthouse"),
    VILLA("Luxury Villa"),
    APARTMENT("High-Rise Apartment"),
    ESTATE("Waterfront Estate"),
    TOWNHOUSE("Modern Townhouse"),
    COMMERCIAL("Commercial / Mixed-Use")
}

data class Property(
    val id: String,
    val title: String,
    val tagline: String,
    val priceUsd: Double,
    val listingType: ListingType = ListingType.BUY,
    val propertyType: PropertyType,
    val bedrooms: Int,
    val bathrooms: Int,
    val areaSqFt: Int,
    val address: String,
    val city: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val highlights: List<String>,
    val amenities: List<String>,
    val imageResId: Int? = null,
    val imageUrls: List<String> = emptyList(),
    val virtualTourRooms: List<VirtualTourRoom> = emptyList(),
    val trustScore: Int = 96, // 0 - 100
    val isVerified: Boolean = true,
    val fraudRiskScore: Double = 0.02, // 0.0 (clean) to 1.0 (fraudulent)
    val fraudStatusTag: String = "Passed AI Verification (0 Duplicates)",
    val agentId: String,
    val agentName: String,
    val agentCompany: String,
    val agentPhone: String,
    val agentRating: Double = 4.95,
    val passport: PropertyPassport,
    val investmentMetrics: InvestmentMetrics,
    val nearbyAmenities: List<NearbyAmenity> = emptyList(),
    val documents: List<PropertyDocument> = emptyList(),
    val isFeatured: Boolean = false,
    val isNew: Boolean = false,
    val isPriceDrop: Boolean = false,
    val aiEstimatedValueUsd: Double = priceUsd * 1.04,
    val valuationConfidence: String = "High (94% - 18 local comparables)",
    val viewsCount: Int = 1420,
    val savesCount: Int = 89
) {
    val areaSqM: Int
        get() = (areaSqFt * 0.092903).toInt()

    val pricePerSqFtUsd: Double
        get() = if (areaSqFt > 0) priceUsd / areaSqFt else 0.0
}

data class ViewingAppointment(
    val id: String,
    val propertyId: String,
    val propertyTitle: String,
    val propertyCity: String,
    val buyerName: String,
    val buyerPhone: String,
    val agentName: String,
    val date: String,
    val timeSlot: String,
    val isVirtualTour: Boolean = false,
    val notes: String = "",
    val status: ViewingStatus = ViewingStatus.CONFIRMED
)

enum class ViewingStatus(val label: String) {
    PENDING("Awaiting Confirmation"),
    CONFIRMED("Confirmed"),
    COMPLETED("Completed"),
    RESCHEDULED("Rescheduled"),
    CANCELLED("Cancelled")
}

data class ChatMessage(
    val id: String,
    val propertyId: String,
    val senderName: String,
    val isFromAgent: Boolean,
    val originalText: String,
    val originalLanguage: String = "English",
    val translatedText: String? = null,
    val timestamp: String = "Just now"
)

data class Lead(
    val id: String,
    val propertyId: String,
    val propertyTitle: String,
    val clientName: String,
    val clientEmail: String,
    val clientPhone: String,
    val budgetUsd: Double,
    val stage: LeadStage,
    val lastContactDate: String,
    val notes: String
)

enum class LeadStage(val label: String) {
    NEW_INQUIRY("New Inquiry"),
    CONTACTED("Contacted"),
    VIEWING_SET("Viewing Scheduled"),
    OFFER_RECEIVED("Offer Received"),
    UNDER_CONTRACT("Under Contract"),
    CLOSED("Closed / Won")
}

data class RenovationStyle(
    val id: String,
    val name: String,
    val subtitle: String,
    val description: String,
    val aestheticTokens: List<String>,
    val estimatedRenovationCostUsd: Double,
    val projectedValueIncreaseUsd: Double
)
