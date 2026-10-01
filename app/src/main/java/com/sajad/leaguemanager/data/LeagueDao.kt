package com.sajad.leaguemanager.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LeagueDao {
    @Query("SELECT * FROM leagues ORDER BY rowid DESC")
    fun getAllLeagues(): Flow<List<League>>

    @Query("SELECT * FROM leagues WHERE id = :id")
    suspend fun getLeagueById(id: String): League?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeague(league: League)

    @Delete
    suspend fun deleteLeague(league: League)

    @Query("DELETE FROM leagues WHERE id = :id")
    suspend fun deleteLeagueById(id: String)
}
