package com.example.project_sma.data

class StatisticsRepository(private val sleepDataDao: SleepDataDao) {
    suspend fun getAllSleepData(): List<SleepDataEntity> {
        return sleepDataDao.getAllData()
    }
}