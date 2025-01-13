package com.example.project_sma.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_data")
data class SleepDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,  // Format: YYYY-MM-DD
    val sleepDuration: Int,  // in hours
    val sleepQuality: String,
    val heartRate: Int,
    val spO2: Int)
