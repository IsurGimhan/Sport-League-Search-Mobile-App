package com.example.mad_cw2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LeagueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(league: List<League>)

}

@Dao
interface ClubDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(club : List<Club>)

    @Query("SELECT * FROM Clubs WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' OR LOWER(league) LIKE '%' || LOWER(:query) || '%'")
    fun searchClubs(query: String): List<Club>

}
