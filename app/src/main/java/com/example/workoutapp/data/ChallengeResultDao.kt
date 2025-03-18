package com.example.workoutapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: ChallengeResult)

    @Query("SELECT * FROM challenge_results WHERE challengeId = :challengeId ORDER BY timestamp DESC")
    fun getResultsForChallenge(challengeId: Int): Flow<List<ChallengeResult>>

    @Query("DELETE FROM challenge_results")
    suspend fun clearAllResults()
}