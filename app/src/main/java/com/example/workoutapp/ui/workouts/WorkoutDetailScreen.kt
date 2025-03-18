package com.example.workoutapp.ui.workouts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

val mockWorkouts = listOf(
    Workout(1, "Full Body Workout", listOf(
        ExerciseShort(1, "Push-ups"),
        ExerciseShort(2, "Squats")
    )),
    Workout(2, "Leg Day", listOf(
        ExerciseShort(3, "Lunges"),
        ExerciseShort(4, "Calf Raises")
    ))
)

data class Workout(val id: Int, val name: String, val exercises: List<ExerciseShort>)
data class ExerciseShort(val id: Int, val name: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(navController: NavController, workoutId: Int) {
    val workout = mockWorkouts.find { it.id == workoutId }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(workout?.name ?: "Workout Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        workout?.let {
            Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
                Text(text = it.name, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Exercises:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    items(it.exercises) { exercise ->
                        Text(text = "- ${exercise.name}", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        } ?: run {
            Text(text = "Workout not found", modifier = Modifier.padding(paddingValues).padding(16.dp))
        }
    }
}
