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
        supabaseUrl = "https://drzecaezqceldoozcvuo.supabase.co",
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
        auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
        val userId = auth.currentUserOrNull()?.id ?: return
        SupabaseClient.client.from("profiles").insert(
            ProfileInsert(
                id = userId,
                email = email,
                full_name = fullName,
                phone_number = phoneNumber
            )
        )
    }

    suspend fun sendPasswordReset(email: String) {
        auth.resetPasswordForEmail(email)
    }

    suspend fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser() = auth.currentUserOrNull()
}