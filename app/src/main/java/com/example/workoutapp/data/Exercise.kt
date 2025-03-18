package com.example.workoutapp.data

data class Exercise(
    val id: Int,
    val name: String,
    val description: String,
    val video_url: String,
    val image_url: String,
    val duration_based: Boolean,
    val category: Category
)

data class ExerciseShort(
    val id: Int,
    val name: String,
    val description: String,
    val video_url: String,
    val image_url: String,
    val duration_based: Boolean
)

data class Category(
    val id: Int,
    val name: String,
    val description: String?
)
