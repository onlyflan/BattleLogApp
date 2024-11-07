package com.example.battlelog.model

import com.google.gson.annotations.SerializedName

data class AccountSearch(
    @SerializedName("puuid")
    val puuid: String,

    @SerializedName("gameName")
    val gameName: String,

    @SerializedName("tagLine")
    val tagLine: String,

    @SerializedName("division")
    val division: String,

    @SerializedName("tier")
    val tier: String,

    @SerializedName("profileIconUrl")
    var profileIconUrl: String,
)