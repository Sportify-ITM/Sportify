package com.example.sportify.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "selected_teams")
data class TeamEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val teamId: Int // This should be the ID of the selected team logo
)
