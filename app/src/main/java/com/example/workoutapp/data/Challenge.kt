package com.example.workoutapp.data

data class Challenge(
    val id: Int,
    val name: String,
    val description: String?,
    val count_reps: Boolean,
    val duration: Int?,
    val measurement_method: String,
    val exercise: ExerciseShort
)