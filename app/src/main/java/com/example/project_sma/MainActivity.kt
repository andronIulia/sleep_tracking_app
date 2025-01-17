package com.example.project_sma

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project_sma.data.SleepDataEntity
import com.example.project_sma.ui.SleepTrackerTheme
import com.example.project_sma.ui.main.SleepMonitorViewModel
import com.example.project_sma.ui.main.SleepMonitorViewModelFactory
import com.example.project_sma.ui.statistics.StatisticsScreen
import com.example.project_sma.ui.statistics.StatisticsViewModel
import com.example.project_sma.ui.statistics.StatisticsViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    val healthConnectManager by lazy { HealthConnectManager(this) }
    private val sleepMonitorViewModel: SleepMonitorViewModel by viewModels {
        SleepMonitorViewModelFactory(healthConnectManager)
    }

    private val permissionLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.values.all { it }
            sleepMonitorViewModel.onPermissionsResult(allGranted)
            if (!allGranted) {
                Toast.makeText(this, "Permissions are required.", Toast.LENGTH_SHORT).show()
            }
        }
    private var isHealthConnectOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        setContent {
            SleepTrackerTheme {
                val navController = rememberNavController()
                HealthApp(
                    onLogout = { handleLogout(this) },
                    viewModel = sleepMonitorViewModel,
                    permissionLauncher = permissionLauncher,
                    navController = navController,
                    isHealthConnectOpened = isHealthConnectOpened,
                    onHealthConnectOpened = { isHealthConnectOpened = true},
                    isPreviewMode = false
                    )
            }
        }
    }
    private fun handleLogout(context: Activity) {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
        context.finish()
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthApp(
    onLogout: () -> Unit,
    viewModel: SleepMonitorViewModel,
    permissionLauncher: ActivityResultLauncher<Array<String>>,
    navController: NavHostController,
    isHealthConnectOpened: Boolean,
    onHealthConnectOpened: () -> Unit,
    isPreviewMode: Boolean
) {
    val context = LocalContext.current
    val statisticsViewModel: StatisticsViewModel = viewModel(
        factory = StatisticsViewModelFactory(
            context.applicationContext as Application,
            viewModel.healthConnectManager
            )
    )
    val permissionsGranted by viewModel.permissionsGranted.collectAsState()
    val healthData by viewModel.healthData.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    var isButtonEnabled by remember { mutableStateOf(true) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val isPreviewMode = remember { false }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sleep Tracker") },
                actions = {
                    IconButton(onClick = { dropdownExpanded = true }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Home",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            },
                            onClick = {
                                dropdownExpanded = false
                                navController.navigate("home")
                            },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Statistics",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            },
                            onClick = {
                                dropdownExpanded = false
                                navController.navigate("statistics")
                            },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Log Out",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            },
                            onClick = {
                                dropdownExpanded = false
                                //onLogout()
                                showLogoutDialog = true
                            },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

            )
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable("home") {
                SleepMonitorScreen(
                    healthData = healthData,
                    onLogout = onLogout,
                    isButtonEnabled = isButtonEnabled,
                    isLoading = isLoading,
                    onOpenHealthConnectApp = {
                        if (!isHealthConnectOpened && !isPreviewMode) {
                            isButtonEnabled = false
                            openHealthConnectApp(context)
                            onHealthConnectOpened()
                        }
                    }
                )
            }
            composable("statistics") {
                StatisticsScreen(
                    statisticsViewModel = statisticsViewModel,
                    onBack = {navController.popBackStack()}
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!isPreviewMode) {
            if (permissionsGranted && !isHealthConnectOpened) {
                openHealthConnectApp(context)
                onHealthConnectOpened()
                isLoading = true
                viewModel.loadHealthData()
            }
        }
    }

        LaunchedEffect(Unit) {
            viewModel.checkPermissions()
        }


    if (permissionsGranted) {
        LaunchedEffect(Unit) {
            if (!isHealthConnectOpened) {
                openHealthConnectApp(context)
                onHealthConnectOpened()
                isLoading = true
                viewModel.loadHealthData()
            }
        }
    } else {
        PermissionRequestScreen {
            viewModel.requestPermissions(permissionLauncher)
        }
    }
    if (showLogoutDialog) {
        LogOutDialog(
            onDismiss = { showLogoutDialog = false },
            onLogout = {
                showLogoutDialog = false
                onLogout()
            }
        )
    }
}

@Composable
fun LogOutDialog(onDismiss: () -> Unit, onLogout: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Log Out") },
        text = { Text("Are you sure you want to log out?") },
        confirmButton = {
            Button(onClick = onLogout) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}


fun openHealthConnectApp(context: Context) {

    try {
        val packageManager = context.packageManager
        val healthConnectPackage = "com.google.android.apps.healthdata"

        val isHealthConnectInstalled = try {
            packageManager.getPackageInfo(healthConnectPackage, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

        if (isHealthConnectInstalled) {
            val launchIntent = packageManager.getLaunchIntentForPackage(healthConnectPackage)
            if (launchIntent != null) {
                context.startActivity(launchIntent)
        } else {
                Toast.makeText(context, "Unable to open Health Connect.", Toast.LENGTH_SHORT).show()
            }
        }else {

            Toast.makeText(context, "Health Connect app is not installed.", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Error opening Health Connect.", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun SleepMonitorScreen(
    healthData: SleepDataEntity?,
    onLogout: () -> Unit,
    isButtonEnabled: Boolean,
    isLoading: Boolean,
    onOpenHealthConnectApp: () -> Unit,
    //isPreviewMode: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "Welcome, User!",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        healthData?.let {
            SleepDataCard("Total Sleep", "${it.sleepDuration} hours")
            Spacer(modifier = Modifier.height(24.dp))
            SleepDataCard("Sleep Quality", it.sleepQuality)
            Spacer(modifier = Modifier.height(24.dp))
            SleepDataCard("Heart Rate", "${it.heartRate} bpm")
            Spacer(modifier = Modifier.height(24.dp))
            SleepDataCard("SpO2", "${it.spO2}%")
        } ?: Text("Loading health data...", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun SleepDataCard(title: String, value: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Column {
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
    val fakeLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {}
    SleepTrackerTheme {
        HealthApp(
            onLogout = {},
            viewModel = SleepMonitorViewModel(HealthConnectManager(LocalContext.current)),
            permissionLauncher = fakeLauncher,
            navController = rememberNavController(),
            isHealthConnectOpened = false,
            onHealthConnectOpened = {},
            isPreviewMode = true
        )
    }
}

@Composable
fun PermissionRequestScreen(onRequestPermissions: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Permissions Required", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRequestPermissions) {
            Text("Grant Permissions")
        }
    }
}
