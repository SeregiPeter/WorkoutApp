package com.example.workoutapp.data

import kotlinx.coroutines.flow.Flow

class Repository(
    private val apiService: ApiService,
    private val challengeResultDao: ChallengeResultDao
    ) {
    suspend fun getExercises(): List<Exercise> {
        return apiService.getExercises()
    }

    suspend fun getExercise(id: Int): Exercise {
        return apiService.getExercise(id)
    }

    suspend fun getWorkouts(): List<Workout> {
        return apiService.getWorkouts()
    }

    suspend fun getWorkout(id: Int): Workout {
        return apiService.getWorkout(id)
    }

    suspend fun createWorkout(workout: WorkoutCreateRequest): Workout {
        return apiService.createWorkout(workout)
    }

    suspend fun getChallenges(): List<Challenge> {
        return apiService.getChallenges()
    }

    suspend fun getChallenge(id: Int): Challenge {
        return apiService.getChallenge(id)
    }

    suspend fun saveChallengeResult(result: ChallengeResult) {
        challengeResultDao.insertResult(result)
    }

    fun getChallengeResults(challengeId: Int): Flow<List<ChallengeResult>> {
        return challengeResultDao.getResultsForChallenge(challengeId)
    }
}
