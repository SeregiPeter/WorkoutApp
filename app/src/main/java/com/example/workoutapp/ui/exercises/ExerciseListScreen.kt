package com.example.workoutapp.ui.exercises

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.workoutapp.data.Exercise
import com.example.workoutapp.viewmodels.ExerciseViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseListScreen(navController: NavController, exerciseViewModel: ExerciseViewModel = viewModel()) {

    LaunchedEffect(Unit) {
        exerciseViewModel.fetchExercises()
    }

    val exercises by exerciseViewModel.exercises.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Exercises") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (exercises.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                items(exercises) { exercise ->
                    ExerciseItem(exercise, onClick = {
                        navController.navigate("exerciseDetail/${exercise.id}")
                    })
                }
            }
        }
    }
}

@Composable
fun ExerciseItem(exercise: Exercise, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp) // Smooth edges
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center,
                content = {
                    AsyncImage(
                        model = exercise.image_url,
                        contentDescription = "Exercise Image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            )

            Column(modifier = Modifier.padding(16.dp)) {
                // Title and Category
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f)
                    )

                    AssistChip(
                        onClick = onClick,
                        label = { Text(exercise.category.name) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        elevation = AssistChipDefaults.assistChipElevation()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Exercise Description
                Text(
                    text = exercise.description,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}


