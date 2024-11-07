package com.example.battlelog.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.battlelog.viewmodel.SummonerSearchViewModel
import com.noahkohrs.riot.api.RiotApi

class SummonerSearchViewModelFactory(private val riotApi: RiotApi) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SummonerSearchViewModel::class.java)) {
            return SummonerSearchViewModel(riotApi) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
