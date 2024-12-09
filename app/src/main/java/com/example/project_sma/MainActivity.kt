package com.example.project_sma

import android.icu.text.CaseMap.Title
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SleepTrackerTheme {
                SleepMonitorScreen()
            }
        }
    }
}

@Composable
fun SleepMonitorScreen(){
    var isMonitoring by remember { mutableStateOf(false) }
    var sleepDuration by remember { mutableStateOf(0) }
    var sleepQuality by remember { mutableStateOf("Not registered") }
    var heartRate by remember { mutableStateOf(0) }
    var spO2 by remember { mutableStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    //var steps

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Welcome, User!", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(20.dp))

        //Status Section
        Text(
            text = if (isMonitoring) "Monitoring Sleep: Active " else "Monitoring Sleep: Inactive",
            style = MaterialTheme.typography.bodyLarge,
            color = if (isMonitoring) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Start/Stop Button
        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                    )
        ) {
            Text(text = if (isMonitoring) "Stop Monitoring" else "Start Monitoring")
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Sleep Data Summary
        SleepDataCard(title = "Total Sleep", value = "$sleepDuration hours")
        Spacer(modifier = Modifier.height(16.dp))
        SleepDataCard(title = "Sleep Quality", value = sleepQuality)
        Spacer(modifier = Modifier.height(16.dp))
        SleepDataCard(title = "Heart Rate", value = "$heartRate bpm")
        Spacer(modifier = Modifier.height(16.dp))
        SleepDataCard(title = "SpO2", value = "$spO2%")
        Spacer(modifier = Modifier.height(16.dp))

        /*Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(vertical = 20.dp)
        ) {
            CircularProgressIndicator(
                progress = { spO2 / 100f },
                modifier = Modifier.size(100.dp),
                color = if (spO2 >= 95) Color.Green else Color.Red,
                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
            )
            Text(
                text = "$spO2%",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }*/

        Spacer(modifier = Modifier.height(30.dp))

        //Graph
        /*Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp)
                .background(
                    MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Graph to be implemented", color = Color.DarkGray)
        }*/
    }

    if(showDialog){
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Confirm Action") },
            text = {
                Text(
                    text = if (isMonitoring) {
                        "Are you sure you want to stop monitoring?"
                    } else {
                        "Are you sure you want to start monitoring?"
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        isMonitoring = !isMonitoring
                        if (isMonitoring) {
                            sleepDuration = 7
                            sleepQuality = "Good"
                            heartRate = 75
                            spO2 = 98
                        } else {
                            sleepDuration = 0
                            sleepQuality = "Not registered"
                            heartRate = 0
                            spO2 = 0
                        }
                        showDialog = false
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false }
                ) {
                    Text("No")
                }
            }
        )
    }
}

@Composable
fun SleepDataCard(title: String, value: String){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SleepTrackerTheme {
        SleepMonitorScreen()
    }
}

