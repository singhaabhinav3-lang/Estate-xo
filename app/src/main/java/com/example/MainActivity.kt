package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.auth.AuthManager
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianBlack
import com.example.ui.viewmodel.EstateViewModel
import com.example.ui.viewmodel.Screen
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {

    private val viewModel: EstateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        viewModel.repository.setupFirestore(this)

        setContent {
            MyApplicationTheme {
                AuthGatedApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AuthGatedApp(viewModel: EstateViewModel) {
    var currentUser by remember { mutableStateOf<FirebaseUser?>(Firebase.auth.currentUser) }
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            currentUser = user
            if (user != null) {
                viewModel.repository.syncWithFirebaseUser(user)
                if (viewModel.currentScreen.value is Screen.Auth) {
                    viewModel.onAuthSuccessful()
                }
            } else {
                viewModel.onSignedOut()
            }
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    if (currentUser == null || currentScreen is Screen.Auth) {
        // Enforce strict authentication gate in navigation graph: user cannot access functionality until signed in
        SignInScreen(
            onSignInSuccess = { user ->
                currentUser = user
                viewModel.repository.syncWithFirebaseUser(user)
                viewModel.onAuthSuccessful()
            }
        )
    } else {
        EstateApp(
            viewModel = viewModel,
            onSignOut = {
                viewModel.onSignedOut()
            }
        )
    }
}

@Composable
fun EstateApp(
    viewModel: EstateViewModel,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val properties by viewModel.properties.collectAsStateWithLifecycle()
    val filteredProperties by viewModel.filteredProperties.collectAsStateWithLifecycle()
    val savedIds by viewModel.savedPropertyIds.collectAsStateWithLifecycle()
    val comparedIds by viewModel.comparedPropertyIds.collectAsStateWithLifecycle()
    val appointments by viewModel.appointments.collectAsStateWithLifecycle()
    val leads by viewModel.leads.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val currentCurrency by viewModel.currentCurrency.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    val matchmakerState by viewModel.matchmakerState.collectAsStateWithLifecycle()
    val aiAssistantMessages by viewModel.aiAssistantMessages.collectAsStateWithLifecycle()
    val isAiThinking by viewModel.isAiThinking.collectAsStateWithLifecycle()

    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showRoleDialog by remember { mutableStateOf(false) }

    // Intercept back navigation
    BackHandler(enabled = currentScreen !is Screen.Home && currentScreen !is Screen.Auth) {
        viewModel.navigateBack()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        topBar = {
            if (currentScreen !is Screen.Auth && currentScreen !is Screen.PropertyDetail && currentScreen !is Screen.Chat) {
                EstateTopAppBar(
                    currentScreen = currentScreen,
                    currentCurrency = currentCurrency,
                    currentRole = userProfile.role,
                    savedCount = savedIds.size,
                    comparedCount = comparedIds.size,
                    onBackClick = { viewModel.navigateBack() },
                    onCurrencyClick = { showCurrencyDialog = true },
                    onRoleClick = { showRoleDialog = true },
                    onCompareClick = { viewModel.navigateTo(Screen.Compare) },
                    onAdminClick = { viewModel.navigateTo(Screen.AdminModeration) }
                )
            }
        },
        bottomBar = {
            val showBottomNav = when (currentScreen) {
                is Screen.Auth -> false
                is Screen.Home, is Screen.Explore, is Screen.MapView, is Screen.AiMatchmaker, is Screen.Saved, is Screen.Profile, is Screen.AgentDashboard -> true
                else -> false
            }
            if (showBottomNav) {
                EstateBottomNavigationBar(
                    currentScreen = currentScreen,
                    userRole = userProfile.role,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )
            }
        },
        containerColor = ObsidianBlack
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is Screen.Auth -> {
                    SignInScreen(
                        onSignInSuccess = { user ->
                            viewModel.repository.syncWithFirebaseUser(user)
                            viewModel.onAuthSuccessful()
                        }
                    )
                }

                is Screen.Home -> {
                    HomeScreen(
                        properties = properties,
                        savedIds = savedIds,
                        comparedIds = comparedIds,
                        currency = currentCurrency,
                        onPropertyClick = { id -> viewModel.navigateTo(Screen.PropertyDetail(id)) },
                        onSaveClick = { id -> viewModel.toggleSave(id) },
                        onCompareClick = { id -> viewModel.toggleCompare(id) },
                        onNavigate = { dest -> viewModel.navigateTo(dest) },
                        onSearchDestination = { dest ->
                            viewModel.updateFilters { copy(selectedCity = dest) }
                        }
                    )
                }

                is Screen.Explore -> {
                    ExploreScreen(
                        properties = filteredProperties,
                        filterState = filterState,
                        savedIds = savedIds,
                        comparedIds = comparedIds,
                        currency = currentCurrency,
                        onPropertyClick = { id -> viewModel.navigateTo(Screen.PropertyDetail(id)) },
                        onSaveClick = { id -> viewModel.toggleSave(id) },
                        onCompareClick = { id -> viewModel.toggleCompare(id) },
                        onFilterChange = { update -> viewModel.updateFilters(update) },
                        onResetFilters = { viewModel.resetFilters() },
                        onOpenMap = { viewModel.navigateTo(Screen.MapView) }
                    )
                }

                is Screen.MapView -> {
                    MapScreen(
                        properties = filteredProperties,
                        currency = currentCurrency,
                        onPropertyClick = { id -> viewModel.navigateTo(Screen.PropertyDetail(id)) },
                        onSwitchToList = { viewModel.navigateTo(Screen.Explore) }
                    )
                }

                is Screen.AiMatchmaker -> {
                    AiMatchmakerScreen(
                        form = matchmakerState,
                        currency = currentCurrency,
                        savedIds = savedIds,
                        comparedIds = comparedIds,
                        onFormUpdate = { update -> viewModel.updateMatchmaker(update) },
                        onRunMatchmaker = { viewModel.runAiMatchmaker() },
                        onPropertyClick = { id -> viewModel.navigateTo(Screen.PropertyDetail(id)) },
                        onSaveClick = { id -> viewModel.toggleSave(id) },
                        onCompareClick = { id -> viewModel.toggleCompare(id) },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is Screen.PropertyDetail -> {
                    val property = properties.find { it.id == screen.propertyId } ?: properties.first()
                    PropertyDetailScreen(
                        property = property,
                        currency = currentCurrency,
                        isSaved = savedIds.contains(property.id),
                        isCompared = comparedIds.contains(property.id),
                        aiMessages = aiAssistantMessages[property.id] ?: emptyList(),
                        isAiThinking = isAiThinking,
                        onBack = { viewModel.navigateBack() },
                        onSaveToggle = { viewModel.toggleSave(property.id) },
                        onCompareToggle = { viewModel.toggleCompare(property.id) },
                        onBookViewing = { viewModel.navigateTo(Screen.BookViewing(property.id)) },
                        onChatWithAgent = { viewModel.navigateTo(Screen.Chat(property.id)) },
                        onAskAi = { q -> viewModel.askAiPropertyAssistant(property, q) }
                    )
                }

                is Screen.Compare -> {
                    val selectedProps = properties.filter { comparedIds.contains(it.id) }
                    CompareScreen(
                        properties = selectedProps,
                        currency = currentCurrency,
                        onRemoveProperty = { id -> viewModel.toggleCompare(id) },
                        onPropertyClick = { id -> viewModel.navigateTo(Screen.PropertyDetail(id)) },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is Screen.Chat -> {
                    val property = properties.find { it.id == screen.propertyId } ?: properties.first()
                    val chatList = messages[property.id] ?: emptyList()
                    ChatScreen(
                        property = property,
                        messages = chatList,
                        onSendMessage = { text, targetLang ->
                            viewModel.sendMessage(property.id, text, isFromAgent = false, targetLanguage = targetLang)
                        },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is Screen.BookViewing -> {
                    val property = properties.find { it.id == screen.propertyId } ?: properties.first()
                    ViewingBookingScreen(
                        property = property,
                        currency = currentCurrency,
                        onConfirmBooking = { date, slot, isVirtual, notes ->
                            viewModel.bookViewing(property, date, slot, isVirtual, notes)
                            viewModel.navigateBack()
                        },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is Screen.AgentDashboard -> {
                    AgentDashboardScreen(
                        properties = properties,
                        leads = leads,
                        appointments = appointments,
                        currency = currentCurrency,
                        onLeadStageUpdate = { id, stage -> viewModel.repository.updateLeadStage(id, stage) },
                        onAppointmentStatusUpdate = { id, status -> viewModel.repository.updateAppointmentStatus(id, status) },
                        onPropertyClick = { id -> viewModel.navigateTo(Screen.PropertyDetail(id)) },
                        onNavigate = { dest -> viewModel.navigateTo(dest) }
                    )
                }

                is Screen.AiListingCreator -> {
                    AiListingCreatorScreen(
                        currency = currentCurrency,
                        onGenerate = { type, loc, price, beds, baths, sqft, features, onGen ->
                            viewModel.createAiListing(type, loc, price, beds, baths, sqft, features, onGen)
                        },
                        onPublish = { title, tag, desc, price, type, city, country, beds, baths, sqft, amenities, highlights ->
                            viewModel.publishNewListing(title, tag, desc, price, type, city, country, beds, baths, sqft, amenities, highlights)
                        },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is Screen.Saved -> {
                    val savedProperties = properties.filter { savedIds.contains(it.id) }
                    SavedScreen(
                        savedProperties = savedProperties,
                        comparedIds = comparedIds,
                        appointments = appointments,
                        currency = currentCurrency,
                        onPropertyClick = { id -> viewModel.navigateTo(Screen.PropertyDetail(id)) },
                        onSaveToggle = { id -> viewModel.toggleSave(id) },
                        onCompareToggle = { id -> viewModel.toggleCompare(id) }
                    )
                }

                is Screen.Profile -> {
                    ProfileScreen(
                        userProfile = userProfile,
                        currency = currentCurrency,
                        onRoleSelect = { role -> viewModel.setUserRole(role) },
                        onCurrencyClick = { showCurrencyDialog = true },
                        onNavigate = { dest -> viewModel.navigateTo(dest) },
                        onSignOut = {
                            AuthManager.signOut(
                                context = context,
                                credentialManager = credentialManager,
                                scope = coroutineScope,
                                onComplete = onSignOut
                            )
                        }
                    )
                }

                is Screen.AdminModeration -> {
                    AdminModerationScreen(
                        properties = properties,
                        onBack = { viewModel.navigateBack() }
                    )
                }
            }

            // Currency Selector Modal Bottom Sheet
            if (showCurrencyDialog) {
                CurrencySelectorBottomSheet(
                    currentCurrency = currentCurrency,
                    onCurrencySelected = { curr -> viewModel.setCurrency(curr) },
                    onDismiss = { showCurrencyDialog = false }
                )
            }

            // Role Switcher Modal Bottom Sheet
            if (showRoleDialog) {
                RoleSelectorBottomSheet(
                    currentRole = userProfile.role,
                    onRoleSelected = { role -> viewModel.setUserRole(role) },
                    onDismiss = { showRoleDialog = false }
                )
            }
        }
    }
}
