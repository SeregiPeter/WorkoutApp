package com.example.workoutapp.ui.exercises

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.workoutapp.viewmodels.ExerciseViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    navController: NavController,
    exerciseId: Int,
    exerciseViewModel: ExerciseViewModel = viewModel()
) {
    val exercise by exerciseViewModel.selectedExercise.collectAsState()
    val errorMessage by exerciseViewModel.uiErrorMessage.collectAsState()
    val isLoading by exerciseViewModel.isLoading.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Hiba megjelenítése Snackbar-ban
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Short)
            exerciseViewModel.clearError()
        }
    }

    LaunchedEffect(exerciseId) {
        if (exercise == null || exercise?.id != exerciseId) {
            exerciseViewModel.fetchExercise(exerciseId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(modifier = Modifier.testTag("exerciseDetailNameText"), text = exercise?.name ?: "Exercise Detail") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if(isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("loadingBox"),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        else if (errorMessage != null && exercise == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Exercise could not be loaded.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else if (exercise != null) {
            val ex = exercise!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Cím
                Text(
                    text = ex.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Kép, ha van
                if (ex.image_url.isNotEmpty()) {
                    AsyncImage(
                        model = ex.image_url,
                        contentDescription = "${ex.name} image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Leírás kártyában
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Description",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = ex.description.ifEmpty { "No description provided." },
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Kategória infó
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Category: ${ex.category.name}", style = MaterialTheme.typography.titleMedium)
                        ex.category.description?.let {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(it, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Videó
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Video", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        if (ex.video_url.isNotEmpty()) {
                            YouTubePlayerComponent(videoUrl = ex.video_url)
                        } else {
                            Text("No video provided.")
                        }
                    }
                }
            }

        }
    }
}



@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubePlayerComponent(videoUrl: String) {
    val context = LocalContext.current
    val videoId = remember(videoUrl) { videoUrl.substringAfter("embed/") }

    var youTubePlayerView: YouTubePlayerView? = null

    AndroidView(
        factory = {
            YouTubePlayerView(context).also { view ->
                youTubePlayerView = view
                view.addYouTubePlayerListener(object : YouTubePlayerListener {
                    override fun onReady(player: YouTubePlayer) {
                        player.cueVideo(videoId, 0f) // csak betölti, nem játssza le
                    }

                    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {}
                    override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {}
                    override fun onPlaybackQualityChange(youTubePlayer: YouTubePlayer, playbackQuality: PlayerConstants.PlaybackQuality) {}
                    override fun onPlaybackRateChange(youTubePlayer: YouTubePlayer, playbackRate: PlayerConstants.PlaybackRate) {}
                    override fun onApiChange(youTubePlayer: YouTubePlayer) {}
                    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {}
                    override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {}
                    override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {}
                    override fun onVideoLoadedFraction(youTubePlayer: YouTubePlayer, loadedFraction: Float) {}
                })
            }
        },
        update = { /* semmit ne csináljon */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )

    // Navigáláskor/eltűnéskor szabadítsuk fel a videót
    DisposableEffect(Unit) {
        onDispose {
            youTubePlayerView?.release()
        }
    }
}
