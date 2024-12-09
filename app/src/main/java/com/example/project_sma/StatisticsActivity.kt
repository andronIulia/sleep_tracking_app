package com.example.project_sma

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class StatisticsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                StatisticsScreen()
            }
        }
    }
}

@Composable
fun StatisticsScreen(){
    var sleepDuration by remember { mutableStateOf(listOf(4,5,6,7,9) )}
    var heartRate by remember { mutableStateOf(listOf(70,77,75,72,74)) }
    var spO2 by remember { mutableStateOf(listOf(97,96,98,97,96)) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Statistics Overview", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Sleep Duration (Hours)", style = MaterialTheme.typography.bodyMedium)
        SimpleBarChart(dataPoints = sleepDuration)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Heart Rate (BPM)", style = MaterialTheme.typography.bodyMedium)
        SimpleBarChart(dataPoints = heartRate)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "SpO2 (%)", style = MaterialTheme.typography.bodyMedium)
        SimpleBarChart(dataPoints = spO2)
    }
}

@Composable
fun SimpleBarChart(dataPoints: List<Int>){
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        dataPoints.forEach { value ->
            Box(
                modifier = Modifier
                    .height(value.dp)
                    .width(40.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}