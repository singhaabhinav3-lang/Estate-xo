package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppCurrency
import com.example.data.model.Property
import com.example.data.model.PropertyType
import com.example.ui.components.PropertyCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.AiMatchmakerForm

@Composable
fun AiMatchmakerScreen(
    form: AiMatchmakerForm,
    currency: AppCurrency,
    savedIds: Set<String>,
    comparedIds: List<String>,
    onFormUpdate: (AiMatchmakerForm.() -> AiMatchmakerForm) -> Unit,
    onRunMatchmaker: () -> Unit,
    onPropertyClick: (String) -> Unit,
    onSaveClick: (String) -> Unit,
    onCompareClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val destinations = listOf("Dubai, UAE", "New York, USA", "London, UK", "Tokyo, Japan", "Paris, France", "Zurich, Switzerland")
    val lifestyles = listOf("Beachfront & Waterfront", "Skyline Penthouse Living", "Historic Diplomatic Enclave", "Alpine Lake Sanctuary")
    val goals = listOf("High Rental Yield & Cash Flow", "Long-Term Capital Preservation", "Primary Family Trophy Estate")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Row(
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
                        text = "AI Property Matchmaker",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Algorithmically match requirements to global verified deeds",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }
            }
        }

        // Questionnaire Card
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Question 1: Budget
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "1. Target Acquisition Budget",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                            )
                            Text(
                                text = currency.format(form.budgetUsd),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = GoldLight)
                            )
                        }
                        Slider(
                            value = form.budgetUsd.toFloat(),
                            onValueChange = { onFormUpdate { copy(budgetUsd = it.toDouble()) } },
                            valueRange = 5000000f..35000000f,
                            colors = SliderDefaults.colors(thumbColor = GoldLight, activeTrackColor = GoldPrimary)
                        )
                    }

                    HorizontalDivider(color = SlateBorder.copy(alpha = 0.5f))

                    // Question 2: Destination Hub
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "2. Preferred Global Destination",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            destinations.forEach { dest ->
                                val isSelected = form.destination == dest
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isSelected) GoldLight else SlateCard,
                                    border = BorderStroke(1.dp, if (isSelected) GoldDark else SlateBorder),
                                    modifier = Modifier.clickable { onFormUpdate { copy(destination = dest) } }
                                ) {
                                    Text(
                                        text = dest,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) ObsidianBlack else TextPrimary
                                        ),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = SlateBorder.copy(alpha = 0.5f))

                    // Question 3: Property Type
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "3. Property Classification",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PropertyType.values().forEach { pType ->
                                val isSelected = form.propertyType == pType
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isSelected) GoldLight else SlateCard,
                                    border = BorderStroke(1.dp, if (isSelected) GoldDark else SlateBorder),
                                    modifier = Modifier.clickable { onFormUpdate { copy(propertyType = pType) } }
                                ) {
                                    Text(
                                        text = pType.label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) ObsidianBlack else TextPrimary
                                        ),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = SlateBorder.copy(alpha = 0.5f))

                    // Question 4: Lifestyle
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "4. Desired Lifestyle Vibe",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                        )
                        lifestyles.forEach { style ->
                            val isSelected = form.lifestyle == style
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) GoldContainer else SlateCard,
                                border = BorderStroke(1.dp, if (isSelected) GoldLight else SlateBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onFormUpdate { copy(lifestyle = style) } }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = style,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = if (isSelected) GoldLight else TextPrimary,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                    if (isSelected) {
                                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = GoldLight, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Run AI Match Button
                    Button(
                        onClick = onRunMatchmaker,
                        enabled = !form.isAnalyzing,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = ObsidianBlack),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("run_ai_matchmaker_button")
                    ) {
                        if (form.isAnalyzing) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = ObsidianBlack, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Analyzing Global Market Data...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Compute AI Matches", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Matchmaker Results
        if (form.matchedProperties.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Curated AI Recommendations",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                    )

                    // AI Rationale Card
                    if (form.aiRationale.isNotEmpty()) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
                            border = BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(imageVector = Icons.Default.Insights, contentDescription = null, tint = EmeraldLight)
                                    Text(
                                        text = "AI Match Rationale",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                                    )
                                }
                                Text(
                                    text = form.aiRationale,
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, lineHeight = 20.sp)
                                )
                            }
                        }
                    }
                }
            }

            items(form.matchedProperties) { (prop, score) ->
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldContainer
                        ) {
                            Text(
                                text = "$score% AI MATCH SCORE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldLight
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Text(
                            text = "Trust: ${prop.trustScore}/100",
                            style = MaterialTheme.typography.labelSmall.copy(color = GoldLight)
                        )
                    }

                    PropertyCard(
                        property = prop,
                        currency = currency,
                        isSaved = savedIds.contains(prop.id),
                        isCompared = comparedIds.contains(prop.id),
                        onCardClick = { onPropertyClick(prop.id) },
                        onSaveClick = { onSaveClick(prop.id) },
                        onCompareClick = { onCompareClick(prop.id) }
                    )
                }
            }
        }
    }
}
