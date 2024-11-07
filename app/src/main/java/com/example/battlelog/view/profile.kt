package com.example.battlelog.view

import android.content.Context
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.battlelog.R
import com.example.battlelog.model.Account
import com.example.battlelog.model.MatchHistory
import com.example.battlelog.model.SummonerRankInfo
import com.example.battlelog.parser.BaseUrl
import com.example.battlelog.parser.BaseUrl.Companion.RIOT_DATA_DRAGON_CHAMPION
import com.example.battlelog.parser.BaseUrl.Companion.RIOT_DATA_DRAGON_GET_CHAMPION_SPLASH
import com.example.battlelog.parser.QueueParser
import com.example.battlelog.parser.RuneParser
import com.example.battlelog.parser.SpellParser
import com.example.battlelog.ui.theme.BatteLogTheme
import com.example.battlelog.ui.theme.bottomBorder
import com.example.battlelog.ui.theme.loseColor
import com.example.battlelog.ui.theme.rakingTitleColor
import com.example.battlelog.ui.theme.tagGrayColor
import com.example.battlelog.ui.theme.topBorder
import com.example.battlelog.ui.theme.winColor
import com.example.battlelog.viewmodel.ProfileViewModel
import com.example.battlelog.viewmodel.ProfileViewModelFactory
import com.noahkohrs.riot.api.RiotApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import java.net.URL
import kotlinx.coroutines.*
import java.util.Locale

@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    BatteLogTheme {
        ProfileDetail(puuid = "", riotApi = riotApi)
    }
}

@Composable
fun ProfileDetail(
    navController: NavController? = null,
    puuid: String,
    riotApi: RiotApi
) {
    val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(riotApi))

    val accountInfo by viewModel.accountInfo.collectAsState()
    val summonerRankInfoList by viewModel.summonerRankInfoList.collectAsState()
    val matchHistoryList by viewModel.matchHistoryList.collectAsState()

    val wasDataFetched = rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(puuid) {
        if (!wasDataFetched.value) {
            viewModel.fetchProfileData(puuid)
            wasDataFetched.value = true
        }
    }

    Scaffold(
        topBar = {
            ProfileHeader(navController, accountInfo)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ProfileRanking(summonerRankInfoList)
            if (matchHistoryList == null) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                MatchHistory(navController, matchHistoryList, puuid)
            }
        }
    }
}

@Composable
fun ProfileHeader(
    navController: NavController?,
    accountInfo: Account?
) {
    val profileName = accountInfo?.gameName ?: "Unknown"
    val profileTag = accountInfo?.tagLine ?: "#Unknown"
    val profileIconUrl = accountInfo?.profileIconUrl
    val splashSkin = getRandomChampionSplashUrl()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .paint(
                painter = rememberAsyncImagePainter(model = splashSkin),
                contentScale = ContentScale.FillBounds
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(
                onClick = {
                    navController?.navigateUp()
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.fachevronleft),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color.White
                )
            }
            IconButton(
                modifier = Modifier.padding(end = 10.dp),
                onClick = {
                    // Yêu thích
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.faheartcircleplus),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color.White
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 15.dp, end = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = profileIconUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(5.dp))
            )
            Column(
                modifier = Modifier.padding(start = 10.dp)
            ) {
                Text(
                    text = profileName,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "#${profileTag}",
                    color = tagGrayColor,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 15.dp, end = 15.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                onClick = {
                    // Update logic
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF42A5F5)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(35.dp)
            ) {
                Text(text = "Update", color = Color.White)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    navController?.navigate("liveGame/${accountInfo?.puuid}")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE0E0E0)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(35.dp)
            ) {
                Text(text = "Ingame", color = Color.Gray)
            }
        }
    }
}

@Composable
fun ProfileRanking(
    summonerRankInfoList: List<SummonerRankInfo>
) {
    val soloRankInfo = summonerRankInfoList.find { it.queueType == "RankedSoloQueue" }
    val flexRankInfo = summonerRankInfoList.find { it.queueType == "RankedFlexQueue" }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, start = 10.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .border(width = 1.5.dp, color = Color.Gray, shape = RoundedCornerShape(10.dp))
                .padding(top = 8.dp, start = 8.dp, bottom = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(0.475f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(
                        when (soloRankInfo?.tier) {
                            "CHALLENGER" -> R.drawable.challenger
                            "GRANDMASTER" -> R.drawable.grandmaster
                            "MASTER" -> R.drawable.master
                            "DIAMOND" -> R.drawable.diamond
                            "EMERALD" -> R.drawable.emerald
                            "PLATINUM" -> R.drawable.platinum
                            "GOLD" -> R.drawable.gold
                            "SILVER" -> R.drawable.silver
                            "BRONZE" -> R.drawable.bronze
                            "IRON" -> R.drawable.iron
                            else -> R.drawable.unranked

                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(45.dp)
                )
                Column(
                    modifier = Modifier.padding(start = 5.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = stringResource(R.string.rank_solo),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier
                            .background(color = rakingTitleColor)
                            .padding(2.dp)
                    )
                    if (soloRankInfo != null) {
                        Text(
                            text = "${soloRankInfo.tier} ${soloRankInfo.rank}",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = "${soloRankInfo.leaguePoints} LP",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                        )
                        Text(
                            text = "${soloRankInfo.wins} Wins ${soloRankInfo.losses} Loses",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                        )
                    } else {
                        Text(
                            text = "UNRANKED",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.5.dp, color = Color.Gray, shape = RoundedCornerShape(10.dp))
                .padding(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(
                        when (flexRankInfo?.tier) {
                            "CHALLENGER" -> R.drawable.challenger
                            "MASTER" -> R.drawable.challenger
                            "DIAMOND" -> R.drawable.diamond
                            "EMERALD" -> R.drawable.emerald
                            "PLATINUM" -> R.drawable.platinum
                            "GOLD" -> R.drawable.gold
                            "SILVER" -> R.drawable.silver
                            "BRONZE" -> R.drawable.bronze
                            "IRON" -> R.drawable.iron
                            else -> R.drawable.unranked

                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(45.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 5.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = stringResource(R.string.rank_flex),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier
                            .background(color = rakingTitleColor)
                            .padding(2.dp)
                    )
                    if (flexRankInfo != null) {
                        Text(
                            text = "${flexRankInfo.tier} ${flexRankInfo.rank}",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = "${flexRankInfo.leaguePoints} LP",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                        )
                        Text(
                            text = "${flexRankInfo.wins} Wins ${flexRankInfo.losses} Loses",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                        )
                    } else {
                        Text(
                            text = "UNRANKED",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MatchHistory(
    navController: NavController? = null,
    matchHistoryList: List<MatchHistory>?,
    puuid: String
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF0F0F0))
            .padding(top = 10.dp)
    ) {
        Text(
            text = stringResource(R.string.match_History),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 15.dp, end = 15.dp)
        )

        if (matchHistoryList == null) {
            Log.e("MatchHistory", "Match history list is null for PUUID $puuid.")
        } else if (matchHistoryList.isEmpty()) {
            Log.e("MatchHistory", "Match history list is empty for PUUID $puuid.")
        } else {
            matchHistoryList.forEach { match ->
                val player = match.info.participants.find { it.puuid == puuid }

                if (player != null) {
                    val rank = produceState(initialValue = "") {
                        withContext(Dispatchers.IO) {
                            value = when (match.info.queueId) {
                                420 -> "Xếp hạng đơn đôi"
                                440 -> "Xếp hạng động"
                                450 -> "ARAM"
                                1400 -> "ULTBOOK"
                                490 -> "Đấu nhanh"

                                else -> getQueueType(match.info.queueId, context)
                            }
                        }
                    }.value

                    MatchHistoryItem(
                        kda = getKDA(player),
                        kp = calculateKP(player, match),
                        rank = rank,
                        timeAgo = calculateTimeAgo(match.info.gameEndTimestamp),
                        navController = navController,
                        participant = player,
                        matchId = match.info.matchId,
                        puuid = puuid
                    )
                } else {
                    Log.e(
                        "MatchHistory", "No participant found in match wi" +
                                "th PUUID $puuid."
                    )
                }
            }
        }
    }
}

@Composable
fun MatchHistoryItem(
    kda: String,
    kp: String,
    rank: String,
    timeAgo: String,
    matchId: String,
    navController: NavController? = null,
    puuid: String,
    participant: MatchHistory.Info.Participants,
) {
    val context = LocalContext.current
    val win = participant.win
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .topBorder(1.dp, Color.Gray)
            .bottomBorder(1.dp, Color.Gray)
            .clickable {
                navController?.navigate("match_Detail/${matchId}/${puuid}")
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight(1f)
                .background(if (win) winColor else loseColor)
                .padding(
                    PaddingValues(
                        start = if (win) 8.5.dp else 10.dp,
                        end = if (win) 8.dp else 10.dp,
                        top = 32.dp,
                        bottom = 32.dp
                    )
                )
        ) {
            Text(
                text = if (win) "W" else "L",
                style = MaterialTheme.typography.titleSmall,
                color = Color.White
            )
        }
        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = getChampionPortraitURL(
                                participant
                            )
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    SummonerSpells(context, participant)
                    SummonerRunes()
                    Column {
                        Text(text = kda, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = "KP: $kp",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
                ItemRow(participant)
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Text(text = rank, style = MaterialTheme.typography.bodyMedium)
                Text(text = timeAgo, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}

@Composable
fun ItemRow(participant: MatchHistory.Info.Participants) {
    Row(
        modifier = Modifier.padding(top = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(7) { index ->
            val imageUrl = getItemImageURL(participant, index)
            if (imageUrl.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(model = imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(5.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.Gray.copy(alpha = 0.5f))
                )
            }
        }
    }
}

@Composable
fun SummonerSpells(
    context: Context, participant: MatchHistory.Info.Participants
) {
    Column(
        modifier = Modifier.padding(end = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(2) { index ->
            val spellIndex = index + 1
            val imageUrl = runBlocking { getSpellImageURL(participant, spellIndex, context) }
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(15.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }
    }
}

@Composable
fun SummonerRunes() {
    Column(
        modifier = Modifier.padding(end = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.sstc),
            contentDescription = null,
            modifier = Modifier
                .size(18.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Image(
            painter = painterResource(R.drawable.cam_hung),
            contentDescription = null,
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(12.dp))
        )
    }
}

fun getRandomChampionSplashUrl(): String {
    return runBlocking {
        withContext(Dispatchers.IO) {
            val championJsonUrl = RIOT_DATA_DRAGON_CHAMPION
            val championDataStr = URL(championJsonUrl).readText()
            val json: JsonObject =
                Json.parseToJsonElement(championDataStr).jsonObject["data"]?.jsonObject
                    ?: JsonObject(emptyMap())

            val champions = json.keys.toList()
            if (champions.isEmpty()) return@withContext "No champions found."

            val randomChampionName = champions.random()
            return@withContext "${RIOT_DATA_DRAGON_GET_CHAMPION_SPLASH}${randomChampionName}_1.jpg"
        }
    }
}

fun getKDA(participant: MatchHistory.Info.Participants?): String {
    return "${participant?.kills ?: 0} / ${participant?.deaths ?: 0} / ${participant?.assists ?: 0}"
}

fun calculateKP(participant: MatchHistory.Info.Participants?, match: MatchHistory): String {
    val teamId = participant?.teamId ?: 0
    val totalKills = match.info.participants.filter { it.teamId == teamId }.sumOf { it.kills }
    val participantKills = participant?.kills ?: 0
    val participantAssists = participant?.assists ?: 0
    val kp =
        if (totalKills > 0) ((participantKills + participantAssists) / totalKills.toDouble()) * 100 else 0.0
    return "${"%.0f".format(kp)}%"
}

fun calculateTimeAgo(gameEndTimestamp: Long): String {
    val currentTime = System.currentTimeMillis()
    val diffInMinutes = (currentTime - gameEndTimestamp) / (1000 * 60)

    return when {
        diffInMinutes < 60 -> "$diffInMinutes phút trước"
        diffInMinutes < 1440 -> "${diffInMinutes / 60} giờ trước"
        diffInMinutes < 10080 -> "${diffInMinutes / 1440} ngày trước"
        else -> "${diffInMinutes / 10080} tuần trước"
    }
}

private suspend fun getQueueType(queueId: Int, context: Context): String {
    return withContext(Dispatchers.IO) {
        val parser = QueueParser(context)
        parser.getQueueType(queueId)
    }
}

private fun getChampionPortraitURL(participants: MatchHistory.Info.Participants): String {
    return "${BaseUrl.RIOT_DATA_DRAGON_GET_CHAMPION_SQUARE}${participants.championName}.png"
}

suspend fun getSpellImageURL(
    participants: MatchHistory.Info.Participants,
    spellIndex: Int,
    context: Context
): String {
    return withContext(Dispatchers.IO) {
        val spellId = when (spellIndex) {
            1 -> participants.summoner1Id
            2 -> participants.summoner2Id
            else -> 0
        }
        val parser = SpellParser(context)
        val spellName = parser.getSpellName(spellId)
        "${BaseUrl.RIOT_DATA_DRAGON_GET_SPELL_IMAGE}$spellName.png"
    }
}

fun getItemImageURL(participants: MatchHistory.Info.Participants, itemIndex: Int): String {
    val itemId = when (itemIndex) {
        0 -> participants.item0
        1 -> participants.item1
        2 -> participants.item2
        3 -> participants.item3
        4 -> participants.item4
        5 -> participants.item5
        6 -> participants.item6
        else -> 0
    }
    return if (itemId != 0) {
        "${BaseUrl.RIOT_DATA_DRAGON_GET_ITEM_IMAGE}$itemId.png"
    } else {
        ""
    }
}