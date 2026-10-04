package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.*
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class EstateRepository(
    private var firestoreDb: FirebaseFirestore? = null
) {
    fun setupFirestore(context: Context) {
        if (firestoreDb == null) {
            try {
                val dbId = context.applicationContext.getString(R.string.firestore_database_id)
                firestoreDb = FirebaseFirestore.getInstance(dbId)
                Log.i("EstateRepository", "Firestore initialized with database ID: $dbId")
            } catch (e: Exception) {
                Log.w("EstateRepository", "Could not initialize Firestore with custom database ID", e)
            }
        }
    }

    fun syncWithFirebaseUser(user: FirebaseUser?) {
        if (user != null) {
            val displayName = user.displayName?.ifBlank { null }
                ?: user.email?.substringBefore("@")?.replace(".", " ")?.replaceFirstChar { it.uppercase() }
                ?: "Alexander Vance"
            _userProfile.value = _userProfile.value.copy(
                id = user.uid,
                name = displayName,
                email = user.email ?: "investor@estatexo.com",
                avatarUrl = user.photoUrl?.toString() ?: ""
            )

            firestoreDb?.let { db ->
                val userMap = mapOf(
                    "id" to user.uid,
                    "name" to displayName,
                    "email" to (user.email ?: ""),
                    "role" to _userProfile.value.role.name,
                    "avatarUrl" to (user.photoUrl?.toString() ?: ""),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                db.collection("users").document(user.uid).set(userMap, SetOptions.merge())
                    .addOnFailureListener { Log.w("EstateRepository", "Failed to sync user doc to Firestore", it) }
            }
        }
    }

    private val _properties = MutableStateFlow<List<Property>>(initialProperties())
    val properties: StateFlow<List<Property>> = _properties.asStateFlow()

    private val _savedPropertyIds = MutableStateFlow<Set<String>>(setOf("prop_dubai_1", "prop_ny_3"))
    val savedPropertyIds: StateFlow<Set<String>> = _savedPropertyIds.asStateFlow()

    private val _comparedPropertyIds = MutableStateFlow<List<String>>(listOf("prop_dubai_1", "prop_ny_3"))
    val comparedPropertyIds: StateFlow<List<String>> = _comparedPropertyIds.asStateFlow()

    private val _appointments = MutableStateFlow<List<ViewingAppointment>>(initialAppointments())
    val appointments: StateFlow<List<ViewingAppointment>> = _appointments.asStateFlow()

    private val _leads = MutableStateFlow<List<Lead>>(initialLeads())
    val leads: StateFlow<List<Lead>> = _leads.asStateFlow()

    private val _messages = MutableStateFlow<Map<String, List<ChatMessage>>>(initialChatMessages())
    val messages: StateFlow<Map<String, List<ChatMessage>>> = _messages.asStateFlow()

    private val _userProfile = MutableStateFlow(
        UserProfile(
            id = "user_me",
            name = "Alexander Vance",
            email = "alexander.vance@investor.com",
            phone = "+1 (212) 555-0198",
            role = UserRole.BUYER
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _currentCurrency = MutableStateFlow(AppCurrency.USD)
    val currentCurrency: StateFlow<AppCurrency> = _currentCurrency.asStateFlow()

    fun setCurrency(currency: AppCurrency) {
        _currentCurrency.value = currency
    }

    fun setUserRole(role: UserRole) {
        _userProfile.value = _userProfile.value.copy(role = role)
    }

    fun toggleSaveProperty(propertyId: String) {
        val current = _savedPropertyIds.value.toMutableSet()
        val isNowSaved = !current.contains(propertyId)
        if (isNowSaved) {
            current.add(propertyId)
        } else {
            current.remove(propertyId)
        }
        _savedPropertyIds.value = current

        val uid = Firebase.auth.currentUser?.uid
        if (uid != null && firestoreDb != null) {
            val docId = "${uid}_$propertyId"
            val docRef = firestoreDb!!.collection("saved_properties").document(docId)
            if (isNowSaved) {
                val data = mapOf(
                    "userId" to uid,
                    "propertyId" to propertyId,
                    "createdAt" to FieldValue.serverTimestamp()
                )
                docRef.set(data, SetOptions.merge())
                    .addOnFailureListener { Log.w("EstateRepository", "Failed to save property in Firestore", it) }
            } else {
                docRef.delete()
                    .addOnFailureListener { Log.w("EstateRepository", "Failed to delete saved property in Firestore", it) }
            }
        }
    }

    fun toggleCompareProperty(propertyId: String) {
        val current = _comparedPropertyIds.value.toMutableList()
        if (current.contains(propertyId)) {
            current.remove(propertyId)
        } else {
            if (current.size < 4) {
                current.add(propertyId)
            }
        }
        _comparedPropertyIds.value = current
    }

    fun addProperty(property: Property) {
        val list = _properties.value.toMutableList()
        list.add(0, property)
        _properties.value = list
    }

    fun bookViewing(appointment: ViewingAppointment) {
        val current = _appointments.value.toMutableList()
        current.add(0, appointment)
        _appointments.value = current

        val uid = Firebase.auth.currentUser?.uid
        if (uid != null && firestoreDb != null) {
            val appMap = mapOf(
                "id" to appointment.id,
                "userId" to uid,
                "propertyId" to appointment.propertyId,
                "propertyTitle" to appointment.propertyTitle,
                "buyerName" to appointment.buyerName,
                "agentName" to appointment.agentName,
                "date" to appointment.date,
                "timeSlot" to appointment.timeSlot,
                "status" to appointment.status.name,
                "createdAt" to FieldValue.serverTimestamp()
            )
            firestoreDb!!.collection("appointments").document(appointment.id).set(appMap)
                .addOnFailureListener { Log.w("EstateRepository", "Failed to write appointment in Firestore", it) }
        }
    }

    fun updateAppointmentStatus(appointmentId: String, newStatus: ViewingStatus) {
        _appointments.value = _appointments.value.map {
            if (it.id == appointmentId) it.copy(status = newStatus) else it
        }
    }

    fun updateLeadStage(leadId: String, newStage: LeadStage) {
        _leads.value = _leads.value.map {
            if (it.id == leadId) it.copy(stage = newStage) else it
        }
    }

    fun sendMessage(propertyId: String, text: String, isFromAgent: Boolean, translatedText: String? = null) {
        val map = _messages.value.toMutableMap()
        val currentList = map[propertyId]?.toMutableList() ?: mutableListOf()
        val newMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            propertyId = propertyId,
            senderName = if (isFromAgent) "Julian Montgomery (Agent)" else _userProfile.value.name,
            isFromAgent = isFromAgent,
            originalText = text,
            translatedText = translatedText,
            timestamp = "Just now"
        )
        currentList.add(newMessage)
        map[propertyId] = currentList
        _messages.value = map
    }

    companion object {
        fun initialProperties(): List<Property> {
            return listOf(
                Property(
                    id = "prop_dubai_1",
                    title = "The Royal Palm Villa",
                    tagline = "Ultra-luxury beachfront sanctuary with private infinity pool and marina access",
                    priceUsd = 14800000.0,
                    listingType = ListingType.BUY,
                    propertyType = PropertyType.VILLA,
                    bedrooms = 6,
                    bathrooms = 7,
                    areaSqFt = 9400,
                    address = "Frond G, Palm Jumeirah",
                    city = "Dubai",
                    country = "United Arab Emirates",
                    latitude = 25.1124,
                    longitude = 55.1390,
                    description = "A bespoke masterwork of contemporary coastal architecture situated on the premier frond of Palm Jumeirah. Boasts double-height floor-to-ceiling glass, Italian Calacatta marble flooring, private 25-meter infinity pool facing the Dubai skyline, and custom show kitchen.",
                    highlights = listOf(
                        "Direct private beachfront with 120ft shoreline",
                        "Show kitchen & auxiliary catering chef kitchen",
                        "Private wellness spa with steam & hammam",
                        "Automated biometric smart-estate infrastructure",
                        "Rooftop sunset observation deck with lounge"
                    ),
                    amenities = listOf("Infinity Pool", "Private Beach", "Smart Home", "Spa & Hammam", "Cinema", "6-Car Garage", "Elevator"),
                    imageResId = R.drawable.img_hero_villa,
                    virtualTourRooms = listOf(
                        VirtualTourRoom("room_1", "Grand Double-Height Foyer", "Dramatic 7-meter ceiling with custom crystal chandelier and floating spiral marble staircase.", 850, listOf("Calacatta Marble", "Custom Chandelier")),
                        VirtualTourRoom("room_2", "Panoramic Gulf Living Salon", "Seamless pocket sliding doors that open completely to the infinity terrace.", 1450, listOf("Burj Al Arab View", "Acoustic Insulation")),
                        VirtualTourRoom("room_3", "Primary Presidential Suite", "Wraparound glass terrace overlooking the Arabian Gulf with dual custom walk-in dressing suites.", 1100, listOf("Private Terrace", "Onyx Ensuite")),
                        VirtualTourRoom("room_4", "Rooftop Sky Lounge", "Entertaining deck equipped with full outdoor kitchen, teppanyaki bar, and fire lounge.", 980, listOf("Outdoor Bar", "Skyline Views"))
                    ),
                    trustScore = 98,
                    isVerified = true,
                    fraudRiskScore = 0.01,
                    fraudStatusTag = "Verified Real-Estate Registry #DXB-98412-A",
                    agentId = "agent_julian",
                    agentName = "Julian Montgomery",
                    agentCompany = "Knight Frank Prime Global",
                    agentPhone = "+971 4 555 8920",
                    agentRating = 4.98,
                    passport = PropertyPassport(
                        titleDeedNumber = "DXB-TD-2024-89311",
                        landRegistryHash = "0x8fa1b9802c34d9134a6bc72e",
                        cadastreId = "PLM-JUM-FRG-0042",
                        energyRating = "Estidama Pearl 3 / EPC A+",
                        carbonFootprintKgPerYear = 1420,
                        structuralInspectionRating = 99,
                        escrowAccountId = "DLD-ESCROW-448291"
                    ),
                    investmentMetrics = InvestmentMetrics(
                        estimatedMonthlyRentUsd = 85000.0,
                        annualPropertyTaxUsd = 0.0, // 0% property tax in Dubai
                        annualHoaFeesUsd = 18500.0,
                        annualInsuranceUsd = 9200.0,
                        annualMaintenanceBudgetUsd = 14000.0,
                        managementFeePercent = 7.0,
                        projectedAppreciationRateAnnual = 7.8,
                        historicalOccupancyRate = 96.2
                    ),
                    nearbyAmenities = listOf(
                        NearbyAmenity(AmenityCategory.DINING, "Nobu Dubai at Atlantis", 1.8, 4, 4.9),
                        NearbyAmenity(AmenityCategory.SCHOOL, "GEMS Wellington International", 6.2, 11, 4.8),
                        NearbyAmenity(AmenityCategory.AIRPORT, "Dubai Int'l Airport (DXB)", 28.5, 24, 4.9),
                        NearbyAmenity(AmenityCategory.LEISURE, "Dubai Marina Yacht Club", 5.4, 9, 4.8)
                    ),
                    documents = listOf(
                        PropertyDocument("doc_1", "Dubai Land Dept Title Deed", "Title Deed", "2.4 MB", DocumentStatus.VERIFIED, "Official authenticated title deed with electronic QR seal and clean title guarantee."),
                        PropertyDocument("doc_2", "Architectural CAD Blueprint", "Floor Plan", "8.9 MB", DocumentStatus.VERIFIED, "Full structural and MEP engineering schematic approved by Dubai Municipality."),
                        PropertyDocument("doc_3", "Escrow & NOC Clearance", "Legal Certificate", "1.2 MB", DocumentStatus.VERIFIED, "Unconditional developer No Objection Certificate (NOC) and escrow audit.")
                    ),
                    isFeatured = true,
                    isNew = true,
                    aiEstimatedValueUsd = 15350000.0,
                    valuationConfidence = "High (96% - 14 Palm Jumeirah closed comparables)"
                ),
                Property(
                    id = "prop_ny_3",
                    title = "Billionaires' Row Sky Penthouse",
                    tagline = "Panoramic Central Park view triplex with 360-degree glass perimeter",
                    priceUsd = 22500000.0,
                    listingType = ListingType.BUY,
                    propertyType = PropertyType.PENTHOUSE,
                    bedrooms = 5,
                    bathrooms = 6,
                    areaSqFt = 7200,
                    address = "111 West 57th Street",
                    city = "New York",
                    country = "United States",
                    latitude = 40.7648,
                    longitude = -73.9782,
                    description = "Soaring over 1,000 feet above Manhattan, this trophy residence occupies three full floors. Features sweeping, perfectly centered vistas of Central Park to the north and the Downtown skyline to the south. Private elevator vestibule, custom bronze accents, and hand-selected statuary marble.",
                    highlights = listOf(
                        "Centered Central Park unobstructed view lines",
                        "Direct high-speed key-locked elevator access",
                        "14-foot ceiling heights with motorized acoustic glazing",
                        "Temperature-controlled 600-bottle glass wine gallery",
                        "24/7 Park Hyatt white-glove hotel service integration"
                    ),
                    amenities = listOf("Central Park View", "Private Elevator", "Wine Cellar", "Concierge 24/7", "Indoor Pool", "Valet Parking"),
                    imageResId = R.drawable.img_penthouse_interior,
                    virtualTourRooms = listOf(
                        VirtualTourRoom("ny_1", "Grand Park Salon", "Stunning north-facing salon with floor-to-ceiling glass framing Central Park.", 1600, listOf("Direct Park View", "14ft Ceilings")),
                        VirtualTourRoom("ny_2", "Primary Corner Suite", "Dual oversized dressing rooms and radiant-heated marble master bath.", 1100, listOf("Corner Exposure", "Statuary Marble")),
                        VirtualTourRoom("ny_3", "Formal Dining & Wine Gallery", "Custom bronze-framed dining room with temperature-controlled glass vault.", 750, listOf("600-Bottle Storage", "Bespoke Bronze"))
                    ),
                    trustScore = 99,
                    isVerified = true,
                    fraudRiskScore = 0.005,
                    fraudStatusTag = "Verified ACRIS NYC Land Record",
                    agentId = "agent_eleanor",
                    agentName = "Eleanor Vance-Sterling",
                    agentCompany = "Sotheby's International Realty NY",
                    agentPhone = "+1 212 555 3300",
                    agentRating = 4.99,
                    passport = PropertyPassport(
                        titleDeedNumber = "NYC-DEED-2023-77192",
                        landRegistryHash = "0x4b7890fca921e54c82b",
                        cadastreId = "BLOCK-1010-LOT-1002",
                        energyRating = "LEED Gold Certified",
                        carbonFootprintKgPerYear = 1850,
                        structuralInspectionRating = 98,
                        escrowAccountId = "JP-MORGAN-ESCROW-9182"
                    ),
                    investmentMetrics = InvestmentMetrics(
                        estimatedMonthlyRentUsd = 120000.0,
                        annualPropertyTaxUsd = 195000.0,
                        annualHoaFeesUsd = 142000.0,
                        annualInsuranceUsd = 24000.0,
                        annualMaintenanceBudgetUsd = 16000.0,
                        managementFeePercent = 6.0,
                        projectedAppreciationRateAnnual = 5.9,
                        historicalOccupancyRate = 98.0
                    ),
                    nearbyAmenities = listOf(
                        NearbyAmenity(AmenityCategory.LEISURE, "Central Park South Entrance", 0.1, 2, 5.0),
                        NearbyAmenity(AmenityCategory.DINING, "Le Bernardin (3 Michelin Stars)", 0.6, 5, 4.9),
                        NearbyAmenity(AmenityCategory.TRANSIT, "57th St Subway Station", 0.2, 3, 4.7),
                        NearbyAmenity(AmenityCategory.SCHOOL, "Collegiate School NYC", 2.1, 10, 4.8)
                    ),
                    documents = listOf(
                        PropertyDocument("doc_ny_1", "ACRIS Real Property Record", "Title Deed", "1.8 MB", DocumentStatus.VERIFIED, "Verified deed recorded with NYC Department of Finance."),
                        PropertyDocument("doc_ny_2", "Condominium Offering Plan & Bylaws", "Offering Plan", "14.2 MB", DocumentStatus.VERIFIED, "Full Attorney General approved condominium offering disclosure.")
                    ),
                    isFeatured = true,
                    isNew = false,
                    aiEstimatedValueUsd = 23200000.0,
                    valuationConfidence = "High (95% - 22 Manhattan ultra-prime comparables)"
                ),
                Property(
                    id = "prop_london_2",
                    title = "Kensington Private Garden Mansion",
                    tagline = "Grade II listed ambassadorial residence with subterranean wellness oasis",
                    priceUsd = 28500000.0,
                    listingType = ListingType.BUY,
                    propertyType = PropertyType.VILLA,
                    bedrooms = 7,
                    bathrooms = 8,
                    areaSqFt = 11200,
                    address = "Phillimore Gardens, Kensington",
                    city = "London",
                    country = "United Kingdom",
                    latitude = 51.5015,
                    longitude = -0.1985,
                    description = "An exceptional detached Victorian residence meticulously transformed behind a classical facade. Extends across five expansive levels with private passenger elevator, subterranean 15m swimming pool, cinema room, wine room, and landscaped south-facing garden.",
                    highlights = listOf(
                        "Subterranean wellness sanctuary with 15m pool & sauna",
                        "South-facing mature landscaped garden with courtyard",
                        "Ambassadorial grand reception rooms with 3.8m ceilings",
                        "Discreet secondary staff quarters with separate entrance",
                        "Freehold tenure in Royal Borough of Kensington & Chelsea"
                    ),
                    amenities = listOf("Indoor Pool", "Private Garden", "Elevator", "Home Cinema", "Wine Room", "Staff Quarters", "Secure Parking"),
                    imageResId = R.drawable.img_hero_villa,
                    trustScore = 99,
                    isVerified = true,
                    fraudRiskScore = 0.01,
                    fraudStatusTag = "Verified HM Land Registry Title BGL4921",
                    agentId = "agent_charles",
                    agentName = "Lord Charles Pembroke",
                    agentCompany = "Savills Prime Central London",
                    agentPhone = "+44 20 7946 0912",
                    agentRating = 4.96,
                    passport = PropertyPassport(
                        titleDeedNumber = "HMLR-BGL-4921-X",
                        landRegistryHash = "0x9812afb49102c77d",
                        cadastreId = "RBKC-PHIL-0019",
                        energyRating = "EPC B (Modernized HVAC)",
                        carbonFootprintKgPerYear = 2100,
                        structuralInspectionRating = 97,
                        escrowAccountId = "BARCLAYS-PRIVATE-ESCROW-29"
                    ),
                    investmentMetrics = InvestmentMetrics(
                        estimatedMonthlyRentUsd = 145000.0,
                        annualPropertyTaxUsd = 4200.0, // UK Council Tax Band H
                        annualHoaFeesUsd = 0.0, // Freehold
                        annualInsuranceUsd = 18500.0,
                        annualMaintenanceBudgetUsd = 28000.0,
                        managementFeePercent = 10.0,
                        projectedAppreciationRateAnnual = 5.2,
                        historicalOccupancyRate = 95.0
                    ),
                    nearbyAmenities = listOf(
                        NearbyAmenity(AmenityCategory.LEISURE, "Holland Park & Kyoto Garden", 0.3, 4, 4.9),
                        NearbyAmenity(AmenityCategory.DINING, "The Ledbury (3 Michelin Stars)", 2.4, 9, 4.9),
                        NearbyAmenity(AmenityCategory.SCHOOL, "Kensington Prep & Thomas's", 1.2, 5, 4.8),
                        NearbyAmenity(AmenityCategory.TRANSIT, "High Street Kensington Station", 0.4, 6, 4.6)
                    ),
                    isFeatured = true,
                    isNew = true,
                    aiEstimatedValueUsd = 29400000.0,
                    valuationConfidence = "High (93% - 9 Kensington Freehold transactions)"
                ),
                Property(
                    id = "prop_tokyo_4",
                    title = "Minato Hinoki Sanctuary",
                    tagline = "Seismically isolated architectural villa with cedar onsen and Zen gardens",
                    priceUsd = 9200000.0,
                    listingType = ListingType.BUY,
                    propertyType = PropertyType.VILLA,
                    bedrooms = 4,
                    bathrooms = 4,
                    areaSqFt = 4900,
                    address = "Moto-Azabu, Minato-ku",
                    city = "Tokyo",
                    country = "Japan",
                    latitude = 35.6560,
                    longitude = 139.7310,
                    description = "Rare low-rise residence nestled in the diplomatic enclave of Moto-Azabu. Features artisan Yoshino cedar woodwork, private stone hot spring bath (onsen), interior bamboo garden, and earthquake-proof base-isolation engineering.",
                    highlights = listOf(
                        "Earthquake base-isolation foundation damper system",
                        "Aged Hinoki wood Japanese onsen bath pavilion",
                        "Traditional meditative Zen stone garden with koi stream",
                        "Underground 3-car heated turntable garage",
                        "Direct walking distance to Roppongi Hills & embassies"
                    ),
                    amenities = listOf("Hinoki Onsen", "Zen Garden", "Car Turntable", "Seismic Isolation", "Heated Floors", "Tea Room"),
                    imageResId = R.drawable.img_penthouse_interior,
                    trustScore = 97,
                    isVerified = true,
                    fraudRiskScore = 0.015,
                    fraudStatusTag = "Verified Tokyo Legal Affairs Bureau Record",
                    agentId = "agent_kenji",
                    agentName = "Kenji Takahashi",
                    agentCompany = "Mitsui Fudosan Luxury Realty",
                    agentPhone = "+81 3 5555 0192",
                    agentRating = 4.97,
                    passport = PropertyPassport(
                        titleDeedNumber = "TOKYO-MINATO-88192",
                        landRegistryHash = "0x77ab41029cba81",
                        cadastreId = "AZABU-3-44-12",
                        energyRating = "CASBEE S-Rank (Highest)",
                        carbonFootprintKgPerYear = 1100,
                        structuralInspectionRating = 100,
                        escrowAccountId = "SMBC-ESCROW-TOKYO-88"
                    ),
                    investmentMetrics = InvestmentMetrics(
                        estimatedMonthlyRentUsd = 45000.0,
                        annualPropertyTaxUsd = 38000.0,
                        annualHoaFeesUsd = 4800.0,
                        annualInsuranceUsd = 8900.0,
                        annualMaintenanceBudgetUsd = 9500.0,
                        managementFeePercent = 5.0,
                        projectedAppreciationRateAnnual = 6.8,
                        historicalOccupancyRate = 99.1
                    ),
                    nearbyAmenities = listOf(
                        NearbyAmenity(AmenityCategory.DINING, "Sukiyabashi Jiro Roppongi", 0.9, 4, 4.9),
                        NearbyAmenity(AmenityCategory.LEISURE, "Arisugawa-no-miya Memorial Park", 0.4, 5, 4.8),
                        NearbyAmenity(AmenityCategory.SCHOOL, "The American School in Japan (ASIJ)", 2.8, 12, 4.9),
                        NearbyAmenity(AmenityCategory.AIRPORT, "Haneda Airport (HND)", 16.5, 20, 4.8)
                    ),
                    isFeatured = false,
                    isNew = true,
                    aiEstimatedValueUsd = 9600000.0,
                    valuationConfidence = "High (95% - Minato diplomatic enclave baseline)"
                ),
                Property(
                    id = "prop_paris_5",
                    title = "Avenue Montaigne Haussmannian Jewel",
                    tagline = "Golden Triangle residence with direct Eiffel Tower views and historic gilding",
                    priceUsd = 11500000.0,
                    listingType = ListingType.BUY,
                    propertyType = PropertyType.APARTMENT,
                    bedrooms = 3,
                    bathrooms = 4,
                    areaSqFt = 4200,
                    address = "Avenue Montaigne, 8th Arrondissement",
                    city = "Paris",
                    country = "France",
                    latitude = 48.8662,
                    longitude = 2.3075,
                    description = "Positioned on the prestigious Avenue Montaigne among haute couture houses. Featuring soaring 4.2m ceilings, hand-restored 19th-century gold-leaf moldings, herringbone parquet de Versailles, and direct unobstructed vistas of the Eiffel Tower.",
                    highlights = listOf(
                        "Direct Eiffel Tower balcony views",
                        "Original 1880s restored gold-leaf plasterwork & marble fireplaces",
                        "Point de Hongrie solid oak parquet flooring",
                        "Haute couture Golden Triangle prime address",
                        "Full Bang & Olufsen integrated architectural audio"
                    ),
                    amenities = listOf("Eiffel Tower View", "Balcony", "Fireplace", "Wine Salon", "Concierge", "Security"),
                    imageResId = R.drawable.img_hero_villa,
                    trustScore = 96,
                    isVerified = true,
                    fraudRiskScore = 0.02,
                    fraudStatusTag = "Verified French Notarial Deed (Notaire de Paris)",
                    agentId = "agent_celeste",
                    agentName = "Céleste Laurent",
                    agentCompany = "Daniel Féau Prime Paris",
                    agentPhone = "+33 1 42 68 55 00",
                    agentRating = 4.93,
                    passport = PropertyPassport(
                        titleDeedNumber = "PARIS-NOTAIRE-48291",
                        landRegistryHash = "0x55dc991a0293",
                        cadastreId = "PARIS-08-AM-0014",
                        energyRating = "DPE C / Modern Thermodynamic",
                        carbonFootprintKgPerYear = 1600,
                        structuralInspectionRating = 95,
                        escrowAccountId = "CAISSE-DES-DEPOTS-PARIS"
                    ),
                    investmentMetrics = InvestmentMetrics(
                        estimatedMonthlyRentUsd = 52000.0,
                        annualPropertyTaxUsd = 14500.0,
                        annualHoaFeesUsd = 16200.0,
                        annualInsuranceUsd = 7500.0,
                        annualMaintenanceBudgetUsd = 11000.0,
                        managementFeePercent = 8.0,
                        projectedAppreciationRateAnnual = 5.4,
                        historicalOccupancyRate = 96.0
                    ),
                    nearbyAmenities = listOf(
                        NearbyAmenity(AmenityCategory.DINING, "Plaza Athénée (Alain Ducasse)", 0.2, 2, 4.9),
                        NearbyAmenity(AmenityCategory.LEISURE, "Grand Palais & Champs-Élysées", 0.5, 6, 4.8),
                        NearbyAmenity(AmenityCategory.TRANSIT, "Franklin D. Roosevelt Metro", 0.3, 4, 4.7)
                    ),
                    isFeatured = true,
                    isNew = false,
                    aiEstimatedValueUsd = 11950000.0,
                    valuationConfidence = "High (92% - Golden Triangle price/sqm index)"
                ),
                Property(
                    id = "prop_zurich_6",
                    title = "Lake Zurich Waterfront Sanctuary",
                    tagline = "Private alpine lake estate with deepwater boathouse and private jetty",
                    priceUsd = 16900000.0,
                    listingType = ListingType.BUY,
                    propertyType = PropertyType.ESTATE,
                    bedrooms = 5,
                    bathrooms = 6,
                    areaSqFt = 7600,
                    address = "Goldküste, Küsnacht",
                    city = "Zurich",
                    country = "Switzerland",
                    latitude = 47.3195,
                    longitude = 8.5830,
                    description = "Positioned on Zurich's fabled 'Gold Coast' with 80 meters of private lake frontage. Includes private heated boathouse, cantilevered glass infinity pool, lakeside sauna pavilion, and zero-emission geothermal energy loop.",
                    highlights = listOf(
                        "Direct Lake Zurich private boat dock & heated boathouse",
                        "Cantilevered lakeside infinity pool with alpine panorama",
                        "Ultra-low municipal tax rate (Küsnacht canton)",
                        "Geothermal heating & solar Tesla battery storage",
                        "High-security subterranean safe room"
                    ),
                    amenities = listOf("Lake Frontage", "Private Jetty", "Boathouse", "Infinity Pool", "Sauna Pavilion", "Geothermal", "Safe Room"),
                    imageResId = R.drawable.img_hero_villa,
                    trustScore = 100,
                    isVerified = true,
                    fraudRiskScore = 0.002,
                    fraudStatusTag = "Verified Grundbuchamt Zurich (Swiss Land Registry)",
                    agentId = "agent_marc",
                    agentName = "Marc von Steiger",
                    agentCompany = "Engel & Völkers Private Office",
                    agentPhone = "+41 44 555 2200",
                    agentRating = 4.99,
                    passport = PropertyPassport(
                        titleDeedNumber = "CH-ZH-GB-008129",
                        landRegistryHash = "0x11ab44ff99812",
                        cadastreId = "KUSN-GOLD-0098",
                        energyRating = "Minergie-P Eco (Net Zero)",
                        carbonFootprintKgPerYear = 450,
                        structuralInspectionRating = 100,
                        escrowAccountId = "UBS-SWISS-ESCROW-0021"
                    ),
                    investmentMetrics = InvestmentMetrics(
                        estimatedMonthlyRentUsd = 72000.0,
                        annualPropertyTaxUsd = 8200.0,
                        annualHoaFeesUsd = 0.0,
                        annualInsuranceUsd = 12000.0,
                        annualMaintenanceBudgetUsd = 16000.0,
                        managementFeePercent = 5.0,
                        projectedAppreciationRateAnnual = 6.1,
                        historicalOccupancyRate = 97.5
                    ),
                    nearbyAmenities = listOf(
                        NearbyAmenity(AmenityCategory.LEISURE, "Zurich Yacht Club", 3.2, 7, 4.8),
                        NearbyAmenity(AmenityCategory.SCHOOL, "Zurich International School (ZIS)", 5.1, 10, 4.9),
                        NearbyAmenity(AmenityCategory.AIRPORT, "Zurich Airport (ZRH)", 22.0, 22, 4.9)
                    ),
                    isFeatured = true,
                    isNew = true,
                    aiEstimatedValueUsd = 17500000.0,
                    valuationConfidence = "High (97% - Lake Zurich Gold Coast benchmark)"
                )
            )
        }

        fun initialAppointments(): List<ViewingAppointment> {
            return listOf(
                ViewingAppointment(
                    id = "app_1",
                    propertyId = "prop_dubai_1",
                    propertyTitle = "The Royal Palm Villa",
                    propertyCity = "Dubai",
                    buyerName = "Alexander Vance",
                    buyerPhone = "+1 (212) 555-0198",
                    agentName = "Julian Montgomery",
                    date = "Tomorrow, 10:30 AM",
                    timeSlot = "Morning (10:30 AM - 11:30 AM)",
                    isVirtualTour = false,
                    notes = "Client requested private helicopter transfer and escrow documentation review.",
                    status = ViewingStatus.CONFIRMED
                ),
                ViewingAppointment(
                    id = "app_2",
                    propertyId = "prop_ny_3",
                    propertyTitle = "Billionaires' Row Sky Penthouse",
                    propertyCity = "New York",
                    buyerName = "Alexander Vance",
                    buyerPhone = "+1 (212) 555-0198",
                    agentName = "Eleanor Vance-Sterling",
                    date = "Thursday, 3:00 PM",
                    timeSlot = "Afternoon (3:00 PM - 4:00 PM)",
                    isVirtualTour = true,
                    notes = "3D Interactive virtual walkthrough with lead architectural designer.",
                    status = ViewingStatus.CONFIRMED
                )
            )
        }

        fun initialLeads(): List<Lead> {
            return listOf(
                Lead(
                    id = "lead_1",
                    propertyId = "prop_dubai_1",
                    propertyTitle = "The Royal Palm Villa",
                    clientName = "Sheikh Tariq Al-Nuaimi",
                    clientEmail = "tariq.alnuaimi@familyoffice.ae",
                    clientPhone = "+971 50 123 4567",
                    budgetUsd = 16000000.0,
                    stage = LeadStage.VIEWING_SET,
                    lastContactDate = "Today, 2:15 PM",
                    notes = "High net worth buyer looking for turnkey beachfront mansion. Funds verified through Dubai Islamic Bank."
                ),
                Lead(
                    id = "lead_2",
                    propertyId = "prop_ny_3",
                    propertyTitle = "Billionaires' Row Sky Penthouse",
                    clientName = "Victoria Sterling-Fox",
                    clientEmail = "v.sterling@apexcapital.co.uk",
                    clientPhone = "+44 7700 900123",
                    budgetUsd = 25000000.0,
                    stage = LeadStage.OFFER_RECEIVED,
                    lastContactDate = "Yesterday, 6:40 PM",
                    notes = "Submitted formal all-cash offer at $21.8M with 14-day close. Seller reviewing counteroffer strategy."
                ),
                Lead(
                    id = "lead_3",
                    propertyId = "prop_london_2",
                    propertyTitle = "Kensington Private Garden Mansion",
                    clientName = "Dr. Henrik Lindqvist",
                    clientEmail = "henrik@lindqvist-biotech.ch",
                    clientPhone = "+41 79 555 4321",
                    budgetUsd = 30000000.0,
                    stage = LeadStage.NEW_INQUIRY,
                    lastContactDate = "3 hours ago",
                    notes = "Relocating research HQ to London. Inquired about planning permission for underground gym expansion."
                )
            )
        }

        fun initialChatMessages(): Map<String, List<ChatMessage>> {
            return mapOf(
                "prop_dubai_1" to listOf(
                    ChatMessage(
                        id = "msg_1",
                        propertyId = "prop_dubai_1",
                        senderName = "Julian Montgomery (Agent)",
                        isFromAgent = true,
                        originalText = "Welcome to EstateXo! I have the updated title deed and private beach access permits ready for your inspection.",
                        originalLanguage = "English",
                        translatedText = null,
                        timestamp = "10:15 AM"
                    ),
                    ChatMessage(
                        id = "msg_2",
                        propertyId = "prop_dubai_1",
                        senderName = "Alexander Vance",
                        isFromAgent = false,
                        originalText = "Thank you Julian. Could you confirm if the yacht berth can accommodate an 85ft vessel at low tide?",
                        originalLanguage = "English",
                        translatedText = "شكراً جوليان. هل يمكنك تأكيد ما إذا كان مرسى اليخوت يستوعب يختاً بطول 85 قدماً أثناء الجزر؟",
                        timestamp = "10:18 AM"
                    ),
                    ChatMessage(
                        id = "msg_3",
                        propertyId = "prop_dubai_1",
                        senderName = "Julian Montgomery (Agent)",
                        isFromAgent = true,
                        originalText = "Yes, the frond channel was dredged in 2024 to a minimum depth of 4.8 meters. An 85ft vessel fits comfortably.",
                        originalLanguage = "English",
                        translatedText = "نعم، تم تجريف قناة السعفة في عام 2024 إلى عمق لا يقل عن 4.8 أمتار. اليخت بطول 85 قدماً يناسبها بسهولة.",
                        timestamp = "10:20 AM"
                    )
                )
            )
        }

        fun renovationStyles(): List<RenovationStyle> {
            return listOf(
                RenovationStyle(
                    id = "style_scandi",
                    name = "Nordic Minimalist",
                    subtitle = "Organic oak, muted ceramics & sculptural light",
                    description = "Light blonde Scandinavian timbers, micro-cement flooring, neutral bouclé textiles, and museum-grade recessed spotlighting.",
                    aestheticTokens = listOf("Light Oak", "Bouclé", "Limewash", "Recessed Track"),
                    estimatedRenovationCostUsd = 185000.0,
                    projectedValueIncreaseUsd = 420000.0
                ),
                RenovationStyle(
                    id = "style_luxury_glam",
                    name = "Modern Luxury Glam",
                    subtitle = "Bookmatched marble, champagne brass & velvet",
                    description = "Statuary Italian marble feature wall with backlighting, brushed brass accents, smoked glass vitrines, and custom cove lighting.",
                    aestheticTokens = listOf("Calacatta Gold", "Brushed Brass", "Smoked Glass", "Velvet"),
                    estimatedRenovationCostUsd = 340000.0,
                    projectedValueIncreaseUsd = 850000.0
                ),
                RenovationStyle(
                    id = "style_japandi",
                    name = "Japandi Zen",
                    subtitle = "Shoji geometry, charred timber & wabi-sabi stone",
                    description = "Warm cypress wood screens, tatami lounge integration, fluted stone fire plinth, and natural clay plaster finishes.",
                    aestheticTokens = listOf("Yakisugi Cedar", "Shoji Screens", "Clay Plaster", "Zen Water"),
                    estimatedRenovationCostUsd = 240000.0,
                    projectedValueIncreaseUsd = 560000.0
                ),
                RenovationStyle(
                    id = "style_parisian",
                    name = "Parisian Chic",
                    subtitle = "Chevron parquet, ornate moldings & contemporary art",
                    description = "Classic Haussmannian wall panelling paired with striking contemporary art pieces, black steel windows, and antique brass chandeliers.",
                    aestheticTokens = listOf("Chevron Parquet", "Ornate Cornice", "Black Iron", "Gilded Mirror"),
                    estimatedRenovationCostUsd = 290000.0,
                    projectedValueIncreaseUsd = 680000.0
                )
            )
        }
    }
}
