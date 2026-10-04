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
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.PropertyCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.FilterState
import com.example.ui.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    properties: List<Property>,
    filterState: FilterState,
    savedIds: Set<String>,
    comparedIds: List<String>,
    currency: AppCurrency,
    onPropertyClick: (String) -> Unit,
    onSaveClick: (String) -> Unit,
    onCompareClick: (String) -> Unit,
    onFilterChange: (FilterState.() -> FilterState) -> Unit,
    onResetFilters: () -> Unit,
    onOpenMap: () -> Unit
) {
    var showFilterSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
    ) {
        // Search and Filter Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DeepNavy)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Search Input Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = filterState.query,
                    onValueChange = { q -> onFilterChange { copy(query = q) } },
                    placeholder = {
                        Text(
                            text = "Search city, country, or luxury amenities...",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextTertiary)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = GoldLight
                        )
                    },
                    trailingIcon = {
                        if (filterState.query.isNotEmpty()) {
                            IconButton(onClick = { onFilterChange { copy(query = "") } }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = TextSecondary
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SlateCard,
                        unfocusedContainerColor = SlateCard,
                        focusedBorderColor = GoldLight,
                        unfocusedBorderColor = SlateBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("explore_search_input")
                )

                // Map View Switcher Button
                FilledTonalIconButton(
                    onClick = onOpenMap,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = SlateCardElevated,
                        contentColor = GoldLight
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .size(54.dp)
                        .testTag("explore_map_toggle_button")
                ) {
                    Icon(imageVector = Icons.Default.Map, contentDescription = "Map View")
                }
            }

            // Quick Filter Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Verified Only Toggle Chip
                FilterChip(
                    selected = filterState.verifiedOnly,
                    onClick = { onFilterChange { copy(verifiedOnly = !verifiedOnly) } },
                    label = { Text("Verified Only") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = if (filterState.verifiedOnly) ObsidianBlack else EmeraldLight,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldLight,
                        selectedLabelColor = ObsidianBlack,
                        containerColor = SlateCard,
                        labelColor = TextPrimary
                    ),
                    border = BorderStroke(1.dp, if (filterState.verifiedOnly) EmeraldLight else SlateBorder)
                )

                // 3D Tour Available
                FilterChip(
                    selected = filterState.virtualTourOnly,
                    onClick = { onFilterChange { copy(virtualTourOnly = !virtualTourOnly) } },
                    label = { Text("3D Virtual Tour") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.ViewInAr,
                            contentDescription = null,
                            tint = if (filterState.virtualTourOnly) ObsidianBlack else SkyAccent,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SkyAccent,
                        selectedLabelColor = ObsidianBlack,
                        containerColor = SlateCard,
                        labelColor = TextPrimary
                    ),
                    border = BorderStroke(1.dp, if (filterState.virtualTourOnly) SkyAccent else SlateBorder)
                )

                // High Yield Only
                FilterChip(
                    selected = filterState.investmentOnly,
                    onClick = { onFilterChange { copy(investmentOnly = !investmentOnly) } },
                    label = { Text("High Yield (5%+)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = if (filterState.investmentOnly) ObsidianBlack else GoldLight,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldLight,
                        selectedLabelColor = ObsidianBlack,
                        containerColor = SlateCard,
                        labelColor = TextPrimary
                    ),
                    border = BorderStroke(1.dp, if (filterState.investmentOnly) GoldLight else SlateBorder)
                )

                // Reset Filters
                if (filterState.verifiedOnly || filterState.virtualTourOnly || filterState.investmentOnly || filterState.query.isNotEmpty() || filterState.selectedCity != "All") {
                    TextButton(onClick = onResetFilters) {
                        Text("Reset", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }

        // Active count & results header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${properties.size} Properties Available",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )

            Text(
                text = "Live Currency: ${currency.code}",
                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
            )
        }

        // Listings
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
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "No Properties Found",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Try adjusting your filters or destination keywords.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )
                    Button(
                        onClick = onResetFilters,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = ObsidianBlack),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Reset Filters")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                items(properties) { property ->
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
        }
    }
}
