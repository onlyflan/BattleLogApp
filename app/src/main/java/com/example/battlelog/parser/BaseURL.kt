package com.example.battlelog.parser

import kotlinx.coroutines.runBlocking

class BaseUrl {
    companion object {
        private val latestVersion: String by lazy {
            runBlocking {
                VersionParser().getLatestVersion()
            }
        }

        val RIOT_DATA_DRAGON_GET_CHAMPION_SQUARE
                get() = "https://ddragon.leagueoflegends.com/cdn/$latestVersion/img/champion/"
        val RIOT_DATA_DRAGON_GET_ITEM_IMAGE
            get() = "https://ddragon.leagueoflegends.com/cdn/$latestVersion/img/item/"
        val RIOT_DATA_DRAGON_GET_SPELL_IMAGE
            get() = "https://ddragon.leagueoflegends.com/cdn/$latestVersion/img/spell/"
        const val RIOT_DATA_DRAGON_GET_RUNE_IMAGE = "https://ddragon.leagueoflegends.com/cdn/img/"
        val RIOT_DATA_DRAGON_CHAMPION
            get() = "https://ddragon.leagueoflegends.com/cdn/$latestVersion/data/vi_VN/champion.json"
        val RIOT_DATA_DRAGON_GET_CHAMPION_SPLASH
            get() = "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/"
        val RIOT_DATA_DRAGON_GET_CHAMPION_LOADING
            get() = "https://ddragon.leagueoflegends.com/cdn/img/champion/loading/"
    }
}
