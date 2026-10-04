package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.AppCurrency
import com.example.data.model.Property
import com.example.ui.components.PropertyCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.Screen

@Composable
fun HomeScreen(
    properties: List<Property>,
    savedIds: Set<String>,
    comparedIds: List<String>,
    currency: AppCurrency,
    onPropertyClick: (String) -> Unit,
    onSaveClick: (String) -> Unit,
    onCompareClick: (String) -> Unit,
    onNavigate: (Screen) -> Unit,
    onSearchDestination: (String) -> Unit
) {
    val destinations = listOf("All", "Dubai", "New York", "London", "Tokyo", "Paris", "Zurich")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Hero Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                AsyncImage(
                    model = R.drawable.img_hero_villa,
                    contentDescription = "EstateXo Hero Luxury Estate",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dramatic Dark Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.4f),
                                    ObsidianBlack.copy(alpha = 0.85f),
                                    ObsidianBlack
                                )
                            )
                        )
                )

                // Hero Content
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = GoldContainer,
                        border = BorderStroke(1.dp, GoldDark)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = null,
                                tint = GoldLight,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "GLOBAL TROPHY ASSETS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = GoldLight
                                )
                            )
                        }
                    }

                    Text(
                        text = "Where Global Luxury\nFinds Its Home",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 30.sp,
                            lineHeight = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )

                    Text(
                        text = "Discover verified prime real-estate in Dubai, London, New York, Tokyo and beyond.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary
                        )
                    )
                }
            }
        }

        // Quick Global Hub Selector
        item {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "WHERE DO YOU WANT TO LIVE?",
                    style = MaterialTheme.typography.labelLarge.copy(
                        letterSpacing = 1.5.sp,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    destinations.forEach { dest ->
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = SlateCard,
                            border = BorderStroke(1.dp, SlateBorder),
                            modifier = Modifier
                                .clickable {
                                    onSearchDestination(dest)
                                    onNavigate(Screen.Explore)
                                }
                                .testTag("hub_chip_$dest")
                        ) {
                            Text(
                                text = dest,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }
        }

        // AI Matchmaker Promo Banner
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clickable { onNavigate(Screen.AiMatchmaker) }
                    .testTag("home_ai_matchmaker_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(GoldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Match",
                            tint = GoldLight,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AI Property Matchmaker",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Answer 6 questions about budget, lifestyle, and yields for curated matches.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Proceed",
                        tint = GoldLight,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Featured Luxury Listings Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Featured Trophy Properties",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Hand-curated prime residences with verified passports",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }

                TextButton(
                    onClick = { onNavigate(Screen.Explore) },
                    modifier = Modifier.testTag("see_all_featured_button")
                ) {
                    Text(text = "Explore All", color = GoldLight)
                }
            }
        }

        // Featured Listings Carousel
        item {
            val featured = properties.filter { it.isFeatured }
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(featured) { property ->
                    PropertyCard(
                        property = property,
                        currency = currency,
                        isSaved = savedIds.contains(property.id),
                        isCompared = comparedIds.contains(property.id),
                        onCardClick = { onPropertyClick(property.id) },
                        onSaveClick = { onSaveClick(property.id) },
                        onCompareClick = { onCompareClick(property.id) },
                        modifier = Modifier.width(320.dp)
                    )
                }
            }
        }

        // Market Intelligence Strip
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "GLOBAL MARKET INTELLIGENCE",
                    style = MaterialTheme.typography.labelLarge.copy(
                        letterSpacing = 1.5.sp,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MarketIndexCard("Dubai Prime", "+8.4% YoY", "Avg $1,650/sqft", EmeraldSuccess)
                    MarketIndexCard("New York Ultra", "+5.2% YoY", "Avg $2,420/sqft", EmeraldSuccess)
                    MarketIndexCard("Tokyo Diplomatic", "+7.1% YoY", "Avg $1,890/sqft", EmeraldSuccess)
                    MarketIndexCard("London Kensington", "+4.8% YoY", "Avg £2,150/sqft", SkyAccent)
                    MarketIndexCard("Zurich Gold Coast", "+6.2% YoY", "Avg 22,000 CHF/sqm", EmeraldSuccess)
                }
            }
        }

        // High-Yield Investment Section
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "High-Yield Investments",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Properties offering >5.5% estimated net rental yields",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }

                IconButton(onClick = { onNavigate(Screen.Explore) }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "View", tint = GoldLight)
                }
            }
        }

        // Investment Properties list
        items(properties.filter { it.investmentMetrics.calculateNetYield(it.priceUsd) >= 5.0 }.take(3)) { property ->
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                PropertyCard(
                    property = property,
                    currency = currency,
                    isSaved = savedIds.contains(property.id),
                    isCompared = comparedIds.contains(property.id),
                    onCardClick = { onPropertyClick(property.id) },
                    onSaveClick = { onSaveClick(property.id) },
                    onCompareClick = { onCompareClick(property.id) }
                )
            }
        }

        // Verified Agents Spotlight
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Verified Elite Partner Agents",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    AgentSpotlightCard("Julian Montgomery", "Knight Frank Dubai", "4.98 ★", "24 Closed ($140M+)")
                    AgentSpotlightCard("Eleanor Vance-Sterling", "Sotheby's NY", "4.99 ★", "31 Closed ($280M+)")
                    AgentSpotlightCard("Kenji Takahashi", "Mitsui Tokyo", "4.97 ★", "18 Closed ($95M+)")
                    AgentSpotlightCard("Lord Charles Pembroke", "Savills London", "4.96 ★", "15 Closed ($210M+)")
                }
            }
        }
    }
}

@Composable
fun MarketIndexCard(city: String, growth: String, priceSqft: String, trendColor: Color) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SlateCard,
        border = BorderStroke(1.dp, SlateBorder),
        modifier = Modifier.width(180.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = city,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = trendColor,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = growth,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = trendColor
                    )
                )
            }
            Text(
                text = priceSqft,
                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
            )
        }
    }
}

@Composable
fun AgentSpotlightCard(name: String, agency: String, rating: String, trackRecord: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SlateCard,
        border = BorderStroke(1.dp, SlateBorder),
        modifier = Modifier.width(220.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(GoldContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name.split(" ").map { it.take(1) }.joinToString(""),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                    )
                }

                Column {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = agency,
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            HorizontalDivider(color = SlateBorder.copy(alpha = 0.5f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = rating,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GoldLight
                    )
                )
                Text(
                    text = trackRecord,
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                )
            }
        }
    }
}
