package com.example.workoutapp.ui.challenges

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.workoutapp.data.Challenge
import com.example.workoutapp.viewmodels.ChallengeViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeListScreen(navController: NavController, challengeViewModel: ChallengeViewModel = viewModel()) {

    LaunchedEffect(Unit) {
        challengeViewModel.fetchChallenges()
    }

    val challenges by challengeViewModel.challenges.collectAsState()
    val isLoading by challengeViewModel.isLoading.collectAsState()
    val errorMessage by challengeViewModel.uiErrorMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Snackbar megjelenítése, ha van hibaüzenet
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short)
            challengeViewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    text = "Challenges",
                    modifier = Modifier.testTag("challengesTitleText")
                ) },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if(isLoading) {
            Box(modifier = Modifier.fillMaxSize().testTag("loadingBox"), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        else if (challenges.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No categories found.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                items(challenges) { challenge ->
                    ChallengeItem(
                        challenge,
                        onStartClick = {
                            navController.navigate("challengePlayer/${challenge.id}")
                        },
                        onViewResultsClick = {
                            navController.navigate("challengeResults/${challenge.id}/${challenge.name}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ChallengeItem(challenge: Challenge, onStartClick: () -> Unit, onViewResultsClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onStartClick() }
            .testTag(challenge.id.toString() + "_challengeCard"),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Kép doboz
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = challenge.exercise.image_url,
                    contentDescription = "Exercise Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = challenge.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
                    .testTag(challenge.id.toString() + "_challengeName")
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Exercise: ${challenge.exercise.name}",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (challenge.count_reps) {
                    "Goal: Max reps in ${challenge.duration} seconds"
                } else {
                    "Goal: Hold as long as possible"
                },
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { onStartClick() },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Start Challenge")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { onViewResultsClick() },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("View Results")
                }
            }
        }
    }
}



