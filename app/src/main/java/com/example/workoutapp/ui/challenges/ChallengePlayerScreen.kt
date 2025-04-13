package com.example.workoutapp.ui.challenges

import android.hardware.SensorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.workoutapp.data.Challenge
import com.example.workoutapp.sensormanagement.MeasurementStrategyFactory
import com.example.workoutapp.ui.CircularProgressCountdown
import com.example.workoutapp.viewmodels.ChallengeViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengePlayerScreen(
    navController: NavController,
    challengeId: Int,
    challengeViewModel: ChallengeViewModel = viewModel()
) {
    val challenge by challengeViewModel.selectedChallenge.collectAsState()
    val isLoading by challengeViewModel.isLoading.collectAsState()
    val errorMessage by challengeViewModel.uiErrorMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(challengeId) {
        challengeViewModel.fetchChallenge(challengeId)
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            challengeViewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(modifier = Modifier.testTag("challengePlayerNameText"), text = challenge?.name ?: "Challenge Player") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Exit")
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { paddingValues ->

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if(challenge == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Challenge could not be loaded.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            ChallengePlayerContent(navController, challenge!!, challengeViewModel, paddingValues)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengePlayerContent(navController: NavController, challenge: Challenge, challengeViewModel: ChallengeViewModel, paddingValues: PaddingValues) {
    var timeLeft by remember { mutableStateOf(0) }
    var reps by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }
    var isUpright by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(SensorManager::class.java) }
    val measurementStrategy = remember {
        MeasurementStrategyFactory.create(
            method = challenge.measurement_method,
            onRepCountChanged = { newReps -> reps = newReps },
            onUprightChanged = { newIsUpright -> isUpright = newIsUpright }
        )
    }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            measurementStrategy.registerSensors(sensorManager)
        } else {
            measurementStrategy.unregisterSensors(sensorManager)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            measurementStrategy.unregisterSensors(sensorManager)
        }
    }

    LaunchedEffect(challenge) {
        timeLeft = if (challenge.count_reps) {
            challenge.duration ?: 60
        } else {
            0
        }
        reps = 0
        isRunning = false
        isFinished = false
    }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000L)
            if (challenge.count_reps) {
                timeLeft = (timeLeft - 1).coerceAtLeast(0)
                if (timeLeft == 0) {
                    isRunning = false
                    isFinished = true
                }
            } else {
                timeLeft += 1
            }
        }
    }


        ChallengePlayerBody(
            challenge = challenge,
            challengeViewModel = challengeViewModel,
            timeLeft = timeLeft,
            reps = reps,
            isRunning = isRunning,
            isFinished = isFinished,
            onStart = { isRunning = true },
            onStop = {
                isRunning = false
                isFinished = true
            },
            onAddRep = { reps += 1 },
            navController = navController,
            paddingValues = paddingValues
        )

        if (!isUpright && !isFinished) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Place your phone upright!",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Icon(
                        imageVector = Icons.Default.ScreenRotation,
                        contentDescription = "Rotate",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }

}


@Composable
fun ChallengePlayerBody(
    challengeViewModel: ChallengeViewModel,
    challenge: Challenge,
    timeLeft: Int,
    reps: Int,
    isRunning: Boolean,
    isFinished: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onAddRep: () -> Unit,
    navController: NavController,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = challenge.exercise.name,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (challenge.count_reps) "Complete as many reps as possible!" else "Time yourself!",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (isFinished) {
                        Text(
                            text = "Challenge Completed!",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.Green
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (challenge.count_reps) "Total Reps: $reps" else "Total Time: ${timeLeft}s",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        if (challenge.count_reps) {
                            CircularProgressCountdown(
                                durationSeconds = challenge.duration ?: 60,
                                timeLeft = timeLeft,
                                isRunning = isRunning,
                                ""
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Reps: $reps",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                text = "Elapsed Time: ${timeLeft}s",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ChallengeControls(
                isRunning = isRunning,
                isFinished = isFinished,
                challenge = challenge,
                onStart = onStart,
                onAddRep = onAddRep,
                onStop = onStop
            )

            if (isFinished) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    challengeViewModel.saveResult(challenge, reps)
                    navController.popBackStack()
                }) {
                    Text("Finnish challenge")
                }
            }
        }
    }
}






@Composable
fun ChallengeControls(
    isRunning: Boolean,
    isFinished: Boolean,
    challenge: Challenge,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onAddRep: () -> Unit
) {
    if (!isFinished) {
        if (!isRunning) {
            Button(onClick = onStart) {
                Text("Start Challenge")
            }
        } else {
//            Row {
//                if (challenge.count_reps) {
//                    Button(onClick = onAddRep) {
//                        Text("Add Rep")
//                    }
//                } else {
//                    Button(onClick = onStop) {
//                        Text("Stop Challenge")
//                    }
//                }
//            }
        }
    }
}


