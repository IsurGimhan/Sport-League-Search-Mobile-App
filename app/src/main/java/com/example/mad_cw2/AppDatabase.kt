package com.example.mad_cw2

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [League::class, Club::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun LeagueDao(): LeagueDao
    abstract fun ClubDao(): ClubDao
}