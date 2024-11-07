package com.example.battlelog.parser

import android.content.Context
import android.util.Log
import com.example.battlelog.parser.LanguageParser
import com.example.battlelog.parser.VersionParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class RuneParser(private val context: Context) {

    private val versionParser = VersionParser()
    private val languageParser = LanguageParser()

    suspend fun getRuneIcon(runeId: Int): String {
        // 서브 룬 범주는 100 단위로 끊어지기 때문에
        val isSubRune = runeId % 100 == 0
        var icon = ""
        val version = versionParser.getLatestVersion()
        val language = languageParser.getLanguage()
        try {
            val url = "https://ddragon.leagueoflegends.com/cdn/$version/data/$language/runesReforged.json"

            // Chuyển thao tác mạng sang IO thread
            val json = withContext(Dispatchers.IO) {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.inputStream.use { inputStream ->
                    val buffer = inputStream.readBytes()
                    String(buffer, StandardCharsets.UTF_8)
                }
            }

            Log.d("TESTLOG", "[getRuneIcon] JSON response: $json")

            val jsonArray = JSONArray(json)

            for (i in 0 until jsonArray.length()) {
                val mainObject = jsonArray.getJSONObject(i)
                if (isSubRune) {
                    // 서브 룬 범주 아이콘(정밀, 지배, 마법, 결의, 영감)
                    if (mainObject["id"] == runeId) {
                        icon = mainObject.getString("icon")
                        Log.d("TESTLOG", "[getRuneIcon] Rune icon URL: $icon")
                        break
                    }
                } else {
                    // 메인 룬 아이콘(정복자, 감전 등..)
                    val slots = mainObject.getJSONArray("slots")
                    for (j in 0 until slots.length()) {
                        val slotObject = slots.getJSONObject(j)
                        val runes = slotObject.getJSONArray("runes")
                        for (k in 0 until runes.length()) {
                            val runeObject = runes.getJSONObject(k)
                            if (runeObject["id"] == runeId) {
                                icon = runeObject.getString("icon")
                                Log.d("TESTLOG", "[getRuneIcon] Rune icon URL: $icon")
                                break
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("TESTLOG", "[getRuneIcon] exception: $e")
            e.printStackTrace()
        }
        return icon
    }
}

