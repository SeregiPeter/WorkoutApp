package com.example.workoutapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workoutapp.data.Repository
import com.example.workoutapp.data.Result
import com.example.workoutapp.data.Workout
import com.example.workoutapp.data.WorkoutCreateRequest
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

    private val _uiErrorMessage = MutableStateFlow<String?>(null)
    val uiErrorMessage: StateFlow<String?> = _uiErrorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchWorkouts()
    }

    fun fetchWorkouts() {
        viewModelScope.launch {
            if(_workouts.value.isEmpty()) {
                _isLoading.value = true
            }
            when (val result = repository.getWorkouts()) {
                is Result.Success -> _workouts.value = result.data
                is Result.Error -> {
                    Log.e("WorkoutViewModel", "Error fetching workouts", result.exception)
                    _uiErrorMessage.value = getFriendlyErrorMessage(result.exception)
                }

                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun fetchWorkout(id: Int, remote: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repository.getWorkout(id, remote)) {
                is Result.Success -> _selectedWorkout.value = result.data
                is Result.Error -> {
                    Log.e("WorkoutViewModel", "Error fetching workout with ID: $id", result.exception)
                    _uiErrorMessage.value = getFriendlyErrorMessage(result.exception)
                }

                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun createWorkout(workout: WorkoutCreateRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repository.createWorkout(workout)) {
                is Result.Success -> {
                    _workouts.value = _workouts.value + result.data
                    _workoutCreated.value = true
                }
                is Result.Error -> {
                    Log.e("WorkoutViewModel", "Error creating workout", result.exception)
                    _uiErrorMessage.value = getFriendlyErrorMessage(result.exception)
                    _workoutCreated.value = false
                }

                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun deleteWorkout(workoutId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repository.deleteWorkout(workoutId)) {
                is Result.Success -> fetchWorkouts()
                is Result.Error -> {
                    Log.e("WorkoutViewModel", "Error deleting workout", result.exception)
                    _uiErrorMessage.value = getFriendlyErrorMessage(result.exception)
                }

                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun resetWorkoutCreated() {
        _workoutCreated.value = false
    }

    fun clearError() {
        _uiErrorMessage.value = null
    }

    private fun getFriendlyErrorMessage(e: Throwable): String {
        return when (e) {
            is java.net.UnknownHostException -> "No internet connection."
            is retrofit2.HttpException -> "Server error (${e.code()})."
            is IllegalStateException -> e.message.toString()
            else -> "Unknown error."
        }
    }
}

