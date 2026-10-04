package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.AppCurrency
import com.example.data.model.Property
import com.example.ui.theme.*

@Composable
fun CompareScreen(
    properties: List<Property>,
    currency: AppCurrency,
    onRemoveProperty: (String) -> Unit,
    onPropertyClick: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
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
                    text = "Side-by-Side Property Comparison",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                )
                Text(
                    text = "${properties.size} of 4 properties compared",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
            }
        }

        if (properties.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.CompareArrows, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(54.dp))
                    Text(
                        text = "No Properties Selected for Comparison",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                    )
                    Text(
                        text = "Tap the compare icon on any property card to analyze features side-by-side.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )
                }
            }
        } else {
            // Horizontal scrollable comparison matrix
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        properties.forEach { property ->
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
                                border = BorderStroke(1.dp, SlateBorder),
                                modifier = Modifier
                                    .width(260.dp)
                                    .testTag("compare_column_${property.id}")
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    // Top image & remove button
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(130.dp)
                                    ) {
                                        AsyncImage(
                                            model = property.imageResId ?: R.drawable.img_hero_villa,
                                            contentDescription = property.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(12.dp))
                                        )

                                        IconButton(
                                            onClick = { onRemoveProperty(property.id) },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(6.dp)
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(ObsidianBlack.copy(alpha = 0.7f))
                                        ) {
                                            Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", tint = TextPrimary, modifier = Modifier.size(16.dp))
                                        }
                                    }

                                    Text(
                                        text = property.title,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary),
                                        maxLines = 1
                                    )

                                    Text(
                                        text = currency.format(property.priceUsd),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = GoldLight)
                                    )

                                    HorizontalDivider(color = SlateBorder.copy(alpha = 0.5f))

                                    // Metric Rows
                                    MatrixRow("Location", "${property.city}, ${property.country}")
                                    MatrixRow("Price / sqft", "${currency.format(property.pricePerSqFtUsd)}")
                                    MatrixRow("Beds / Baths", "${property.bedrooms} Beds • ${property.bathrooms} Baths")
                                    MatrixRow("Area", "${property.areaSqFt} sqft (${property.areaSqM} m²)")
                                    MatrixRow("Net Yield", "${String.format("%.2f", property.investmentMetrics.calculateNetYield(property.priceUsd))}%")
                                    MatrixRow("Trust Score", "${property.trustScore}/100 Grade A+")
                                    MatrixRow("Energy Rating", property.passport.energyRating)
                                    MatrixRow("Escrow Protection", "Secured (${property.passport.escrowAccountId})")

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Button(
                                        onClick = { onPropertyClick(property.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = ObsidianBlack),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("View Property", fontWeight = FontWeight.Bold)
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

@Composable
fun MatrixRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 10.sp))
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold))
    }
}
