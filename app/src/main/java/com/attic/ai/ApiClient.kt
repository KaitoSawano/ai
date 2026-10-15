package com.attic.ai

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val PROXY_URL = "https://attic-ai-proxy.aldianokto.workers.dev"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    fun chat(history: List<Pair<String, String>>): String {
        val messages = JSONArray()
        history.forEach { (role, content) ->
            messages.put(JSONObject().apply {
                put("role", role)
                put("content", content)
            })
        }

        val body = JSONObject().put("messages", messages)
            .toString()
            .toRequestBody("application/json".toMediaType())

        val req = Request.Builder()
            .url(PROXY_URL)
            .post(body)
            .build()

        client.newCall(req).execute().use { resp ->
            val txt = resp.body?.string() ?: return "Error: empty"
            val json = JSONObject(txt)
            if (json.has("error")) return "Error: ${json.get("error")}"
            return json.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
        }
    }
}
