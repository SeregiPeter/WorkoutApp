package com.example.workoutapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workoutapp.Config
import com.example.workoutapp.data.Repository
import com.example.workoutapp.data.Workout
import com.example.workoutapp.data.WorkoutCreateRequest
import com.example.workoutapp.data.WorkoutExercise
import com.example.workoutapp.data.dummyExercises
import com.example.workoutapp.data.dummyWorkouts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WorkoutViewModel(private val repository: Repository) : ViewModel() {

    private val _workouts = MutableStateFlow<List<Workout>>(emptyList())
    val workouts: StateFlow<List<Workout>> = _workouts

    private val _selectedWorkout = MutableStateFlow<Workout?>(null)
    val selectedWorkout: StateFlow<Workout?> = _selectedWorkout

    private val _workoutCreated = MutableStateFlow<Boolean>(false)
    val workoutCreated: StateFlow<Boolean> = _workoutCreated

//    init {
//        fetchWorkouts()
//    }

    fun fetchWorkouts() {
        viewModelScope.launch {
            if(Config.USE_DUMMY_DATA) {
                _workouts.value = dummyWorkouts
            } else {
                try {
                    val response = repository.getWorkouts()
                    Log.d("WorkoutViewModel", "API Response: $response")
                    _workouts.value = response
                } catch (e: Exception) {
                    Log.e("WorkoutViewModel", "Error fetching workouts", e)
                }
            }
        }
    }

    fun fetchWorkout(id: Int) {
        viewModelScope.launch {
            if(Config.USE_DUMMY_DATA) {
                _selectedWorkout.value = dummyWorkouts.find{it.id == id}
            } else {
                try {
                    val response = repository.getWorkout(id)
                    Log.d("WorkoutViewModel", "API Response: $response")
                    _selectedWorkout.value = response
                } catch (e: Exception) {
                    Log.e("WorkoutViewModel", "Error fetching workout with ID: $id", e)
                }
            }

        }
    }

    fun createWorkout(workout: WorkoutCreateRequest) {
        viewModelScope.launch {
            if (Config.USE_DUMMY_DATA) {
                val exercises = dummyExercises
                val newWorkout = Workout(
                    id = (_workouts.value.maxOfOrNull { it.id } ?: 0) + 1,
                    name = workout.name,
                    exercises = workout.exercises.mapNotNull { we ->
                        val exercise = exercises.find { it.id == we.exercise_id }
                        if (exercise != null) {
                            WorkoutExercise(
                                id = exercise.id,
                                name = exercise.name,
                                description = exercise.description,
                                video_url = exercise.video_url,
                                image_url = exercise.image_url,
                                duration_based = exercise.duration_based,
                                sets = we.sets,
                                reps = we.reps,
                                duration = we.duration,
                                rest_time_between = we.rest_time_between,
                                rest_time_after = we.rest_time_after
                            )
                        } else {
                            null
                        }
                    }
                )
                _workouts.value = _workouts.value + listOf(newWorkout)
                _workoutCreated.value = true
                dummyWorkouts = dummyWorkouts + newWorkout
                Log.d("WorkoutViewModel", "Dummy workout created: $newWorkout")
                Log.d("WorkoutViewModel", workouts.value.toString())

            } else {
                try {
                    val createdWorkout = repository.createWorkout(workout)
                    _workouts.value = _workouts.value + createdWorkout
                    _workoutCreated.value = true
                    Log.d("WorkoutViewModel", "Workout created successfully: $createdWorkout")
                } catch (e: Exception) {
                    Log.e("WorkoutViewModel", "Error creating workout", e)
                    _workoutCreated.value = false
                }
            }
        }
    }

    fun resetWorkoutCreated() {
        _workoutCreated.value = false
    }
}
