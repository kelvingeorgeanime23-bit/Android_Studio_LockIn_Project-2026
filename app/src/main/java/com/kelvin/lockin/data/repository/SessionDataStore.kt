package com.kelvin.lockin.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "focus_session")