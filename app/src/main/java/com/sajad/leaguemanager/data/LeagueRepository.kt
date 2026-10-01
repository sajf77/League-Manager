package com.sajad.leaguemanager.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow

class LeagueRepository(context: Context) {
    private val dao = AppDatabase.getDatabase(context).leagueDao()
    private val prefs: SharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    init {
        TeamOverrides.load(context)
    }

    fun getPref(key: String, default: String): String = prefs.getString(key, default) ?: default

    fun setPref(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getAllLeagues(): Flow<List<League>> = dao.getAllLeagues()

    suspend fun getLeagueById(id: String): League? = dao.getLeagueById(id)

    suspend fun saveLeague(league: League) = dao.insertLeague(league)

    suspend fun deleteLeague(league: League) = dao.deleteLeague(league)

    suspend fun deleteLeagueById(id: String) = dao.deleteLeagueById(id)

    fun teamByNumber(num: Int, assignment: List<Int>): TeamData {
        val dbIndex = assignment.getOrNull(num - 1)
        return if (dbIndex != null && dbIndex in TeamDatabase.allTeams.indices) {
            TeamOverrides.resolve(TeamDatabase.allTeams[dbIndex])
        } else {
            TeamData("Team $num", "T$num", "", "")
        }
    }

    fun parseRoundsFromText(text: String): List<Round> {
        val lines = text.trim().split("\n")
        val rounds = mutableListOf<Round>()
        lines.forEach { rawLine ->
            val line = rawLine.trim()
            if (line.isNotEmpty()) {
                val pairMatches = Regex("\\d+\\s*-\\s*\\d+").findAll(line).toList()
                if (pairMatches.isNotEmpty()) {
                    val matches = pairMatches.map { pair ->
                        val nums = pair.value.split("-").map { it.trim().toInt() }
                        Pair(nums[0], nums[1])
                    }
                    rounds.add(Round(rounds.size + 1, matches))
                }
            }
        }
        return rounds
    }
}
