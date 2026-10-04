package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
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
fun MapScreen(
    properties: List<Property>,
    currency: AppCurrency,
    onPropertyClick: (String) -> Unit,
    onSwitchToList: () -> Unit
) {
    var selectedPropertyId by remember { mutableStateOf<String?>(properties.firstOrNull()?.id) }
    var selectedCityFilter by remember { mutableStateOf("All") }

    val cities = listOf("All", "Dubai", "New York", "London", "Tokyo", "Paris", "Zurich")

    val visibleProperties = remember(selectedCityFilter, properties) {
        if (selectedCityFilter == "All") properties
        else properties.filter { it.city.equals(selectedCityFilter, ignoreCase = true) }
    }

    val selectedProperty = properties.find { it.id == selectedPropertyId }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
    ) {
        // Stylized Interactive Global Map Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        // Deselect or tap background
                    }
                }
        ) {
            val w = size.width
            val h = size.height

            // Latitude and Longitude Grid Lines
            val gridColor = SlateBorder.copy(alpha = 0.25f)
            for (i in 1..8) {
                val y = h * (i / 9f)
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 1f
                )
            }
            for (j in 1..6) {
                val x = w * (j / 7f)
                drawLine(
                    color = gridColor,
                    start = Offset(x, 0f),
                    end = Offset(x, h),
                    strokeWidth = 1f
                )
            }

            // Stylized World Continents outline representation
            val landColor = SlateCard.copy(alpha = 0.5f)
            val strokeColor = SlateBorder.copy(alpha = 0.6f)

            // North America outline
            val pathNA = Path().apply {
                moveTo(w * 0.12f, h * 0.22f)
                lineTo(w * 0.32f, h * 0.20f)
                lineTo(w * 0.28f, h * 0.38f)
                lineTo(w * 0.20f, h * 0.44f)
                lineTo(w * 0.14f, h * 0.32f)
                close()
            }
            drawPath(pathNA, landColor)
            drawPath(pathNA, strokeColor, style = Stroke(width = 1.5f))

            // Europe outline
            val pathEU = Path().apply {
                moveTo(w * 0.44f, h * 0.24f)
                lineTo(w * 0.56f, h * 0.22f)
                lineTo(w * 0.54f, h * 0.34f)
                lineTo(w * 0.45f, h * 0.35f)
                close()
            }
            drawPath(pathEU, landColor)
            drawPath(pathEU, strokeColor, style = Stroke(width = 1.5f))

            // Middle East & Asia outline
            val pathAsia = Path().apply {
                moveTo(w * 0.55f, h * 0.32f)
                lineTo(w * 0.85f, h * 0.24f)
                lineTo(w * 0.88f, h * 0.45f)
                lineTo(w * 0.70f, h * 0.50f)
                lineTo(w * 0.58f, h * 0.44f)
                close()
            }
            drawPath(pathAsia, landColor)
            drawPath(pathAsia, strokeColor, style = Stroke(width = 1.5f))
        }

        // Custom Property Map Pins
        // Pin layout positions distributed on world map representation
        val pinPositions = mapOf(
            "prop_dubai_1" to Offset(0.60f, 0.43f),
            "prop_ny_3" to Offset(0.24f, 0.32f),
            "prop_london_2" to Offset(0.47f, 0.26f),
            "prop_tokyo_4" to Offset(0.84f, 0.36f),
            "prop_paris_5" to Offset(0.48f, 0.31f),
            "prop_zurich_6" to Offset(0.50f, 0.33f)
        )

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val widthPx = maxWidth.value
            val heightPx = maxHeight.value

            visibleProperties.forEach { property ->
                val normPos = pinPositions[property.id] ?: Offset(0.5f, 0.5f)
                val xPos = (normPos.x * widthPx).dp
                val yPos = (normPos.y * heightPx).dp

                val isSelected = property.id == selectedPropertyId

                Box(
                    modifier = Modifier
                        .offset(x = xPos - 35.dp, y = yPos - 30.dp)
                        .clickable { selectedPropertyId = property.id }
                        .testTag("map_pin_${property.id}")
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) GoldLight else SlateCardElevated,
                            border = BorderStroke(1.5.dp, if (isSelected) ObsidianBlack else GoldPrimary),
                            shadowElevation = if (isSelected) 8.dp else 4.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = if (isSelected) ObsidianBlack else GoldLight,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = currency.format(property.priceUsd).replace(" ", ""),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp,
                                        color = if (isSelected) ObsidianBlack else TextPrimary
                                    )
                                )
                            }
                        }

                        // Pin stem
                        Box(
                            modifier = Modifier
                                .size(width = 2.dp, height = 8.dp)
                                .background(if (isSelected) GoldLight else GoldPrimary)
                        )
                    }
                }
            }
        }

        // Top Floating Bar (City filter & list switcher)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SurfaceGlass,
                    border = BorderStroke(1.dp, SlateBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Public, contentDescription = null, tint = GoldLight, modifier = Modifier.size(16.dp))
                        Text(
                            text = "Global Radar View",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }
                }

                FilledTonalButton(
                    onClick = onSwitchToList,
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = SlateCardElevated, contentColor = TextPrimary),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("map_switch_to_list_button")
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ViewList, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("List View", style = MaterialTheme.typography.labelMedium)
                }
            }

            // City chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                cities.forEach { city ->
                    val isCitySelected = selectedCityFilter == city
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isCitySelected) GoldLight else SurfaceGlass,
                        border = BorderStroke(1.dp, if (isCitySelected) GoldDark else SlateBorder),
                        modifier = Modifier.clickable { selectedCityFilter = city }
                    ) {
                        Text(
                            text = city,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isCitySelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isCitySelected) ObsidianBlack else TextPrimary
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Bottom Selected Property Preview Card
        AnimatedVisibility(
            visible = selectedProperty != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .navigationBarsPadding()
        ) {
            selectedProperty?.let { prop ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPropertyClick(prop.id) }
                        .testTag("map_bottom_preview_card")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = prop.imageResId ?: R.drawable.img_hero_villa,
                            contentDescription = prop.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(14.dp))
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = currency.format(prop.priceUsd),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = EmeraldContainer
                                ) {
                                    Text(
                                        text = "${prop.trustScore} Trust",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = EmeraldLight,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = prop.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                ),
                                maxLines = 1
                            )
                            Text(
                                text = "${prop.city}, ${prop.country} • ${prop.bedrooms} Beds • ${prop.areaSqFt} sqft",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                                maxLines = 1
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Tap to explore full passport & 3D tour",
                                    style = MaterialTheme.typography.labelSmall.copy(color = GoldLight)
                                )
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = GoldLight, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
