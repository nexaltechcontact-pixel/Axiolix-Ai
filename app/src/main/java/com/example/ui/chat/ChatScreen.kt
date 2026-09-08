package com.example.ui.chat

import android.net.Uri

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Security

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.R
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.UserAccountEntity

import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextSecondary

@Composable
fun ChatScreen(
    messages: List<ChatMessageEntity>,
    isGenerating: Boolean,
    currentUser: UserAccountEntity?,
    conversationTitle: String,
    onSendMessage: (String) -> Unit,
    onImageSelected: (Uri) -> Unit,
    onGenerateImage: (String) -> Unit,
    onOpenDashboard: () -> Unit,
    onOpenAuthDialog: () -> Unit,
    onCreateNewSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Image picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onImageSelected(it)
        }
    }

    // Auto-scroll to bottom on new messages or during generation
    LaunchedEffect(messages.size, isGenerating) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(
        label = "pulse_online"
    )

    val pulseOnline by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                1000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_pulse"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .testTag("chat_screen")
    ) {

        // Futuristic Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberDark)
                .border(1.dp, CyberBorder)
                .padding(
                    horizontal = 14.dp,
                    vertical = 10.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // Left: Logo and App Name with Online Pulse
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    onOpenDashboard()
                }
            ) {

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CyberBlack)
                        .border(
                            1.5.dp,
                            NeonCyan,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(
                            id = R.drawable.ic_axiolix_logo
                        ),
                        contentDescription = "Axiolix AI",
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AXIOLIX",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 3.sp,
                            fontFamily = FontFamily.Monospace,
                            color = NeonCyan
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .scale(pulseOnline)
                                .clip(CircleShape)
                                .background(MatrixGreen)
                        )
                    }

                    Text(
                        text = conversationTitle,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
            }

            // Right Action Buttons
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                // New Chat
                IconButton(
                    onClick = onCreateNewSession,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("new_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Session",
                        tint = NeonCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Dashboard
                IconButton(
                    onClick = onOpenDashboard,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("open_dashboard_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Dashboard,
                        contentDescription = "Previous Conversations Dashboard",
                        tint = NeonViolet,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Register / Login
                IconButton(
                    onClick = onOpenAuthDialog,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("open_auth_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Account Clearance / Login",
                        tint = if (currentUser != null) {
                            MatrixGreen
                        } else {
                            NeonCyan
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Main Chat Message Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            if (messages.isEmpty()) {

                // Empty Conversation Hero
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(CyberDark)
                            .border(
                                2.dp,
                                Brush.sweepGradient(
                                    listOf(
                                        NeonCyan,
                                        NeonViolet,
                                        NeonCyan
                                    )
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Image(
                            painter = painterResource(
                                id = R.drawable.ic_axiolix_logo
                            ),
                            contentDescription = "Axiolix AI Core",
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "AXIOLIX NEURAL CORE ONLINE",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp,
                        color = NeonCyan,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Connected to Gemini 3.5 Flash cognitive matrix with end-to-end encrypted local session persistence.",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Default,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(
                            horizontal = 24.dp
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Quick prompt suggestions
                    QuickPromptChips(
                        onSelectPrompt = onSendMessage
                    )
                }

            } else {

                // Message Stream
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    contentPadding = PaddingValues(
                        top = 12.dp,
                        bottom = 16.dp
                    )
                ) {

                    items(
                        messages,
                        key = { it.id }
                    ) { msg ->

                        ChatMessageItem(
                            message = msg,
                            userCallsign = currentUser?.username
                                ?: "Operative"
                        )
                    }

                    // Generating / Thinking Hologram Bubble
                    if (isGenerating) {

                        item {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(CyberDark)
                                        .border(
                                            1.dp,
                                            NeonCyan,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {

                                    Image(
                                        painter = painterResource(
                                            id = R.drawable.ic_axiolix_logo
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                    )
                                }

                                Spacer(
                                    modifier = Modifier.width(10.dp)
                                )

                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = CyberSurface
                                    ),
                                    modifier = Modifier.border(
                                        1.dp,
                                        NeonCyan.copy(alpha = 0.4f),
                                        RoundedCornerShape(12.dp)
                                    )
                                ) {

                                    Row(
                                        modifier = Modifier.padding(
                                            horizontal = 14.dp,
                                            vertical = 10.dp
                                        ),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .scale(pulseOnline)
                                                .clip(CircleShape)
                                                .background(NeonCyan)
                                        )

                                        Spacer(
                                            modifier = Modifier.width(10.dp)
                                        )

                                        Text(
                                            text = "Axiolix is computing neural output...",
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = NeonCyan
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Input Bar
        ChatInputBar(
            isGenerating = isGenerating,

            onSendMessage = onSendMessage,

            onPickImage = {
                imagePickerLauncher.launch("image/*")
            },

            onGenerateImage = onGenerateImage
        )
    }
}
