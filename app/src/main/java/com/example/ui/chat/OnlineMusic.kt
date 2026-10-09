package com.example.ui.chat

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder
import kotlin.concurrent.thread

data class Song(val id: String, val title: String, val artist: String)

object OnlineMusic {
    private const val BASE = "https://api.audius.co/v1"
    private const val APP = "Axiolix"
    private val http = OkHttpClient()
    private val main = Handler(Looper.getMainLooper())
    private var player: ExoPlayer? = null

    var current by mutableStateOf<Song?>(null); private set
    var isPlaying by mutableStateOf(false); private set

    fun init(ctx: Context) {
        if (player != null) return
        player = ExoPlayer.Builder(ctx.applicationContext).build().apply {
            addListener(object : Player.Listener {
                               override fun onIsPlayingChanged(playing: Boolean) { OnlineMusic.isPlaying = playing }  
            })
        }
    }

    /** Callback runs on the main thread: (results, errorMessage) */
    fun search(query: String, onDone: (List<Song>, String?) -> Unit) = thread {
        try {
            val q = URLEncoder.encode(query, "UTF-8")
            val req = Request.Builder().url("$BASE/tracks/search?query=$q&app_name=$APP").build()
            val body = http.newCall(req).execute().use { it.body?.string() ?: "{}" }
            val arr = JSONObject(body).optJSONArray("data")
            val list = (0 until (arr?.length() ?: 0)).map {
                val t = arr!!.getJSONObject(it)
                Song(t.getString("id"), t.getString("title"), t.getJSONObject("user").getString("name"))
            }
            main.post { onDone(list, if (list.isEmpty()) "No songs found" else null) }
        } catch (e: Exception) {
            main.post { onDone(emptyList(), "Couldn't search. Check your internet.") }
        }
    }

    fun play(song: Song) {
        current = song
        player?.apply {
            setMediaItem(MediaItem.fromUri("$BASE/tracks/${song.id}/stream?app_name=$APP"))
            prepare(); play()
        }
    }

    fun toggle() { player?.let { if (it.isPlaying) it.pause() else it.play() } }
    fun setVolume(v: Float) { player?.volume = v }
    fun release() { player?.release(); player = null; current = null; isPlaying = false }
}
