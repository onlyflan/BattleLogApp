package com.example.battlelog.parser

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class QueueParser(private val context: Context) {

    suspend fun getQueueType(queueId: Int): String {
        var queueDescription = ""
        try {
            val url = "https://static.developer.riotgames.com/docs/lol/queues.json"

            // Shift network operation to IO thread using coroutines
            val json = withContext(Dispatchers.IO) {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.inputStream.use { inputStream ->
                    val buffer = inputStream.readBytes()
                    String(buffer, StandardCharsets.UTF_8)
                }
            }

            Log.d("TESTLOG", "[getQueueType] JSON response: $json")

            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                if (jsonObject["queueId"] == queueId) {
                    queueDescription = jsonObject.getString("description")
                    break
                }
            }
        } catch (e: Exception) {
            Log.d("TESTLOG", "[getQueueType] exception: $e")
            e.printStackTrace()
        }

        return queueDescription
    }
}
