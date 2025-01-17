package com.example.project_sma.ui.statistics

import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import com.example.project_sma.data.SleepDataEntity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.time.LocalDate

@Composable
fun StatisticsScreen(statisticsViewModel: StatisticsViewModel, onBack: () -> Unit) {
    //val sleepData by statisticsViewModel.allSleepData.collectAsState()
    val sleepData by statisticsViewModel.weeklySleepData.collectAsState()
    LaunchedEffect(Unit) {
        //statisticsViewModel.loadAllSleepData()
        statisticsViewModel.loadWeeklySleepData()
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            //.padding(16.dp)) {
            .padding(top = 80.dp, start = 16.dp, end = 16.dp)
    ) {
        item {
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

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Weekly Average SpO2 (%)",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            SpO2BarChart(sleepData)
        }
    }
}

@Composable
fun SleepBarChart(sleepDataList: List<SleepDataEntity>) {
    val context = LocalContext.current
    val dayLabels = sleepDataList.map { LocalDate.parse(it.date).dayOfWeek.name.take(7) }
    val entries = sleepDataList
        .mapIndexed { index, sleepData ->
            BarEntry(index.toFloat(), sleepData.sleepDuration.toFloat())
        }

    AndroidView(
            factory = {
                BarChart(context).apply {
                    val barDataSet = BarDataSet(entries, "Sleep Duration (hrs)")
                    barDataSet.apply {
                        color = Color.BLUE
                        valueTextColor = Color.BLACK
                        valueTextSize = 12f
                        setDrawValues(true)
                    }

                    data = BarData(barDataSet).apply {
                        barWidth = 0.5f
                    }

                    description.isEnabled = false
                    setFitBars(true)
                    setScaleEnabled(false)

                    axisLeft.apply {
                        axisMinimum = 0f
                        axisMaximum = 24f
                        granularity = 1f
                        textColor = Color.BLUE
                        textSize = 12f
                    }
                    axisRight.isEnabled = false
                    xAxis.apply {
                        valueFormatter = IndexAxisValueFormatter(sleepDataList.map { it.date })
                        position = XAxis.XAxisPosition.BOTTOM
                        granularity = 1f
                        isGranularityEnabled = true
                        setDrawGridLines(false)
                        labelRotationAngle = -45f
                        textColor = Color.BLUE
                        textSize = 12f
                    }
                    animateY(1000)
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
    val dayLabels = sleepDataList.map { LocalDate.parse(it.date).dayOfWeek.name.take(3) }
    val entries = sleepDataList.mapIndexed { index, sleepData ->
        BarEntry(index.toFloat(), sleepData.heartRate.toFloat())
    }

    AndroidView(
        factory = {
            BarChart(context).apply {
                val barDataSet = BarDataSet(entries, "Heart Rate (BPM)").apply {
                    color = Color.RED
                    valueTextColor = Color.BLACK
                    valueTextSize = 12f
                }
                data = BarData(barDataSet).apply { barWidth = 0.5f }
                description.isEnabled = false
                setFitBars(true)
                axisLeft.axisMinimum = 0f
                axisRight.isEnabled = false
                xAxis.apply {
                    valueFormatter = IndexAxisValueFormatter(sleepDataList.map { it.date })
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f
                    setDrawGridLines(false)
                }
                animateY(1000)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    )
}

@Composable
fun SpO2BarChart(sleepDataList: List<SleepDataEntity>) {
    val context = LocalContext.current
    val dayLabels = sleepDataList.map { LocalDate.parse(it.date).dayOfWeek.name.take(7) }

    val entries = sleepDataList.mapIndexed { index, sleepData ->
        BarEntry(index.toFloat(), sleepData.spO2.toFloat())
    }

    AndroidView(
        factory = {
            BarChart(context).apply {
                val barDataSet = BarDataSet(entries, "SpO2 (%)").apply {
                    color = Color.GREEN
                    valueTextColor = Color.BLACK
                    valueTextSize = 12f
                }

                data = BarData(barDataSet).apply { barWidth = 0.5f }

                description.isEnabled = false
                setFitBars(true)

                axisLeft.axisMinimum = 90f
                axisLeft.axisMaximum = 100f
                axisRight.isEnabled = false

                xAxis.apply {
                    valueFormatter = IndexAxisValueFormatter(sleepDataList.map { it.date })
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f
                    setDrawGridLines(false)
                }

                animateY(1000)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(8.dp)
    )
}