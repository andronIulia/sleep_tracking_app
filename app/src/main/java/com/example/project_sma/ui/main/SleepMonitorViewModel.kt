package com.example.project_sma.ui.main

import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_sma.HealthConnectManager
import com.example.project_sma.data.SleepDataEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SleepMonitorViewModel(
    private val healthConnectManager: HealthConnectManager
) : ViewModel() {
    private val _healthData = MutableStateFlow<SleepDataEntity?>(null)
    val healthData: StateFlow<SleepDataEntity?> = _healthData

    private val _permissionsGranted = MutableStateFlow(false)
    val permissionsGranted: StateFlow<Boolean> = _permissionsGranted

    private val _isHealthConnectOpened = MutableStateFlow(false)
    val isHealthConnectOpened: StateFlow<Boolean> = _isHealthConnectOpened

    init {
        checkPermissions()
    }
    private val _errorMessage = MutableStateFlow("")

    fun checkPermissions() {
        viewModelScope.launch {
            _permissionsGranted.value = healthConnectManager.hasAllPermissions()
            Log.d("HealthConnect", "Permissions granted: ${_permissionsGranted.value}")
        }
    }

    fun requestPermissions(launcher: ActivityResultLauncher<Array<String>>) {
        healthConnectManager.requestPermissions(launcher)
    }

    fun loadHealthData() {
        viewModelScope.launch {
           try {
               Log.d("HealthConnect", "Loading health data...")
               val data = healthConnectManager.fetchAndSaveHealthData()
               _healthData.value = data
               Log.d("HealthConnect", "Health data loaded: $data")
           }catch (e: Exception) {
               Log.e("HealthConnect", "Error loading health data", e)
               _errorMessage.value = "Error loading health data: ${e.localizedMessage}"
           }
           }

    }
    fun onPermissionsResult(allGranted: Boolean) {
        _permissionsGranted.value = allGranted
        if (allGranted) {
            loadHealthData()
        }else {
            _errorMessage.value = "Permissions not granted. Please grant the necessary permissions to proceed."
        }
    }

    fun setHealthConnectOpened() {
        _isHealthConnectOpened.value = true
    }
}

