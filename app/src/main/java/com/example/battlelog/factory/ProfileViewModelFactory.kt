package com.example.battlelog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.noahkohrs.riot.api.RiotApi

class ProfileViewModelFactory(private val riotApi: RiotApi) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(riotApi) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}