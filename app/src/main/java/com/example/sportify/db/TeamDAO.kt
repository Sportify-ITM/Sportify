package com.example.sportify.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TeamDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTeam(teamEntity: TeamEntity?)

    @Query("SELECT * FROM team LIMIT 1")
    fun getSelectedTeam(): TeamEntity?
}
