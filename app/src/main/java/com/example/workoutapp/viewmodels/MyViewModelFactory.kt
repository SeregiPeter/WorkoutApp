package com.example.workoutapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.workoutapp.data.Repository

class MyViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(WorkoutViewModel::class.java) -> WorkoutViewModel(repository) as T
            modelClass.isAssignableFrom(ExerciseViewModel::class.java) -> ExerciseViewModel(repository) as T
            modelClass.isAssignableFrom(ChallengeViewModel::class.java) -> ChallengeViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
