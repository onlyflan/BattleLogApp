package com.example.battlelog

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.battlelog.ui.theme.BatteLogTheme
import com.example.battlelog.view.BottomBar
import com.example.battlelog.view.FreeChampions
import com.example.battlelog.view.Navigate
import com.example.battlelog.view.NavigateSearchSummoner
import com.example.battlelog.view.Routes
import com.example.battlelog.view.SearchBar
import com.example.battlelog.view.SummonerSearch
import com.example.battlelog.view.TierList
import com.example.battlelog.view.TierListChampion
import com.example.battlelog.view.TopBar
import com.example.battlelog.view.championSearch
import com.example.battlelog.view.rotationsChampionDetail

import com.noahkohrs.riot.api.RiotApi
import com.noahkohrs.riot.api.values.Platform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



class MainActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Navigate()
        }


    }
}





