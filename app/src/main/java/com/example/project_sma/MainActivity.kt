package com.example.project_sma

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if(currentUser == null){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            setContent {
                SleepTrackerTheme {
                    SleepMonitorScreen(onLogout = { handleLogout(this) })
                }
            }
        }
    }
     private fun handleLogout(context: Activity) {
        FirebaseAuth.getInstance().signOut() // Sign out the user
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
        context.finish() // Close the current activity
    }


}

@Composable
fun SleepMonitorScreen(onLogout:() ->Unit){
    var isMonitoring by remember { mutableStateOf(false) }
    var sleepDuration by remember { mutableStateOf(0) }
    var sleepQuality by remember { mutableStateOf("Not registered") }
    var heartRate by remember { mutableStateOf(0) }
    var spO2 by remember { mutableStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    val dropdownAnchor = remember { mutableStateOf<Offset?>(null) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Welcome, User!",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        val position = coordinates.positionInRoot()
                        dropdownAnchor.value = position
                    }
            ) {
            IconButton(onClick = { dropdownExpanded = !dropdownExpanded }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    modifier = Modifier.size(24.dp)
                )
            }
            }
            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false } ,
                        modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                DropdownMenuItem(
                    text = { Text("Log Out", fontSize = 16.sp,color = MaterialTheme.colorScheme.primary) },
                    onClick = {
                        dropdownExpanded = false
                        showLogoutDialog = true
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

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

        // asta poate vreau sa o pastrez Spacer(modifier = Modifier.height(30.dp))

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
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(text = "Log Out") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showLogoutDialog = false }
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
        SleepMonitorScreen(onLogout={})
    }
}

