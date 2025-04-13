package com.example.workoutapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workoutapp.data.Challenge
import com.example.workoutapp.data.ChallengeResultEntity
import com.example.workoutapp.data.Repository
import com.example.workoutapp.data.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChallengeViewModel(private val repository: Repository) : ViewModel() {

    private val _challenges = MutableStateFlow<List<Challenge>>(emptyList())
    val challenges: StateFlow<List<Challenge>> = _challenges

    private val _selectedChallenge = MutableStateFlow<Challenge?>(null)
    val selectedChallenge: StateFlow<Challenge?> = _selectedChallenge

    private val _results = MutableStateFlow<List<ChallengeResultEntity>>(emptyList())
    val results: StateFlow<List<ChallengeResultEntity>> = _results

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _uiErrorMessage = MutableStateFlow<String?>(null)
    val uiErrorMessage: StateFlow<String?> = _uiErrorMessage

    init {
        fetchChallenges()
    }

    fun clearError() {
        _uiErrorMessage.value = null
    }

    fun fetchChallenges() {
        viewModelScope.launch {
            if (_challenges.value.isEmpty()) {
                _isLoading.value = true
            }
            when (val result = repository.getChallenges()) {
                is Result.Success -> _challenges.value = result.data
                is Result.Error -> {
                    _uiErrorMessage.value = getFriendlyErrorMessage(result.exception)
                    Log.e("ChallengeViewModel", "Error fetching challenges", result.exception)
                }
            }
            _isLoading.value = false
        }
    }

    fun fetchChallenge(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repository.getChallenge(id)) {
                is Result.Success -> _selectedChallenge.value = result.data
                is Result.Error -> {
                    _uiErrorMessage.value = getFriendlyErrorMessage(result.exception)
                    Log.e("ChallengeViewModel", "Error fetching challenge with ID: $id", result.exception)
                }
            }
            _isLoading.value = false
        }
    }

    fun saveResult(challenge: Challenge, resultValue: Int) {
        val result = ChallengeResultEntity(
            challengeId = challenge.id,
            challengeName = challenge.name,
            resultValue = resultValue
        )
        viewModelScope.launch {
            when (val response = repository.saveChallengeResult(result)) {
                is Result.Success -> { /* Optionally trigger reload or toast */ }
                is Result.Error -> {
                    _uiErrorMessage.value = getFriendlyErrorMessage(response.exception)
                    Log.e("ChallengeViewModel", "Error saving result", response.exception)
                }
            }
        }
    }

    fun loadResults(challengeId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repository.getChallengeResults(challengeId)) {
                is Result.Success -> _results.value = result.data
                is Result.Error -> {
                    _uiErrorMessage.value = getFriendlyErrorMessage(result.exception)
                    Log.e("ChallengeViewModel", "Error loading results", result.exception)
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

