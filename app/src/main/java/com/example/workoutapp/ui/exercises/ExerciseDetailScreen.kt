package com.example.workoutapp.ui.exercises

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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
    LaunchedEffect(exerciseId) {
        exerciseViewModel.fetchExercise(exerciseId)
    }

    val exercise by exerciseViewModel.selectedExercise.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = exercise?.name ?: "Exercise Detail") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (exercise == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val ex = exercise!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text(
                    text = ex.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Text(
                        text = ex.description,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Box(modifier = Modifier.padding(8.dp)) {
                        YouTubePlayerComponent(videoUrl = ex.video_url)
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
    var youTubePlayer by remember { mutableStateOf<YouTubePlayer?>(null) }

    AndroidView(
        factory = {
            val youTubePlayerView = YouTubePlayerView(context)
            youTubePlayerView.addYouTubePlayerListener(object : YouTubePlayerListener {
                override fun onReady(player: YouTubePlayer) {
                    youTubePlayer = player
                    player.loadVideo(videoId, 0f)
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
            youTubePlayerView
        },
        update = { view ->
            youTubePlayer?.loadVideo(videoId, 0f)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}
