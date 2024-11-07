package com.example.battlelog.parser

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONArray

class VersionParser {

    suspend fun getLatestVersion(): String {
        return withContext(Dispatchers.IO) {
            var latestVersion = ""
            try {
                val url = URL("https://ddragon.leagueoflegends.com/api/versions.json")
                val connection = url.openConnection() as HttpURLConnection
                connection.inputStream.use { inputStream ->
                    val buffer = inputStream.readBytes()
                    val json = String(buffer)

                    Log.d("TESTLOG", "[getLatestVersion] JSON response: $json")

                    val jsonArray = JSONArray(json)
                    if (jsonArray.length() > 0) {
                        latestVersion += jsonArray.getString(0)
                    }
                }
            } catch (e: Exception) {
                Log.d("TESTLOG", "[getLatestVersion] exception: $e")
                e.printStackTrace()
            }
            latestVersion
        }
    }
}
