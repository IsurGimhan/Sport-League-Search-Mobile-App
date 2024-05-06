package com.example.mad_cw2

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leagues")
data class League(
    @PrimaryKey(autoGenerate = false) val leagueId: Int,
    val leagueName: String,
    val sport: String,
    val leagueAlternateName: String
)

@Entity(tableName = "Clubs")
data class Club(
    @PrimaryKey(autoGenerate = false) val idTeam: String,
    val name: String,
    val shortName: String,
    val alternateName: String,
    val formedYear: String,
    val league: String,
    val stadium: String,
    val keywords: String,
    val stadiumThumb: String,
    val stadiumLocation: String,
    val stadiumCapacity: String,
    val website: String,
    val teamJersey: String,
    val teamLogo: String
)

data class Teams(
    val idTeam: String,
    val name: String,
    val teamJersey: String
)

data class  Jerseys(
    val  jerseyLink:String
)