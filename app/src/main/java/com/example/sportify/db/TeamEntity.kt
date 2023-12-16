package com.example.sportify.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "team")
data class TeamEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val teamId: Int)
