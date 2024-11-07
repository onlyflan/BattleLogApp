package com.example.battlelog.model

import com.google.gson.annotations.SerializedName

data class SummonerRankInfo(
    /**
     * queueType: Loại hàng chờ: đơn/đôi, linh hoạt
     * tier: Hạng (Thách Đấu ~ Không Xếp Hạng)
     * rank: Bậc (I ~ IV)
     * leaguePoints: Điểm LP 
     * wins: Số trận thắng
     * losses: Số trận thua
     */


    @SerializedName("queueType")
    var queueType: String,

    @SerializedName("tier")
    var tier: String,

    @SerializedName("rank")
    var rank: String,

    @SerializedName("leaguePoints")
    var leaguePoints: Int,

    @SerializedName("wins")
    var wins: Int,

    @SerializedName("losses")
    var losses: Int

)