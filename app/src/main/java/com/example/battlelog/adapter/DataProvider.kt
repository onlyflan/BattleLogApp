package com.example.battlelog.adapter

import android.content.Context
import com.example.battlelog.model.ChampionTierList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

object DataProvider {
    fun loadChampionTierList(context: Context): List<ChampionTierList> {
        val inputStream = context.assets.open("tier_list.json")
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<List<ChampionTierList>>() {}.type
        return Gson().fromJson(reader, type)
    }
}