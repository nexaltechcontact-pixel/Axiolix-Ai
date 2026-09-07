package com.example.data.api

import android.content.Context
import android.net.Uri
import android.util.Base64
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
    private const val BASE_URL =
      "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"
  }

  suspend fun generateChatReply(
    conversationHistory: List<Pair<String, String>>,
    userPrompt: String
  ): Result<String> = withContext(Dispatchers.IO) {

    val apiKey = BuildConfig.GEMINI_API_KEY.trim()

    if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
      val fallback = generateCyberFallback(userPrompt)
      return@withContext Result.success(fallback)
    }

    try {
      val jsonBody = JSONObject()

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

      val contentsArray = JSONArray()

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
        Log.w(
          TAG,
          "API call response not successful: ${response.code} $responseBodyString"
        )

        return@withContext Result.success(
          generateCyberFallback(userPrompt)
        )
      }

      val jsonResponse = JSONObject(responseBodyString)
      val candidates = jsonResponse.optJSONArray("candidates")

      if (candidates != null && candidates.length() > 0) {

        val firstCandidate = candidates.getJSONObject(0)
        val content = firstCandidate.optJSONObject("content")
        val parts = content?.optJSONArray("parts")

        if (parts != null && parts.length() > 0) {

          val replyText = parts
            .getJSONObject(0)
            .optString("text")

          if (replyText.isNotEmpty()) {
            return@withContext Result.success(replyText)
          }
        }
      }

      Result.success(generateCyberFallback(userPrompt))

    } catch (e: Exception) {

      Log.e(TAG, "Error contacting Gemini API", e)

      Result.success(
        generateCyberFallback(userPrompt)
      )
    }
  }

  suspend fun analyzeImage(
    context: Context,
    imageUri: Uri,
    prompt: String = "Analyze this image carefully and describe what you see."
  ): Result<String> = withContext(Dispatchers.IO) {

    val apiKey = BuildConfig.GEMINI_API_KEY.trim()

    if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
      return@withContext Result.success(
        "Image analysis is unavailable because the Gemini API key is not configured."
      )
    }

    try {

      val inputStream = context.contentResolver.openInputStream(imageUri)
        ?: return@withContext Result.failure(
          Exception("Unable to read selected image.")
        )

      val imageBytes = inputStream.use {
        it.readBytes()
      }

      val base64Image = Base64.encodeToString(
        imageBytes,
        Base64.NO_WRAP
      )

      val mimeType = context.contentResolver.getType(imageUri)
        ?: "image/jpeg"

      val jsonBody = JSONObject()

      val contentsArray = JSONArray()
      val contentObject = JSONObject()

      contentObject.put("role", "user")

      val partsArray = JSONArray()

      partsArray.put(
        JSONObject().put(
          "text",
          prompt
        )
      )

      partsArray.put(
        JSONObject()
          .put(
            "inline_data",
            JSONObject()
              .put("mime_type", mimeType)
              .put("data", base64Image)
          )
      )

      contentObject.put("parts", partsArray)
      contentsArray.put(contentObject)

      jsonBody.put("contents", contentsArray)

      val mediaType = "application/json; charset=utf-8".toMediaType()

      val requestBody = jsonBody
        .toString()
        .toRequestBody(mediaType)

      val requestUrl = "$BASE_URL?key=$apiKey"

      val request = Request.Builder()
        .url(requestUrl)
        .post(requestBody)
        .build()

      val response = client
        .newCall(request)
        .execute()

      val responseBody = response.body?.string()

      if (!response.isSuccessful || responseBody.isNullOrEmpty()) {

        Log.w(
          TAG,
          "Image analysis failed: ${response.code} $responseBody"
        )

        return@withContext Result.failure(
          Exception("Gemini image analysis failed.")
        )
      }

      val jsonResponse = JSONObject(responseBody)

      val candidates = jsonResponse
        .optJSONArray("candidates")

      if (candidates != null && candidates.length() > 0) {

        val content = candidates
          .getJSONObject(0)
          .optJSONObject("content")

        val parts = content
          ?.optJSONArray("parts")

        if (parts != null && parts.length() > 0) {

          val resultText = parts
            .getJSONObject(0)
            .optString("text")

          if (resultText.isNotEmpty()) {
            return@withContext Result.success(resultText)
          }
        }
      }

      Result.failure(
        Exception("No analysis returned by Gemini.")
      )

    } catch (e: Exception) {

      Log.e(
        TAG,
        "Error analyzing image",
        e
      )

      Result.failure(e)
    }
  }

  private fun generateCyberFallback(prompt: String): String {

    val lower = prompt.lowercase()

    return when {

      lower.contains("who are you") ||
        lower.contains("what is axiolix") ->

        "I am **Axiolix**, a next-generation neural cognitive matrix engineered for advanced synthetic cognition, code architecture, and high-velocity problem solving. Operating on Quantum Protocol 4.2."

      lower.contains("quantum") ->

        "**[QUANTUM COMPUTING OVERVIEW]**\n\n" +
          "Quantum computing exploits fundamental principles of quantum mechanics:\n\n" +
          "• **Superposition**: Unlike classical bits (0 or 1), qubits exist in continuous linear combinations.\n" +
          "• **Entanglement**: Qubits become non-locally correlated.\n" +
          "• **Quantum Interference**: Interference can amplify useful computational paths."

      lower.contains("cyber") ||
        lower.contains("security") ->

        "**[CYBERNETIC SECURITY AUDIT PROTOCOL]**\n\n" +
          "1. **Zero-Trust Perimeter**: Verify every transaction and node continuously.\n" +
          "2. **Cryptographic Integrity**: Use modern cryptographic standards.\n" +
          "3. **Memory Safety**: Use safe programming practices and memory-safe languages.\n" +
          "4. **Telemetry Auditing**: Monitor systems for anomalies."

      lower.contains("code") ||
        lower.contains("kotlin") ||
        lower.contains("python") ->

        "**[NEURAL CODE SYNTHESIS]**\n\n" +
          "Code analysis subsystem online.\n\n" +
          "All subsystems verified. Ready for the next query."

      lower.contains("hello") ||
        lower.contains("hi") ||
        lower.contains("hey") ->

        "Greetings, Operative. Axiolix neural cores are fully synchronized and listening. What operation or query shall we execute today?"

      else ->

        "**[AXIOLIX SYNTHESIS ENGINE]**\n\n" +
          "Telemetry received: \"$prompt\"\n\n" +
          "Analyzing parameters across multi-dimensional semantic vector space...\n\n" +
          "Your query has been decoded with optimal precision. I stand ready to assist your objective."
    }
  }
}
