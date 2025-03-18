package com.example.workoutapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.workoutapp.data.ApiService
import com.example.workoutapp.data.ChallengeDatabase
import com.example.workoutapp.data.Repository
import com.example.workoutapp.ui.HomeScreen
import com.example.workoutapp.ui.challenges.ChallengeListScreen
import com.example.workoutapp.ui.challenges.ChallengePlayerScreen
import com.example.workoutapp.ui.challenges.ChallengeResultsScreen
import com.example.workoutapp.ui.exercises.ExerciseDetailScreen
import com.example.workoutapp.ui.exercises.ExerciseListScreen
import com.example.workoutapp.ui.theme.WorkoutAppTheme
import com.example.workoutapp.ui.workouts.WorkoutCreateScreen
import com.example.workoutapp.ui.workouts.WorkoutDetailScreen
import com.example.workoutapp.ui.workouts.WorkoutListScreen
import com.example.workoutapp.ui.workouts.WorkoutPlayerScreen
import com.example.workoutapp.viewmodels.ChallengeViewModel
import com.example.workoutapp.viewmodels.ExerciseViewModel
import com.example.workoutapp.viewmodels.MyViewModelFactory
import com.example.workoutapp.viewmodels.WorkoutViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    private val repository: Repository by lazy {
        val apiKey = BuildConfig.API_KEY
        val apiService = Retrofit.Builder()
            .baseUrl("https://believable-vision-production.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .addHeader("api_key", apiKey)
                            .build()
                        chain.proceed(request)
                    }
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                    .build()
            )
            .build()
            .create(ApiService::class.java)

        val database = ChallengeDatabase.getDatabase(applicationContext)
        val challengeResultDao = database.challengeResultDao()

        Repository(apiService, challengeResultDao)
    }

    private val workoutViewModel: WorkoutViewModel by viewModels {
        MyViewModelFactory(repository)
    }

    private val exerciseViewModel: ExerciseViewModel by viewModels {
        MyViewModelFactory(repository)
    }
    private val challengeViewModel: ChallengeViewModel by viewModels {
        MyViewModelFactory(repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WorkoutAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavigation(navController, exerciseViewModel, workoutViewModel, challengeViewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController, exerciseViewModel: ExerciseViewModel, workoutViewModel: WorkoutViewModel, challengeViewModel: ChallengeViewModel) {
    val items = listOf("home", "exerciseList", "workoutList", "challengeList")
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val selectedItem = items.indexOf(currentRoute).takeIf { it >= 0 } ?: -1

    Scaffold(
        bottomBar = {
            if(currentRoute != "start") {
                NavigationBar {
                    items.forEachIndexed { index, route ->
                        val label = when (route) {
                            "home" -> "Home"
                            "exerciseList" -> "Exercises"
                            "workoutList" -> "Workouts"
                            "challengeList" -> "Challenges"
                            else -> "Home"
                        }

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = when (route) {
                                        "home" -> Icons.Default.Home
                                        "exerciseList" -> Icons.Default.FitnessCenter
                                        "workoutList" -> Icons.Default.PlayCircle
                                        "challengeList" -> Icons.Default.AccessTimeFilled
                                        else -> Icons.Default.Home
                                    },
                                    contentDescription = label
                                )
                            },
                            label = { Text(label) },
                            selected = selectedItem == index,
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo("home") { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "start",
            modifier = Modifier.padding(padding)
        ) {
            composable("start") { StartScreen(navController) }
            composable("home") { HomeScreen(navController) }
            composable("exerciseList") { ExerciseListScreen(navController, exerciseViewModel) }
            composable("exerciseDetail/{exerciseId}") { backStackEntry ->
                val exerciseId = backStackEntry.arguments?.getString("exerciseId")?.toIntOrNull()
                if (exerciseId != null) {
                    ExerciseDetailScreen(navController, exerciseId, exerciseViewModel)
                } else {
                    navController.navigateUp()
                }
            }
            composable("workoutList") { WorkoutListScreen(navController, workoutViewModel) }
            composable(
                "workoutList/{workoutCreated}",
                arguments = listOf(navArgument("workoutCreated") { defaultValue = "false" })
            ) { backStackEntry ->
                val workoutCreated = backStackEntry.arguments?.getString("workoutCreated")?.toBoolean() ?: false
                WorkoutListScreen(navController, workoutViewModel, workoutCreated)
            }
            composable("workoutDetail/{workoutId}") { backStackEntry ->
                val workoutId = backStackEntry.arguments?.getString("workoutId")?.toIntOrNull()
                if (workoutId != null) {
                    WorkoutDetailScreen(navController, workoutId)
                } else {
                    navController.navigateUp()
                }
            }
            composable("workoutPlayer/{workoutId}") { backStackEntry ->
                val workoutId = backStackEntry.arguments?.getString("workoutId")?.toIntOrNull()
                if (workoutId != null) {
                    WorkoutPlayerScreen(navController, workoutId, workoutViewModel)
                } else {
                    navController.navigateUp()
                }
            }
            composable("workoutCreate") { WorkoutCreateScreen(navController, workoutViewModel = workoutViewModel, exerciseViewModel = exerciseViewModel) }
            composable("challengeList") { ChallengeListScreen(navController = navController, challengeViewModel = challengeViewModel) }
            composable("challengePlayer/{challengeId}") { backStackEntry ->
                val challengeId = backStackEntry.arguments?.getString("challengeId")?.toIntOrNull()
                if (challengeId != null) {
                    ChallengePlayerScreen(navController, challengeId, challengeViewModel)
                } else {
                    navController.navigateUp()
                }
            }
            composable("challengeResults/{challengeId}/{challengeName}") { backStackEntry ->
                val challengeId = backStackEntry.arguments?.getString("challengeId")?.toIntOrNull() ?: 0
                val challengeName = backStackEntry.arguments?.getString("challengeName") ?: "Challenge"

                ChallengeResultsScreen(challengeId, challengeName, challengeViewModel, navController)
            }
        }
    }
}

@Composable
fun StartScreen(navController: NavController) {
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Data Source") },
            text = { Text("Use real data?") },
            confirmButton = {
                Button(onClick = {
                    Config.USE_DUMMY_DATA = false
                    showDialog = false
                    navController.navigate("home") {
                        popUpTo("start") { inclusive = true }
                    }
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = {
                    Config.USE_DUMMY_DATA = true
                    showDialog = false
                    navController.navigate("home") {
                        popUpTo("start") { inclusive = true }
                    }
                }) {
                    Text("No")
                }
            }
        )
    }
}


@Composable
fun CustomCircularProgressCountdown(durationSeconds: Int) {
    val progress = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationSeconds * 1000, easing = LinearEasing)
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(120.dp)
    ) {


        CircularProgressIndicator(
            progress = progress.value,
            modifier = Modifier.size(120.dp),
            strokeWidth = 12.dp,
            color = lerp(Color.Green, Color.Red, 1f - progress.value)
        )


        Text(
            text = "${(durationSeconds * progress.value).toInt()}s",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}