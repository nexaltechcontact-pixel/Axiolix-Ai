
package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ScannerBackground = Color(0xFF080D18)
private val ScannerCard = Color(0xFF111C2D)
private val ScannerCyan = Color(0xFF00E5FF)
private val ScannerMuted = Color(0xFF9AAABD)

@Composable
fun NetworkScannerScreen(
    onBack: () -> Unit
) {
    var selectedMode by remember { mutableStateOf("AI Assistant") }
    var prompt by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScannerBackground)
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = ScannerCyan
                )
            }

            Column {
                Text(
                    "AXIOLIX",
                    color = ScannerCyan,
                    fontSize = 21.sp
                )
                Text(
                    "NETWORK INTELLIGENCE",
                    color = ScannerMuted,
                    fontSize = 10.sp
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = ScannerCard
            ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Radar,
                    contentDescription = null,
                    tint = ScannerCyan,
                    modifier = Modifier.size(64.dp)
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    "NETWORK SCANNER",
                    color = ScannerCyan,
                    fontSize = 20.sp
                )

                Text(
                    "AI-powered network diagnostics",
                    color = ScannerMuted
                )
            }
        }

        Spacer(Modifier.height(22.dp))

        Text(
            "SELECT MODE",
            color = ScannerCyan,
            fontSize = 13.sp
        )

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ModeButton(
                title = "AI Assistant",
                selected = selectedMode == "AI Assistant",
                modifier = Modifier.weight(1f)
            ) {
                selectedMode = "AI Assistant"
            }

            ModeButton(
                title = "Expert Mode",
                selected = selectedMode == "Expert Mode",
                modifier = Modifier.weight(1f)
            ) {
                selectedMode = "Expert Mode"
            }
        }

        Spacer(Modifier.height(20.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = ScannerCard
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (selectedMode == "AI Assistant")
                            Icons.Default.Psychology
                        else
                            Icons.Default.Terminal,
                        contentDescription = null,
                        tint = ScannerCyan
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        selectedMode,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    if (selectedMode == "AI Assistant")
                        "Describe the network diagnostic you need. AI will help explain and prepare an appropriate check."
                    else
                        "Expert command entry will be connected in a later step.",
                    color = ScannerMuted,
                    fontSize = 13.sp
                )

                Spacer(Modifier.height(14.dp))

                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            if (selectedMode == "AI Assistant")
                                "Example: Explain devices on my home network"
                            else
                                "Expert command input"
                        )
                    },
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = ScannerCyan,
                        unfocusedBorderColor = ScannerMuted,
                        focusedPlaceholderColor = ScannerMuted,
                        unfocusedPlaceholderColor = ScannerMuted
                    )
                )

                Spacer(Modifier.height(14.dp))

                Button(
                    onClick = { },
                    enabled = prompt.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ScannerCyan,
                        contentColor = ScannerBackground
                    )
                ) {
                    Text(
                        if (selectedMode == "AI Assistant")
                            "ASK AI ASSISTANT"
                        else
                            "PREVIEW COMMAND"
                    )
                }

                Text(
                    "AI connection and command execution will be added next.",
                    color = ScannerMuted,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            "SCAN RESULTS",
            color = ScannerCyan,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(10.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = ScannerCard
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text(
                    "No scan performed yet",
                    color = Color.White
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Your authorized network diagnostic results will appear here.",
                    color = ScannerMuted,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun ModeButton(
    title: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected)
                ScannerCyan
            else
                ScannerCard,
            contentColor = if (selected)
                ScannerBackground
            else
                Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(title, fontSize = 12.sp)
    }
}
