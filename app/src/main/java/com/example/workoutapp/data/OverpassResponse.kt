package com.example.workoutapp.data

data class OverpassResponse(
    val elements: List<Element>
)

data class Element(
    val id: Long,
    val lat: Double?, // csak node típusnál
    val lon: Double?,
    val tags: Map<String, String>?,
    val type: String,
    val center: Center? // csak way típusnál
)

data class Center(
    val lat: Double,
    val lon: Double
)

