package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.Property
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    property: Property,
    messages: List<ChatMessage>,
    onSendMessage: (String, String?) -> Unit,
    onBack: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf("Arabic (العربية)") }
    var translationEnabled by remember { mutableStateOf(false) }

    val languages = listOf("English", "Arabic (العربية)", "French (Français)", "Japanese (日本語)", "Spanish (Español)", "Mandarin (中文)")

    Scaffold(
        topBar = {
            Surface(
                color = SlateCardElevated,
                border = BorderStroke(1.dp, SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SlateCard)
                            ) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                            }

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = property.agentName,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                                    )
                                    Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = SkyAccent, modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    text = "${property.agentCompany} • ${property.title}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                                    maxLines = 1
                                )
                            }
                        }

                        // Translation toggle
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (translationEnabled) EmeraldContainer else SlateCard,
                            border = BorderStroke(1.dp, if (translationEnabled) EmeraldLight else SlateBorder),
                            modifier = Modifier.clickable { translationEnabled = !translationEnabled }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Translate, contentDescription = null, tint = if (translationEnabled) EmeraldLight else TextSecondary, modifier = Modifier.size(14.dp))
                                Text(
                                    text = if (translationEnabled) "Live Translation ON" else "Translate",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (translationEnabled) EmeraldLight else TextSecondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    // Language Selector if Translation enabled
                    if (translationEnabled) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            languages.forEach { lang ->
                                val isSelected = selectedLanguage == lang
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) GoldLight else SlateCard,
                                    modifier = Modifier.clickable { selectedLanguage = lang }
                                ) {
                                    Text(
                                        text = lang,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) ObsidianBlack else TextPrimary,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = SlateCardElevated,
                border = BorderStroke(1.dp, SlateBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // AI Negotiation Assistant Quick Drafts
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SlateCard,
                            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                            modifier = Modifier.clickable {
                                inputText = "Dear ${property.agentName}, we would like to present a clean formal cash offer at 92% of asking with proof of funds ready for 14-day escrow."
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = GoldLight, modifier = Modifier.size(13.dp))
                                Text("AI Cash Offer Draft (-8%)", style = MaterialTheme.typography.labelSmall.copy(color = GoldLight))
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SlateCard,
                            border = BorderStroke(1.dp, SkyAccent.copy(alpha = 0.5f)),
                            modifier = Modifier.clickable {
                                inputText = "Could you confirm if the developer warranty covers the HVAC automation and when the latest HOA reserve audit was performed?"
                            }
                        ) {
                            Text("Request Due Diligence Audit", style = MaterialTheme.typography.labelSmall.copy(color = SkyAccent), modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                        }
                    }

                    // Message input field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Type confidential message...", style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)) },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SlateCard,
                                unfocusedContainerColor = SlateCard,
                                focusedBorderColor = GoldLight,
                                unfocusedBorderColor = SlateBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_input_field")
                        )

                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    val targetLang = if (translationEnabled) selectedLanguage else null
                                    onSendMessage(inputText, targetLang)
                                    inputText = ""
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary)
                                .testTag("chat_send_button")
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = ObsidianBlack)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBlack)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Escrow security disclaimer banner
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SlateCard,
                    border = BorderStroke(1.dp, SlateBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(16.dp))
                        Text(
                            text = "End-to-End Encrypted Broker Communication. Never transfer deposits outside official EstateXo Escrow.",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )
                    }
                }
            }

            items(messages) { message ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (message.isFromAgent) Arrangement.Start else Arrangement.End
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (message.isFromAgent) SlateCardElevated else GoldContainer,
                        border = BorderStroke(1.dp, if (message.isFromAgent) SlateBorder else GoldDark),
                        modifier = Modifier.widthIn(max = 300.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = message.senderName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (message.isFromAgent) GoldLight else OnGoldContainer
                                )
                            )
                            Text(
                                text = message.originalText,
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary)
                            )

                            // Translated text badge if present
                            if (message.translatedText != null) {
                                HorizontalDivider(color = SlateBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(imageVector = Icons.Default.Translate, contentDescription = null, tint = SkyAccent, modifier = Modifier.size(12.dp))
                                    Text(
                                        text = message.translatedText,
                                        style = MaterialTheme.typography.bodySmall.copy(color = SkyAccent)
                                    )
                                }
                            }

                            Text(
                                text = message.timestamp,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = TextSecondary),
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }
    }
}
