package com.example.workoutapp.ui.workouts

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.workoutapp.data.Workout
import com.example.workoutapp.data.WorkoutExercise
import com.example.workoutapp.ui.CircularProgressCountdown
import com.example.workoutapp.viewmodels.WorkoutViewModel
import kotlinx.coroutines.delay

@Composable
fun WorkoutPlayerScreen(navController: NavController, workoutId: Int, workoutViewModel: WorkoutViewModel = viewModel()) {
    val workout by workoutViewModel.selectedWorkout.collectAsState()

    LaunchedEffect(key1 = workoutId) {
        workoutViewModel.fetchWorkout(workoutId)
    }

    if (workout == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        WorkoutPlayerContent(navController, workout)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutPlayerContent(navController: NavController, workout: Workout?) {
    var state by remember { mutableStateOf(WorkoutState(workout)) }
    var sidebarExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(workout) {
        state = WorkoutState(workout)
    }

    LaunchedEffect(state.timeLeft, state.isRunning) {
        if (state.isRunning && state.timeLeft > 0) {
            delay(1000L)
            state = state.copy(timeLeft = state.timeLeft - 1)
        } else if (state.isRunning && state.timeLeft == 0) {
            state = state.nextStep()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(workout?.name ?: "Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Exit")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 48.dp)
                    .padding(16.dp)
            ) {
                when {
                    !state.workoutStarted -> WorkoutStartScreen { state = state.copy(workoutStarted = true) }
                    state.currentExercise == null -> WorkoutCompleteScreen(navController)
                    else -> WorkoutExerciseScreen(state) { newState -> state = newState }
                }
            }


            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
            ) {
                WorkoutSidebar(state, sidebarExpanded) { sidebarExpanded = !sidebarExpanded }
            }
        }
    }
}

@Composable
fun WorkoutSidebar(state: WorkoutState, expanded: Boolean, onExpandToggle: () -> Unit) {
    val sidebarWidth by animateDpAsState(if (expanded) 180.dp else 48.dp, label = "sidebarWidth")

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(sidebarWidth)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onExpandToggle() },
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxHeight().padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (expanded) "Progress" else "⏳",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 8.dp),
                color = MaterialTheme.colorScheme.primary
            )

            LazyColumn(
                modifier = Modifier.fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                state.workout?.exercises?.forEachIndexed { index, exercise ->
                    item {
                        val isCompleted = index < state.exerciseIndex
                        val isCurrent = index == state.exerciseIndex

                        if (expanded) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp, horizontal = 8.dp)
                            ) {
                                Icon(
                                    imageVector = when {
                                        isCompleted -> Icons.Default.CheckCircle
                                        isCurrent -> Icons.Default.PlayCircle
                                        else -> Icons.Default.Circle
                                    },
                                    contentDescription = null,
                                    tint = when {
                                        isCompleted -> Color.Gray
                                        isCurrent -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.onBackground
                                    },
                                    modifier = Modifier.size(16.dp)
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Text(
                                    text = exercise.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = when {
                                        isCompleted -> Color.Gray
                                        isCurrent -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.onBackground
                                    },
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        } else {
                            Icon(
                                imageVector = when {
                                    isCompleted -> Icons.Default.CheckCircle
                                    isCurrent -> Icons.Default.PlayCircle
                                    else -> Icons.Default.Circle
                                },
                                contentDescription = null,
                                tint = when {
                                    isCompleted -> Color.Gray
                                    isCurrent -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.onBackground
                                },
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}




@Composable
fun WorkoutStartScreen(onStart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = onStart) {
            Text("Start Workout")
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}


@Composable
fun WorkoutCompleteScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text("Workout Complete!", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Finish")
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}



@Composable
fun WorkoutExerciseScreen(state: WorkoutState, onStateChange: (WorkoutState) -> Unit) {
    val currentExercise = state.currentExercise!!

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = currentExercise.name,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )

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
                    .height(200.dp)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (state.isResting) "Take a break!" else "Set ${state.setIndex + 1} / ${currentExercise.sets}",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val stableTimeLeft = remember(state.isResting, currentExercise) {
                        state.timeLeft
                    }

                    if (state.isResting || currentExercise.duration_based) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(120.dp)
                        ) {
                            CircularProgressCountdown(
                                durationSeconds = if (state.isResting) stableTimeLeft else currentExercise.duration ?: 0,
                                timeLeft = state.timeLeft,
                                isRunning = state.isRunning
                            )
                        }
                    } else {
                        Text(
                            text = "Do ${currentExercise.reps} reps",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Button(
            onClick = { onStateChange(state.nextStep()) },
            enabled = !state.isRunning || state.isResting
        ) {
            Text(if (state.isResting) "Continue" else "Next")
        }
    }
}




data class WorkoutState(
    val workout: Workout?,
    val exerciseIndex: Int = 0,
    val setIndex: Int = 0,
    val timeLeft: Int = workout?.exercises?.firstOrNull()?.duration.takeIf { workout?.exercises?.firstOrNull()?.duration_based == true } ?: 0,
    val isRunning: Boolean = workout?.exercises?.firstOrNull()?.duration_based == true,
    val isResting: Boolean = false,
    val afterExerciseRest: Boolean = false,
    val workoutStarted: Boolean = false
) {
    val currentExercise: WorkoutExercise? = workout?.exercises?.getOrNull(exerciseIndex)

    private fun startNextExercise(): WorkoutState {
        val nextExercise = workout?.exercises?.getOrNull(exerciseIndex) ?: return completeWorkout()
        return copy(
            setIndex = 0,
            timeLeft = if (nextExercise.duration_based) nextExercise.duration ?: 0 else 0,
            isRunning = nextExercise.duration_based,
            isResting = false,
            afterExerciseRest = false
        )
    }

    private fun startRest(restTime: Int, isAfterExercise: Boolean = false): WorkoutState {
        return copy(
            isResting = true,
            afterExerciseRest = isAfterExercise,
            timeLeft = restTime,
            isRunning = true
        )
    }

    private fun startNextSet(): WorkoutState {
        val nextSetIndex = setIndex + 1
        return if (nextSetIndex < (currentExercise?.sets ?: 1)) {
            copy(
                setIndex = nextSetIndex,
                isResting = false,
                isRunning = currentExercise?.duration_based == true,
                timeLeft = if (currentExercise?.duration_based == true) currentExercise.duration ?: 0 else 0
            )
        } else {
            startRest(currentExercise?.rest_time_after ?: 30, isAfterExercise = true)
        }
    }


    private fun moveToNextExercise(): WorkoutState {
        val nextExerciseIndex = exerciseIndex + 1
        return if (nextExerciseIndex < (workout?.exercises?.size ?: 0)) {
            copy(exerciseIndex = nextExerciseIndex, isResting = false, afterExerciseRest = false).startNextExercise()
        } else {
            completeWorkout()
        }
    }

    private fun completeWorkout(): WorkoutState {
        return copy(
            workoutStarted = true,
            exerciseIndex = workout?.exercises?.size ?: 0,
            isRunning = false,
            isResting = false
        )
    }

    fun nextStep(): WorkoutState {
        return when {
            afterExerciseRest -> moveToNextExercise()

            isResting -> startNextSet()

            currentExercise?.duration_based == true -> {
                if (setIndex + 1 < (currentExercise?.sets ?: 1)) {
                    startRest(currentExercise.rest_time_between)
                } else {
                    startRest(currentExercise?.rest_time_after ?: 30, isAfterExercise = true)
                }
            }

            else -> {
                if (setIndex + 1 < (currentExercise?.sets ?: 1)) {
                    startRest(currentExercise?.rest_time_between ?: 30)
                } else {
                    startRest(currentExercise?.rest_time_after ?: 30, isAfterExercise = true)
                }
            }
        }
    }
}






