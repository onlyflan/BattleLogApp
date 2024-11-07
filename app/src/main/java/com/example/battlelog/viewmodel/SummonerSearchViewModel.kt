package com.example.battlelog.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battlelog.model.AccountSearch
import com.google.gson.Gson
import com.noahkohrs.riot.api.RiotApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SummonerSearchViewModel(private val riotApi: RiotApi) : ViewModel() {
    var currentSearchKey by mutableStateOf("")
    var validSummoner: AccountSearch? by mutableStateOf(null)
    var showErrorMessage by mutableStateOf(false)
    var isLoading by mutableStateOf(false)

    // Load cached recent search list on start
    fun loadCachedRecentList(context: Context): List<AccountSearch> {
        val sharedPreferences = context.getSharedPreferences("RecentSummoners", Context.MODE_PRIVATE)
        val jsonString = sharedPreferences.getString("summoner_list", null)
        return if (!jsonString.isNullOrEmpty()) {
            Log.d("SummonerSearchViewModel", "Loaded cached recent list from SharedPreferences.")
            parseJsonToAccounts(jsonString)
        } else {
            Log.d("SummonerSearchViewModel", "No cached recent list found.")
            emptyList()
        }
    }

    fun searchSummoner(name: String, tag: String) {
        Log.d("SummonerSearchViewModel", "Starting search for summoner: $name, $tag")
        viewModelScope.launch {
            isLoading = true
            withContext(Dispatchers.IO) {
                try {
                    val account = riotApi.account.getAccountByRiotId(name, tag)

                    val trimmedName = name.trim()
                    val trimmedTag = tag.trim()

                    if (account.gameName.equals(trimmedName, ignoreCase = true) &&
                        account.tagLine.equals(trimmedTag, ignoreCase = true)
                    ) {
                        Log.d("SummonerSearchViewModel", "Account found: ${account.puuid}")
                        val puuid = account.puuid
                        val summoner = riotApi.lol.summoner.getSummonerByPuuid(puuid)
                        val profileIconId = summoner.profileIconId
                        val profileIconUrl = "https://ddragon.leagueoflegends.com/cdn/14.21.1/img/profileicon/${profileIconId}.png"
                        val summonerId = summoner.id
                        val leagueEntries =
                            riotApi.lol.league.getLeagueEntriesBySummoner(summonerId)
                        val leagueEntry = leagueEntries.firstOrNull()

                        validSummoner = AccountSearch(
                            puuid = puuid,
                            gameName = trimmedName,
                            tagLine = trimmedTag,
                            division = leagueEntry?.division?.toString() ?: "",
                            tier = leagueEntry?.tier?.toString() ?: "UNRANKED",
                            profileIconUrl = profileIconUrl
                        )
                        showErrorMessage = false
                        Log.d("SummonerSearchViewModel", "Valid summoner found and set.")
                    } else {
                        showErrorMessage = true
                        Log.d("SummonerSearchViewModel", "Account name and tag do not match.")
                    }
                } catch (e: Exception) {
                    showErrorMessage = true
                    Log.e("SummonerSearchViewModel", "Error in searching summoner", e)
                }
            }
            isLoading = false
            Log.d("SummonerSearchViewModel", "Search summoner process completed.")
        }
    }

    private fun parseJsonToAccounts(jsonString: String): List<AccountSearch> {
        return Gson().fromJson(jsonString, Array<AccountSearch>::class.java).toList().also {
            Log.d("SummonerSearchViewModel", "Parsed JSON to Account list.")
        }
    }
}
