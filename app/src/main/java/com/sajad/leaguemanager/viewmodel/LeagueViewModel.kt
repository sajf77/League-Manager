package com.sajad.leaguemanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sajad.leaguemanager.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LeagueViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LeagueRepository(application)

    val allLeagues: StateFlow<List<League>> = repository.getAllLeagues()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeLeagueId = MutableStateFlow<String?>(null)
    val activeLeagueId: StateFlow<String?> = _activeLeagueId.asStateFlow()

    val activeLeague: StateFlow<League?> = combine(allLeagues, _activeLeagueId) { leagues, id ->
        leagues.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _currentRoundIndex = MutableStateFlow(0)
    val currentRoundIndex: StateFlow<Int> = _currentRoundIndex.asStateFlow()

    private val _theme = MutableStateFlow(repository.getPref("theme", "dark"))
    val theme: StateFlow<String> = _theme.asStateFlow()

    private val _fontSize = MutableStateFlow(repository.getPref("fontSize", "default"))
    val fontSize: StateFlow<String> = _fontSize.asStateFlow()

    private val _fontFamily = MutableStateFlow(repository.getPref("fontFamily", "Oswald"))
    val fontFamily: StateFlow<String> = _fontFamily.asStateFlow()

    init {
        viewModelScope.launch {
            allLeagues.filter { it.isNotEmpty() }.first().let { leagues ->
                _activeLeagueId.value = leagues.maxByOrNull { it.id }!!.id
                initCurrentRound()
            }
        }
    }

    fun switchLeague(id: String) {
        _activeLeagueId.value = id
        viewModelScope.launch {
            initCurrentRound()
        }
    }

    private suspend fun initCurrentRound() {
        val league = activeLeague.first() ?: return
        val idx = league.rounds.indexOfFirst { round ->
            round.matches.withIndex().any { (matchIdx, _) ->
                val key = "r${round.round}-m$matchIdx"
                val s = league.scores[key]
                s == null || s.home == null || s.away == null
            }
        }
        _currentRoundIndex.value = if (idx >= 0) idx else (league.rounds.size - 1).coerceAtLeast(0)
    }

    fun navigateRound(direction: Int) {
        val league = activeLeague.value ?: return
        val current = _currentRoundIndex.value
        _currentRoundIndex.value = when {
            current + direction < 0 -> league.rounds.size - 1
            current + direction >= league.rounds.size -> 0
            else -> current + direction
        }
    }

    fun goToRound(index: Int) {
        _currentRoundIndex.value = index
    }

    fun setTheme(theme: String) {
        _theme.value = theme
        repository.setPref("theme", theme)
    }

    fun setFontSize(size: String) {
        _fontSize.value = size
        repository.setPref("fontSize", size)
    }

    fun setFontFamily(family: String) {
        _fontFamily.value = family
        repository.setPref("fontFamily", family)
    }

    fun createLeague(
        name: String,
        numTeams: Int,
        teamAssignment: List<Int>,
        roundsText: String
    ): Boolean {
        val rounds = repository.parseRoundsFromText(roundsText)
        if (rounds.isEmpty()) return false

        val league = League(
            id = "league_${System.currentTimeMillis()}",
            name = name,
            numTeams = numTeams,
            teamAssignment = teamAssignment,
            roundsText = roundsText,
            rounds = rounds,
            scores = emptyMap()
        )

        viewModelScope.launch {
            repository.saveLeague(league)
            _activeLeagueId.value = league.id
            initCurrentRound()
        }
        return true
    }

    fun updateLeague(
        leagueId: String,
        name: String,
        numTeams: Int,
        teamAssignment: List<Int>,
        roundsText: String
    ): Boolean {
        val rounds = repository.parseRoundsFromText(roundsText)
        if (rounds.isEmpty()) return false

        viewModelScope.launch {
            val oldLeague = repository.getLeagueById(leagueId) ?: return@launch
            val oldScores = oldLeague.scores
            val oldRounds = oldLeague.rounds

            val oldTeamNames = mutableMapOf<Int, String>()
            for (i in 1..oldLeague.numTeams) {
                oldTeamNames[i] = repository.teamByNumber(i, oldLeague.teamAssignment).name
            }

            val newTeamNames = mutableMapOf<Int, String>()
            for (i in 1..numTeams) {
                newTeamNames[i] = repository.teamByNumber(i, teamAssignment).name
            }

            val newPairOccurrences = mutableMapOf<String, MutableList<String>>()
            rounds.forEach { round ->
                round.matches.forEachIndexed { matchIdx, pair ->
                    val home = newTeamNames[pair.first] ?: return@forEachIndexed
                    val away = newTeamNames[pair.second] ?: return@forEachIndexed
                    val key = "$home-$away"
                    newPairOccurrences.getOrPut(key) { mutableListOf() }.add("r${round.round}-m$matchIdx")
                }
            }

            val usedNewKeys = mutableSetOf<String>()
            val filteredScores = mutableMapOf<String, Score>()
            oldRounds.forEach { oldRound ->
                oldRound.matches.forEachIndexed { matchIdx, oldPair ->
                    val oldKey = "r${oldRound.round}-m$matchIdx"
                    val score = oldScores[oldKey] ?: return@forEachIndexed
                    val home = oldTeamNames[oldPair.first] ?: return@forEachIndexed
                    val away = oldTeamNames[oldPair.second] ?: return@forEachIndexed
                    val candidates = newPairOccurrences["$home-$away"] ?: return@forEachIndexed
                    val newKey = candidates.firstOrNull { it !in usedNewKeys }
                    if (newKey != null) {
                        usedNewKeys.add(newKey)
                        filteredScores[newKey] = score
                    }
                }
            }

            val updatedLeague = League(
                id = leagueId,
                name = name,
                numTeams = numTeams,
                teamAssignment = teamAssignment,
                roundsText = roundsText,
                rounds = rounds,
                scores = filteredScores
            )
            repository.saveLeague(updatedLeague)
            initCurrentRound()
        }
        return true
    }

    fun deleteLeague(league: League) {
        viewModelScope.launch {
            repository.deleteLeague(league)
            val leagues = allLeagues.first()
            _activeLeagueId.value = if (leagues.isNotEmpty()) leagues.maxByOrNull { it.id }!!.id else null
        }
    }

    fun setScore(key: String, home: Int, away: Int) {
        val league = activeLeague.value ?: return
        val newScores = league.scores.toMutableMap()
        newScores[key] = Score(home, away)
        val updated = league.copy(scores = newScores)
        viewModelScope.launch {
            repository.saveLeague(updated)
        }
    }

    fun clearScore(key: String) {
        val league = activeLeague.value ?: return
        val newScores = league.scores.toMutableMap()
        newScores.remove(key)
        val updated = league.copy(scores = newScores)
        viewModelScope.launch {
            repository.saveLeague(updated)
        }
    }

    fun clearRoundResults() {
        val league = activeLeague.value ?: return
        val round = league.rounds.getOrNull(_currentRoundIndex.value) ?: return
        val newScores = league.scores.toMutableMap()
        round.matches.forEachIndexed { matchIdx, _ ->
            val key = "r${round.round}-m$matchIdx"
            newScores.remove(key)
        }
        val updated = league.copy(scores = newScores)
        viewModelScope.launch {
            repository.saveLeague(updated)
        }
    }

    fun importData(json: String): Boolean {
        return try {
            val gson = com.google.gson.Gson()
            val type = object : com.google.gson.reflect.TypeToken<Map<String, Any>>() {}.type
            val map: Map<String, Any> = gson.fromJson(json, type)
            @Suppress("UNCHECKED_CAST")
            val leaguesJson = gson.toJson(map["leagues"])
            val leaguesType = object : com.google.gson.reflect.TypeToken<List<League>>() {}.type
            val leagues: List<League> = gson.fromJson(leaguesJson, leaguesType)

            viewModelScope.launch {
                leagues.forEach { repository.saveLeague(it) }
                if (leagues.isNotEmpty()) {
                    _activeLeagueId.value = leagues.first().id
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun exportData(): String {
        val gson = com.google.gson.Gson()
        val data = mapOf(
            "leagues" to allLeagues.value,
            "activeLeagueId" to _activeLeagueId.value
        )
        return gson.toJson(data)
    }

    fun teamByNumber(num: Int): TeamData {
        val league = activeLeague.value ?: return TeamData("Team $num", "T$num", "", "")
        return repository.teamByNumber(num, league.teamAssignment)
    }

    fun getSlotByTeamName(name: String): Int {
        val league = activeLeague.value ?: return 1
        for (i in 0 until league.numTeams) {
            val t = teamByNumber(i + 1)
            if (t.name == name) return i + 1
        }
        return 1
    }

    fun computeStandings(maxRoundIndex: Int? = null, league: League? = null): List<StandingRow> {
        val lg = league ?: activeLeague.value ?: return emptyList()
        android.util.Log.d("LMDBG", "computeStandings numTeams=${lg.numTeams} rounds=${lg.rounds.map { it.round }} matches=${lg.rounds.firstOrNull()?.matches} scores=${lg.scores}")
        val stats = mutableMapOf<Int, StandingStats>()
        val formMap = mutableMapOf<Int, MutableList<String>>()
        for (i in 1..lg.numTeams) {
            stats[i] = StandingStats(teamByNumber(i))
            formMap[i] = mutableListOf()
        }

        val limit = maxRoundIndex?.coerceAtMost(lg.rounds.size - 1) ?: (lg.rounds.size - 1)
        for (ri in 0..limit) {
            val round = lg.rounds[ri]
            round.matches.forEachIndexed { matchIdx, pair ->
                val key = "r${round.round}-m$matchIdx"
                val s = lg.scores[key] ?: return@forEachIndexed
                if (s.home == null || s.away == null) return@forEachIndexed

                val home = stats[pair.first] ?: return@forEachIndexed
                val away = stats[pair.second] ?: return@forEachIndexed

                home.p++; away.p++
                home.gf += s.home; home.ga += s.away
                away.gf += s.away; away.ga += s.home

                if (s.home > s.away) {
                    home.w++; away.l++
                    formMap[pair.first]?.add("W")
                    formMap[pair.second]?.add("L")
                } else if (s.home < s.away) {
                    away.w++; home.l++
                    formMap[pair.first]?.add("L")
                    formMap[pair.second]?.add("W")
                } else {
                    home.d++; away.d++
                    formMap[pair.first]?.add("D")
                    formMap[pair.second]?.add("D")
                }
            }
        }

        return stats.map { (slot, s) ->
            StandingRow(
                slot = slot,
                team = s.team,
                p = s.p, w = s.w, d = s.d, l = s.l,
                gf = s.gf, ga = s.ga,
                gd = s.gf - s.ga,
                pts = s.w * 3 + s.d,
                form = formMap[slot]?.takeLast(5) ?: emptyList()
            )
        }.sortedWith(compareByDescending<StandingRow> { it.pts }
            .thenByDescending { it.gd }
            .thenByDescending { it.gf }
            .thenBy { it.team.name })
    }

    fun computeStandingsWithMovement(currentRoundIndex: Int, league: League? = null): List<StandingRow> {
        val lg = league ?: activeLeague.value ?: return emptyList()
        val current = computeStandings(currentRoundIndex, lg)

        if (currentRoundIndex <= 0) {
            return current.map { it.copy(movement = "dash") }
        }

        val previous = computeStandings(currentRoundIndex - 1, lg)
        val prevRankMap = previous.mapIndexed { idx, row -> row.team.name to idx + 1 }.toMap()

        return current.mapIndexed { idx, row ->
            val currentRank = idx + 1
            val prevRank = prevRankMap[row.team.name]
            val movement = when {
                prevRank == null -> "dash"
                currentRank < prevRank -> "up"
                currentRank > prevRank -> "down"
                else -> "none"
            }
            row.copy(movement = movement)
        }
    }

    fun getTeamFixtures(slotNum: Int): List<TeamFixture> {
        val league = activeLeague.value ?: return emptyList()
        val fixtures = mutableListOf<TeamFixture>()

        league.rounds.forEach { round ->
            round.matches.forEachIndexed { matchIdx, pair ->
                val isHome = pair.first == slotNum
                val isAway = pair.second == slotNum
                if (!isHome && !isAway) return@forEachIndexed

                val key = "r${round.round}-m$matchIdx"
                val saved = league.scores[key]
                val hasResult = saved?.home != null && saved.away != null
                val opponentSlot = if (isHome) pair.second else pair.first
                val opponent = teamByNumber(opponentSlot)

                var scoreText = ""
                var result: String? = null
                if (hasResult) {
                    scoreText = "${saved!!.home} - ${saved.away}"
                    val myGoals = if (isHome) saved.home else saved.away
                    val oppGoals = if (isHome) saved.away else saved.home
                    result = when {
                        myGoals!! > oppGoals!! -> "win"
                        myGoals < oppGoals -> "loss"
                        else -> "draw"
                    }
                }

                fixtures.add(TeamFixture(
                    roundNum = round.round,
                    opponent = opponent,
                    isHome = isHome,
                    scoreText = scoreText,
                    result = result
                ))
            }
        }
        return fixtures
    }

    fun getH2H(homeTeam: TeamData, awayTeam: TeamData): H2HResult {
        var homeWins = 0
        var awayWins = 0
        var draws = 0
        val meetings = mutableListOf<H2HMeeting>()

        allLeagues.value.forEach { league ->
            league.rounds.forEach { round ->
                round.matches.forEachIndexed { matchIdx, pair ->
                    val key = "r${round.round}-m$matchIdx"
                    val s = league.scores[key] ?: return@forEachIndexed
                    if (s.home == null || s.away == null) return@forEachIndexed

                    val slotHome = pair.first
                    val slotAway = pair.second
                    val tH = repository.teamByNumber(slotHome, league.teamAssignment)
                    val tA = repository.teamByNumber(slotAway, league.teamAssignment)

                    val isH2H = (tH.name == homeTeam.name && tA.name == awayTeam.name) ||
                            (tH.name == awayTeam.name && tA.name == homeTeam.name)
                    if (!isH2H) return@forEachIndexed

                    val isSameLeague = league.id == _activeLeagueId.value

                    if (tH.name == homeTeam.name) {
                        if (s.home > s.away) homeWins++ else if (s.home < s.away) awayWins++ else draws++
                    } else {
                        if (s.away > s.home) homeWins++ else if (s.away < s.home) awayWins++ else draws++
                    }

                    meetings.add(H2HMeeting(
                        homeTeam = tH,
                        awayTeam = tA,
                        score = "${s.home} - ${s.away}",
                        leagueName = if (!isSameLeague) league.name else null
                    ))
                }
            }
        }

        return H2HResult(homeWins, draws, awayWins, meetings)
    }
}

data class StandingStats(
    val team: TeamData,
    var p: Int = 0, var w: Int = 0, var d: Int = 0, var l: Int = 0,
    var gf: Int = 0, var ga: Int = 0
)

data class StandingRow(
    val slot: Int,
    val team: TeamData,
    val p: Int, val w: Int, val d: Int, val l: Int,
    val gf: Int, val ga: Int, val gd: Int, val pts: Int,
    val form: List<String>,
    val movement: String = "dash"
)

data class TeamFixture(
    val roundNum: Int,
    val opponent: TeamData,
    val isHome: Boolean,
    val scoreText: String,
    val result: String?
)

data class H2HResult(
    val homeWins: Int,
    val draws: Int,
    val awayWins: Int,
    val meetings: List<H2HMeeting>
)

data class H2HMeeting(
    val homeTeam: TeamData,
    val awayTeam: TeamData,
    val score: String,
    val leagueName: String?
)
