package com.example.workoutapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workoutapp.data.Element
import com.example.workoutapp.data.Repository
import com.example.workoutapp.data.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel(
    private val repository: Repository
) : ViewModel() {

    private val _fitnessStations = MutableStateFlow<List<Element>>(emptyList())
    val fitnessStations: StateFlow<List<Element>> = _fitnessStations

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _uiErrorMessage = MutableStateFlow<String?>(null)
    val uiErrorMessage: StateFlow<String?> = _uiErrorMessage

    fun clearError() {
        _uiErrorMessage.value = null
    }

    fun loadFitnessStations(lat: Double, lon: Double, radius: Int = 5000) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repository.fetchFitnessStations(lat, lon, radius)) {
                is Result.Success -> _fitnessStations.value = result.data
                is Result.Error -> {
                    _uiErrorMessage.value = getFriendlyErrorMessage(result.exception)
                    _fitnessStations.value = emptyList()
                }
            }
            _isLoading.value = false
        }
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

