package com.example.workoutapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChallengeResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: ChallengeResultEntity)

    @Query("SELECT * FROM challenge_results WHERE challengeId = :challengeId ORDER BY timestamp DESC")
    suspend fun getResultsForChallenge(challengeId: Int): List<ChallengeResultEntity>

    @Query("DELETE FROM challenge_results")
    suspend fun clearAllResults()
}