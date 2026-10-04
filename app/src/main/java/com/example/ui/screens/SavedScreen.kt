package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppCurrency
import com.example.data.model.Property
import com.example.data.model.ViewingAppointment
import com.example.ui.components.PropertyCard
import com.example.ui.theme.*

enum class SavedTab(val label: String) {
    FAVORITES("Saved Properties"),
    VIEWINGS("My Appointments"),
    RADAR_ALERTS("Opportunity Radar")
}

@Composable
fun SavedScreen(
    savedProperties: List<Property>,
    comparedIds: List<String>,
    appointments: List<ViewingAppointment>,
    currency: AppCurrency,
    onPropertyClick: (String) -> Unit,
    onSaveToggle: (String) -> Unit,
    onCompareToggle: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(SavedTab.FAVORITES) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Buyer Portfolio & Alerts",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                )
                Text(
                    text = "Track saved properties, appointments & market opportunity radar",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
            }
        }

        // Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SavedTab.values().forEach { tab ->
                    val isSelected = selectedTab == tab
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) GoldLight else SlateCard,
                        border = BorderStroke(1.dp, if (isSelected) GoldDark else SlateBorder),
                        modifier = Modifier.clickable { selectedTab = tab }
                    ) {
                        Text(
                            text = tab.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) ObsidianBlack else TextPrimary
                            ),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        when (selectedTab) {
            SavedTab.FAVORITES -> {
                if (savedProperties.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(44.dp))
                                Text(text = "No Saved Properties Yet", style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary))
                                Text(text = "Heart properties during exploration to track price changes.", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                            }
                        }
                    }
                } else {
                    items(savedProperties) { property ->
                        PropertyCard(
                            property = property,
                            currency = currency,
                            isSaved = true,
                            isCompared = comparedIds.contains(property.id),
                            onCardClick = { onPropertyClick(property.id) },
                            onSaveClick = { onSaveToggle(property.id) },
                            onCompareClick = { onCompareToggle(property.id) }
                        )
                    }
                }
            }

            SavedTab.VIEWINGS -> {
                items(appointments) { app ->
                    AppointmentCard(app = app, onStatusUpdate = { _, _ -> })
                }
            }

            SavedTab.RADAR_ALERTS -> {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        RadarAlertCard(
                            title = "Price Drop Alert: Kensington Mansion",
                            detail = "Listing reduced by $1.2M (-4.1%) to align with 2026 Q3 prime London registry average.",
                            timestamp = "2 hours ago",
                            badge = "PRICE DROP",
                            badgeColor = EmeraldLight
                        )
                        RadarAlertCard(
                            title = "New Listing Matching Your Criteria",
                            detail = "6-Bedroom Palm Jumeirah beachfront villa newly added with private berth at $14.8M.",
                            timestamp = "Yesterday",
                            badge = "NEW MATCH",
                            badgeColor = GoldLight
                        )
                        RadarAlertCard(
                            title = "High-Yield Opportunity Radar",
                            detail = "Tokyo Minato Sanctuary updated rental forecast: Net projected yield increased to 5.8%.",
                            timestamp = "2 days ago",
                            badge = "YIELD SURGE",
                            badgeColor = SkyAccent
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RadarAlertCard(title: String, detail: String, timestamp: String, badge: String, badgeColor: Color) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SlateCardElevated,
        border = BorderStroke(1.dp, SlateBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(shape = RoundedCornerShape(6.dp), color = badgeColor.copy(alpha = 0.2f)) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = badgeColor, fontSize = 9.sp),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(text = timestamp, style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 10.sp))
            }

            Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
            Text(text = detail, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, lineHeight = 18.sp))
        }
    }
}
