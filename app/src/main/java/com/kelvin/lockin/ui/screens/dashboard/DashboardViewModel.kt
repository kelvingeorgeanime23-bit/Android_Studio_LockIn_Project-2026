package com.kelvin.lockin.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelvin.lockin.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WeeklyStats(
    val weeklyFocusTime: String = "0h 0m",
    val sessionsCompleted: Int = 0,
    val currentStreak: Int = 0
)

class DashboardViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _isLockedIn = MutableStateFlow(false)
    val isLockedIn: StateFlow<Boolean> = _isLockedIn.asStateFlow()

    private val _weeklyStats = MutableStateFlow(WeeklyStats())
    val weeklyStats: StateFlow<WeeklyStats> = _weeklyStats.asStateFlow()

    init {
        loadUserData()
        loadStats()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            // Get full_name from profiles table, fallback to email first part
            _userName.value = user?.userMetadata?.get("full_name")?.toString()
                ?.split(" ")?.firstOrNull()
                ?.replaceFirstChar { it.uppercase() }
                ?: user?.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() }
                        ?: "Warrior"
        }
    }

    private fun loadStats() {
        // TODO: Load from local database or Supabase
        _weeklyStats.value = WeeklyStats(
            weeklyFocusTime = "12h 30m",
            sessionsCompleted = 8,
            currentStreak = 5
        )
    }

    fun endFocusSession() {
        viewModelScope.launch {
            _isLockedIn.value = false
            // TODO: Save session to database, update stats
        }
    }
}