package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstateTopAppBar(
    currentScreen: Screen,
    currentCurrency: AppCurrency,
    currentRole: UserRole,
    savedCount: Int,
    comparedCount: Int,
    onBackClick: () -> Unit,
    onCurrencyClick: () -> Unit,
    onRoleClick: () -> Unit,
    onCompareClick: () -> Unit,
    onAdminClick: () -> Unit
) {
    Surface(
        color = ObsidianBlack,
        tonalElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (currentScreen !is Screen.Home && currentScreen !is Screen.Explore && currentScreen !is Screen.MapView && currentScreen !is Screen.AgentDashboard && currentScreen !is Screen.Profile) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SlateCard)
                            .testTag("top_bar_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(GoldPrimary, GoldDark)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Apartment,
                            contentDescription = "EstateXo Emblem",
                            tint = ObsidianBlack,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "ESTATE",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.sp,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "XO",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.sp,
                                color = GoldLight
                            )
                        )
                    }
                    Text(
                        text = "GLOBAL MARKETPLACE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.sp,
                            letterSpacing = 1.5.sp,
                            color = TextSecondary
                        )
                    )
                }
            }

            // Right side action chips
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Currency Chip
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SlateCard,
                    border = BorderStroke(1.dp, SlateBorder),
                    modifier = Modifier
                        .clickable { onCurrencyClick() }
                        .testTag("currency_selector_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = currentCurrency.flag, fontSize = 12.sp)
                        Text(
                            text = currentCurrency.code,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 12.sp,
                                color = TextPrimary
                            )
                        )
                    }
                }

                // Role Chip
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = GoldContainer,
                    border = BorderStroke(1.dp, GoldDark.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .clickable { onRoleClick() }
                        .testTag("role_switcher_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = when (currentRole) {
                                UserRole.BUYER -> Icons.Default.Person
                                UserRole.AGENT -> Icons.Default.Badge
                                UserRole.OWNER -> Icons.Default.HomeWork
                                UserRole.INVESTOR -> Icons.AutoMirrored.Filled.TrendingUp
                            },
                            contentDescription = currentRole.displayName,
                            tint = GoldLight,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = currentRole.displayName.split(" ")[0],
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 11.sp,
                                color = GoldLight
                            )
                        )
                    }
                }

                // Compare Badge
                if (comparedCount > 0) {
                    BadgedBox(
                        badge = {
                            Badge(containerColor = GoldPrimary, contentColor = ObsidianBlack) {
                                Text("$comparedCount")
                            }
                        }
                    ) {
                        IconButton(
                            onClick = onCompareClick,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SlateCard)
                                .testTag("compare_top_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                                contentDescription = "Compare",
                                tint = GoldLight,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EstateBottomNavigationBar(
    currentScreen: Screen,
    userRole: UserRole,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = ObsidianBlack,
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        val navItems = listOf(
            Triple(Screen.Home, "Home", Icons.Default.Home to Icons.Outlined.Home),
            Triple(Screen.Explore, "Explore", Icons.Default.Search to Icons.Outlined.Search),
            Triple(Screen.MapView, "Map", Icons.Default.Explore to Icons.Outlined.Explore),
            Triple(Screen.AiMatchmaker, "AI Match", Icons.Default.AutoAwesome to Icons.Outlined.AutoAwesome),
            Triple(
                if (userRole == UserRole.AGENT || userRole == UserRole.OWNER) Screen.AgentDashboard else Screen.Saved,
                if (userRole == UserRole.AGENT) "CRM" else if (userRole == UserRole.OWNER) "Manage" else "Saved",
                if (userRole == UserRole.AGENT || userRole == UserRole.OWNER) (Icons.Default.Dashboard to Icons.Outlined.Dashboard) else (Icons.Default.Bookmark to Icons.Outlined.BookmarkBorder)
            ),
            Triple(Screen.Profile, "Profile", Icons.Default.Person to Icons.Outlined.Person)
        )

        navItems.forEach { (screen, label, icons) ->
            val isSelected = currentScreen::class == screen::class
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(screen) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) icons.first else icons.second,
                        contentDescription = label,
                        tint = if (isSelected) GoldLight else TextSecondary
                    )
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) GoldLight else TextSecondary
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = SlateCardElevated
                )
            )
        }
    }
}

@Composable
fun PropertyCard(
    property: Property,
    currency: AppCurrency,
    isSaved: Boolean,
    isCompared: Boolean,
    onCardClick: () -> Unit,
    onSaveClick: () -> Unit,
    onCompareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = BorderStroke(1.dp, SlateBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("property_card_${property.id}")
    ) {
        Column {
            // Image Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            ) {
                AsyncImage(
                    model = property.imageResId ?: R.drawable.img_hero_villa,
                    contentDescription = property.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Top Gradient Scrim
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)
                            )
                        )
                )

                // Badges Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Trust Score Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceGlass,
                            border = BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = "Trust Score",
                                    tint = EmeraldLight,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "${property.trustScore} Trust",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                            }
                        }

                        // 3D Tour Badge
                        if (property.virtualTourRooms.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SurfaceGlass,
                                border = BorderStroke(1.dp, SkyAccent.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ViewInAr,
                                        contentDescription = "3D Tour",
                                        tint = SkyAccent,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "3D Tour",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Action buttons (Save & Compare)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = onCompareClick,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(SurfaceGlass)
                                .testTag("compare_button_${property.id}")
                        ) {
                            Icon(
                                imageVector = if (isCompared) Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.CompareArrows,
                                contentDescription = "Compare",
                                tint = if (isCompared) GoldLight else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = onSaveClick,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(SurfaceGlass)
                                .testTag("save_button_${property.id}")
                        ) {
                            Icon(
                                imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Save",
                                tint = if (isSaved) CrimsonDanger else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Bottom Overlay: Price Pill & Type
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SurfaceGlass,
                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = property.propertyType.label.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = GoldLight
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Card Body
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Price & Location
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currency.format(property.priceUsd),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "${property.city}, ${property.country}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = property.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = property.tagline,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        lineHeight = 18.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Divider
                HorizontalDivider(color = SlateBorder.copy(alpha = 0.5f), thickness = 1.dp)

                // Specifications Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SpecItem(Icons.Default.Bed, "${property.bedrooms} Beds")
                    SpecItem(Icons.Default.Bathtub, "${property.bathrooms} Baths")
                    SpecItem(Icons.Default.SquareFoot, "${property.areaSqFt} sqft")
                    SpecItem(
                        Icons.AutoMirrored.Filled.TrendingUp,
                        "${String.format("%.1f", property.investmentMetrics.calculateNetYield(property.priceUsd))}% Yield",
                        highlight = true
                    )
                }
            }
        }
    }
}

@Composable
fun SpecItem(icon: ImageVector, text: String, highlight: Boolean = false) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (highlight) EmeraldLight else TextSecondary,
            modifier = Modifier.size(15.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal,
                color = if (highlight) EmeraldLight else TextSecondary
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySelectorBottomSheet(
    currentCurrency: AppCurrency,
    onCurrencySelected: (AppCurrency) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DeepNavy,
        dragHandle = { BottomSheetDefaults.DragHandle(color = TextSecondary) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Select Preferred Currency",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Text(
                text = "All property listings, valuations, and investment calculations will automatically convert at live interbank rates.",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )

            Spacer(modifier = Modifier.height(6.dp))

            AppCurrency.values().forEach { currency ->
                val isSelected = currency == currentCurrency
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) SlateCardElevated else SlateCard,
                    border = BorderStroke(1.dp, if (isSelected) GoldLight else SlateBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onCurrencySelected(currency)
                            onDismiss()
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(text = currency.flag, fontSize = 24.sp)
                            Column {
                                Text(
                                    text = "${currency.code} (${currency.symbol})",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Text(
                                    text = currency.fullName,
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                )
                            }
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                tint = GoldLight,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectorBottomSheet(
    currentRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DeepNavy,
        dragHandle = { BottomSheetDefaults.DragHandle(color = TextSecondary) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Switch User Role",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Text(
                text = "Experience EstateXo tailored for each participant in global high-end real estate.",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )

            Spacer(modifier = Modifier.height(6.dp))

            UserRole.values().forEach { role ->
                val isSelected = role == currentRole
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) SlateCardElevated else SlateCard,
                    border = BorderStroke(1.dp, if (isSelected) GoldLight else SlateBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onRoleSelected(role)
                            onDismiss()
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) GoldContainer else ObsidianBlack),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (role) {
                                    UserRole.BUYER -> Icons.Default.Person
                                    UserRole.AGENT -> Icons.Default.Badge
                                    UserRole.OWNER -> Icons.Default.HomeWork
                                    UserRole.INVESTOR -> Icons.AutoMirrored.Filled.TrendingUp
                                },
                                contentDescription = role.displayName,
                                tint = if (isSelected) GoldLight else TextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = role.displayName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) GoldDark.copy(alpha = 0.3f) else SlateBorder.copy(alpha = 0.4f)
                                ) {
                                    Text(
                                        text = role.badge,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            color = if (isSelected) GoldLight else TextSecondary
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = role.description,
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                                maxLines = 2
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Active",
                                tint = GoldLight,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
