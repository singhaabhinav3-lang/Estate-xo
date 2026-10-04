package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.data.auth.AuthManager
import com.example.ui.theme.*
import com.google.firebase.auth.FirebaseUser

enum class AuthTab {
    GOOGLE,
    EMAIL_PASSWORD
}

enum class EmailAuthMode {
    SIGN_IN,
    SIGN_UP
}

@Composable
fun SignInScreen(
    onSignInSuccess: (FirebaseUser) -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    var selectedTab by remember { mutableStateOf(AuthTab.GOOGLE) }
    var emailAuthMode by remember { mutableStateOf(EmailAuthMode.SIGN_IN) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var infoDialogMessage by remember { mutableStateOf<String?>(null) }

    // Attempt silent sign-in on first composition
    LaunchedEffect(Unit) {
        AuthManager.attemptAutoSignIn(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = onSignInSuccess,
            onUnauthenticated = { /* stay on sign-in screen */ },
            scope = coroutineScope
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Brand Logo & Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.linearGradient(listOf(GoldPrimary, GoldDark))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Apartment,
                        contentDescription = "EstateXo Emblem",
                        tint = ObsidianBlack,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "ESTATE",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp,
                            fontSize = 30.sp,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "XO",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp,
                            fontSize = 30.sp,
                            color = GoldLight
                        )
                    )
                }

                Text(
                    text = "GLOBAL LUXURY PROPERTY MARKETPLACE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 2.sp,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                )
            }

            // Main Auth Container Card
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Auth Method Tabs
                    TabRow(
                        selectedTabIndex = if (selectedTab == AuthTab.GOOGLE) 0 else 1,
                        containerColor = SlateCard,
                        contentColor = GoldLight,
                        indicator = {},
                        divider = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = selectedTab == AuthTab.GOOGLE,
                            onClick = {
                                selectedTab = AuthTab.GOOGLE
                                errorMessage = null
                            },
                            text = {
                                Text(
                                    "Google Sign-In",
                                    fontWeight = if (selectedTab == AuthTab.GOOGLE) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == AuthTab.GOOGLE) GoldLight else TextSecondary,
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier
                                .background(if (selectedTab == AuthTab.GOOGLE) SlateCardElevated else Color.Transparent)
                                .testTag("tab_google_auth")
                        )
                        Tab(
                            selected = selectedTab == AuthTab.EMAIL_PASSWORD,
                            onClick = {
                                selectedTab = AuthTab.EMAIL_PASSWORD
                                errorMessage = null
                            },
                            text = {
                                Text(
                                    "Email & Password",
                                    fontWeight = if (selectedTab == AuthTab.EMAIL_PASSWORD) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == AuthTab.EMAIL_PASSWORD) GoldLight else TextSecondary,
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier
                                .background(if (selectedTab == AuthTab.EMAIL_PASSWORD) SlateCardElevated else Color.Transparent)
                                .testTag("tab_email_auth")
                        )
                    }

                    // Error Banner
                    errorMessage?.let { err ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CrimsonDanger.copy(alpha = 0.18f),
                            border = BorderStroke(1.dp, CrimsonDanger.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Error",
                                    tint = CrimsonDanger,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = err,
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // TAB CONTENT
                    AnimatedContent(targetState = selectedTab, label = "auth_tab_content") { tab ->
                        when (tab) {
                            AuthTab.GOOGLE -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Text(
                                        text = "Sign in with your verified Google Account to access verified global listings, AI matchmaker, virtual tours, and private broker negotiations.",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = TextSecondary,
                                            textAlign = TextAlign.Center,
                                            lineHeight = 22.sp
                                        )
                                    )

                                    // Google Sign-In Button
                                    Button(
                                        onClick = {
                                            isLoading = true
                                            errorMessage = null
                                            val activity = context.findActivity()
                                            if (activity != null) {
                                                AuthManager.signInWithGoogle(
                                                    activity = activity,
                                                    credentialManager = credentialManager,
                                                    onAuthSuccess = { user ->
                                                        isLoading = false
                                                        onSignInSuccess(user)
                                                    },
                                                    onAuthError = { err ->
                                                        isLoading = false
                                                        errorMessage = err
                                                    },
                                                    onAuthCancelled = {
                                                        isLoading = false
                                                    },
                                                    scope = coroutineScope
                                                )
                                            } else {
                                                isLoading = false
                                                errorMessage = "Activity host could not be located."
                                            }
                                        },
                                        enabled = !isLoading,
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.White,
                                            contentColor = Color(0xFF1F1F1F)
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                            .testTag("google_sign_in_button")
                                    ) {
                                        if (isLoading) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(22.dp),
                                                color = ObsidianBlack,
                                                strokeWidth = 2.dp
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = "Connecting to Google...",
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF1F1F1F)
                                                )
                                            )
                                        } else {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = Color(0xFF4285F4),
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text(
                                                            text = "G",
                                                            fontWeight = FontWeight.Black,
                                                            color = Color.White,
                                                            fontSize = 15.sp
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(
                                                    text = "Continue with Google",
                                                    style = MaterialTheme.typography.titleSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF1F1F1F)
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            AuthTab.EMAIL_PASSWORD -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Mode Switcher: Sign In vs Sign Up
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(SlateCard)
                                            .padding(4.dp),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (emailAuthMode == EmailAuthMode.SIGN_IN) GoldPrimary.copy(alpha = 0.2f) else Color.Transparent)
                                                .clickable { emailAuthMode = EmailAuthMode.SIGN_IN }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "Sign In",
                                                fontWeight = if (emailAuthMode == EmailAuthMode.SIGN_IN) FontWeight.Bold else FontWeight.Normal,
                                                color = if (emailAuthMode == EmailAuthMode.SIGN_IN) GoldLight else TextSecondary,
                                                fontSize = 13.sp
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (emailAuthMode == EmailAuthMode.SIGN_UP) GoldPrimary.copy(alpha = 0.2f) else Color.Transparent)
                                                .clickable { emailAuthMode = EmailAuthMode.SIGN_UP }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "Sign Up (Register)",
                                                fontWeight = if (emailAuthMode == EmailAuthMode.SIGN_UP) FontWeight.Bold else FontWeight.Normal,
                                                color = if (emailAuthMode == EmailAuthMode.SIGN_UP) GoldLight else TextSecondary,
                                                fontSize = 13.sp
                                            )
                                        }
                                    }

                                    // Name Field (only if Sign Up)
                                    if (emailAuthMode == EmailAuthMode.SIGN_UP) {
                                        OutlinedTextField(
                                            value = displayName,
                                            onValueChange = { displayName = it },
                                            label = { Text("Full Name") },
                                            leadingIcon = {
                                                Icon(Icons.Default.Person, contentDescription = null, tint = GoldLight)
                                            },
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = GoldLight,
                                                unfocusedBorderColor = SlateBorder,
                                                focusedLabelColor = GoldLight,
                                                unfocusedLabelColor = TextSecondary,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            singleLine = true,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("full_name_input")
                                        )
                                    }

                                    // Email Field
                                    OutlinedTextField(
                                        value = email,
                                        onValueChange = { email = it },
                                        label = { Text("Email Address") },
                                        leadingIcon = {
                                            Icon(Icons.Default.Email, contentDescription = null, tint = GoldLight)
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = GoldLight,
                                            unfocusedBorderColor = SlateBorder,
                                            focusedLabelColor = GoldLight,
                                            unfocusedLabelColor = TextSecondary,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("email_input")
                                    )

                                    // Password Field
                                    OutlinedTextField(
                                        value = password,
                                        onValueChange = { password = it },
                                        label = { Text("Password") },
                                        leadingIcon = {
                                            Icon(Icons.Default.Lock, contentDescription = null, tint = GoldLight)
                                        },
                                        trailingIcon = {
                                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                                Icon(
                                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                    contentDescription = "Toggle password visibility",
                                                    tint = TextSecondary
                                                )
                                            }
                                        },
                                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = if (emailAuthMode == EmailAuthMode.SIGN_UP) ImeAction.Next else ImeAction.Done),
                                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = GoldLight,
                                            unfocusedBorderColor = SlateBorder,
                                            focusedLabelColor = GoldLight,
                                            unfocusedLabelColor = TextSecondary,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("password_input")
                                    )

                                    // Confirm Password (if Sign Up)
                                    if (emailAuthMode == EmailAuthMode.SIGN_UP) {
                                        OutlinedTextField(
                                            value = confirmPassword,
                                            onValueChange = { confirmPassword = it },
                                            label = { Text("Confirm Password") },
                                            leadingIcon = {
                                                Icon(Icons.Default.Lock, contentDescription = null, tint = GoldLight)
                                            },
                                            visualTransformation = PasswordVisualTransformation(),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = GoldLight,
                                                unfocusedBorderColor = SlateBorder,
                                                focusedLabelColor = GoldLight,
                                                unfocusedLabelColor = TextSecondary,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            singleLine = true,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("confirm_password_input")
                                        )
                                    }

                                    // Submit Email Action Button
                                    Button(
                                        onClick = {
                                            focusManager.clearFocus()
                                            if (email.isBlank() || !email.contains("@")) {
                                                errorMessage = "Please enter a valid email address."
                                                return@Button
                                            }
                                            if (password.length < 6) {
                                                errorMessage = "Password must be at least 6 characters."
                                                return@Button
                                            }
                                            if (emailAuthMode == EmailAuthMode.SIGN_UP && password != confirmPassword) {
                                                errorMessage = "Passwords do not match."
                                                return@Button
                                            }

                                            // Show guidance for email/password provider enablement
                                            infoDialogMessage = "Notice: The Email/Password authentication provider is not yet activated on this backend project. To enable it, navigate to Firebase Console > Authentication > Sign-in method and enable 'Email/Password'.\n\nFor immediate full access now, please use the 'Google Sign-In' tab to authenticate with your verified Google account."
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = GoldPrimary,
                                            contentColor = ObsidianBlack
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                            .testTag("email_auth_submit_button")
                                    ) {
                                        Text(
                                            text = if (emailAuthMode == EmailAuthMode.SIGN_IN) "Sign In with Email" else "Create Investor Account",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                    }

                                    // Shortcut to Google
                                    TextButton(
                                        onClick = { selectedTab = AuthTab.GOOGLE },
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        Text(
                                            "Prefer Google? Switch to Google Sign-In",
                                            color = GoldLight,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Security Assurances
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SecurityPill(Icons.Default.VerifiedUser, "Firebase Auth")
                SecurityPill(Icons.Default.Security, "Escrow Certified")
                SecurityPill(Icons.Default.Lock, "Zero-Trust Rules")
            }
        }

        // Info / Guidance Dialog
        infoDialogMessage?.let { msg ->
            AlertDialog(
                onDismissRequest = { infoDialogMessage = null },
                confirmButton = {
                    Button(
                        onClick = {
                            infoDialogMessage = null
                            selectedTab = AuthTab.GOOGLE
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = ObsidianBlack)
                    ) {
                        Text("Use Google Sign-In", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { infoDialogMessage = null }) {
                        Text("Close", color = TextSecondary)
                    }
                },
                title = {
                    Text("Firebase Authentication Setup", fontWeight = FontWeight.Bold, color = TextPrimary)
                },
                text = {
                    Text(msg, color = TextSecondary, lineHeight = 20.sp)
                },
                containerColor = SlateCardElevated,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
fun SecurityPill(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(14.dp))
        Text(text = text, style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 11.sp))
    }
}

private fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}
