package com.example.battlelog.model

import com.google.gson.annotations.SerializedName

data class ChampionRotation(
    @SerializedName("name")
    var name: String,
    @SerializedName("imageUrl")
    var imageUrl: String,
    @SerializedName("title")
    var title: String,
)
