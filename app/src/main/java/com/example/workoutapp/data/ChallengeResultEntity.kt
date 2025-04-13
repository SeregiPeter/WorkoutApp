package com.example.workoutapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "challenge_results")
data class ChallengeResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val challengeId: Int,
    val challengeName: String,
    val resultValue: Int,
    val timestamp: Long = System.currentTimeMillis()
)
