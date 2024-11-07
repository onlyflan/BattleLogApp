package com.example.battlelog.model

import com.google.gson.annotations.SerializedName

data class ChampionTierList(
    @SerializedName("name")
    var name: String,
    @SerializedName("imageUrl")
    var imageUrl: String,
    @SerializedName("position")
    var position: String,
    @SerializedName("winRate")
    var winRate: Double,
    @SerializedName("banRate")
    var banRate: Double,
    @SerializedName("pickRate")
    var pickRate: Double,
    @SerializedName("tier")
    var tier: String,
)
