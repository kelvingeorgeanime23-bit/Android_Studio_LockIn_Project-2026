package com.kelvin.lockin.data.repository

class AuthRepository {
    private val authService = AuthService()

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            authService.signIn(email, password)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String
    ): Result<Unit> {
        return try {
            authService.signUp(email, password, fullName, phoneNumber)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun forgotPassword(email: String): Result<Unit> {
        return try {
            authService.sendPasswordReset(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            authService.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser() = authService.getCurrentUser()

    suspend fun getProfileName(): String? {
        return try {
            authService.getProfileName()
        } catch (e: Exception) {
            null
        }
    }
}