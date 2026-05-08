package com.kelvin.lockin.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "blocked_apps")

class BlockedAppsRepository(private val context: Context) {

    // Store as "AppName|PackageName" pairs in ONE set
    private val BLOCKED_APPS_KEY = stringSetPreferencesKey("blocked_apps")

    // Raw data: "Instagram|com.instagram.android"
    val blockedAppsRaw: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[BLOCKED_APPS_KEY] ?: emptySet()
    }

    // Get app names for display
    val blockedAppNames: Flow<List<String>> = blockedAppsRaw.map { set ->
        set.map { it.substringBefore("|") }
    }

    // Get package names for accessibility service
    val blockedPackages: Flow<Set<String>> = blockedAppsRaw.map { set ->
        set.map { it.substringAfter("|") }.toSet()
    }

    suspend fun blockApp(appName: String, packageName: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[BLOCKED_APPS_KEY] ?: emptySet()
            prefs[BLOCKED_APPS_KEY] = current + "$appName|$packageName"
        }
    }

    suspend fun unblockApp(packageName: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[BLOCKED_APPS_KEY] ?: emptySet()
            // Remove entry that ends with "|packageName"
            prefs[BLOCKED_APPS_KEY] = current.filter { !it.endsWith("|$packageName") }.toSet()
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { prefs ->
            prefs[BLOCKED_APPS_KEY] = emptySet()
        }
    }
}