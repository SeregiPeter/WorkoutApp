package com.example.workoutapp.ui.workouts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.workoutapp.data.Exercise
import com.example.workoutapp.data.WorkoutCreateRequest
import com.example.workoutapp.data.WorkoutExerciseRequest
import com.example.workoutapp.viewmodels.ExerciseViewModel
import com.example.workoutapp.viewmodels.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutCreateScreen(
    navController: NavController,
    workoutViewModel: WorkoutViewModel = viewModel(),
    exerciseViewModel: ExerciseViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        exerciseViewModel.fetchExercises()
    }

    var workoutName by remember { mutableStateOf("") }
    val availableExercises by exerciseViewModel.exercises.collectAsState()
    var selectedExercises by remember { mutableStateOf<List<WorkoutExerciseRequest>>(emptyList()) }
    var showExercisePicker by remember { mutableStateOf(false) }

    val exerciseErrorMessage by exerciseViewModel.uiErrorMessage.collectAsState()
    val workoutErrorMessage by workoutViewModel.uiErrorMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(exerciseErrorMessage) {
        exerciseErrorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            exerciseViewModel.clearError()
        }
    }
    LaunchedEffect(workoutErrorMessage) {
        workoutErrorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            workoutViewModel.clearError()
        }
    }



    Scaffold(
        snackbarHost = {SnackbarHost(snackbarHostState)},
        topBar = {
            TopAppBar(
                title = { Text("Create Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Exit")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (workoutName.isNotBlank() && selectedExercises.isNotEmpty()) {
                        val newWorkout = WorkoutCreateRequest(
                            name = workoutName,
                            exercises = selectedExercises
                        )
                        workoutViewModel.createWorkout(newWorkout)
                        navController.navigate("workoutList/true") {
                            popUpTo("workoutList") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.padding(bottom = 72.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = "Save Workout")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = workoutName,
                onValueChange = { workoutName = it },
                label = { Text("Workout Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Added Exercises", style = MaterialTheme.typography.titleMedium)

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(selectedExercises) { exercise ->
                    ExerciseItem(
                        exercise = exercise,
                        exercises = availableExercises,
                        onUpdate = { updatedExercise ->
                            selectedExercises = selectedExercises.map {
                                if (it.exercise_id == updatedExercise.exercise_id) updatedExercise else it
                            }
                        },
                        onDelete = {
                            selectedExercises =
                                selectedExercises.filterNot { it.exercise_id == exercise.exercise_id }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showExercisePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Exercise")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Exercise")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }


        if (showExercisePicker) {
            ExercisePickerDialog(
                exercises = availableExercises,
                onExerciseSelected = { selectedExercise ->
                    selectedExercises = selectedExercises + WorkoutExerciseRequest(
                        exercise_id = selectedExercise.id,
                        sets = 1,
                        reps = if (!selectedExercise.duration_based) 10 else null,
                        duration = if (selectedExercise.duration_based) 30 else null,
                        rest_time_between = 0,
                        rest_time_after = 0
                    )
                    showExercisePicker = false
                },
                onDismiss = { showExercisePicker = false }
            )
        }
    }
}


@Composable
fun ExerciseItem(
    exercise: WorkoutExerciseRequest,
    exercises: List<Exercise>,
    onUpdate: (WorkoutExerciseRequest) -> Unit,
    onDelete: () -> Unit
) {
    var sets by remember { mutableStateOf(exercise.sets.toString()) }
    var reps by remember { mutableStateOf(exercise.reps?.toString() ?: "") }
    var duration by remember { mutableStateOf(exercise.duration?.toString() ?: "") }
    var restBetween by remember { mutableStateOf(exercise.rest_time_between.toString()) }
    var restAfter by remember { mutableStateOf(exercise.rest_time_after.toString()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    exercises.find { it.id == exercise.exercise_id }?.name ?: "Exercise",
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { onDelete() }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove Exercise")
                }
            }

            Row {
                OutlinedTextField(
                    value = sets,
                    onValueChange = {
                        sets = it
                        sets.toIntOrNull()?.let { updatedValue ->
                            onUpdate(exercise.copy(sets = updatedValue))
                        }
                    },
                    label = { Text("Sets") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(onDone = {
                        sets.toIntOrNull()?.let { onUpdate(exercise.copy(sets = it)) }
                    })
                )

                Spacer(modifier = Modifier.width(8.dp))

                if (exercise.reps != null) {
                    OutlinedTextField(
                        value = reps,
                        onValueChange = {
                            reps = it
                            reps.toIntOrNull()?.let { updatedValue ->
                                onUpdate(exercise.copy(reps = updatedValue))
                            }
                        },
                        label = { Text("Reps") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        keyboardActions = KeyboardActions(onDone = {
                            reps.toIntOrNull()?.let { onUpdate(exercise.copy(reps = it)) }
                        })
                    )
                } else {
                    OutlinedTextField(
                        value = duration,
                        onValueChange = {
                            duration = it
                            duration.toIntOrNull()?.let { updatedValue ->
                                onUpdate(exercise.copy(duration = updatedValue))
                            }
                        },
                        label = { Text("Duration (sec)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        keyboardActions = KeyboardActions(onDone = {
                            duration.toIntOrNull()?.let { onUpdate(exercise.copy(duration = it)) }
                        })
                    )
                }
            }

            Row {
                OutlinedTextField(
                    value = restBetween,
                    onValueChange = {
                        restBetween = it
                        restBetween.toIntOrNull()?.let { updatedValue ->
                            onUpdate(exercise.copy(rest_time_between = updatedValue))
                        }
                    },
                    label = { Text("Rest Between (sec)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(onDone = {
                        restBetween.toIntOrNull()
                            ?.let { onUpdate(exercise.copy(rest_time_between = it)) }
                    })
                )

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = restAfter,
                    onValueChange = {
                        restAfter = it
                        restAfter.toIntOrNull()?.let { updatedValue ->
                            onUpdate(exercise.copy(rest_time_after = updatedValue))
                        }
                    },
                    label = { Text("Rest After (sec)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(onDone = {
                        restAfter.toIntOrNull()
                            ?.let { onUpdate(exercise.copy(rest_time_after = it)) }
                    })
                )
            }
        }
    }
}


@Composable
fun ExercisePickerDialog(
    exercises: List<Exercise>,
    onExerciseSelected: (Exercise) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Select an Exercise", style = MaterialTheme.typography.titleMedium)

                LazyColumn {
                    items(exercises) { exercise ->
                        TextButton(onClick = { onExerciseSelected(exercise) }) {
                            Text(exercise.name)
                        }
                    }
                }

                Button(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Close")
                }
            }
        }
    }
}
