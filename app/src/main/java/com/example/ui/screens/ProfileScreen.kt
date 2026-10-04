package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppCurrency
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.Screen

@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    currency: AppCurrency,
    onRoleSelect: (UserRole) -> Unit,
    onCurrencyClick: () -> Unit,
    onNavigate: (Screen) -> Unit,
    onSignOut: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // User Profile Header
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(GoldContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userProfile.name.split(" ").map { it.take(1) }.joinToString(""),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = GoldLight)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(text = userProfile.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                                Icon(imageVector = Icons.Default.Verified, contentDescription = "Verified Identity", tint = SkyAccent, modifier = Modifier.size(18.dp))
                            }
                            Text(text = "${userProfile.role.displayName} • ${userProfile.email}", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                            Surface(shape = RoundedCornerShape(8.dp), color = GoldContainer) {
                                Text(
                                    text = userProfile.role.badge,
                                    style = MaterialTheme.typography.labelSmall.copy(color = GoldLight, fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = SlateBorder.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProfileStatItem("KYC Status", "Accredited HNW")
                        ProfileStatItem("Registry ID", userProfile.licenseNumber)
                        ProfileStatItem("Member Since", userProfile.memberSince)
                    }
                }
            }
        }

        // Active Role Switcher
        item {
            Text(text = "Current Platform Persona", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                UserRole.values().forEach { role ->
                    val isSelected = userProfile.role == role
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) SlateCardElevated else SlateCard,
                        border = BorderStroke(1.dp, if (isSelected) GoldLight else SlateBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRoleSelect(role) }
                            .testTag("profile_role_item_${role.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = when (role) {
                                        UserRole.BUYER -> Icons.Default.Person
                                        UserRole.AGENT -> Icons.Default.Badge
                                        UserRole.OWNER -> Icons.Default.HomeWork
                                        UserRole.INVESTOR -> Icons.AutoMirrored.Filled.TrendingUp
                                    },
                                    contentDescription = null,
                                    tint = if (isSelected) GoldLight else TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = role.displayName,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = if (isSelected) GoldLight else TextPrimary,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                    Text(text = role.description, style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                                }
                            }

                            if (isSelected) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Active", tint = GoldLight)
                            }
                        }
                    }
                }
            }
        }

        // Currency Setting
        item {
            Text(text = "Marketplace Preferences", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SlateCard,
                border = BorderStroke(1.dp, SlateBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCurrencyClick() }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = currency.flag, fontSize = 22.sp)
                        Column {
                            Text(text = "Display Currency", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                            Text(text = "${currency.code} (${currency.fullName})", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                        }
                    }
                    Text(text = "Change", color = GoldLight, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
        }

        // Admin Moderation Hub
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SlateCard,
                border = BorderStroke(1.dp, IndigoAccent.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(Screen.AdminModeration) }
                    .testTag("open_admin_moderation_button")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = SkyAccent)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Admin Moderation & Compliance", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                        Text(text = "Review listing verification requests, fraud detection alerts & audit logs.", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                    }
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = SkyAccent)
                }
            }
        }

        // Monetization & Subscription Tiers
        item {
            Text(text = "EstateXo Partner Monetization", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                MonetizationTierCard(
                    title = "Buyer Discovery",
                    price = "Complimentary",
                    features = "Unlimited global search, AI property matching, 3D virtual tours, and viewing scheduling.",
                    isCurrent = userProfile.role == UserRole.BUYER
                )
                MonetizationTierCard(
                    title = "Agent Pro License",
                    price = "$299 / month",
                    features = "Unlimited verified listings, AI Listing Creator, CRM pipeline, and translated client chat.",
                    isCurrent = userProfile.role == UserRole.AGENT
                )
                MonetizationTierCard(
                    title = "Institutional Developer",
                    price = "Custom Enterprise",
                    features = "Multi-unit project showcases, digital land registry escrow integration, and API syndication.",
                    isCurrent = userProfile.role == UserRole.OWNER
                )
            }
        }

        // Account & Security Sign-Out
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = BorderStroke(1.dp, CrimsonDanger.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSignOut() }
                    .testTag("profile_sign_out_button")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = "Sign Out", tint = CrimsonDanger)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Sign Out",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = CrimsonDanger)
                        )
                        Text(
                            text = "Signed in as ${userProfile.email}",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileStatItem(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = title, style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 10.sp))
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
    }
}

@Composable
fun MonetizationTierCard(title: String, price: String, features: String, isCurrent: Boolean) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isCurrent) SlateCardElevated else SlateCard,
        border = BorderStroke(1.dp, if (isCurrent) GoldLight else SlateBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                Text(text = price, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold, color = GoldLight))
            }
            Text(text = features, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, lineHeight = 18.sp))
        }
    }
}
