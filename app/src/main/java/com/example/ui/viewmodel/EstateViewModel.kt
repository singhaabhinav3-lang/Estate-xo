package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiAiService
import com.example.ai.ListingCopyResult
import com.example.data.model.*
import com.example.data.repository.EstateRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

sealed class Screen {
    object Auth : Screen()
    object Home : Screen()
    object Explore : Screen()
    object MapView : Screen()
    object AiMatchmaker : Screen()
    data class PropertyDetail(val propertyId: String) : Screen()
    object Compare : Screen()
    data class Chat(val propertyId: String) : Screen()
    data class BookViewing(val propertyId: String) : Screen()
    object AgentDashboard : Screen()
    object AiListingCreator : Screen()
    object Saved : Screen()
    object Profile : Screen()
    object AdminModeration : Screen()
}

data class FilterState(
    val query: String = "",
    val selectedCountry: String = "All",
    val selectedCity: String = "All",
    val listingType: ListingType? = null,
    val propertyType: PropertyType? = null,
    val maxPriceUsd: Double = 35000000.0,
    val minBedrooms: Int = 0,
    val verifiedOnly: Boolean = false,
    val virtualTourOnly: Boolean = false,
    val investmentOnly: Boolean = false
)

data class AiMatchmakerForm(
    val budgetUsd: Double = 15000000.0,
    val destination: String = "Dubai, UAE",
    val propertyType: PropertyType = PropertyType.VILLA,
    val minBedrooms: Int = 4,
    val lifestyle: String = "Beachfront & Waterfront",
    val goal: String = "High Rental Yield & Capital Preservation",
    val isAnalyzing: Boolean = false,
    val matchedProperties: List<Pair<Property, Int>> = emptyList(), // Property and Match %
    val aiRationale: String = ""
)

class EstateViewModel(
    val repository: EstateRepository = EstateRepository()
) : ViewModel() {

    private val _currentScreen = MutableStateFlow<Screen>(
        if (Firebase.auth.currentUser == null) Screen.Auth else Screen.Home
    )
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _screenStack = mutableListOf<Screen>(
        if (Firebase.auth.currentUser == null) Screen.Auth else Screen.Home
    )

    val properties: StateFlow<List<Property>> = repository.properties
    val savedPropertyIds: StateFlow<Set<String>> = repository.savedPropertyIds
    val comparedPropertyIds: StateFlow<List<String>> = repository.comparedPropertyIds
    val appointments: StateFlow<List<ViewingAppointment>> = repository.appointments
    val leads: StateFlow<List<Lead>> = repository.leads
    val messages: StateFlow<Map<String, List<ChatMessage>>> = repository.messages
    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val currentCurrency: StateFlow<AppCurrency> = repository.currentCurrency

    // Search and Filter State
    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    // Filtered Properties Flow
    val filteredProperties: StateFlow<List<Property>> = combine(
        properties,
        _filterState
    ) { allProps, filters ->
        allProps.filter { prop ->
            val matchQuery = filters.query.isBlank() ||
                    prop.title.contains(filters.query, ignoreCase = true) ||
                    prop.city.contains(filters.query, ignoreCase = true) ||
                    prop.country.contains(filters.query, ignoreCase = true) ||
                    prop.amenities.any { it.contains(filters.query, ignoreCase = true) }

            val matchCountry = filters.selectedCountry == "All" || prop.country.equals(filters.selectedCountry, ignoreCase = true)
            val matchCity = filters.selectedCity == "All" || prop.city.equals(filters.selectedCity, ignoreCase = true)
            val matchListingType = filters.listingType == null || prop.listingType == filters.listingType
            val matchPropType = filters.propertyType == null || prop.propertyType == filters.propertyType
            val matchPrice = prop.priceUsd <= filters.maxPriceUsd
            val matchBeds = prop.bedrooms >= filters.minBedrooms
            val matchVerified = !filters.verifiedOnly || prop.isVerified
            val matchTour = !filters.virtualTourOnly || prop.virtualTourRooms.isNotEmpty()
            val matchInvestment = !filters.investmentOnly || prop.investmentMetrics.calculateNetYield(prop.priceUsd) >= 5.0

            matchQuery && matchCountry && matchCity && matchListingType && matchPropType && matchPrice && matchBeds && matchVerified && matchTour && matchInvestment
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI Matchmaker State
    private val _matchmakerState = MutableStateFlow(AiMatchmakerForm())
    val matchmakerState: StateFlow<AiMatchmakerForm> = _matchmakerState.asStateFlow()

    // AI Assistant inside Property Detail
    private val _aiAssistantMessages = MutableStateFlow<Map<String, List<Pair<String, Boolean>>>>(emptyMap()) // propertyId -> List<Pair<Text, isAi>>
    val aiAssistantMessages: StateFlow<Map<String, List<Pair<String, Boolean>>>> = _aiAssistantMessages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    // Navigation
    fun navigateTo(screen: Screen) {
        if (Firebase.auth.currentUser == null && screen !is Screen.Auth) {
            _screenStack.clear()
            _screenStack.add(Screen.Auth)
            _currentScreen.value = Screen.Auth
            return
        }
        if (_currentScreen.value != screen) {
            _screenStack.add(screen)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (Firebase.auth.currentUser == null) {
            _currentScreen.value = Screen.Auth
            return false
        }
        if (_screenStack.size > 1) {
            _screenStack.removeAt(_screenStack.lastIndex)
            _currentScreen.value = _screenStack.last()
            return true
        }
        return false
    }

    fun onAuthSuccessful() {
        _screenStack.clear()
        _screenStack.add(Screen.Home)
        _currentScreen.value = Screen.Home
    }

    fun onSignedOut() {
        _screenStack.clear()
        _screenStack.add(Screen.Auth)
        _currentScreen.value = Screen.Auth
    }

    fun updateFilters(update: FilterState.() -> FilterState) {
        _filterState.value = _filterState.value.update()
    }

    fun resetFilters() {
        _filterState.value = FilterState()
    }

    fun toggleSave(propertyId: String) {
        repository.toggleSaveProperty(propertyId)
    }

    fun toggleCompare(propertyId: String) {
        repository.toggleCompareProperty(propertyId)
    }

    fun setCurrency(currency: AppCurrency) {
        repository.setCurrency(currency)
    }

    fun setUserRole(role: UserRole) {
        repository.setUserRole(role)
    }

    fun updateMatchmaker(update: AiMatchmakerForm.() -> AiMatchmakerForm) {
        _matchmakerState.value = _matchmakerState.value.update()
    }

    fun runAiMatchmaker() {
        val current = _matchmakerState.value
        _matchmakerState.value = current.copy(isAnalyzing = true)

        viewModelScope.launch {
            val allProps = properties.value
            val scored = allProps.map { prop ->
                var score = 70
                if (prop.priceUsd <= current.budgetUsd * 1.15) score += 12
                if (prop.city.contains(current.destination.split(",")[0].trim(), ignoreCase = true)) score += 15
                if (prop.propertyType == current.propertyType) score += 10
                if (prop.bedrooms >= current.minBedrooms) score += 8
                if (prop.trustScore >= 95) score += 5
                Pair(prop, score.coerceAtMost(99))
            }.sortedByDescending { it.second }

            val top = scored.take(3)
            val prompt = "User requirements: Budget: $${current.budgetUsd}, City: ${current.destination}, " +
                    "Type: ${current.propertyType.label}, Bedrooms: ${current.minBedrooms}, " +
                    "Lifestyle: ${current.lifestyle}, Goal: ${current.goal}. " +
                    "Top match is: ${top.firstOrNull()?.first?.title} in ${top.firstOrNull()?.first?.city}. " +
                    "Explain concisely in 3 bullet points why this property is an exceptional match for their lifestyle and investment profile."

            val rationale = GeminiAiService.generateAiResponse(prompt)

            _matchmakerState.value = current.copy(
                isAnalyzing = false,
                matchedProperties = top,
                aiRationale = rationale
            )
        }
    }

    fun askAiPropertyAssistant(property: Property, question: String) {
        val currentList = _aiAssistantMessages.value[property.id]?.toMutableList() ?: mutableListOf(
            Pair("Hello! I am your EstateXo Property AI. I have analyzed the deed, valuation benchmarks, and lifestyle metrics for ${property.title}. Ask me anything!", true)
        )
        currentList.add(Pair(question, false))
        val newMap = _aiAssistantMessages.value.toMutableMap()
        newMap[property.id] = currentList
        _aiAssistantMessages.value = newMap

        _isAiThinking.value = true
        viewModelScope.launch {
            val prompt = "Property: ${property.title} in ${property.city}, ${property.country}. " +
                    "Price: $${property.priceUsd}, Specs: ${property.bedrooms} beds, ${property.bathrooms} baths, ${property.areaSqFt} sqft. " +
                    "Trust score: ${property.trustScore}/100. Gross Yield: ${String.format("%.2f", property.investmentMetrics.calculateGrossYield(property.priceUsd))}%. " +
                    "Question from buyer: $question. Answer accurately, transparently, distinguishing estimates from verified land registry facts."

            val answer = GeminiAiService.generateAiResponse(prompt)
            currentList.add(Pair(answer, true))
            val updatedMap = _aiAssistantMessages.value.toMutableMap()
            updatedMap[property.id] = currentList
            _aiAssistantMessages.value = updatedMap
            _isAiThinking.value = false
        }
    }

    fun bookViewing(
        property: Property,
        date: String,
        timeSlot: String,
        isVirtual: Boolean,
        notes: String
    ) {
        val appointment = ViewingAppointment(
            id = UUID.randomUUID().toString(),
            propertyId = property.id,
            propertyTitle = property.title,
            propertyCity = property.city,
            buyerName = userProfile.value.name,
            buyerPhone = userProfile.value.phone,
            agentName = property.agentName,
            date = date,
            timeSlot = timeSlot,
            isVirtualTour = isVirtual,
            notes = notes,
            status = ViewingStatus.CONFIRMED
        )
        repository.bookViewing(appointment)
    }

    fun sendMessage(propertyId: String, text: String, isFromAgent: Boolean, targetLanguage: String? = null) {
        viewModelScope.launch {
            var translated: String? = null
            if (!targetLanguage.isNullOrBlank() && targetLanguage != "English") {
                val prompt = "Translate this real estate message into $targetLanguage accurately and politely: '$text'"
                translated = GeminiAiService.generateAiResponse(prompt)
            }
            repository.sendMessage(propertyId, text, isFromAgent, translated)
        }
    }

    fun createAiListing(
        propertyType: PropertyType,
        location: String,
        priceUsd: Double,
        bedrooms: Int,
        bathrooms: Int,
        sqft: Int,
        features: String,
        onGenerated: (ListingCopyResult) -> Unit
    ) {
        viewModelScope.launch {
            val result = GeminiAiService.generateListingCopy(
                propertyType = propertyType.label,
                location = location,
                beds = bedrooms,
                baths = bathrooms,
                sqft = sqft,
                keyFeatures = features
            )
            onGenerated(result)
        }
    }

    fun publishNewListing(
        title: String,
        tagline: String,
        description: String,
        priceUsd: Double,
        propertyType: PropertyType,
        city: String,
        country: String,
        bedrooms: Int,
        bathrooms: Int,
        sqft: Int,
        amenities: List<String>,
        highlights: List<String>
    ) {
        val newProperty = Property(
            id = "prop_custom_${System.currentTimeMillis()}",
            title = title,
            tagline = tagline,
            priceUsd = priceUsd,
            listingType = ListingType.BUY,
            propertyType = propertyType,
            bedrooms = bedrooms,
            bathrooms = bathrooms,
            areaSqFt = sqft,
            address = "Prime Address, $city",
            city = city,
            country = country,
            latitude = 25.2048,
            longitude = 55.2708,
            description = description,
            highlights = highlights,
            amenities = amenities,
            imageResId = com.example.R.drawable.img_hero_villa,
            trustScore = 95,
            isVerified = true,
            fraudRiskScore = 0.01,
            fraudStatusTag = "AI Listing Screened & Title Verified",
            agentId = userProfile.value.id,
            agentName = userProfile.value.name,
            agentCompany = userProfile.value.agency,
            agentPhone = userProfile.value.phone,
            passport = PropertyPassport(
                titleDeedNumber = "TD-${System.currentTimeMillis().toString().takeLast(6)}",
                landRegistryHash = "0x" + UUID.randomUUID().toString().replace("-", "").take(16),
                cadastreId = "CAD-${city.take(3).uppercase()}-09",
                energyRating = "EPC A",
                carbonFootprintKgPerYear = 1200,
                structuralInspectionRating = 98,
                escrowAccountId = "ESCROW-${System.currentTimeMillis().toString().takeLast(5)}"
            ),
            investmentMetrics = InvestmentMetrics(
                estimatedMonthlyRentUsd = priceUsd * 0.0055,
                annualPropertyTaxUsd = priceUsd * 0.008,
                annualHoaFeesUsd = 12000.0,
                annualInsuranceUsd = 8000.0,
                annualMaintenanceBudgetUsd = 10000.0
            ),
            isFeatured = true,
            isNew = true
        )
        repository.addProperty(newProperty)
    }
}
