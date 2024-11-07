package com.example.battlelog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battlelog.model.Account
import com.example.battlelog.model.MatchHistory
import com.example.battlelog.model.SummonerRankInfo
import com.noahkohrs.riot.api.RiotApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProfileViewModel(private val riotApi: RiotApi) : ViewModel() {

    private val _accountInfo = MutableStateFlow<Account?>(null)
    val accountInfo: StateFlow<Account?> get() = _accountInfo

    private val _summonerRankInfoList = MutableStateFlow<List<SummonerRankInfo>>(emptyList())
    val summonerRankInfoList: StateFlow<List<SummonerRankInfo>> get() = _summonerRankInfoList

    private val _matchHistoryList = MutableStateFlow<List<MatchHistory>?>(null)
    val matchHistoryList: StateFlow<List<MatchHistory>?> get() = _matchHistoryList

    fun fetchProfileData(puuid: String) {
        viewModelScope.launch {
            _accountInfo.value = withContext(Dispatchers.IO) { fetchAccountInfo(puuid) }
            _summonerRankInfoList.value = withContext(Dispatchers.IO) { fetchSummonerRankInfoList(puuid) }
            _matchHistoryList.value = withContext(Dispatchers.IO) { fetchMatchHistoryList(puuid) }
        }
    }

    private fun fetchAccountInfo(puuid: String): Account? {
        return try {
            val accountInfo = riotApi.account.getAccountByPuuid(puuid)
            val summonerInfo = riotApi.lol.summoner.getSummonerByPuuid(puuid)
            val profileIconUrl = "https://ddragon.leagueoflegends.com/cdn/14.21.1/img/profileicon/${summonerInfo.profileIconId}.png"

            Account(
                puuid = accountInfo.puuid,
                gameName = accountInfo.gameName,
                tagLine = accountInfo.tagLine,
                profileIconUrl = profileIconUrl
            )
        } catch (e: Exception) {
            // Handle the error or log it
            null
        }
    }

    private fun fetchSummonerRankInfoList(puuid: String): List<SummonerRankInfo> {
        return try {
            val summonerID = riotApi.lol.summoner.getSummonerByPuuid(puuid).id
            riotApi.lol.league.getLeagueEntriesBySummoner(summonerID).map { entry ->
                SummonerRankInfo(
                    queueType = entry.queue.toString(),
                    tier = entry.tier.toString(),
                    rank = entry.division.toString(),
                    leaguePoints = entry.leaguePoints,
                    wins = entry.wins,
                    losses = entry.losses
                )
            }
        } catch (e: Exception) {
            // Handle the error or log it
            emptyList()
        }
    }

    private fun fetchMatchHistoryList(puuid: String): List<MatchHistory>? {
        return try {
            val numberOfMatches = 10
            val matchIds = riotApi.lol.match.getMatchIdsByPuuid(puuid, count = numberOfMatches)
            matchIds.mapNotNull { matchId ->
                try {
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
                } catch (e: Exception) {
                    // Handle the error or log it for a specific match
                    null
                }
            }
        } catch (e: Exception) {
            // Handle general errors
            null
        }
    }
}