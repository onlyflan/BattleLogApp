package com.example.battlelog.model

import com.google.gson.annotations.SerializedName

data class ChampionSearch(
    @SerializedName("name")
    var name: String,
    @SerializedName("imageUrl")
    var imageUrl: String,
)

