package com.example.battlelog.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.battlelog.R
import com.example.battlelog.model.LiveGame
import com.example.battlelog.parser.BaseUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import android.util.Log
import com.example.battlelog.parser.RuneParser
import com.example.battlelog.parser.SpellParser
import com.example.battlelog.ui.theme.BatteLogTheme
import com.example.battlelog.ui.theme.loseColor
import com.example.battlelog.ui.theme.topBorder
import com.example.battlelog.ui.theme.winColor
import com.noahkohrs.riot.api.RiotApi
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.HttpURLConnection
import java.net.URL

@Preview(showBackground = true)
@Composable
fun LiveGamePreview() {
    BatteLogTheme {
        // LiveGameView(puuid = "12345", riotApi = RiotApi(), navController = null)
    }
}

@Composable
fun LiveGameView(
    puuid: String,
    riotApi: RiotApi,
    navController: NavController? = null
) {
    var liveGameData by remember { mutableStateOf<LiveGame?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(puuid) {
        coroutineScope.launch {
            liveGameData = fetchLiveGameData(puuid, riotApi)
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.padding(top = 50.dp, start = 10.dp, end = 10.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.angle_left_solid),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            navController?.navigateUp()
                        }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Live game",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 15.dp)
                )
            }
        }
    ) { paddingValues ->
        liveGameData?.let { data ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                data.participants.groupBy { it.teamId }.forEach { (teamId, participants) ->
                    val teamColor = if (teamId == 100L) winColor else loseColor
                    LiveMatchDetailSection(
                        team = if (teamId == 100L) "Blue" else "Red",
                        participants = participants,
                        teamColor = teamColor
                    )
                }
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Người chơi hiện không đang trong trận",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Red
                )
            }
        }
    }
}

@Composable
fun LiveMatchDetailSection(
    team: String,
    participants: List<LiveGame.Participant>,
    teamColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .then(
                Modifier.topBorder(
                    3.dp,
                    teamColor
                )
            )
            .padding(start = 15.dp, end = 15.dp)
    ) {
        Text(
            text = team,
            style = MaterialTheme.typography.titleMedium,
            color = teamColor,
            modifier = Modifier.padding(top = 10.dp)
        )
        val context = LocalContext.current
        participants.forEach { participant ->
            LiveDetailItem(participant, context = context)
        }
    }
}

@Composable
fun LiveDetailItem(
    participant: LiveGame.Participant,
    context: Context
) {
    var imageUrl by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(participant) {
        coroutineScope.launch {
            imageUrl = fetchChampionPortraitURL(participant)
        }
        onDispose { }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .background(Color.White)
            .padding(8.dp),
    ) {
        imageUrl?.let {
            Image(
                painter = rememberAsyncImagePainter(model = it),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(Modifier.width(5.dp))

        SummonerSpellsLive(context, participant)

        SummonerRunesLive(context, participant.perks)

        Column(modifier = Modifier.weight(0.8f)) {
            Text(
                text = "${participant.riotId.substringBefore('#')} #${
                    participant.riotId.substringAfter('#')
                }",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.width(5.dp))
    }
}

@Composable
fun SummonerSpellsLive(
    context: Context, participant: LiveGame.Participant
) {
    Column(
        modifier = Modifier.padding(end = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(2) { index ->
            val spellIndex = index + 1
            val imageUrl = runBlocking { getSpellImageURLLive(participant, spellIndex, context) }
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
fun SummonerRunesLive(
    context: Context, perks: LiveGame.Perks
) {
    Column(
        modifier = Modifier.padding(end = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val mainRuneIcon = runBlocking { 
            val url = getRuneImageURL(perks, 1, context)
            Log.d("LiveGame", "Main Rune URL: $url")
            url
        }
        val subRuneIcon = runBlocking { 
            val url = getRuneImageURL(perks, 2, context)
            Log.d("LiveGame", "Sub Rune URL: $url")
            url
        }

        Image(
            painter = rememberAsyncImagePainter(model = mainRuneIcon),
            contentDescription = null,
            modifier = Modifier
                .size(18.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Image(
            painter = rememberAsyncImagePainter(model = subRuneIcon),
            contentDescription = null,
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(12.dp))
        )
    }
}

suspend fun fetchLiveGameData(puuid: String, riotApi: RiotApi): LiveGame? {
    return withContext(Dispatchers.IO) {
        try {
            val liveGameInfo = riotApi.lol.spectator.getCurrentGameInfoByPuuid(puuid)
            LiveGame(
                gameId = liveGameInfo.gameId,
                gameMode = liveGameInfo.gameMode,
                gameStartTime = liveGameInfo.gameStartTime,
                mapId = liveGameInfo.mapId,
                participants = liveGameInfo.participants.map { participant ->
                    LiveGame.Participant(
                        summonerId = participant.summonerId,
                        riotId = participant.riotId,
                        championId = participant.championId,
                        teamId = participant.teamId,
                        spell1Id = participant.spell1Id,
                        spell2Id = participant.spell2Id,
                        perks = participant.perks.run {
                            LiveGame.Perks(
                                perkStyle = this.perkStyle,
                                perkSubStyle = this.perkSubStyle,
                                perks = this.perkIds.toList()
                            )
                        }
                    )
                }.toList(),
                bannedChampions = liveGameInfo.bannedChampions.map { ban ->
                    LiveGame.BannedChampion(
                        championId = ban.championId,
                        teamId = ban.teamId,
                        pickTurn = ban.pickTurn
                    )
                }
            )
        } catch (e: Exception) {
            println("Error fetching live game data: ${e.message}")
            null
        }
    }
}

suspend fun fetchChampionPortraitURL(participant: LiveGame.Participant): String {
    val championName = fetchChampionName(participant.championId.toString())
    val url = "${BaseUrl.RIOT_DATA_DRAGON_GET_CHAMPION_SQUARE}$championName.png"
    Log.d("LiveGame", "Champion URL: $url")
    return url
}

suspend fun fetchChampionName(championId: String): String {
    return withContext(Dispatchers.IO) {
        val championJsonUrl =
            "https://ddragon.leagueoflegends.com/cdn/14.21.1/data/vi_VN/champion.json"
        val url = URL(championJsonUrl)
        try {
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            connection.inputStream.use { inputStream ->
                val championDataStr = inputStream.bufferedReader().readText()
                val jsonData =
                    Json.parseToJsonElement(championDataStr).jsonObject["data"]?.jsonObject
                        ?: JsonObject(emptyMap())

                jsonData.entries.firstOrNull { (_, value) ->
                    value.jsonObject["key"]?.jsonPrimitive?.content == championId
                }?.key ?: "Unknown Champion"
            }
        } catch (e: Exception) {
            Log.e("LiveGame", "Error fetching champion name: ${e.message}")
            "Unknown Champion"
        }
    }
}

suspend fun getSpellImageURLLive(
    participant: LiveGame.Participant,
    spellIndex: Int,
    context: Context
): String {
    return withContext(Dispatchers.IO) {
        val spellId = when (spellIndex) {
            1 -> participant.spell1Id.toInt()
            2 -> participant.spell2Id.toInt()
            else -> 0
        }
        val parser = SpellParser(context)
        val spellName = parser.getSpellName(spellId)
        "${BaseUrl.RIOT_DATA_DRAGON_GET_SPELL_IMAGE}$spellName.png"
    }
}

private suspend fun getRuneImageURL(
    perks: LiveGame.Perks,
    runeIndex: Int,
    context: Context
): String {
    val runeId = if (runeIndex == 1) {
        perks.perkStyle.toInt()
    } else {
        perks.perkSubStyle.toInt()
    }

    return withContext(Dispatchers.IO) {
        val parser = RuneParser(context)
        val icon: String = parser.getRuneIcon(runeId)
        BaseUrl.RIOT_DATA_DRAGON_GET_RUNE_IMAGE + icon
    }
}