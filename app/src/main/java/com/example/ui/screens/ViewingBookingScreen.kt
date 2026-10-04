package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppCurrency
import com.example.data.model.Property
import com.example.ui.theme.*

@Composable
fun ViewingBookingScreen(
    property: Property,
    currency: AppCurrency,
    onConfirmBooking: (String, String, Boolean, String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedDate by remember { mutableStateOf("Tomorrow (10:30 AM)") }
    var selectedSlot by remember { mutableStateOf("Morning (10:00 AM - 11:30 AM)") }
    var isVirtualTour by remember { mutableStateOf(false) }
    var specialRequests by remember { mutableStateOf("") }

    val dateOptions = listOf("Tomorrow (10:30 AM)", "In 2 Days", "This Saturday", "Next Monday", "Next Thursday")
    val timeSlots = listOf(
        "Morning (10:00 AM - 11:30 AM)",
        "Afternoon (2:00 PM - 3:30 PM)",
        "Sunset Golden Hour (5:30 PM - 7:00 PM)"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SlateCard)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }

                Column {
                    Text(
                        text = "Schedule Private Viewing",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                    )
                    Text(
                        text = "VIP escorted walkthrough or live 3D virtual tour",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }
            }
        }

        // Property Summary Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
                border = BorderStroke(1.dp, SlateBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = property.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                    Text(text = "${property.city}, ${property.country} • ${currency.format(property.priceUsd)}", style = MaterialTheme.typography.bodySmall.copy(color = GoldLight))
                    Text(text = "Host Partner: ${property.agentName} (${property.agentCompany})", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                }
            }
        }

        // Tour Type Toggle
        item {
            Text(text = "1. Select Viewing Format", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (!isVirtualTour) GoldContainer else SlateCard,
                    border = BorderStroke(1.dp, if (!isVirtualTour) GoldLight else SlateBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isVirtualTour = false }
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(imageVector = Icons.Default.DirectionsCar, contentDescription = null, tint = if (!isVirtualTour) GoldLight else TextSecondary)
                        Text(text = "Private In-Person", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                        Text(text = "VIP chauffeur and on-site walkthrough", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp))
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isVirtualTour) SkyAccent.copy(alpha = 0.2f) else SlateCard,
                    border = BorderStroke(1.dp, if (isVirtualTour) SkyAccent else SlateBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isVirtualTour = true }
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(imageVector = Icons.Default.ViewInAr, contentDescription = null, tint = if (isVirtualTour) SkyAccent else TextSecondary)
                        Text(text = "Live 3D Virtual", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                        Text(text = "Remote high-res guided walkthrough", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp))
                    }
                }
            }
        }

        // Date Selection
        item {
            Text(text = "2. Preferred Date", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                dateOptions.forEach { date ->
                    val isSelected = selectedDate == date
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) GoldLight else SlateCard,
                        border = BorderStroke(1.dp, if (isSelected) GoldDark else SlateBorder),
                        modifier = Modifier.clickable { selectedDate = date }
                    ) {
                        Text(
                            text = date,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) ObsidianBlack else TextPrimary
                            ),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }

        // Time Slot Selection
        item {
            Text(text = "3. Preferred Time Slot", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                timeSlots.forEach { slot ->
                    val isSelected = selectedSlot == slot
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) SlateCardElevated else SlateCard,
                        border = BorderStroke(1.dp, if (isSelected) GoldLight else SlateBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedSlot = slot }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = slot,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (isSelected) GoldLight else TextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                            if (isSelected) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = GoldLight)
                            }
                        }
                    }
                }
            }
        }

        // Special Requests & Escort Notes
        item {
            Text(text = "4. Special Requests & Confidential Notes", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))

            OutlinedTextField(
                value = specialRequests,
                onValueChange = { specialRequests = it },
                placeholder = {
                    Text(
                        text = "e.g., Client requires confidential NDA, helipad arrival clearance, or architectural CAD drawings.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                },
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SlateCard,
                    unfocusedContainerColor = SlateCard,
                    focusedBorderColor = GoldLight,
                    unfocusedBorderColor = SlateBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("booking_special_requests_input")
            )
        }

        // Confirm Button
        item {
            Button(
                onClick = {
                    onConfirmBooking(selectedDate, selectedSlot, isVirtualTour, specialRequests)
                    Toast.makeText(context, "VIP Viewing confirmed! Host partner notified.", Toast.LENGTH_LONG).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = ObsidianBlack),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("confirm_booking_submit_button")
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirm VIP Viewing Appointment", fontWeight = FontWeight.Bold)
            }
        }
    }
}
