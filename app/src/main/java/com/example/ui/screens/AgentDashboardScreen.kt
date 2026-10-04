package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.Screen

enum class CrmTab(val label: String) {
    OVERVIEW("Overview"),
    LEADS("Leads Pipeline"),
    APPOINTMENTS("Viewings"),
    MY_PROPERTIES("My Listings")
}

@Composable
fun AgentDashboardScreen(
    properties: List<Property>,
    leads: List<Lead>,
    appointments: List<ViewingAppointment>,
    currency: AppCurrency,
    onLeadStageUpdate: (String, LeadStage) -> Unit,
    onAppointmentStatusUpdate: (String, ViewingStatus) -> Unit,
    onPropertyClick: (String) -> Unit,
    onNavigate: (Screen) -> Unit
) {
    var selectedTab by remember { mutableStateOf(CrmTab.OVERVIEW) }
    var leadFilterStage by remember { mutableStateOf<LeadStage?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Partner CRM & Dashboard",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                    )
                    Text(
                        text = "Global listing performance, high-net-worth leads & viewings",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }

                Button(
                    onClick = { onNavigate(Screen.AiListingCreator) },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = ObsidianBlack),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("agent_new_listing_ai_button")
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("AI Creator", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Navigation Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CrmTab.values().forEach { tab ->
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

        // Tab Content
        when (selectedTab) {
            CrmTab.OVERVIEW -> {
                // KPI Metric Cards
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CrmMetricCard("Active Listings", "${properties.size}", EmeraldLight, Modifier.weight(1f))
                        CrmMetricCard("HNW Leads", "${leads.size}", GoldLight, Modifier.weight(1f))
                        CrmMetricCard("Viewings", "${appointments.size}", SkyAccent, Modifier.weight(1f))
                    }
                }

                // AI Creator Promo inside Overview
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate(Screen.AiListingCreator) }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(GoldContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = GoldLight)
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Launch New Listing with AI",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                                )
                                Text(
                                    text = "Auto-generate luxury titles, editorial copy, highlights, and social posts.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                )
                            }

                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = GoldLight)
                        }
                    }
                }

                // Recent Leads Section
                item {
                    Text(
                        text = "Recent High-Net-Worth Inquiries",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                    )
                }

                items(leads.take(2)) { lead ->
                    LeadCard(lead = lead, currency = currency, onUpdateStage = onLeadStageUpdate)
                }

                // Upcoming Appointments
                item {
                    Text(
                        text = "Upcoming Scheduled Viewings",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                    )
                }

                items(appointments.take(2)) { app ->
                    AppointmentCard(app = app, onStatusUpdate = onAppointmentStatusUpdate)
                }
            }

            CrmTab.LEADS -> {
                // Stage Filter Pills
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (leadFilterStage == null) GoldLight else SlateCard,
                            modifier = Modifier.clickable { leadFilterStage = null }
                        ) {
                            Text(
                                text = "All Leads (${leads.size})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (leadFilterStage == null) ObsidianBlack else TextPrimary,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        LeadStage.values().forEach { stage ->
                            val isSelected = leadFilterStage == stage
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) GoldLight else SlateCard,
                                modifier = Modifier.clickable { leadFilterStage = stage }
                            ) {
                                Text(
                                    text = stage.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) ObsidianBlack else TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                val filteredLeads = if (leadFilterStage == null) leads else leads.filter { it.stage == leadFilterStage }

                items(filteredLeads) { lead ->
                    LeadCard(lead = lead, currency = currency, onUpdateStage = onLeadStageUpdate)
                }
            }

            CrmTab.APPOINTMENTS -> {
                items(appointments) { app ->
                    AppointmentCard(app = app, onStatusUpdate = onAppointmentStatusUpdate)
                }
            }

            CrmTab.MY_PROPERTIES -> {
                items(properties) { prop ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
                        border = BorderStroke(1.dp, SlateBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPropertyClick(prop.id) }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = prop.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                                Text(text = "${prop.city}, ${prop.country} • ${currency.format(prop.priceUsd)}", style = MaterialTheme.typography.bodySmall.copy(color = GoldLight))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Passport: ${prop.passport.titleDeedNumber} • Trust: ${prop.trustScore}/100", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                            }
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = GoldLight)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CrmMetricCard(title: String, value: String, accentColor: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SlateCardElevated,
        border = BorderStroke(1.dp, SlateBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 10.sp))
            Text(text = value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = accentColor))
        }
    }
}

@Composable
fun LeadCard(lead: Lead, currency: AppCurrency, onUpdateStage: (String, LeadStage) -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
        border = BorderStroke(1.dp, SlateBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = lead.clientName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (lead.stage) {
                        LeadStage.NEW_INQUIRY -> SkyAccent.copy(alpha = 0.2f)
                        LeadStage.VIEWING_SET -> IndigoAccent.copy(alpha = 0.2f)
                        LeadStage.OFFER_RECEIVED -> GoldContainer
                        LeadStage.CLOSED -> EmeraldContainer
                        else -> SlateCard
                    }
                ) {
                    Text(
                        text = lead.stage.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = when (lead.stage) {
                                LeadStage.NEW_INQUIRY -> SkyAccent
                                LeadStage.VIEWING_SET -> IndigoAccent
                                LeadStage.OFFER_RECEIVED -> GoldLight
                                LeadStage.CLOSED -> EmeraldLight
                                else -> TextPrimary
                            }
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Text(text = "Target: ${lead.propertyTitle} • Budget: ${currency.format(lead.budgetUsd)}", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
            Text(text = lead.notes, style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary))

            HorizontalDivider(color = SlateBorder.copy(alpha = 0.5f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Updated ${lead.lastContactDate}", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 10.sp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (lead.stage != LeadStage.CLOSED) {
                        TextButton(
                            onClick = { onUpdateStage(lead.id, LeadStage.CLOSED) },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Mark Closed", color = EmeraldLight, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentCard(app: ViewingAppointment, onStatusUpdate: (String, ViewingStatus) -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
        border = BorderStroke(1.dp, SlateBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = app.propertyTitle, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (app.status == ViewingStatus.CONFIRMED) EmeraldContainer else SlateCard
                ) {
                    Text(
                        text = app.status.label,
                        style = MaterialTheme.typography.labelSmall.copy(color = if (app.status == ViewingStatus.CONFIRMED) EmeraldLight else TextSecondary),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Text(text = "Client: ${app.buyerName} (${app.buyerPhone})", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
            Text(text = "Time: ${app.date} • ${app.timeSlot}", style = MaterialTheme.typography.bodySmall.copy(color = GoldLight, fontWeight = FontWeight.SemiBold))

            if (app.notes.isNotEmpty()) {
                Text(text = "Notes: ${app.notes}", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
            }
        }
    }
}
