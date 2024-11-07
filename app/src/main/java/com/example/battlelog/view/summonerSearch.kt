package com.example.battlelog.view

import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.battlelog.R
import com.example.battlelog.factory.SummonerSearchViewModelFactory
import com.example.battlelog.model.AccountSearch
import com.example.battlelog.ui.theme.BatteLogTheme
import com.example.battlelog.ui.theme.bronzeColor
import com.noahkohrs.riot.api.RiotApi
import com.example.battlelog.viewmodel.SummonerSearchViewModel
import com.google.gson.Gson

@Composable
fun SummonerSearch(
    navController: NavController,
    riotApi: RiotApi,
    context: Context
) {
    val viewModel: SummonerSearchViewModel = viewModel(factory = SummonerSearchViewModelFactory(riotApi))
    val summonerRecentList = remember { mutableStateListOf<AccountSearch>() }

    LaunchedEffect(Unit) {
        summonerRecentList.addAll(viewModel.loadCachedRecentList(context))
    }

    BatteLogTheme {
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier.padding(top = 50.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    IconButton(
                        onClick = {
                            navController.navigate(Routes.home)
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.angle_left_solid),
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                                .offset(x = 10.dp)
                        )
                    }
                    SearchBar(
                        modifier = Modifier,
                        placeholder = stringResource(R.string.search_placeholder),
                        value = viewModel.currentSearchKey,
                        onChange = { viewModel.currentSearchKey = it },
                        onEnterPress = {
                            val parts = viewModel.currentSearchKey.split("#")
                            if (parts.size == 2) {
                                viewModel.searchSummoner(parts[0], parts[1])
                            } else {
                                viewModel.showErrorMessage = true
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when {
                    viewModel.isLoading -> {
                        // Replacing text indicator with a loading spinner
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 20.dp)
                        )
                    }
                    viewModel.showErrorMessage -> {
                        Text(
                            text = stringResource(R.string.unidentified),
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 20.dp)
                        )
                    }
                    viewModel.validSummoner != null -> {
                        SummonerProfile(
                            summoner = viewModel.validSummoner!!,
                            navController = navController,
                            summonerRecentList = summonerRecentList,
                            context = context
                        )
                    }
                    viewModel.currentSearchKey.isEmpty() -> {
                        SearchRecent(navController, summonerRecentList, context = context)
                    }
                }
            }
        }
    }
}

@Composable
fun SummonerProfile(
    summoner: AccountSearch,
    navController: NavController,
    summonerRecentList: SnapshotStateList<AccountSearch>,
    context: Context
) {
    Row(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .clickable {
                if (!summonerRecentList.any { it.puuid == summoner.puuid }) {
                    summonerRecentList.add(summoner)
                    saveCachedRecentList(context, summonerRecentList)
                }
                navController.navigate("profile_Detail/${summoner.puuid}")
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box {
            Image(
                painter = rememberAsyncImagePainter(model = summoner.profileIconUrl),
                contentDescription = null,
                modifier = Modifier.size(38.dp)
            )
            Column(
                modifier = Modifier
                    .padding(start = 50.dp, end = 20.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = summoner.gameName,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "#${summoner.tagLine}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = if (summoner.tier == "null" || summoner.division == "null") {
                        "UNRANKED"
                    } else {
                        "${summoner.tier} ${summoner.division}"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = bronzeColor
                )
            }
        }
    }
}

@Composable
fun SearchRecent(
    navController: NavController,
    summonerRecentList: SnapshotStateList<AccountSearch>,
    context: Context
) {
    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tìm kiếm gần đây",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.alignByBaseline()
            )
            Text(
                text = "Xóa lịch sử",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .alignByBaseline()
                    .clickable {
                        summonerRecentList.clear()
                        saveCachedRecentList(context, summonerRecentList)
                    }
            )
        }
        summonerRecentList.forEach { summoner ->
            summonerRecent(summoner, navController = navController, onDelete = {
                summonerRecentList.remove(summoner)
                saveCachedRecentList(context, summonerRecentList)
            })
        }
    }
}

@Composable
fun summonerRecent(
    summoner: AccountSearch,
    onDelete: () -> Unit,
    navController: NavController
) {
    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate("profile_Detail/${summoner.puuid}")
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier.clickable {
                navController.navigate("profile_Detail/${summoner.puuid}")
            }
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = summoner.profileIconUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(38.dp)
            )
            Column(
                modifier = Modifier
                    .padding(start = 50.dp, end = 20.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = summoner.gameName,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "#${summoner.tagLine}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = if (summoner.tier == "null") {
                        "UNRANKED"
                    } else {
                        "${summoner.tier} ${summoner.division}"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = bronzeColor
                )
            }
        }
        IconButton(
            onClick = {
                onDelete()
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.xmark_solid),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

fun saveCachedRecentList(context: Context, summonerRecentList: List<AccountSearch>) {
    val sharedPreferences = context.getSharedPreferences("RecentSummoners", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    val jsonString = Gson().toJson(summonerRecentList)
    editor.putString("summoner_list", jsonString)
    editor.apply()
}
