package com.example.workoutapp.ui.workouts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.workoutapp.data.Workout
import com.example.workoutapp.viewmodels.WorkoutViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutListScreen(
    navController: NavController,
    workoutViewModel: WorkoutViewModel = viewModel(),
    triedToCreateWorkout: Boolean = false
) {

    val snackBarHostState = remember { SnackbarHostState()}

    val workouts by workoutViewModel.workouts.collectAsState()
    val workoutCreated by workoutViewModel.workoutCreated.collectAsState()
    val errorMessage by workoutViewModel.uiErrorMessage.collectAsState()
    val isLoading by workoutViewModel.isLoading.collectAsState()

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackBarHostState.showSnackbar(message, duration = SnackbarDuration.Short)
            workoutViewModel.clearError()
        }
    }

    LaunchedEffect(triedToCreateWorkout, workoutCreated) {
        if (triedToCreateWorkout && workoutCreated) {
            snackBarHostState.showSnackbar("Workout successfully created!")
            workoutViewModel.resetWorkoutCreated()
        }
    }

    LaunchedEffect(Unit) {
        workoutViewModel.fetchWorkouts()
    }


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(
                    text = "Workouts",
                    modifier = Modifier.testTag("workoutsTitleText")
                ) },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("workoutCreate")},
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Workout")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize().testTag("loadingBox"), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            else if (workouts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No Workouts found.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

            } else {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(workouts) { workout ->
                        WorkoutItem(workout, navController, onDeleteClick = { workoutViewModel.deleteWorkout(workout.id)})
                    }
                }
            }
        }
    }
}


@Composable
fun WorkoutItem(workout: Workout, navController: NavController, onDeleteClick: () -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { isExpanded = !isExpanded }
            .testTag(workout.id.toString() + "_workoutCard"),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(modifier = Modifier.testTag(workout.id.toString() + "_workoutPlayButton"), onClick = { navController.navigate("workoutPlayer/${workout.id}/${workout.remote}") }) {
                    Icon(
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = "Start workout",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = workout.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                        .testTag(workout.id.toString() + "_workoutName")
                )

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand"
                )
                if (!workout.remote) {
                    IconButton(onClick = {showDialog = true}) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Workout", tint = Color.Red)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(12.dp)
                ) {
                    workout.exercises.forEachIndexed { index, exercise ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 8.dp)
                            )

                            val exerciseText = if (exercise.duration_based) {
                                "${exercise.name} - ${exercise.sets} x ${exercise.duration} sec"
                            } else {
                                "${exercise.name} - ${exercise.sets} x ${exercise.reps} reps"
                            }

                            Text(
                                text = exerciseText,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        if (exercise.rest_time_after > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HourglassBottom,
                                    contentDescription = "Rest period",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(end = 8.dp)
                                )

                                Text(
                                    text = "Rest: ${exercise.rest_time_after} sec",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete Workout") },
            text = { Text("Are you sure you want to delete this workout? This action cannot be undone.") },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        onDeleteClick()
                        showDialog = false
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}



