package com.example.sportify.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sportify.db.TeamEntity

@Dao
interface TeamDAO {
    @Insert
    suspend fun insertTeam(team: TeamEntity)

    @Query("SELECT * FROM team WHERE id = 1")
    suspend fun getSelectedTeam(): TeamEntity?
}