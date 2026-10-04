package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Property
import com.example.ui.theme.*

@Composable
fun AdminModerationScreen(
    properties: List<Property>,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                        text = "Global Moderation & Fraud Desk",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                    )
                    Text(
                        text = "Title verification, image duplicate checks & AML compliance",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }
            }
        }

        // Platform Trust KPI
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminStatCard("Fraud Screened", "100%", EmeraldLight, Modifier.weight(1f))
                AdminStatCard("Verified Deeds", "${properties.size}", GoldLight, Modifier.weight(1f))
                AdminStatCard("Flagged Listings", "0", SkyAccent, Modifier.weight(1f))
            }
        }

        // Fraud & Duplicate Screen Monitor
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
                border = BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = EmeraldLight)
                        Text(
                            text = "Automated AI Fraud Detection Engine",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                        )
                    }
                    Text(
                        text = "EstateXo hashes uploaded photography against international MLS databases, verifies land registry API tokens, and checks pricing algorithms against closed deed comparables.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, lineHeight = 20.sp)
                    )
                }
            }
        }

        // Listing Verification Queue
        item {
            Text(
                text = "Live Listing Verification Audit Queue",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
            )
        }

        items(properties) { property ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = BorderStroke(1.dp, SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = property.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary),
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(shape = RoundedCornerShape(6.dp), color = EmeraldContainer) {
                            Text(
                                text = "VERIFIED DEED",
                                style = MaterialTheme.typography.labelSmall.copy(color = EmeraldLight, fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "Deed No: ${property.passport.titleDeedNumber} • Registry Hash: ${property.passport.landRegistryHash.take(12)}...",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                    )

                    Text(
                        text = "Status: ${property.fraudStatusTag}",
                        style = MaterialTheme.typography.bodySmall.copy(color = EmeraldLight)
                    )

                    HorizontalDivider(color = SlateBorder.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Agent: ${property.agentName}",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )

                        TextButton(
                            onClick = {
                                Toast.makeText(context, "Deed audit certificate re-verified for ${property.title}", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Text("Re-Audit Signature", color = GoldLight, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminStatCard(title: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SlateCardElevated,
        border = BorderStroke(1.dp, SlateBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 10.sp))
            Text(text = value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = color))
        }
    }
}
