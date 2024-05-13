package com.example.mad_cw2

import androidx.room.Entity
import androidx.room.PrimaryKey

// League data class is used to save the leaguee information to database
@Entity(tableName = "leagues")
data class League(
    @PrimaryKey(autoGenerate = false) val leagueId: Int,
    val leagueName: String,
    val sport: String,
    val leagueAlternateName: String
)

// Club data class is used to save the club information to the database
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

// Teams data class is used to crate object of Teams
// this class is used in SearchClubInWeb activity
data class Teams(
    val idTeam: String,
    val name: String,
    val teamJersey: String
)

// Jerseys data class is used to crate object of Jerseys
// this class is used in SearchClubInWeb activity
data class  Jerseys(
    val  jerseyLink:String
)