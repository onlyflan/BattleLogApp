package com.example.battlelog.model

import com.google.gson.annotations.SerializedName

data class LiveGame(
    @SerializedName("gameId") val gameId: Long,
    @SerializedName("gameMode") val gameMode: String,
    @SerializedName("gameStartTime") val gameStartTime: Long,
    @SerializedName("mapId") val mapId: Long,
    @SerializedName("participants") val participants: List<Participant>,
    @SerializedName("bannedChampions") val bannedChampions: List<BannedChampion>
) {
    data class Participant(
        @SerializedName("summonerId") val summonerId: String,
        @SerializedName("riotId") val riotId: String,
        @SerializedName("championId") val championId: Long,
        @SerializedName("teamId") val teamId: Long,
        @SerializedName("spell1Id") val spell1Id: Long,
        @SerializedName("spell2Id") val spell2Id: Long,
        @SerializedName("perks") val perks: Perks
    )

    data class Perks(
        @SerializedName("perkStyle") val perkStyle: Long,
        @SerializedName("perkSubStyle") val perkSubStyle: Long,
        @SerializedName("perks") val perks: List<Long>
    )

    data class BannedChampion(
        @SerializedName("championId") val championId: Long,
        @SerializedName("teamId") val teamId: Long,
        @SerializedName("pickTurn") val pickTurn: Int
    )
}