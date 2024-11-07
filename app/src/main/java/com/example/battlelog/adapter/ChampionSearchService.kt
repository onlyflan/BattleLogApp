package com.example.battlelog.adapter

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.URL

class ChampionSearchService {
    fun fetchAllChampionNamesAndImages(): List<Pair<String, String>> {
        val championJsonUrl = "https://ddragon.leagueoflegends.com/cdn/14.21.1/data/vi_VN/champion.json"
        val championDataStr = URL(championJsonUrl).readText()
        val json: JsonObject = Json.parseToJsonElement(championDataStr).jsonObject["data"]?.jsonObject ?: JsonObject(emptyMap())

        val champions = json.mapNotNull { entry ->
            val championObj = entry.value.jsonObject
            val name = championObj["name"]?.jsonPrimitive?.content
            val id = championObj["id"]?.jsonPrimitive?.content
            if (name != null && id != null) {
                val imageUrl = "https://ddragon.leagueoflegends.com/cdn/14.20.1/img/champion/${id}.png"
                name to imageUrl
            } else {
                null
            }
        }

        return champions
    }
}