package com.example.workoutapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workoutapp.Config
import com.example.workoutapp.data.Challenge
import com.example.workoutapp.data.ChallengeResult
import com.example.workoutapp.data.Repository
import com.example.workoutapp.data.dummyChallenges
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChallengeViewModel(private val repository: Repository) : ViewModel() {

    private val _challenges = MutableStateFlow<List<Challenge>>(emptyList())
    val challenges: StateFlow<List<Challenge>> = _challenges

    private val _selectedChallenge = MutableStateFlow<Challenge?>(null)
    val selectedChallenge: StateFlow<Challenge?> = _selectedChallenge

    private val _results = MutableStateFlow<List<ChallengeResult>>(emptyList())
    val results: StateFlow<List<ChallengeResult>> = _results

    fun fetchChallenges() {
        viewModelScope.launch {
            if(Config.USE_DUMMY_DATA) {
                _challenges.value = dummyChallenges
            } else {
                try {
                    val response = repository.getChallenges()
                    Log.d("ChallengeViewModel", "API Response: $response")
                    _challenges.value = response
                } catch (e: Exception) {
                    Log.e("ChallengeViewModel", "Error fetching challenges", e)
                }
            }
        }
    }

    fun fetchChallenge(id: Int) {
        viewModelScope.launch {
            if(Config.USE_DUMMY_DATA) {
                _selectedChallenge.value = dummyChallenges.find{it.id == id}
            } else {
                try {
                    val response = repository.getChallenge(id)
                    Log.d("ChallengeViewModel", "API Response: $response")
                    _selectedChallenge.value = response
                } catch (e: Exception) {
                    Log.e("ChallengeViewModel", "Error fetching challenge with ID: $id", e)
                }
            }

        }
    }

    fun saveResult(challenge: Challenge, resultValue: Int) {
        val result = ChallengeResult(
            challengeId = challenge.id,
            challengeName = challenge.name,
            resultValue = resultValue
        )
        viewModelScope.launch {
            repository.saveChallengeResult(result)
        }
    }

    fun loadResults(challengeId: Int) {
        viewModelScope.launch {
            repository.getChallengeResults(challengeId)
                .collect { challengeResults ->
                    _results.value = challengeResults
                }
        }
    }

}
