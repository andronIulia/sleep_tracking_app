package com.example.project_sma.ui.statistics

import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.project_sma.data.SleepDataEntity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

@Composable
fun StatisticsScreen(statisticsViewModel: StatisticsViewModel, onBack: () -> Unit) {
    val sleepData by statisticsViewModel.allSleepData.collectAsState(initial = emptyList())
    Log.d("StatisticsScreen", "Received sleep data: $sleepData")
    /*LaunchedEffect(Unit) {
        statisticsViewModel.loadAllSleepData()
    }*/
    LaunchedEffect(sleepData) {
        if (sleepData.isEmpty()) {
            Log.d("StatisticsScreen", "Loading sleep data...")
            statisticsViewModel.loadAllSleepData()
        } else {
            Log.d("StatisticsScreen", "Sleep data loaded: $sleepData")
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            //.padding(16.dp)) {
            .padding(top = 80.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = "Weekly Sleep Duration (hrs)",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        SleepBarChart(sleepData)

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Weekly Average Heart Rate (BPM)",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        HeartRateBarChart(sleepData)
    }
}

@Composable
fun SleepBarChart(sleepDataList: List<SleepDataEntity>) {
    val context = LocalContext.current

    val entries = sleepDataList
        .filter { it.sleepDuration > 0 }
        .mapIndexed { index, sleepData ->
            BarEntry(index.toFloat(), sleepData.sleepDuration.toFloat())
        }
    Log.d("SleepBarChart", "Entries: $entries")
    val labels = sleepDataList.map { it.date }
    LaunchedEffect(sleepDataList) {
        Log.d("SleepBarChart", "Data changed, reloading chart...")
    }
    AndroidView(
        factory = {
            BarChart(context).apply {
                val barDataSet = BarDataSet(entries, "Sleep Duration")
                barDataSet.apply {
                    //colors = ColorTemplate.MATERIAL_COLORS.toList()
                    color = Color.BLUE
                    valueTextColor = Color.BLACK
                    valueTextSize = 12f
                    setDrawValues(true)
                }

                data = BarData(barDataSet).apply {
                    barWidth = 0.5f
                }
                setData(data)

                description.isEnabled = false
                setFitBars(true)
                setScaleEnabled(false)

                axisLeft.apply {
                    axisMinimum = 0f
                    axisMaximum = 10f
                    granularity = 1f
                    textColor = Color.BLACK
                    textSize = 12f
                }
                axisRight.isEnabled = false
                xAxis.apply {
                    valueFormatter = IndexAxisValueFormatter(labels)
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f
                    isGranularityEnabled = true
                    setDrawGridLines(false)
                    labelRotationAngle = -45f
                    textColor = Color.BLACK
                    textSize = 12f
                }
                animateY(1000)
                notifyDataSetChanged()
                invalidate()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(8.dp)
    )
}

@Composable
fun HeartRateBarChart(sleepDataList: List<SleepDataEntity>) {
    val context = LocalContext.current

    val entries = sleepDataList.mapIndexed { index, sleepData ->
        BarEntry(index.toFloat(), sleepData.heartRate.toFloat())
    }

    AndroidView(
        factory = {
            BarChart(context).apply {
                data = BarData(BarDataSet(entries, "Heart Rate BPM").apply {
                    colors = ColorTemplate.COLORFUL_COLORS.toList()
                    valueTextColor = Color.BLACK
                    valueTextSize = 12f
                })

                description.isEnabled = false
                setFitBars(true)
                axisLeft.axisMinimum = 0f
                axisRight.isEnabled = false
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.granularity = 1f
                animateY(1000)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    )
}