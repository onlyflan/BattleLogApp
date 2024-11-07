package com.example.battlelog.view

import android.content.Context
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.battlelog.R
import com.example.battlelog.model.MatchHistory
import com.example.battlelog.parser.BaseUrl
import com.example.battlelog.ui.theme.BatteLogTheme
import com.example.battlelog.ui.theme.loseColor
import com.example.battlelog.ui.theme.topBorder
import com.example.battlelog.ui.theme.winColor
import com.noahkohrs.riot.api.RiotApi
import kotlinx.coroutines.*
import java.util.Locale

@Preview(showBackground = true)
@Composable
fun MatchDetailPreview() {
    BatteLogTheme {
        MatchDetail(matchId = "", riotApi = riotApi, puuid = "")
    }
}

@Composable
fun MatchDetail(
    navController: NavController? = null,
    puuid: String,
    matchId: String,
    riotApi: RiotApi
) {
    val matchHistoryState by produceState<MatchHistory?>(null, matchId) {
        value = withContext(Dispatchers.IO) { fetchMatchHistoryByMatchId(matchId, riotApi) }
    }

    matchHistoryState?.let { history ->
        val participantWin = history.info.participants.find { it.puuid == puuid }?.win ?: false
        val winningParticipants = history.info.participants.filter { it.win }
        val losingParticipants = history.info.participants.filter { !it.win }

        Scaffold(
            topBar = { BuildTopBar(navController, participantWin) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                val resultTextWinner = if (winningParticipants.any { it.teamId == 100L }) "WIN (Blue)" else "WIN (Red)"
                val resultTextLoser = if (losingParticipants.any { it.teamId == 100L }) "LOSS (Blue)" else "LOSS (Red)"

                MatchDetailSection(
                    matchId = matchId,
                    resultText = resultTextWinner,
                    isTeamWin = true,
                    participants = winningParticipants
                )

                HorizontalDivider(
                    thickness = 2.dp,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth()
                )

                MatchDetailSection(
                    matchId = matchId,
                    resultText = resultTextLoser,
                    isTeamWin = false,
                    participants = losingParticipants
                )
            }
        }
    }
}

@Composable
fun BuildTopBar(
    navController: NavController?,
    isWin: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp)
            .background(if (isWin) winColor else loseColor),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AddIconButton(navController)
        AddResultText(isWin)
    }
}

@Composable
fun AddIconButton(navController: NavController?) {
    IconButton(onClick = { navController?.navigateUp() }) {
        Icon(
            painter = painterResource(R.drawable.fachevronleft),
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = Color.White
        )
    }
}

@Composable
fun AddResultText(isWin: Boolean) {
    Text(
        text = if (isWin) "WIN" else "LOSE",
        modifier = Modifier.fillMaxWidth().offset(x = (-15).dp),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.headlineMedium,
        color = Color.White
    )
}

@Composable
fun MatchDetailSection(
    matchId: String,
    resultText: String,
    isTeamWin: Boolean,
    participants: List<MatchHistory.Info.Participants>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(start = 15.dp, end = 15.dp)
    ) {
        Text(
            text = resultText,
            style = MaterialTheme.typography.titleMedium,
            color = if (isTeamWin) winColor else loseColor,
            modifier = Modifier.padding(top = 10.dp)
        )
        for (participant in participants) {
            MatchDetailItem(
                riotName = participant.riotIdGameName,
                tagLine = participant.riotIdTagline,
                kda = participant.kda,
                isTeamWin = isTeamWin,
                matchId = matchId,
                participant = participant,
                riotApi = riotApi
            )
        }
    }
}

@Composable
fun MatchDetailItem(
    riotName: String,
    tagLine: String,
    kda: Float,
    isTeamWin: Boolean,
    matchId: String,
    participant: MatchHistory.Info.Participants,
    riotApi: RiotApi
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .background(Color.White)
            .clickable {}
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = getChampionPortraitURL(participant)),
            contentDescription = null,
            modifier = Modifier.size(30.dp)
        )
        Spacer(Modifier.width(5.dp))
        SummonerSpells(context, participant)
        SummonerRunes()
        Column(modifier = Modifier.weight(0.8f)) {
            Text(text = riotName, style = MaterialTheme.typography.bodySmall)
            Text(text = "#$tagLine", style = MaterialTheme.typography.bodySmall)
            Text(
                text = "${participant.kills}/${participant.deaths}/${participant.assists}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            Text(
                text = String.format(Locale.US, "%.2f:1", kda),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Blue
            )
        }
        Spacer(Modifier.width(5.dp))
        ItemRow(participant)
    }
}

private suspend fun fetchMatchHistoryByMatchId(matchId: String, riotApi: RiotApi): MatchHistory? {
    return try {
        withContext(Dispatchers.IO) {
            val matchData = riotApi.lol.match.getMatchById(matchId)
            MatchHistory(
                MatchHistory.Info(
                    gameEndTimestamp = matchData.info.gameEndTimestamp,
                    gameDuration = matchData.info.gameDuration,
                    gameId = matchData.info.gameId,
                    queueId = matchData.info.queueId,
                    matchId = matchId,
                    participants = matchData.info.participants.map { participant ->
                        MatchHistory.Info.Participants(
                            assists = participant.assists,
                            championId = participant.championStats.id,
                            championName = participant.championStats.name,
                            deaths = participant.deaths,
                            firstBloodKill = participant.killsStats.firstBloodKill,
                            item0 = participant.items[0],
                            item1 = participant.items[1],
                            item2 = participant.items[2],
                            item3 = participant.items[3],
                            item4 = participant.items[4],
                            item5 = participant.items[5],
                            item6 = participant.items[6],
                            kills = participant.kills,
                            kda = participant.challenges.kda,
                            lane = participant.playerStats.lane,
                            neutralMinionsKilled = participant.killsStats.neutralMinionsKilled,
                            participantId = participant.participantId,
                            pentaKills = participant.killsStats.pentaKills,
                            riotIdGameName = participant.userData.riotIdGameName,
                            riotIdTagline = participant.userData.riotIdTagLine,
                            puuid = participant.userData.puuid,
                            summoner1Id = participant.playerStats.summoner1Id,
                            summoner2Id = participant.playerStats.summoner2Id,
                            summonerId = participant.userData.summonerId,
                            summonerName = participant.userData.summonerName,
                            teamPosition = participant.playerStats.teamPosition.toString(),
                            totalMinionsKilled = participant.killsStats.totalMinionsKilled,
                            win = participant.teamStats.win,
                            teamId = participant.teamStats.teamId,
                        )
                    }.toCollection(ArrayList())
                )
            )
        }
    } catch (e: Exception) {
        Log.e("fetchMatchHistoryByMatchId", "Error fetching match data: ${e.message}")
        null
    }
}

private fun getChampionPortraitURL(participants: MatchHistory.Info.Participants): String {
    return "${BaseUrl.RIOT_DATA_DRAGON_GET_CHAMPION_SQUARE}${participants.championName}.png"
}