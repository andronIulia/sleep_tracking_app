package com.example.project_sma.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.project_sma.HealthConnectManager
import com.example.project_sma.data.SleepDataEntity
import com.example.project_sma.data.StatisticsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StatisticsViewModel(private val repository: StatisticsRepository,private val healthConnectManager: HealthConnectManager
) : ViewModel() {
    private val _allSleepData = MutableStateFlow<List<SleepDataEntity>>(emptyList())
    val allSleepData: StateFlow<List<SleepDataEntity>> = _allSleepData
    private val _weeklySleepData = MutableStateFlow<List<SleepDataEntity>>(emptyList())
    val weeklySleepData: StateFlow<List<SleepDataEntity>> = _weeklySleepData

    init {
        //loadAllSleepData()
        loadWeeklySleepData()
    }
    fun loadAllSleepData() {
        val data = listOf(
            SleepDataEntity(id=0, date = "2025-01-01", sleepDuration = 7, sleepQuality = "good", heartRate = 70, spO2 = 98),
            SleepDataEntity(id=1, date = "2025-01-02", sleepDuration = 6, sleepQuality = "good", heartRate = 72, spO2 = 99),
            SleepDataEntity(id=2, date = "2025-01-03", sleepDuration = 8, sleepQuality = "good", heartRate = 68, spO2 = 99),
            SleepDataEntity(id=3, date = "2025-01-04", sleepDuration = 7, sleepQuality = "good", heartRate = 75, spO2 = 97)
        )
        viewModelScope.launch {
              _allSleepData.value = repository.getAllSleepData()
        }
        //_allSleepData.value = data
    }
    fun loadWeeklySleepData() {
        viewModelScope.launch {
            try {
                val weeklyData = healthConnectManager.fetchWeeklyHealthData()
                _weeklySleepData.value = weeklyData
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    /*fun getWeeklyAverageSleepDuration() = liveData {
        val data = _weeklySleepData.value
        val average = data.map { it.sleepDuration }.average()
        emit(average)
    }
    fun getAverageSleepDuration() = liveData {
        val data = repository.getAllSleepData()
        val average = data.map { it.sleepDuration }.average()
        emit(average)
    }*/

}