package com.example.project_sma.ui.statistics
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.project_sma.data.SleepDatabase
import com.example.project_sma.data.StatisticsRepository

class StatisticsViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    private val repository: StatisticsRepository = StatisticsRepository(
        SleepDatabase.getDatabase(application).sleepDataDao()  // Corrected DAO reference
    )

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatisticsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}