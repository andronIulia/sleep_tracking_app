package com.example.project_sma.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SleepDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleepData(healthData: SleepDataEntity)

    @Query("SELECT * FROM sleep_data WHERE date = :date")
    suspend fun getDataByDate(date: String): SleepDataEntity?

    @Query("SELECT * FROM sleep_data WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getDataBetweenDates(startDate: String, endDate: String): List<SleepDataEntity>

    @Query("SELECT * FROM sleep_data ORDER BY date DESC")
    suspend fun getAllData(): List<SleepDataEntity>
}