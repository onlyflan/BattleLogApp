package com.example.battlelog.model

import com.google.gson.annotations.SerializedName

data class MatchHistory(
    @SerializedName("info")
    var info: Info
) {
    data class Info(
        @SerializedName("gameCreation")
        var gameEndTimestamp: Long,

        @SerializedName("gameDuration")
        var gameDuration: Long,

        @SerializedName("gameId")
        var gameId: Long,

        @SerializedName("queueId")
        var queueId: Int,

        @SerializedName("participants")
        var participants: ArrayList<Participants>,

        @SerializedName("matchId")
        var matchId: String,
    ) {
        data class Participants(
            @SerializedName("assists")
            var assists: Int,

            @SerializedName("championId")
            var championId: Int,

            @SerializedName("championName")
            var championName: String,

            @SerializedName("deaths")
            var deaths: Int,

            @SerializedName("firstBloodKill")
            var firstBloodKill: Boolean,

            @SerializedName("item0")
            var item0: Int,

            @SerializedName("item1")
            var item1: Int,

            @SerializedName("item2")
            var item2: Int,

            @SerializedName("item3")
            var item3: Int,

            @SerializedName("item4")
            var item4: Int,

            @SerializedName("item5")
            var item5: Int,

            @SerializedName("item6")
            var item6: Int,

            @SerializedName("kills")
            var kills: Int,

            @SerializedName("kda")
            var kda: Float,

            @SerializedName("lane")
            var lane: String,

            // 정글 미니언 처치 수
            @SerializedName("neutralMinionsKilled")
            var neutralMinionsKilled: Int,

            @SerializedName("participantId")
            var participantId: Int,

            @SerializedName("pentaKills")
            var pentaKills: Int,

            @SerializedName("riotIdGameName")
            var riotIdGameName: String,

            @SerializedName("riotIdTagline")
            var riotIdTagline: String,

            @SerializedName("puuid")
            var puuid: String,

            // 첫 번째 스펠
            @SerializedName("summoner1Id")
            var summoner1Id: Int,

            // 두 번째 스펠
            @SerializedName("summoner2Id")
            var summoner2Id: Int,

            @SerializedName("summonerId")
            var summonerId: String,

            @SerializedName("summonerName")
            var summonerName: String,

            @SerializedName("teamPosition")
            var teamPosition: String,

            // Số lượng tiêu diệt lính (trừ lính rừng)
            @SerializedName("totalMinionsKilled")
            var totalMinionsKilled: Int,

            @SerializedName("win")
            var win: Boolean,

            @SerializedName("teamId")
            var teamId: Long,

        )
    }
}