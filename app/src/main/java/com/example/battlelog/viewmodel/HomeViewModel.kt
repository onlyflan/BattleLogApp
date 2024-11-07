package com.example.battlelog.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battlelog.adapter.ChampionRotationService
import com.example.battlelog.adapter.DataProvider
import com.example.battlelog.model.ChampionRotation
import com.example.battlelog.model.ChampionTierList
import com.noahkohrs.riot.api.RiotApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _freeChampions = MutableStateFlow<List<ChampionRotation>>(emptyList())
    val freeChampions: StateFlow<List<ChampionRotation>> = _freeChampions

    private val championRotationService = ChampionRotationService()

    fun loadFreeChampions(riotApi: RiotApi, imageType: String, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("HomeViewModel", "Loading free champions.")
                val championList = championRotationService.getFreeChampionImages(riotApi, imageType, context).map { (name, imageUrl, title) ->
                    ChampionRotation(name, imageUrl, title)
                }
                _freeChampions.value = championList
                Log.d("HomeViewModel", "Free champions loaded successfully: ${championList.size} found.")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading free champions", e)
            }
        }
    }
}
