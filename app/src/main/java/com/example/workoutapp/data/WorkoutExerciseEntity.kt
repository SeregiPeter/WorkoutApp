package com.example.workoutapp.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val remote: Boolean = true
)



@Entity(
    tableName = "workout_exercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WorkoutExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val workoutId: Int,
    val exerciseId: Int, // Csak az exercise azonosítóját tároljuk
    val sets: Int,
    val reps: Int?,
    val duration: Int?,
    val rest_time_between: Int,
    val rest_time_after: Int
)


fun WorkoutExerciseEntity.toWorkoutExercise(exercise: Exercise): WorkoutExercise {
    return WorkoutExercise(
        id = exercise.id,
        name = exercise.name,
        description = exercise.description ?: "No description.",
        video_url = exercise.video_url,
        image_url = exercise.image_url,
        duration_based = exercise.duration_based ?: false,
        sets = sets,
        reps = reps,
        duration = duration,
        rest_time_between = rest_time_between,
        rest_time_after = rest_time_after
    )
}

data class WorkoutWithExercises(
    @Embedded val workout: WorkoutEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "workoutId"
    )
    val exercises: List<WorkoutExerciseEntity>
)

suspend fun WorkoutWithExercises.toWorkout(getExercise: suspend (Int) -> Exercise?): Workout {
    val exercisesWithData = exercises.mapNotNull { entity ->
        val exercise = getExercise(entity.exerciseId) // Lekérjük az Exercise objektumot
        exercise?.let { entity.toWorkoutExercise(it) } // Ha sikerült lekérni, átalakítjuk
    }

    return Workout(
        id = workout.id,
        name = workout.name,
        exercises = exercisesWithData,
        remote = false // Lokálisan tárolt adat
    )
}



