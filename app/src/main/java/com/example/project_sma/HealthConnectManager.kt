package com.example.project_sma

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat.startActivity
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.example.project_sma.data.SleepDataEntity
import com.example.project_sma.data.SleepDatabase
import java.time.Duration
import java.time.Instant
import java.time.LocalDate


class HealthConnectManager(private val context: Context) {
    private val healthConnectClient by lazy {
        HealthConnectClient.getOrCreate(context)
    }

    private val PERMISSIONS = setOf(
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getWritePermission(SleepSessionRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getWritePermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(OxygenSaturationRecord::class),
        HealthPermission.getWritePermission(OxygenSaturationRecord::class),
    )

    suspend fun hasAllPermissions(): Boolean {//=
        val grantedPermissions = healthConnectClient.permissionController.getGrantedPermissions()
        return PERMISSIONS.all { it in grantedPermissions }
    }

    fun requestPermissions(launcher: ActivityResultLauncher<Array<String>>) {
       val permissionsArray = PERMISSIONS.map { it }.toTypedArray()
        launcher.launch(permissionsArray)

    }

    suspend fun fetchHealthData(): SleepDataEntity {
        val sleepData = fetchSleepData()
        val heartRate = fetchHeartRateData()
        val spO2 = fetchSpO2Data()
        return SleepDataEntity(
            date = LocalDate.now().toString(),
            sleepDuration = sleepData,
            sleepQuality = "Good",
            heartRate = heartRate,
            spO2 = spO2
        )


    }

    suspend fun fetchAndSaveHealthData() : SleepDataEntity{
        val sleepDataEntity = fetchHealthData()
        val db = SleepDatabase.getDatabase(context)
        db.sleepDataDao().insertSleepData(sleepDataEntity)
        return sleepDataEntity
    }

    private suspend fun fetchSleepData(): Int {
        val now = Instant.now()
        val startOfDay = now.minus(Duration.ofDays(1))
        val timeRangeFilter = TimeRangeFilter.between(startOfDay, now)
        Log.d("HealthConnect", "Fetching sleep data from: $startOfDay to $now")
        val request = ReadRecordsRequest(SleepSessionRecord::class, timeRangeFilter)
        val response = healthConnectClient.readRecords(request)
        Log.d("HealthConnect", "Fetched Sleep Data: ${response.records}")

        val totalDuration = response.records.sumOf {
            Duration.between(it.startTime, it.endTime).toHours().toInt()
        }
        return totalDuration
    }

    private suspend fun fetchHeartRateData(): Int {
        val now = Instant.now()
        val timeRangeFilter = TimeRangeFilter.between(now.minus(Duration.ofDays(2)), now)
        val request = ReadRecordsRequest(HeartRateRecord::class, timeRangeFilter)
        val response = healthConnectClient.readRecords(request)
        val bpmValues = response.records.flatMap { record ->
            record.samples.map { it.beatsPerMinute }
        }
        return bpmValues.average().toInt()
    }

    private suspend fun fetchSpO2Data(): Int {
        val now = Instant.now()
        val timeRangeFilter = TimeRangeFilter.between(now.minus(Duration.ofDays(2)), now)
        val request = ReadRecordsRequest(OxygenSaturationRecord::class, timeRangeFilter)

        val response = healthConnectClient.readRecords(request)
        val validSpo2Values = response.records.mapNotNull { it.percentage?.value }
        return if (validSpo2Values.isNotEmpty()) {
            validSpo2Values.average().toInt()
        } else {
            -1
        }
    }
}