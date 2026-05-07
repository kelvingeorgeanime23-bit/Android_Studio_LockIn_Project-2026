package com.kelvin.lockin.data.repository

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "https://drzecaezqceldoozcvuo.supabase.co",  // ← FIXED: removed trailing space
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRyemVjYWV6cWNlbGRvb3pjdnVvIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzgwODA4ODEsImV4cCI6MjA5MzY1Njg4MX0.l4-JBbCH8jYJ36HIIo1zU9HGTZ5sg_Zm6Wd2Mefi_yA"
    ) {
        install(Auth)
        install(Postgrest)
    }
}

@Serializable
data class ProfileInsert(
    val id: String,
    val email: String,
    val full_name: String,
    val phone_number: String
)

class AuthService {
    private val auth = SupabaseClient.client.auth

    suspend fun signIn(email: String, password: String) {
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signUp(email: String, password: String, fullName: String, phoneNumber: String) {
        // Step 1: Sign up and CAPTURE the result
        val result = auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }

        // Step 2: Get user ID from the sign-up result (NOT currentUserOrNull)
        val userId = result?.id
        android.util.Log.d("LOCKIN_DEBUG", "SignUp result userId: $userId")

        if (userId == null) {
            android.util.Log.e("LOCKIN_DEBUG", "ERROR: signUp returned null userId")
            throw Exception("Failed to create user account")
        }

        // Step 3: Insert into profiles table
        try {
            SupabaseClient.client.from("profiles").insert(
                ProfileInsert(
                    id = userId,
                    email = email,
                    full_name = fullName,
                    phone_number = phoneNumber
                )
            )
            android.util.Log.d("LOCKIN_DEBUG", "Profile inserted successfully for user: $userId")
        } catch (e: Exception) {
            android.util.Log.e("LOCKIN_DEBUG", "Profile insert failed: ${e.message}", e)
            throw Exception("Account created but profile save failed: ${e.message}")
        }
    }

    suspend fun sendPasswordReset(email: String) {
        auth.resetPasswordForEmail(email)
    }

    suspend fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser() = auth.currentUserOrNull()

    suspend fun getProfileName(): String? {
        val userId = auth.currentUserOrNull()?.id
        android.util.Log.d("LOCKIN_DEBUG", "getProfileName() - current userId: $userId")

        if (userId == null) {
            android.util.Log.d("LOCKIN_DEBUG", "No user logged in")
            return null
        }

        return try {
            val profiles = SupabaseClient.client
                .from("profiles")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeList<ProfileInsert>()

            android.util.Log.d("LOCKIN_DEBUG", "Found ${profiles.size} profiles for user $userId")
            profiles.forEach {
                android.util.Log.d("LOCKIN_DEBUG", "Profile data: full_name=${it.full_name}, email=${it.email}")
            }

            profiles.firstOrNull()?.full_name

        } catch (e: Exception) {
            android.util.Log.e("LOCKIN_DEBUG", "getProfileName() query failed: ${e.message}", e)
            null
        }
    }
}