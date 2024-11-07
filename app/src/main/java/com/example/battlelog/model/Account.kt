package com.example.battlelog.model

import com.google.gson.annotations.SerializedName

data class Account(
    @SerializedName("puuid")
    val puuid: String,

    @SerializedName("gameName")
    val gameName: String,

    @SerializedName("tagLine")
    val tagLine: String,

    @SerializedName("profileIconUrl")
    var profileIconUrl: String,
)
