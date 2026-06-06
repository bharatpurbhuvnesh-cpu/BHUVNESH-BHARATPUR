package com.example.api

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateRaceCommentary(
        parentHeartRate: Int,
        parentSpeedKmh: Double,
        distanceMeters: Double,
        durationSecs: Int,
        babyState: String,
        babyHeartRate: Int,
        vibrationState: String,
        opponentName: String,
        winnerName: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "API Key is missing or default!")
            return@withContext "API Configuration Error: Please set your GEMINI_API_KEY in the Secrets panel in AI Studio. (Simulated Commentator): Wow, what a spectacular dash on the very high running field! Parent flew across the line at ${String.format("%.1f", parentSpeedKmh)} km/h while baby was in a '$babyState' state. The victory belongs to $winnerName!"
        }

        val prompt = """
            Please analyze this baby-stroller race and generate an exciting commentary:
            RACE SUMMARY:
            - Distance: ${String.format("%.0f", distanceMeters)} meters
            - Duration: $durationSecs seconds
            - Opponent: $opponentName
            - SECURED WINNER: $winnerName on this very high running field!
            
            PARENT METRICS:
            - Average Parent Heart Rate: $parentHeartRate BPM
            - Runner Pace: ${String.format("%.1f", parentSpeedKmh)} km/h
            
            BABY CARRIER STATE:
            - Baby State during Run: $babyState
            - Baby Heart Rate: $babyHeartRate BPM
            - Stroller Vibration Level: $vibrationState
            
            Write an energetic, witty, and engaging play-by-play commentary from the perspectives of a grand athletic arena sports commentator. Highlight parent vitals, remark extensively on the baby's comfort vs stroller vibration, praise or playfully critique the runner, and declare $winnerName the undeniable ruler of the high running field! Keep it to 2-3 short, fun, action-packed paragraphs.
        """.trimIndent()

        try {
            // Build direct JSON
            val requestBodyJson = JSONObject().apply {
                val contentsArray = org.json.JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", org.json.JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                }
                put("contents", contentsArray)

                val systemInstructionJson = JSONObject().apply {
                    put("parts", org.json.JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", "You are an expert, lively, and incredibly entertaining sports commentator for the Grand Baby-Stroller Championship. You analyze runs, track metrics, evaluate safety, and declare champions of the 'very high running field' with style, energetic humor, and precision.")
                        })
                    })
                }
                put("systemInstruction", systemInstructionJson)

                val configJson = JSONObject().apply {
                    put("temperature", 0.8)
                }
                put("generationConfig", configJson)
            }

            val requestBody = requestBodyJson.toString().toRequestBody("application/json".toMediaType())
            
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                val errBody = response.body?.string() ?: ""
                Log.e(TAG, "Unsuccessful response from Gemini: Code ${response.code}, Body: $errBody")
                return@withContext "The arena commentary tower is temporarily experiencing signal static! However, you dashed across the finish line beautifully. (Winner: $winnerName, Speed: ${String.format("%.1f", parentSpeedKmh)} km/h, Baby: $babyState)."
            }

            val respString = response.body?.string() ?: ""
            val jsonResponse = JSONObject(respString)
            val candidates = jsonResponse.getJSONArray("candidates")
            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.getJSONObject("content")
            val parts = content.getJSONArray("parts")
            val text = parts.getJSONObject(0).getString("text")

            text
        } catch (e: Exception) {
            Log.e(TAG, "Exception during Gemini generation: ${e.message}", e)
            "A static crackle on the microphone! The commentator lost voice: '${e.localizedMessage}'. But the finish line photo confirms: $winnerName wins! Average speed was ${String.format("%.1f", parentSpeedKmh)} km/h."
        }
    }
}
