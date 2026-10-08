package com.example.ui.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberDark
import com.example.ui.theme.NeonCyan

/** Drop this anywhere in your chat top bar / input row. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicButton() {
    val ctx = LocalContext.current
    LaunchedEffect(Unit) { OnlineMusic.init(ctx) }
    var open by remember { mutableStateOf(false) }

    IconButton(onClick = { open = true }) { Text("🎵", style = MaterialTheme.typography.titleLarge) }

    if (open) {
        ModalBottomSheet(
            onDismissRequest = { open = false },
            containerColor = CyberDark,
            contentColor = Color.White
        ) {
            MaterialTheme(colorScheme = darkColorScheme(primary = NeonCyan)) { MusicPanel() }
        }
    }
}

@Composable
fun MusicPanel() {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<Song>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var volume by remember { mutableFloatStateOf(1f) }

    fun doSearch() {
        if (query.isBlank()) return
        loading = true; message = null
        OnlineMusic.search(query.trim()) { list, err -> results = list; message = err; loading = false }
    }

    Column(Modifier.padding(16.dp).navigationBarsPadding().imePadding()) {
        OutlinedTextField(
            value = query, onValueChange = { query = it },
            label = { Text("Search songs or artists") }, singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { doSearch() }),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        // Now playing bar
        OnlineMusic.current?.let { s ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(s.title, style = MaterialTheme.typography.titleSmall, maxLines = 1)
                            Text(s.artist, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                        }
                        FilledIconButton(onClick = { OnlineMusic.toggle() }) {
                            Text(if (OnlineMusic.isPlaying) "⏸" else "▶")
                        }
                    }
                    Slider(value = volume, onValueChange = { volume = it; OnlineMusic.setVolume(it) })
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())
        message?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        LazyColumn(Modifier.heightIn(max = 360.dp)) {
            items(results, key = { it.id }) { s ->
                ListItem(
                    headlineContent = { Text(s.title, maxLines = 1) },
                    supportingContent = { Text(s.artist, maxLines = 1) },
                    trailingContent = { Text("▶") },
                    modifier = Modifier.clickable { OnlineMusic.play(s) }
                )
            }
        }
        Text("Music via Audius", style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 8.dp))
    }
}
