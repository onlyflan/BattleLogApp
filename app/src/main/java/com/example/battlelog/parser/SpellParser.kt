package com.example.battlelog.parser

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class SpellParser(private val context: Context) {

    suspend fun getSpellName(spellKey: Int): String {
        var spellName = ""
        try {
            // Lấy phiên bản và ngôn ngữ mới nhất
            val versionParser = VersionParser()
            val languageParser = LanguageParser()
            val latestVersion = versionParser.getLatestVersion()
            val language = languageParser.getLanguage()

            // Tạo URL cho JSON dữ liệu phép bổ trợ
            val url = "https://ddragon.leagueoflegends.com/cdn/$latestVersion/data/$language/summoner.json"

            // Chuyển thao tác mạng sang IO thread
            val json = withContext(Dispatchers.IO) {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.inputStream.bufferedReader().use { it.readText() }
            }

            Log.d("TESTLOG", "[getSpellName] JSON response: $json")

            val jsonObject = JSONObject(json)
            val dataObject = jsonObject.getJSONObject("data")
            val iterator: Iterator<*> = dataObject.keys()

            while (iterator.hasNext()) {
                val name = iterator.next().toString()
                val spellObject = dataObject.getJSONObject(name)
                if (spellKey.toString() == spellObject.getString("key")) {
                    spellName = name
                    break
                }
            }
        } catch (e: Exception) {
            Log.d("TESTLOG", "[getSpellName] exception: $e")
            e.printStackTrace()
        }
        return spellName
    }
}
