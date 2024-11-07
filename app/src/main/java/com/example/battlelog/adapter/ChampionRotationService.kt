package com.example.battlelog.adapter

import android.content.Context
import com.example.battlelog.parser.BaseUrl.Companion.RIOT_DATA_DRAGON_CHAMPION
import com.example.battlelog.parser.BaseUrl.Companion.RIOT_DATA_DRAGON_GET_CHAMPION_LOADING
import com.example.battlelog.parser.BaseUrl.Companion.RIOT_DATA_DRAGON_GET_CHAMPION_SPLASH
import com.noahkohrs.riot.api.RiotApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.URL

class ChampionRotationService{
    fun getFreeChampionImages(
        riotApi: RiotApi,
        type: String,
        context: Context
    ): List<Triple<String, String, String>> {
        val championRotation = riotApi.lol.champion.getChampionRotations()
        val championData = fetchChampionData(type)

        return championRotation.freeChampionIds.mapNotNull { championId ->
            championData[championId.toString()]
        }
    }

    fun fetchChampionData(type: String): Map<String, Triple<String, String, String>> {
        val championJsonUrl = RIOT_DATA_DRAGON_CHAMPION
        val championDataStr = URL(championJsonUrl).readText()
        val json: JsonObject =
            Json.parseToJsonElement(championDataStr).jsonObject["data"]?.jsonObject ?: JsonObject(
                emptyMap()
            )

        return json.mapNotNull { entry ->
            val championObj = entry.value.jsonObject
            val key = championObj["key"]?.jsonPrimitive?.content
            val name = championObj["id"]?.jsonPrimitive?.content
            val title = championObj["title"]?.jsonPrimitive?.content
            val championName = championObj["name"]?.jsonPrimitive?.content

            val championImageURL = when (type) {
                "splash" -> "${RIOT_DATA_DRAGON_GET_CHAMPION_SPLASH}${name}_0.jpg"
                "loading" -> "${RIOT_DATA_DRAGON_GET_CHAMPION_LOADING}${name}_0.jpg"
                else -> null
            }

            if (key != null && name != null && title != null && championName != null && championImageURL != null) {
                key to Triple(championName, championImageURL, title)
            } else {
                null
            }
        }.toMap()
    }
}


