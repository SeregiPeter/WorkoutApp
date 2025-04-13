package com.example.workoutapp.data

import retrofit2.http.GET
import retrofit2.http.Path

interface WorkoutApiService {
    @GET("exercises")
    suspend fun getExercises(): List<Exercise>

    @GET("exercises/{id}")
    suspend fun getExercise(@Path("id") exerciseId: Int): Exercise

    @GET("workouts")
    suspend fun getWorkouts(): List<Workout>

    @GET("workouts/{id}")
    suspend fun getWorkout(@Path("id") workoutId: Int): Workout

    @GET("categories")
    suspend fun getCategories(): List<Category>

    @GET("challenges")
    suspend fun getChallenges(): List<Challenge>

    @GET("challenges/{id}")
    suspend fun getChallenge(@Path("id") challengeId: Int): Challenge
}
