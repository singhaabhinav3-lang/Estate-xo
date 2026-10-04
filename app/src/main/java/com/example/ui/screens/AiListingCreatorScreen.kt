package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ai.ListingCopyResult
import com.example.data.model.AppCurrency
import com.example.data.model.PropertyType
import com.example.ui.theme.*

@Composable
fun AiListingCreatorScreen(
    currency: AppCurrency,
    onGenerate: (PropertyType, String, Double, Int, Int, Int, String, (ListingCopyResult) -> Unit) -> Unit,
    onPublish: (String, String, String, Double, PropertyType, String, String, Int, Int, Int, List<String>, List<String>) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedType by remember { mutableStateOf(PropertyType.PENTHOUSE) }
    var city by remember { mutableStateOf("Dubai") }
    var country by remember { mutableStateOf("United Arab Emirates") }
    var priceText by remember { mutableStateOf("18500000") }
    var bedrooms by remember { mutableStateOf("5") }
    var bathrooms by remember { mutableStateOf("6") }
    var sqft by remember { mutableStateOf("8200") }
    var keyFeatures by remember { mutableStateOf("Private rooftop infinity pool, 360 skyline views, biometric smart home, Italian marble finish, private superyacht berth.") }

    var isGenerating by remember { mutableStateOf(false) }
    var generatedResult by remember { mutableStateOf<ListingCopyResult?>(null) }

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
                        text = "AI Listing Creator",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                    )
                    Text(
                        text = "Transform architectural specs into Sotheby's-grade copy",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }
            }
        }

        // Input Form Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
                border = BorderStroke(1.dp, SlateBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Property Specifications",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                    )

                    // Property type selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(PropertyType.PENTHOUSE, PropertyType.VILLA, PropertyType.ESTATE).forEach { pType ->
                            val isSelected = selectedType == pType
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) GoldLight else SlateCard,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedType = pType }
                            ) {
                                Text(
                                    text = pType.label.split(" ")[0],
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) ObsidianBlack else TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp)
                                )
                            }
                        }
                    }

                    // Location fields
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("City") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = country,
                            onValueChange = { country = it },
                            label = { Text("Country") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Price & Sqft
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = priceText,
                            onValueChange = { priceText = it },
                            label = { Text("Asking Price (USD)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = sqft,
                            onValueChange = { sqft = it },
                            label = { Text("Area (Sq. Ft.)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Beds & Baths
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = bedrooms,
                            onValueChange = { bedrooms = it },
                            label = { Text("Bedrooms") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = bathrooms,
                            onValueChange = { bathrooms = it },
                            label = { Text("Bathrooms") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Key Architectural Features
                    OutlinedTextField(
                        value = keyFeatures,
                        onValueChange = { keyFeatures = it },
                        label = { Text("Key Finishes & Amenities") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Generate Button
                    Button(
                        onClick = {
                            isGenerating = true
                            val priceVal = priceText.toDoubleOrNull() ?: 15000000.0
                            val bedsVal = bedrooms.toIntOrNull() ?: 4
                            val bathsVal = bathrooms.toIntOrNull() ?: 5
                            val sqftVal = sqft.toIntOrNull() ?: 6500

                            onGenerate(selectedType, "$city, $country", priceVal, bedsVal, bathsVal, sqftVal, keyFeatures) { result ->
                                generatedResult = result
                                isGenerating = false
                            }
                        },
                        enabled = !isGenerating,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = ObsidianBlack),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("ai_generate_listing_button")
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = ObsidianBlack, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Composing Luxury Copy with AI...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate Luxury Listing Copy", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Generated Results Display
        generatedResult?.let { result ->
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
                    border = BorderStroke(1.dp, GoldLight.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldLight)
                            Text(text = "AI Generated Editorial Copy", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                        }

                        // Generated Title
                        Text(text = "TITLE", style = MaterialTheme.typography.labelSmall.copy(color = GoldLight, letterSpacing = 1.sp))
                        Text(text = result.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))

                        // Tagline
                        Text(text = "TAGLINE", style = MaterialTheme.typography.labelSmall.copy(color = GoldLight, letterSpacing = 1.sp))
                        Text(text = result.tagline, style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic))

                        // Description
                        Text(text = "DESCRIPTION", style = MaterialTheme.typography.labelSmall.copy(color = GoldLight, letterSpacing = 1.sp))
                        Text(text = result.description, style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary, lineHeight = 22.sp))

                        // Highlights
                        Text(text = "HIGHLIGHTS", style = MaterialTheme.typography.labelSmall.copy(color = GoldLight, letterSpacing = 1.sp))
                        result.highlights.forEach { h ->
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(text = "•", color = GoldLight)
                                Text(text = h, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                            }
                        }

                        // Social Media Copy
                        Text(text = "SOCIAL MEDIA CAPTION", style = MaterialTheme.typography.labelSmall.copy(color = GoldLight, letterSpacing = 1.sp))
                        Surface(shape = RoundedCornerShape(10.dp), color = SlateCard) {
                            Text(
                                text = result.socialCopy,
                                style = MaterialTheme.typography.bodySmall.copy(color = SkyAccent),
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Publish Button
                        Button(
                            onClick = {
                                val priceVal = priceText.toDoubleOrNull() ?: 15000000.0
                                val bedsVal = bedrooms.toIntOrNull() ?: 4
                                val bathsVal = bathrooms.toIntOrNull() ?: 5
                                val sqftVal = sqft.toIntOrNull() ?: 6500

                                onPublish(
                                    result.title,
                                    result.tagline,
                                    result.description,
                                    priceVal,
                                    selectedType,
                                    city,
                                    country,
                                    bedsVal,
                                    bathsVal,
                                    sqftVal,
                                    listOf("Infinity Pool", "Smart Home", "Private Elevator", "Concierge"),
                                    result.highlights
                                )
                                Toast.makeText(context, "Listing published to EstateXo global marketplace!", Toast.LENGTH_LONG).show()
                                onBack()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess, contentColor = ObsidianBlack),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("publish_listing_button")
                        ) {
                            Icon(imageVector = Icons.Default.Public, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Publish to Global Marketplace", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
