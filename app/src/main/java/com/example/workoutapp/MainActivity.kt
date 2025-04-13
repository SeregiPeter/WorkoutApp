package com.example.workoutapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.workoutapp.data.OverpassApiService
import com.example.workoutapp.data.Repository
import com.example.workoutapp.data.WorkoutApiService
import com.example.workoutapp.data.WorkoutDatabase
import com.example.workoutapp.ui.HomeScreen
import com.example.workoutapp.ui.MapScreen
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
import com.example.workoutapp.viewmodels.MapViewModel
import com.example.workoutapp.viewmodels.MyViewModelFactory
import com.example.workoutapp.viewmodels.WorkoutViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private val repository: Repository by lazy {
        val apiKey = BuildConfig.API_KEY
        val workoutApiService = Retrofit.Builder()
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
            .create(WorkoutApiService::class.java)

        val overpassApiService: OverpassApiService =
            Retrofit.Builder()
                .baseUrl("https://overpass-api.de/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(
                    OkHttpClient.Builder()
                        .addInterceptor(HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        })
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .build()
                )
                .build()
                .create(OverpassApiService::class.java)

        val database = WorkoutDatabase.getDatabase(applicationContext)
        val challengeResultDao = database.challengeResultDao()
        val workoutDao = database.workoutDao()

        Repository(workoutApiService, overpassApiService, challengeResultDao, workoutDao)
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

    private val mapViewModel: MapViewModel by viewModels {
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
                    AppNavigation(navController, exerciseViewModel, workoutViewModel, challengeViewModel, mapViewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    exerciseViewModel: ExerciseViewModel,
    workoutViewModel: WorkoutViewModel,
    challengeViewModel: ChallengeViewModel,
    mapViewModel: MapViewModel
) {
    exerciseViewModel.fetchExercises()
    workoutViewModel.fetchWorkouts()
    challengeViewModel.fetchChallenges()

    val items = listOf("home", "exerciseList", "workoutList", "challengeList", "maps")
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val selectedItem = items.indexOf(currentRoute).takeIf { it >= 0 } ?: -1

    Scaffold(
        bottomBar = {
            if(currentRoute != "") {
                NavigationBar {
                    items.forEachIndexed { index, route ->
                        val label = when (route) {
                            "home" -> "Home"
                            "exerciseList" -> "Exercises"
                            "workoutList" -> "Workouts"
                            "challengeList" -> "Challenges"
                            "maps" -> "Parks"
                            else -> "Home"
                        }

                        NavigationBarItem(
                            modifier = Modifier.testTag(route + "BottomBarButton"),
                            icon = {
                                when (route) {
                                    "maps" -> {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_location),
                                            contentDescription = label,
                                            tint = Color.Unspecified, // ha nem akarod újraszínezni az ikont
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    "challengeList" -> {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_progress),
                                            contentDescription = label,
                                            tint = Color.Unspecified, // ha nem akarod újraszínezni az ikont
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    "exerciseList" -> {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_pull_up),
                                            contentDescription = label,
                                            tint = Color.Unspecified, // ha nem akarod újraszínezni az ikont
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    "workoutList" -> {
                                        Icon(
                                            imageVector = Icons.Default.PlayCircle,
                                            contentDescription = label
                                        )
                                    }
                                    "home" -> {
                                        Icon(
                                            imageVector = Icons.Default.Home,
                                            contentDescription = label
                                        )
                                    }
                                }
                            },
                            label = { Text(text = label, fontSize = 9.sp) },
                            selected = selectedItem == index,
                            onClick = {
                                navController.navigate(route)
                                {
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
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
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
            composable("workoutPlayer/{workoutId}/{remote}") { backStackEntry ->
                val workoutId = backStackEntry.arguments?.getString("workoutId")?.toIntOrNull()
                val remote = backStackEntry.arguments?.getString("remote")?.toBooleanStrictOrNull() ?: false

                if (workoutId != null) {
                    WorkoutPlayerScreen(navController, workoutId, remote, workoutViewModel)
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

            composable("maps") { MapScreen(mapViewModel, navController) }
        }
    }
}