package com.kelvin.lockin.data.models

data class User(
    val id: String,
    val email: String,
    val fullName: String = "",
    val phoneNumber: String = ""
)