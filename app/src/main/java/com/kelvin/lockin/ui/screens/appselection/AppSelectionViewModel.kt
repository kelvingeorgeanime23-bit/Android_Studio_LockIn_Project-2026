package com.kelvin.lockin.ui.screens.appselection

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kelvin.lockin.data.models.AppInfo
import com.kelvin.lockin.data.repository.BlockedAppsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AppSelectionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BlockedAppsRepository(application)

    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())

    // Merge installed apps with blocked state from DataStore
    val apps: StateFlow<List<AppInfo>> = combine(
        _apps,
        repository.blockedPackages
    ) { installedApps, blockedPackages ->
        installedApps.map { app ->
            app.copy(isSelected = blockedPackages.contains(app.packageName))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadInstalledApps()
    }

    private fun loadInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true

            val pm = getApplication<Application>().packageManager

            val launchableApps = pm.queryIntentActivities(
                Intent(Intent.ACTION_MAIN).also {
                    it.addCategory(Intent.CATEGORY_LAUNCHER)
                },
                PackageManager.GET_META_DATA
            )

            val appList = launchableApps
                .map { resolveInfo ->
                    AppInfo(
                        appName = resolveInfo.loadLabel(pm).toString(),
                        packageName = resolveInfo.activityInfo.packageName,
                        isSelected = false
                    )
                }
                .filter { it.packageName != getApplication<Application>().packageName }
                .sortedBy { it.appName.lowercase() }

            _apps.value = appList
            _isLoading.value = false
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun toggleAppSelection(packageName: String) {
        val app = apps.value.find { it.packageName == packageName } ?: return

        viewModelScope.launch {
            if (app.isSelected) {
                repository.unblockApp(packageName)
            } else {
                repository.blockApp(packageName)
            }
        }
    }

    fun getFilteredApps(): List<AppInfo> {
        val query = _searchQuery.value.lowercase()
        val currentApps = apps.value
        return if (query.isEmpty()) {
            currentApps
        } else {
            currentApps.filter { it.appName.lowercase().contains(query) }
        }
    }

    fun getSelectedCount(): Int {
        return apps.value.count { it.isSelected }
    }
}