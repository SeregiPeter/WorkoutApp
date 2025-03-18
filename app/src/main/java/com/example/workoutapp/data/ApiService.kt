package com.example.workoutapp.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("exercises")
    suspend fun getExercises(): List<Exercise>

    @GET("exercises/{id}")
    suspend fun getExercise(@Path("id") exerciseId: Int): Exercise

    @GET("workouts")
    suspend fun getWorkouts(): List<Workout>

    @GET("workouts/{id}")
    suspend fun getWorkout(@Path("id") workoutId: Int): Workout

    @GET("categories/{id}")
    suspend fun getCategory(@Path("id") categoryId: Int): Category

    @POST("workouts")
    suspend fun createWorkout(@Body workout: WorkoutCreateRequest): Workout

    @GET("challenges")
    suspend fun getChallenges(): List<Challenge>

    @GET("challenges/{id}")
    suspend fun getChallenge(@Path("id") challengeId: Int): Challenge
}
