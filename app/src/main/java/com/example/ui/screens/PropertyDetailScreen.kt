package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.*
import com.example.data.repository.EstateRepository
import com.example.ui.theme.*

enum class DetailTab(val label: String, val icon: ImageVector) {
    OVERVIEW("Overview", Icons.Default.Info),
    PASSPORT("Passport & Trust", Icons.Default.VerifiedUser),
    VIRTUAL_TOUR("3D Virtual Tour", Icons.Default.ViewInAr),
    RENOVATION("AI Renovation", Icons.Default.Architecture),
    INVESTMENT("Investment", Icons.Default.Calculate),
    LIFESTYLE("Lifestyle Radar", Icons.Default.Explore),
    AI_ASSISTANT("AI Assistant", Icons.Default.AutoAwesome)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailScreen(
    property: Property,
    currency: AppCurrency,
    isSaved: Boolean,
    isCompared: Boolean,
    aiMessages: List<Pair<String, Boolean>>,
    isAiThinking: Boolean,
    onBack: () -> Unit,
    onSaveToggle: () -> Unit,
    onCompareToggle: () -> Unit,
    onBookViewing: () -> Unit,
    onChatWithAgent: () -> Unit,
    onAskAi: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(DetailTab.OVERVIEW) }
    var selectedVirtualRoom by remember { mutableStateOf(property.virtualTourRooms.firstOrNull()) }
    var selectedRenovationStyle by remember { mutableStateOf(EstateRepository.renovationStyles().first()) }
    var showRenovationAfter by remember { mutableStateOf(true) }

    // Investment Calculator States
    var downPaymentPercent by remember { mutableFloatStateOf(30f) }
    var interestRateAnnual by remember { mutableFloatStateOf(5.5f) }

    // AI Assistant text input
    var aiQuestionInput by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            Surface(
                color = SlateCardElevated,
                tonalElevation = 10.dp,
                border = BorderStroke(1.dp, SlateBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currency.format(property.priceUsd),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "${currency.format(property.pricePerSqFtUsd)}/sqft",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )
                    }

                    OutlinedButton(
                        onClick = onChatWithAgent,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = BorderStroke(1.dp, SlateBorder),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("detail_chat_agent_button")
                    ) {
                        Icon(imageVector = Icons.Default.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Chat")
                    }

                    Button(
                        onClick = onBookViewing,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = ObsidianBlack),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("detail_book_viewing_button")
                    ) {
                        Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Book Viewing", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBlack)
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Photo Gallery
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                ) {
                    AsyncImage(
                        model = property.imageResId ?: R.drawable.img_hero_villa,
                        contentDescription = property.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Scrim
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.6f),
                                        Color.Transparent,
                                        ObsidianBlack.copy(alpha = 0.9f)
                                    )
                                )
                            )
                    )

                    // Top Bar Floating Actions
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SurfaceGlass)
                                .testTag("detail_back_button")
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = onCompareToggle,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceGlass)
                                    .testTag("detail_compare_toggle_button")
                            ) {
                                Icon(
                                    imageVector = if (isCompared) Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.CompareArrows,
                                    contentDescription = "Compare",
                                    tint = if (isCompared) GoldLight else Color.White
                                )
                            }

                            IconButton(
                                onClick = onSaveToggle,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceGlass)
                                    .testTag("detail_save_toggle_button")
                            ) {
                                Icon(
                                    imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Save",
                                    tint = if (isSaved) CrimsonDanger else Color.White
                                )
                            }

                            IconButton(
                                onClick = {
                                    Toast.makeText(context, "Listing link copied to clipboard", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceGlass)
                            ) {
                                Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                            }
                        }
                    }

                    // Bottom Image Info
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SurfaceGlass,
                            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = property.propertyType.label.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GoldLight,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Text(
                            text = property.title,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )

                        Text(
                            text = "${property.address}, ${property.city}, ${property.country}",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                        )
                    }
                }
            }

            // Specs Card Strip
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateCard),
                    border = BorderStroke(1.dp, SlateBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SpecColumn(Icons.Default.Bed, "${property.bedrooms}", "Bedrooms")
                        SpecDivider()
                        SpecColumn(Icons.Default.Bathtub, "${property.bathrooms}", "Bathrooms")
                        SpecDivider()
                        SpecColumn(Icons.Default.SquareFoot, "${property.areaSqFt}", "Sq. Ft.")
                        SpecDivider()
                        SpecColumn(Icons.AutoMirrored.Filled.TrendingUp, "${String.format("%.1f", property.investmentMetrics.calculateGrossYield(property.priceUsd))}%", "Gross Yield")
                    }
                }
            }

            // AI Valuation Banner
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SlateCardElevated,
                    border = BorderStroke(1.dp, IndigoAccent.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(IndigoAccent.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Analytics, contentDescription = null, tint = SkyAccent)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "AI Property Valuation",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                                )
                                Surface(shape = RoundedCornerShape(6.dp), color = ObsidianBlack) {
                                    Text(
                                        text = "ESTIMATE",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = GoldLight),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${currency.format(property.aiEstimatedValueUsd)} • ${property.valuationConfidence}",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }
                    }
                }
            }

            // Feature Tabs
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DetailTab.values().forEach { tab ->
                        val isSelected = selectedTab == tab
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) GoldLight else SlateCard,
                            border = BorderStroke(1.dp, if (isSelected) GoldDark else SlateBorder),
                            modifier = Modifier
                                .clickable { selectedTab = tab }
                                .testTag("detail_tab_${tab.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = null,
                                    tint = if (isSelected) ObsidianBlack else TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = tab.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) ObsidianBlack else TextPrimary
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Tab Content Body
            when (selectedTab) {
                DetailTab.OVERVIEW -> {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Description
                            Text(
                                text = "About This Residence",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                            )
                            Text(
                                text = property.description,
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, lineHeight = 22.sp)
                            )

                            // Highlights
                            Text(
                                text = "Key Architectural Highlights",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                            )
                            property.highlights.forEach { highlight ->
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(16.dp))
                                    Text(text = highlight, style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary))
                                }
                            }

                            // Amenities Grid
                            Text(
                                text = "Luxury Amenities",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                property.amenities.forEach { amenity ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = SlateCard,
                                        border = BorderStroke(1.dp, SlateBorder)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Spa, contentDescription = null, tint = GoldLight, modifier = Modifier.size(14.dp))
                                            Text(text = amenity, style = MaterialTheme.typography.labelSmall.copy(color = TextPrimary))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                DetailTab.PASSPORT -> {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Trust Score Summary
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
                                border = BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(CircleShape)
                                            .background(EmeraldContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${property.trustScore}",
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = EmeraldLight
                                            )
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Global Trust Score: ${property.trustScore}/100",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                                        )
                                        Text(
                                            text = "Identity, title registry, escrow compliance, and structural audit verified.",
                                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                        )
                                    }
                                }
                            }

                            // Fraud Check Certificate
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = SlateCard,
                                border = BorderStroke(1.dp, SlateBorder)
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(18.dp))
                                        Text(
                                            text = "Fraud & Duplicate-Listing Audit",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                                        )
                                    }
                                    Text(
                                        text = "• AI Signature Analysis: 0 duplicate imagery or duplicate registry entries found.\n" +
                                                "• Valuation Anomaly Index: Clean (Priced within 3.4% of municipal average).\n" +
                                                "• Ownership Chain: Unbroken legal title chain confirmed.",
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, lineHeight = 18.sp)
                                    )
                                }
                            }

                            // Passport Details
                            Text(
                                text = "Digital Land Registry Record",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                            )
                            PassportRow("Title Deed Number", property.passport.titleDeedNumber)
                            PassportRow("Land Registry Hash", property.passport.landRegistryHash)
                            PassportRow("Cadastre ID", property.passport.cadastreId)
                            PassportRow("Energy Efficiency", property.passport.energyRating)
                            PassportRow("Carbon Footprint", "${property.passport.carbonFootprintKgPerYear} kg CO₂ / year")
                            PassportRow("Structural Inspection", "${property.passport.structuralInspectionRating}/100 Grade A+")
                            PassportRow("Escrow Account Protection", property.passport.escrowAccountId)

                            // Verified Documents
                            Text(
                                text = "Available Property Documents",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                            )
                            property.documents.forEach { doc ->
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = SlateCard,
                                    border = BorderStroke(1.dp, SlateBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = GoldLight)
                                            Column {
                                                Text(text = doc.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                                                Text(text = "${doc.category} • ${doc.fileSize}", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                                            }
                                        }
                                        Surface(shape = RoundedCornerShape(8.dp), color = EmeraldContainer) {
                                            Text(
                                                text = "VERIFIED",
                                                style = MaterialTheme.typography.labelSmall.copy(color = EmeraldLight, fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                DetailTab.VIRTUAL_TOUR -> {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "Interactive 3D Walkthrough",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                            )
                            Text(
                                text = "Navigate through rooms with interactive 360 viewpoint simulation.",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )

                            // Room Selector Chips
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                property.virtualTourRooms.forEach { room ->
                                    val isRoomSelected = selectedVirtualRoom?.roomId == room.roomId
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (isRoomSelected) SkyAccent else SlateCard,
                                        border = BorderStroke(1.dp, if (isRoomSelected) Color.White else SlateBorder),
                                        modifier = Modifier.clickable { selectedVirtualRoom = room }
                                    ) {
                                        Text(
                                            text = room.name,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = if (isRoomSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isRoomSelected) ObsidianBlack else TextPrimary
                                            ),
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }

                            // 3D Viewport Box
                            selectedVirtualRoom?.let { room ->
                                Card(
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
                                    border = BorderStroke(1.dp, SkyAccent.copy(alpha = 0.5f))
                                ) {
                                    Column {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(240.dp)
                                        ) {
                                            AsyncImage(
                                                model = property.imageResId ?: R.drawable.img_hero_villa,
                                                contentDescription = room.name,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )

                                            // 360 Gyro Indicator
                                            Surface(
                                                shape = RoundedCornerShape(20.dp),
                                                color = SurfaceGlass,
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .padding(12.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Icon(imageVector = Icons.Default.ScreenRotation, contentDescription = null, tint = SkyAccent, modifier = Modifier.size(16.dp))
                                                    Text(text = "360° Gyro Active", style = MaterialTheme.typography.labelSmall.copy(color = TextPrimary))
                                                }
                                            }

                                            // Center Panorama Hotspot
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.Center)
                                                    .size(48.dp)
                                                    .clip(CircleShape)
                                                    .background(SurfaceGlass)
                                                    .border(2.dp, SkyAccent, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(imageVector = Icons.Default.ViewInAr, contentDescription = "Tour", tint = SkyAccent)
                                            }
                                        }

                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(text = room.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                                                Text(text = "${room.squareFootage} sqft", style = MaterialTheme.typography.labelMedium.copy(color = SkyAccent))
                                            }
                                            Text(text = room.description, style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))

                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                room.keyFeatures.forEach { feature ->
                                                    Surface(shape = RoundedCornerShape(8.dp), color = SlateCard) {
                                                        Text(
                                                            text = feature,
                                                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary),
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                DetailTab.RENOVATION -> {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "AI Virtual Renovation Studio",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                            )
                            Text(
                                text = "Visualize interior aesthetics and calculate renovation ROI prior to acquisition.",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )

                            // Style Selector
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                EstateRepository.renovationStyles().forEach { style ->
                                    val isStyleSelected = selectedRenovationStyle.id == style.id
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (isStyleSelected) GoldLight else SlateCard,
                                        border = BorderStroke(1.dp, if (isStyleSelected) GoldDark else SlateBorder),
                                        modifier = Modifier.clickable { selectedRenovationStyle = style }
                                    ) {
                                        Text(
                                            text = style.name,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = if (isStyleSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isStyleSelected) ObsidianBlack else TextPrimary
                                            ),
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }

                            // Before / After Visual Card
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
                                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(230.dp)
                                    ) {
                                        AsyncImage(
                                            model = if (showRenovationAfter) R.drawable.img_penthouse_interior else (property.imageResId ?: R.drawable.img_hero_villa),
                                            contentDescription = "Renovation Simulation",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )

                                        // Toggle Switcher Overlay
                                        Surface(
                                            shape = RoundedCornerShape(20.dp),
                                            color = SurfaceGlass,
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .padding(12.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(4.dp),
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Surface(
                                                    shape = RoundedCornerShape(14.dp),
                                                    color = if (!showRenovationAfter) GoldLight else Color.Transparent,
                                                    modifier = Modifier.clickable { showRenovationAfter = false }
                                                ) {
                                                    Text(
                                                        text = "Current State",
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            color = if (!showRenovationAfter) ObsidianBlack else TextPrimary,
                                                            fontWeight = FontWeight.Bold
                                                        ),
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                    )
                                                }

                                                Surface(
                                                    shape = RoundedCornerShape(14.dp),
                                                    color = if (showRenovationAfter) GoldLight else Color.Transparent,
                                                    modifier = Modifier.clickable { showRenovationAfter = true }
                                                ) {
                                                    Text(
                                                        text = "AI Renovated",
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            color = if (showRenovationAfter) ObsidianBlack else TextPrimary,
                                                            fontWeight = FontWeight.Bold
                                                        ),
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = selectedRenovationStyle.name,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                                        )
                                        Text(
                                            text = selectedRenovationStyle.description,
                                            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                                        )

                                        HorizontalDivider(color = SlateBorder.copy(alpha = 0.5f))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(text = "Est. Renovation Cost", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                                                Text(
                                                    text = currency.format(selectedRenovationStyle.estimatedRenovationCostUsd),
                                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                                                )
                                            }

                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(text = "Projected Value Increase", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                                                Text(
                                                    text = "+${currency.format(selectedRenovationStyle.projectedValueIncreaseUsd)}",
                                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldLight)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                DetailTab.INVESTMENT -> {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Institutional Investment Model",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                            )

                            // ROI KPI Cards
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                InvestmentKpiCard(
                                    title = "Net Rental Yield",
                                    value = "${String.format("%.2f", property.investmentMetrics.calculateNetYield(property.priceUsd))}%",
                                    color = EmeraldLight,
                                    modifier = Modifier.weight(1f)
                                )
                                InvestmentKpiCard(
                                    title = "Gross Yield",
                                    value = "${String.format("%.2f", property.investmentMetrics.calculateGrossYield(property.priceUsd))}%",
                                    color = GoldLight,
                                    modifier = Modifier.weight(1f)
                                )
                                InvestmentKpiCard(
                                    title = "Monthly Cash Flow",
                                    value = currency.format(property.investmentMetrics.calculateMonthlyNetCashFlow(property.priceUsd, downPaymentPercent.toDouble(), interestRateAnnual.toDouble())),
                                    color = SkyAccent,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Interactive Scenario Sliders
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
                                border = BorderStroke(1.dp, SlateBorder)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Text(
                                        text = "Acquisition Financing Assumptions",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                                    )

                                    // Down payment slider
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = "Down Payment: ${downPaymentPercent.toInt()}%", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                                            Text(
                                                text = currency.format(property.priceUsd * (downPaymentPercent / 100.0)),
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                                            )
                                        }
                                        Slider(
                                            value = downPaymentPercent,
                                            onValueChange = { downPaymentPercent = it },
                                            valueRange = 10f..60f,
                                            colors = SliderDefaults.colors(thumbColor = GoldLight, activeTrackColor = GoldPrimary)
                                        )
                                    }

                                    // Interest rate slider
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = "Mortgage Interest Rate", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                                            Text(
                                                text = "${String.format("%.1f", interestRateAnnual)}% Fixed",
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                                            )
                                        }
                                        Slider(
                                            value = interestRateAnnual,
                                            onValueChange = { interestRateAnnual = it },
                                            valueRange = 3f..8f,
                                            colors = SliderDefaults.colors(thumbColor = SkyAccent, activeTrackColor = IndigoAccent)
                                        )
                                    }

                                    HorizontalDivider(color = SlateBorder.copy(alpha = 0.5f))

                                    // Expense Breakdown Table
                                    ExpenseRow("Est. Monthly Rent Income", "+${currency.format(property.investmentMetrics.estimatedMonthlyRentUsd)}", EmeraldLight)
                                    ExpenseRow("Annual HOA & Service Charges", "-${currency.format(property.investmentMetrics.annualHoaFeesUsd / 12)}/mo", TextSecondary)
                                    ExpenseRow("Annual Property Taxes", "-${currency.format(property.investmentMetrics.annualPropertyTaxUsd / 12)}/mo", TextSecondary)
                                    ExpenseRow("Insurance & Maintenance Reserve", "-${currency.format((property.investmentMetrics.annualInsuranceUsd + property.investmentMetrics.annualMaintenanceBudgetUsd) / 12)}/mo", TextSecondary)
                                }
                            }
                        }
                    }
                }

                DetailTab.LIFESTYLE -> {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "Neighborhood & Lifestyle Proximity Radar",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                            )
                            Text(
                                text = "Verified distances to international schools, Michelin dining, transport and airports.",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )

                            property.nearbyAmenities.forEach { amenity ->
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = SlateCard,
                                    border = BorderStroke(1.dp, SlateBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(SlateCardElevated),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = when (amenity.category) {
                                                        AmenityCategory.SCHOOL -> Icons.Default.School
                                                        AmenityCategory.HOSPITAL -> Icons.Default.LocalHospital
                                                        AmenityCategory.TRANSIT -> Icons.Default.Train
                                                        AmenityCategory.DINING -> Icons.Default.Restaurant
                                                        AmenityCategory.LEISURE -> Icons.Default.SportsGolf
                                                        AmenityCategory.AIRPORT -> Icons.Default.Flight
                                                    },
                                                    contentDescription = null,
                                                    tint = GoldLight,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }

                                            Column {
                                                Text(text = amenity.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                                                Text(text = "${amenity.category.label} • ${amenity.rating} ★", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                                            }
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "${amenity.travelTimeMinutes} min",
                                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = GoldLight)
                                            )
                                            Text(text = "${amenity.distanceKm} km", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                DetailTab.AI_ASSISTANT -> {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "Dedicated Property AI Copilot",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                            )
                            Text(
                                text = "Ask questions based directly on this property's deed, valuation, and comparable market data.",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )

                            // Quick Prompt Pills
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val prompts = listOf(
                                    "Why does this match my criteria?",
                                    "What questions should I ask the agent?",
                                    "Calculate estimated net rental yield",
                                    "What information is missing from this listing?",
                                    "Draft a formal purchase offer letter"
                                )
                                prompts.forEach { p ->
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = SlateCard,
                                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
                                        modifier = Modifier.clickable { onAskAi(p) }
                                    ) {
                                        Text(
                                            text = p,
                                            style = MaterialTheme.typography.labelSmall.copy(color = GoldLight),
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            // Conversation Area
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
                                border = BorderStroke(1.dp, SlateBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    if (aiMessages.isEmpty()) {
                                        Text(
                                            text = "Hello! I am your EstateXo Property AI. I have analyzed the deed, valuation benchmarks, and lifestyle metrics for ${property.title}. Tap a prompt above or type your question below.",
                                            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, lineHeight = 20.sp)
                                        )
                                    } else {
                                        aiMessages.forEach { (text, isAi) ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = if (isAi) Arrangement.Start else Arrangement.End
                                            ) {
                                                Surface(
                                                    shape = RoundedCornerShape(14.dp),
                                                    color = if (isAi) SlateCard else GoldContainer,
                                                    border = BorderStroke(1.dp, if (isAi) SlateBorder else GoldDark),
                                                    modifier = Modifier.widthIn(max = 280.dp)
                                                ) {
                                                    Column(modifier = Modifier.padding(10.dp)) {
                                                        Text(
                                                            text = if (isAi) "EstateXo AI" else "You",
                                                            style = MaterialTheme.typography.labelSmall.copy(
                                                                color = if (isAi) GoldLight else OnGoldContainer,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        )
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Text(
                                                            text = text,
                                                            style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (isAiThinking) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = GoldLight, strokeWidth = 2.dp)
                                            Text(text = "Consulting deed records and market data...", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                                        }
                                    }

                                    // Input row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = aiQuestionInput,
                                            onValueChange = { aiQuestionInput = it },
                                            placeholder = { Text("Ask about title, yield, or comparables...", style = MaterialTheme.typography.bodySmall) },
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedContainerColor = SlateCard,
                                                unfocusedContainerColor = SlateCard,
                                                focusedBorderColor = GoldLight,
                                                unfocusedBorderColor = SlateBorder
                                            ),
                                            modifier = Modifier.weight(1f)
                                        )

                                        IconButton(
                                            onClick = {
                                                if (aiQuestionInput.isNotBlank()) {
                                                    onAskAi(aiQuestionInput)
                                                    aiQuestionInput = ""
                                                }
                                            },
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(GoldPrimary)
                                        ) {
                                            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = ObsidianBlack)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Listing Agent Profile Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateCard),
                    border = BorderStroke(1.dp, SlateBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Licensed Listing Partner",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, letterSpacing = 1.sp)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(GoldContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = property.agentName.split(" ").map { it.take(1) }.joinToString(""),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = GoldLight)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = property.agentName,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                                    )
                                    Icon(imageVector = Icons.Default.Verified, contentDescription = "Verified Agent", tint = SkyAccent, modifier = Modifier.size(16.dp))
                                }
                                Text(text = property.agentCompany, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                                Text(text = "Rating: ${property.agentRating} ★ • Prime Global Specialist", style = MaterialTheme.typography.labelSmall.copy(color = GoldLight))
                            }
                        }
                    }
                }
            }

            // Bottom Spacing
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun SpecColumn(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(imageVector = icon, contentDescription = null, tint = GoldLight, modifier = Modifier.size(20.dp))
        Text(text = value, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 10.sp))
    }
}

@Composable
fun SpecDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(30.dp)
            .background(SlateBorder)
    )
}

@Composable
fun PassportRow(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = TextPrimary))
    }
}

@Composable
fun InvestmentKpiCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SlateCardElevated,
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 10.sp))
            Text(text = value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = color))
        }
    }
}

@Composable
fun ExpenseRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = valueColor))
    }
}
