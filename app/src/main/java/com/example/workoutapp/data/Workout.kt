package com.example.workoutapp.data

data class WorkoutExercise(
    val id: Int,
    val name: String,
    val description: String,
    val video_url: String,
    val image_url: String,
    val duration_based: Boolean,
    val sets: Int,
    val reps: Int?,
    val duration: Int?,
    val rest_time_between: Int,
    val rest_time_after: Int
)

data class Workout(
    val id: Int,
    val name: String,
    val exercises: List<WorkoutExercise>
)


data class WorkoutCreateRequest(
    val name: String,
    val exercises: List<WorkoutExerciseRequest>
)

data class WorkoutExerciseRequest(
    val exercise_id: Int,
    val sets: Int,
    val reps: Int?,
    val duration: Int?,
    val rest_time_between: Int,
    val rest_time_after: Int
)