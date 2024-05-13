package com.example.mad_cw2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface LeagueDao {
    // insert  method will add data to the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(league: List<League>)

}

@Dao
interface ClubDao{
    // insert method will add data to the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(club : List<Club>)

    // searchClub method will check if the name contain the inputString or not and retrieve a List of clubs
    @Query("SELECT * FROM Clubs WHERE LOWER(name) LIKE '%' || LOWER(:inputString) || '%' OR LOWER(league) LIKE '%' || LOWER(:inputString) || '%'")
    fun searchClubs(inputString: String): List<Club>

}
