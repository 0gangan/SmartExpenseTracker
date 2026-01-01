package com.example.smart_expense_tracker.network

import com.example.smart_expense_tracker.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class DeepSeekApiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    suspend fun getAnalysis(prompt: String): Result<String> {
        // Switch to the IO dispatcher for the blocking network call
        return withContext(Dispatchers.IO) {
            try {
                val json = JSONObject()
                json.put("model", "deepseek-chat") // Specify the model
                val messages = JSONObject()
                messages.put("role", "user")
                messages.put("content", prompt)
                json.put("messages", org.json.JSONArray(arrayOf(messages)))

                val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

                val request = Request.Builder()
                    .url("https://api.deepseek.com/chat/completions")
                    .header("Authorization", "Bearer ${BuildConfig.DEEPSEEK_API_KEY}")
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        val errorBody = response.body?.string() ?: "Unknown API error"
                        return@withContext Result.failure<String>(IOException("API request failed with code: ${response.code}, message: $errorBody"))
                    }

                    val responseBody = response.body?.string()
                    if (responseBody == null) {
                        return@withContext Result.failure<String>(IOException("API returned an empty response body"))
                    }

                    // Parse JSON to extract "content"
                    val responseJson = JSONObject(responseBody)
                    val content = responseJson.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content")
                    Result.success(content)
                }
            } catch (e: Exception) {
                // Catch any other exceptions (network, JSON parsing, etc.)
                Result.failure(e)
            }
        }
    }
}
