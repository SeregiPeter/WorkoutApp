package com.example.workoutapp.data
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface WorkoutDao {

    @Transaction
    @Query("SELECT * FROM workouts")
    suspend fun getWorkoutsWithExercises(): List<WorkoutWithExercises>

    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    suspend fun getWorkoutById(workoutId: Int): WorkoutWithExercises?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<WorkoutExerciseEntity>)

    @Transaction
    suspend fun insertWorkoutWithExercises(workout: WorkoutEntity, exercises: List<WorkoutExerciseEntity>): Long {
        val workoutId = insertWorkout(workout).toInt()
        val exerciseEntities = exercises.map { it.copy(workoutId = workoutId) }
        insertExercises(exerciseEntities)
        return workoutId.toLong()
    }

    @Query("DELETE FROM workouts WHERE id = :workoutId")
    suspend fun deleteWorkout(workoutId: Int)

    @Transaction
    suspend fun deleteWorkoutWithExercises(workoutId: Int) {
        deleteWorkoutExercises(workoutId)
        deleteWorkout(workoutId)
    }

    @Query("DELETE FROM workout_exercises WHERE workoutId = :workoutId")
    suspend fun deleteWorkoutExercises(workoutId: Int)
}


