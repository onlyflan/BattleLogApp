package com.example.battlelog.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.battlelog.R
import com.example.battlelog.adapter.DataProvider
import com.example.battlelog.model.ChampionRotation
import com.example.battlelog.ui.theme.BatteLogTheme
import com.example.battlelog.ui.theme.bottomBorder
import com.example.battlelog.ui.theme.topBorder
import com.example.battlelog.viewmodel.HomeViewModel
import com.noahkohrs.riot.api.RiotApi
import com.noahkohrs.riot.api.values.Platform
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun TopBar(
    modifier: Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 25.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.lol_battle_log_nobg),
            contentDescription = null,
            modifier = modifier
                .size(100.dp)
                .padding(15.dp)
        )
        Text(
            style = MaterialTheme.typography.titleLarge,
            text = stringResource(R.string.app_name),
        )
        ServerChoose()
    }
}

@Composable
fun BottomBar(
    modifier: Modifier,
    navController: NavController? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .topBorder(1.dp, MaterialTheme.colorScheme.primary)
            .windowInsetsPadding(WindowInsets.navigationBars),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,

        ) {
        var selectedBox by remember { mutableStateOf("Home") }

        val defaultModifier = Modifier.size(90.dp, 65.dp)

        Box(
            modifier = defaultModifier.clickable { selectedBox = "Home" },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(R.drawable.house_solid),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    colorFilter = if (selectedBox != "Home") androidx.compose.ui.graphics.ColorFilter.tint(
                        colorResource(R.color.gray_998F8F)
                    ) else null
                )
                Text(
                    text = stringResource(R.string.home),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selectedBox != "Home") MaterialTheme.colorScheme.primary else Color.Black

                )
            }
        }

        Box(
            modifier = defaultModifier.clickable { selectedBox = "Champions" },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    navController?.navigate("Champion_Search")
                }
            ) {
                Image(
                    painter = painterResource(R.drawable.champion),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    colorFilter = if (selectedBox != "Champions") androidx.compose.ui.graphics.ColorFilter.tint(
                        colorResource(R.color.gray_998F8F)
                    ) else null
                )
                Text(
                    text = stringResource(R.string.Champions),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selectedBox != "Champions") MaterialTheme.colorScheme.primary else Color.Black
                )
            }
        }

        Box(
            modifier = defaultModifier.clickable {
                selectedBox = "Settings"

           },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    navController?.navigate(Routes.settings)
                }
            ) {
                Image(
                    painter = painterResource(R.drawable.settings),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    colorFilter = if (selectedBox != "Settings") androidx.compose.ui.graphics.ColorFilter.tint(
                        colorResource(R.color.gray_998F8F)
                    ) else null
                )
                Text(
                    text = stringResource(R.string.settings),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selectedBox == "Settings") MaterialTheme.colorScheme.primary else Color.Black
                )
            }
        }
    }
}

fun saveSelectedServer(context: Context, server: String) {
    val sharedPref = context.getSharedPreferences("battlelog_prefs", Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putString("selected_server", server)
        apply()
    }
}

fun getSelectedServer(context: Context, servers: List<String>): String {
    val sharedPref = context.getSharedPreferences("battlelog_prefs", Context.MODE_PRIVATE)
    return sharedPref.getString("selected_server", null) ?: servers[0]
}

@Composable
fun ServerChoose() {
    val context = LocalContext.current
    val serverToPlatformMap = mapOf(
        "VN" to Platform.VN2, "KR" to Platform.KR, "NA" to Platform.NA1,
        "EUW" to Platform.EUW1, "BR" to Platform.BR1, "EUN" to Platform.EUN1,
        "RU" to Platform.RU, "JP" to Platform.JP1, "LA1" to Platform.LA1,
        "LA2" to Platform.LA2, "ME" to Platform.ME1, "OC" to Platform.OC1,
        "PH" to Platform.PH2, "SG" to Platform.SG2, "TH" to Platform.TH2,
        "TR" to Platform.TR1, "TW" to Platform.TW2
    )
    val servers = serverToPlatformMap.keys.toList()
    var expanded by remember { mutableStateOf(false) }
    var selectedServer by remember { mutableStateOf(getSelectedServer(context, servers)) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .padding(10.dp)
            .clickable { expanded = true },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedServer,
                modifier = Modifier
                    .padding(10.dp)
                    .background(color = colorResource(R.color.EEEDF3))
                    .border(1.dp, MaterialTheme.colorScheme.onPrimary)
                    .padding(10.dp)
            )
            Icon(
                painter = painterResource(R.drawable.angle_down_solid),
                contentDescription = null,
                modifier = Modifier.size(14.dp, 14.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.2f)
        ) {
            servers.forEach { server ->
                DropdownMenuItem(
                    text = { Text(text = server) },
                    onClick = {
                        selectedServer = server
                        saveSelectedServer(context, server)
                        expanded = false
                        riotApi = RiotApi(
                            apiKey = "RGAPI-fdcf049c-86d0-47af-9bb7-a1250f0c8f98",
                            platform = serverToPlatformMap[server]!!
                        )
                    },
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
            }
        }
    }
}

@Composable
fun TierList(
    modifier: Modifier,
    navController: NavController? = null,
    context: Context = LocalContext.current
) {
    var selectedPage by remember { mutableStateOf("TOP") }
    val pages = listOf("TOP", "JUG", "MID", "ADC", "SUP")

    // Load initial data from JSON
    val championTiers = remember { mutableStateListOf(*DataProvider.loadChampionTierList(context).filter { it.position.equals(selectedPage, ignoreCase = true) }.toTypedArray()) }

    Column {
        Text(
            text = "Xếp hạng tướng",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(10.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            pages.forEach { page ->
                Text(
                    text = page.uppercase(),
                    modifier = Modifier
                        .padding(5.dp)
                        .clickable {
                            selectedPage = page
                            championTiers.clear()
                            championTiers.addAll(DataProvider.loadChampionTierList(context).filter {
                                it.position.equals(selectedPage, ignoreCase = true)
                            })
                        }
                        .let { selectedModifier ->
                            if (selectedPage == page) selectedModifier.bottomBorder(1.dp, MaterialTheme.colorScheme.primary)
                            else selectedModifier
                        }
                        .padding(10.dp),
                    color = if (selectedPage == page) Color.Black else colorResource(R.color.gray_998F8F)
                )
            }
        }

        val displayedChampions = championTiers.take(5)

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                listOf(
                    Pair("#", 0.1f),
                    Pair("Tier", 0.12f),
                    Pair("Champ", 0.25f),
                    Pair("Win%", 0.15f),
                    Pair("Pick%", 0.15f),
                    Pair("Ban%", 0.15f)
                ).forEach { (header, weight) ->
                    Text(
                        text = header,
                        modifier = Modifier.weight(weight),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            displayedChampions.forEachIndexed { index, entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = (index + 1).toString(), modifier = Modifier.weight(0.1f), style = MaterialTheme.typography.labelSmall)
                    Box(
                        modifier = Modifier
                            .weight(0.1f)
                            .offset(x = -10.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = entry.imageUrl),
                            contentDescription = null,
                            modifier = Modifier
                                .size(35.dp)
                                .align(Alignment.Center)
                                .offset(x = 3.dp)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .background(color = MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 2.dp)
                        ) {
                            Text(text = entry.tier, color = Color.White, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    Text(text = entry.name, modifier = Modifier.weight(0.25f))
                    Text(text = "${entry.winRate}%", modifier = Modifier.weight(0.15f))
                    Text(text = "${entry.pickRate}%", modifier = Modifier.weight(0.15f))
                    Text(text = "${entry.banRate}%", modifier = Modifier.weight(0.15f))
                }
            }
        }

        ElevatedButton(
            modifier = modifier
                .padding(top = 20.dp)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.8f),
            onClick = {
                navController?.navigate(Routes.tierList_Detail)
            }
        ) {
            Text(text = "Xem tất cả", style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    BatteLogTheme {
        Scaffold(
            topBar = { TopBar(Modifier) },
            bottomBar = { BottomBar(Modifier, navController = null) },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(
                        rememberScrollState()
                    ),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                NavigateSearchSummoner(Modifier, stringResource(R.string.search_placeholder))
                TierList(Modifier)
                FreeChampions(Modifier, riotApi)
            }
        }
    }
}

@Composable
fun NavigateSearchSummoner(
    modifier: Modifier,
    placeholder: String,
    navController: NavController? = null,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .border(
                1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(5.dp)
            )
            .clickable {
                navController?.navigate(Routes.summoner_Search)
            },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically

    ) {
        Icon(
            painter = painterResource(R.drawable.magnifying_glass_solid),
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .padding(10.dp)
        )
        Text(
            text = placeholder,
        )
    }
}

@Composable
fun FreeChampions(
    modifier: Modifier = Modifier,
    riotApi: RiotApi,
    navController: NavController? = null,
    homeViewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        homeViewModel.loadFreeChampions(riotApi, "loading", context)
    }

    // Collect the data from the ViewModel
    val freeChampions by homeViewModel.freeChampions.collectAsState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(0.dp)
    ) {
        Text(
            text = "Tướng xoay tua",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(10.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(freeChampions) { champion ->
                ChampionRotations(champion)
            }
        }
        ElevatedButton(
            modifier = modifier
                .padding(20.dp)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.8f),
            onClick = {
                navController?.navigate(Routes.rotationChampion_Detail)
            }
        ) {
            Text(
                text = "Xem tất cả",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
fun ChampionRotations(champion: ChampionRotation) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .padding(10.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = champion.imageUrl),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .align(Alignment.CenterHorizontally),
            alignment = Alignment.Center
        )
        Text(
            text = "Tướng miễn phí",
            color = Color(0xFFFF7223),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = champion.name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = champion.title,
            color = Color.Red,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

    }
}
