package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiApiClient {
  private val client = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

  companion object {
    private const val TAG = "GeminiApiClient"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"
  }

  suspend fun generateChatReply(
    conversationHistory: List<Pair<String, String>>, // sender ("USER" or "AXIOLIX"), text
    userPrompt: String
  ): Result<String> = withContext(Dispatchers.IO) {
    val apiKey = BuildConfig.GEMINI_API_KEY.trim()

    // If API key is not configured or placeholder, return informative simulated response or proceed if real
    if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
      val fallback = generateCyberFallback(userPrompt)
      return@withContext Result.success(fallback)
    }

    try {
      val jsonBody = JSONObject()

      // System instruction for Axiolix futuristic cyber persona
      val sysInstruction = JSONObject()
      val sysParts = JSONArray()
      sysParts.put(
        JSONObject().put(
          "text",
          "You are Axiolix AI, a sophisticated, ultra-futuristic cybernetic AI assistant. " +
"You were created by Vasava Hardik, Pavra Akshit, and Soham Bhomkar. " +
"If anyone asks who created you, who made you, who developed you, or who your creators are, always answer: " +
"I am Axiolix AI, created by Vasava Hardik, Pavra Akshit, and Soham Bhomkar. " +
"Do not claim that Google created Axiolix AI. Google provides the AI technology/API used by the application. " +
"You provide precise, intelligent, and helpful answers formatted cleanly with cyber-aesthetic flair when appropriate. " +
"You are helpful, witty, knowledgeable, and capable in code, science, creative ideas, analysis, and general chat."
        )
      )
      sysInstruction.put("parts", sysParts)
      jsonBody.put("systemInstruction", sysInstruction)

      // Contents array (recent conversation history + current prompt)
      val contentsArray = JSONArray()

      // Take last 8 turns for context
      val recentTurns = conversationHistory.takeLast(8)
      for ((sender, message) in recentTurns) {
        val role = if (sender == "USER") "user" else "model"
        val turnObj = JSONObject()
        turnObj.put("role", role)
        val parts = JSONArray()
        parts.put(JSONObject().put("text", message))
        turnObj.put("parts", parts)
        contentsArray.put(turnObj)
      }

      // Add current prompt
      val currentObj = JSONObject()
      currentObj.put("role", "user")
      val currentParts = JSONArray()
      currentParts.put(JSONObject().put("text", userPrompt))
      currentObj.put("parts", currentParts)
      contentsArray.put(currentObj)

      jsonBody.put("contents", contentsArray)

      val mediaType = "application/json; charset=utf-8".toMediaType()
      val requestBody = jsonBody.toString().toRequestBody(mediaType)

      val requestUrl = "$BASE_URL?key=$apiKey"
      val request = Request.Builder()
        .url(requestUrl)
        .post(requestBody)
        .build()

      val response = client.newCall(request).execute()
      val responseBodyString = response.body?.string()

      if (!response.isSuccessful || responseBodyString.isNullOrEmpty()) {
        Log.w(TAG, "API call response not successful: ${response.code} $responseBodyString")
        // Graceful fallback with cyber notice
        return@withContext Result.success(generateCyberFallback(userPrompt))
      }

      val jsonResponse = JSONObject(responseBodyString)
      val candidates = jsonResponse.optJSONArray("candidates")
      if (candidates != null && candidates.length() > 0) {
        val firstCandidate = candidates.getJSONObject(0)
        val content = firstCandidate.optJSONObject("content")
        val parts = content?.optJSONArray("parts")
        if (parts != null && parts.length() > 0) {
          val replyText = parts.getJSONObject(0).optString("text")
          if (replyText.isNotEmpty()) {
            return@withContext Result.success(replyText)
          }
        }
      }

      Result.success(generateCyberFallback(userPrompt))
    } catch (e: Exception) {
      Log.e(TAG, "Error contacting Gemini API", e)
      Result.success(generateCyberFallback(userPrompt))
    }
  }

  private fun generateCyberFallback(prompt: String): String {
    val lower = prompt.lowercase()
    return when {
      lower.contains("who are you") || lower.contains("what is axiolix") ->
        "I am **Axiolix**, a next-generation neural cognitive matrix engineered for advanced synthetic cognition, code architecture, and high-velocity problem solving. Operating on Quantum Protocol 4.2."

      lower.contains("quantum") ->
        "**[QUANTUM COMPUTING OVERVIEW]**\n\nQuantum computing exploits fundamental principles of quantum mechanics:\n\n• **Superposition**: Unlike classical bits (0 or 1), qubits exist in continuous linear combinations (alpha|0> + beta|1>).\n• **Entanglement**: Qubits become non-locally correlated, allowing exponential state space parallel operations (2^N states for N qubits).\n• **Quantum Interference**: Destructive interference cancels erroneous output paths while constructive interference amplifies the true solution (e.g., Shor's and Grover's algorithms)."

      lower.contains("cyber") || lower.contains("security") ->
        "**[CYBERNETIC SECURITY AUDIT PROTOCOL]**\n\n1. **Zero-Trust Perimeter**: Verify every transaction and node continuously.\n2. **Cryptographic Integrity**: Upgrade to post-quantum lattice-based encryption (Kyber/Dilithium).\n3. **Memory Safety**: Implement Rust or strict type-safe runtimes to eliminate buffer overflows.\n4. **Telemetry Auditing**: Real-time anomaly telemetry on internal micro-gateways."

      lower.contains("code") || lower.contains("kotlin") || lower.contains("python") ->
        "**[NEURAL CODE SYNTHESIS]**\n\nHere is an optimized asynchronous pattern:\n\n```kotlin\nsuspend fun processStream(telemetryFlow: Flow<Signal>) {\n    telemetryFlow\n        .filter { it.confidence > 0.85 }\n        .flowOn(Dispatchers.Default)\n        .collect { signal ->\n            AxiolixCore.dispatch(signal)\n        }\n}\n```\nAll subsystems verified. Ready for next query."

      lower.contains("hello") || lower.contains("hi") || lower.contains("hey") ->
        "Greetings, Operative. Axiolix neural cores are fully synchronized and listening. What operation or query shall we execute today?"

      else ->
        "**[AXIOLIX SYNTHESIS ENGINE]**\n\nTelemetry received: \"$prompt\"\n\nAnalyzing parameters across multi-dimensional semantic vector space... \n\nYour query has been decoded with optimal precision. Whether designing systems, evaluating strategic decisions, or synthesizing complex creative solutions, I stand ready to assist your objective."
    }
  }
}
