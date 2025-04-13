package com.example.workoutapp.data

data class WorkoutExercise(
    val id: Int,
    val name: String,
    val description: String,
    val video_url: String?,
    val image_url: String?,
    val duration_based: Boolean,
    val sets: Int,
    val reps: Int?,
    val duration: Int?,
    val rest_time_between: Int,
    val rest_time_after: Int
)

fun WorkoutExercise.toWorkoutExerciseEntity(workoutId: Int): WorkoutExerciseEntity {
    return WorkoutExerciseEntity(
        exerciseId = id,
        workoutId = workoutId, // A Workout-hoz kapcsoljuk
        sets = this.sets,
        reps = this.reps,
        duration = this.duration,
        rest_time_between = this.rest_time_between,
        rest_time_after = this.rest_time_after
    )
}

data class Workout(
    val id: Int,
    val name: String,
    val exercises: List<WorkoutExercise>,
    val remote: Boolean = true
)

fun Workout.toWorkoutEntity(): WorkoutEntity {
    return WorkoutEntity(
        id = this.id, // Az ID-t itt figyelni kell, hogy helyesen kezeld
        name = this.name
    )
}


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