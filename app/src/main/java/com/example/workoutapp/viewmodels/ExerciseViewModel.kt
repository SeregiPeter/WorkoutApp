package com.example.workoutapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workoutapp.data.Category
import com.example.workoutapp.data.Exercise
import com.example.workoutapp.data.Repository
import com.example.workoutapp.data.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExerciseViewModel(private val repository: Repository) : ViewModel() {

    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> = _exercises

    private val _selectedExercise = MutableStateFlow<Exercise?>(null)
    val selectedExercise: StateFlow<Exercise?> = _selectedExercise

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    val filteredExercises = combine(_exercises, _selectedCategory) { exercises, category ->
        if (category == null) exercises else exercises.filter { it.category.name == category }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _uiErrorMessage = MutableStateFlow<String?>(null)
    val uiErrorMessage: StateFlow<String?> = _uiErrorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading


    init {
        fetchExercises()
    }

    fun fetchExercises() {
        viewModelScope.launch {
            if (_exercises.value.isEmpty()) {
                _isLoading.value = true
            }
            when (val result = repository.getExercises()) {
                is Result.Success -> _exercises.value = result.data
                is Result.Error -> {
                    Log.e("ExerciseViewModel", "Error fetching exercises", result.exception)
                    _uiErrorMessage.value = getFriendlyErrorMessage(result.exception)
                }

            }
            _isLoading.value = false
        }
    }

    fun fetchExercise(id: Int) {
        viewModelScope.launch {
            _selectedExercise.value = null
            _isLoading.value = true
            when (val result = repository.getExercise(id)) {
                is Result.Success -> _selectedExercise.value = result.data
                is Result.Error -> {
                    Log.e("ExerciseViewModel", "Error fetching exercise with ID: " + id, result.exception)
                    _uiErrorMessage.value = getFriendlyErrorMessage(result.exception)
                }

            }
            _isLoading.value = false
        }
    }

    fun fetchCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repository.getCategories()) {
                is Result.Success -> {
                    _categories.value =
                        listOf(Category(id = -1, name = "All", description = null)) + result.data
                }

                is Result.Error -> {
                    Log.e("ExerciseViewModel", "Error fetching categories", result.exception)
                    _uiErrorMessage.value = getFriendlyErrorMessage(result.exception)
                    // fallback: csak az "All" opció
                    _categories.value = listOf(Category(id = -1, name = "All", description = null))
                }
            }
            _isLoading.value = false
        }
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = if (category == "All") null else category
    }

    fun clearError() {
        _uiErrorMessage.value = null
    }

    private fun getFriendlyErrorMessage(e: Throwable): String {
        return when (e) {
            is java.net.UnknownHostException -> "No internet connection."
            is retrofit2.HttpException -> "Server error (${e.code()})."
            else -> "Unknown error."
        }
    }
}
