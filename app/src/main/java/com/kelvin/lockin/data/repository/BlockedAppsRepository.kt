package com.kelvin.lockin.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "blocked_apps")

class BlockedAppsRepository(private val context: Context) {

    private val BLOCKED_APPS_KEY = stringSetPreferencesKey("blocked_packages")

    // Read blocked apps as a Flow (auto-updates when data changes)
    val blockedPackages: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[BLOCKED_APPS_KEY] ?: emptySet()
    }

    // Check if a specific app is blocked
    suspend fun isBlocked(packageName: String): Boolean {
        return context.dataStore.data.map { prefs ->
            prefs[BLOCKED_APPS_KEY]?.contains(packageName) ?: false
        }.first()
    }

    // Add app to blocked list
    suspend fun blockApp(packageName: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[BLOCKED_APPS_KEY] ?: emptySet()
            prefs[BLOCKED_APPS_KEY] = current + packageName
        }
    }

    // Remove app from blocked list
    suspend fun unblockApp(packageName: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[BLOCKED_APPS_KEY] ?: emptySet()
            prefs[BLOCKED_APPS_KEY] = current - packageName
        }
    }

    // Replace entire blocked list
    suspend fun setBlockedApps(packages: Set<String>) {
        context.dataStore.edit { prefs ->
            prefs[BLOCKED_APPS_KEY] = packages
        }
    }
}