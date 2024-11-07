package com.example.battlelog.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.battlelog.R
import com.example.battlelog.ui.theme.BatteLogTheme
import com.example.battlelog.viewmodel.HomeViewModel
import com.noahkohrs.riot.api.RiotApi
import com.noahkohrs.riot.api.values.Platform

var riotApi =
    RiotApi(
        apiKey = "RGAPI-fdcf049c-86d0-47af-9bb7-a1250f0c8f98",
        Platform.VN2,
    )

@Composable

fun Navigate(){
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val context = LocalContext.current
    NavHost(
        navController = navController,
        startDestination = Routes.home,
        builder = {
            composable(Routes.home){
                BatteLogTheme {
                    Scaffold(
                        topBar = { TopBar(Modifier)},
                        bottomBar = { BottomBar(Modifier, navController) },
                    ){
                            paddingValues ->
                        Column(
                            modifier = Modifier
                                .padding(paddingValues)
                                .verticalScroll(
                                    rememberScrollState()
                                ),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            NavigateSearchSummoner(Modifier, stringResource(R.string.search_placeholder), navController)
                            TierList(Modifier, navController)
                            FreeChampions(Modifier, riotApi, navController)
                        }

                    }
                }
            }
            composable(Routes.champion_Search){
                BatteLogTheme {
                    championSearch(navController)
                }
            }
            composable(Routes.summoner_Search){
                BatteLogTheme {
                    SummonerSearch(navController, riotApi, context)
                }
            }
            composable(Routes.tierList_Detail){
                BatteLogTheme {
                    TierListChampion(navController)
                }
            }
            composable(Routes.rotationChampion_Detail){
                BatteLogTheme {
                    rotationsChampionDetail(navController, riotApi, homeViewModel)
                }
            }
            composable(Routes.profileDetail) { backStackEntry ->
                val puuid = backStackEntry.arguments?.getString("puuid") ?: ""
                ProfileDetail(puuid = puuid, riotApi = riotApi, navController = navController)
            }
            composable(Routes.matchDetail) { backStackEntry ->
                val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
                val puuid = backStackEntry.arguments?.getString("puuid") ?: ""
                MatchDetail(matchId = matchId, puuid = puuid, navController = navController, riotApi = riotApi)
            }

//            composable(Routes.matchDetail){
//                // Cần truyền data fetch ở đây sau khi search https://www.youtube.com/watch?v=bj6OjSjPM1I backstack entry
//                BatteLogTheme {
//                    MatchDetail(navController)
//                }
//            }
            composable(Routes.championDetail){

                BatteLogTheme {
                    ChampionDetail(navController)
                }
            }
            composable(Routes.settings){
                BatteLogTheme {
                    SettingsView(navController)
                }
            }
            composable(Routes.liveGame) { backStackEntry ->
                val puuid = backStackEntry.arguments?.getString("puuid") ?: ""
                LiveGameView(puuid = puuid, riotApi = riotApi, navController = navController)
            }
        }
    )
}