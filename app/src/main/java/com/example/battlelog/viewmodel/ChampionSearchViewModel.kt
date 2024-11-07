package com.example.battlelog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battlelog.adapter.ChampionSearchService
import com.example.battlelog.model.ChampionSearch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChampionSearchViewModel : ViewModel() {
    private val _champions = MutableStateFlow<List<ChampionSearch>>(emptyList())
    val champions: StateFlow<List<ChampionSearch>> = _champions

    private val championSearchService = ChampionSearchService()

    init {
        loadChampions()
    }

    private fun loadChampions() {
        viewModelScope.launch(Dispatchers.IO) {
            val championSearchList = championSearchService.fetchAllChampionNamesAndImages().map { (name, imageUrl) ->
                ChampionSearch(name, imageUrl)
            }
            _champions.value = championSearchList
        }
    }
}