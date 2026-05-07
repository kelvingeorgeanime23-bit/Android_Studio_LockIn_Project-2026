package com.kelvin.lockin.data.models

data class AppInfo(
    val appName: String,
    val packageName: String,
    val isSelected: Boolean = false
)