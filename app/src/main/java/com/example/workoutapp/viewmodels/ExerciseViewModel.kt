package com.example.workoutapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workoutapp.Config
import com.example.workoutapp.data.Exercise
import com.example.workoutapp.data.Repository
import com.example.workoutapp.data.dummyExercises
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExerciseViewModel(private val repository: Repository) : ViewModel() {

    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> = _exercises

    private val _selectedExercise = MutableStateFlow<Exercise?>(null)
    val selectedExercise: StateFlow<Exercise?> = _selectedExercise


    init {
        fetchExercises()
    }

    fun fetchExercises() {
        viewModelScope.launch {
            if(Config.USE_DUMMY_DATA) {
                _exercises.value = dummyExercises
            } else {
                try {
                    val response = repository.getExercises()
                    Log.d("ExerciseViewModel", "API Response: $response")
                    _exercises.value = response
                } catch (e: Exception) {
                    Log.e("ExerciseViewModel", "Error fetching exercises", e)
                }
            }
        }
    }

    fun fetchExercise(id: Int) {
        viewModelScope.launch {
            if(Config.USE_DUMMY_DATA) {
                _selectedExercise.value = dummyExercises.find {it.id == id}
            } else {
                try {
                    val response = repository.getExercise(id)
                    Log.d("ExerciseViewModel", "API Response: $response")
                    _selectedExercise.value = response
                } catch (e: Exception) {
                    Log.e("ExerciseViewModel", "Error fetching exercise with ID: $id", e)
                }
            }
        }
    }
}
