package com.sajad.leaguemanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "leagues")
@TypeConverters(Converters::class)
data class League(
    @PrimaryKey val id: String,
    val name: String,
    val numTeams: Int,
    val teamAssignment: List<Int>,
    val roundsText: String,
    val rounds: List<Round>,
    val scores: Map<String, Score>
)

data class Round(
    val round: Int,
    val matches: List<Pair<Int, Int>>
)

data class Score(
    val home: Int?,
    val away: Int?
)

data class TeamData(
    val name: String,
    val short: String,
    val logo: String,
    val league: String
)
