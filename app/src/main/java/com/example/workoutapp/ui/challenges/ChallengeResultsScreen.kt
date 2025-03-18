package com.example.workoutapp.ui.challenges

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.workoutapp.R
import com.example.workoutapp.data.ChallengeResult
import com.example.workoutapp.viewmodels.ChallengeViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeResultsScreen(
    challengeId: Int,
    challengeName: String,
    challengeViewModel: ChallengeViewModel,
    navController: NavController
) {
    LaunchedEffect(challengeId) {
        challengeViewModel.loadResults(challengeId)
    }

    val results by challengeViewModel.results.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Results for $challengeName") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (results.isEmpty()) {
                Text(
                    text = "No results yet.",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                ChallengeResultsChart(results = results)
                
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(results) { result ->
                        ResultItem(result)
                    }
                }
            }
        }
    }
}

@Composable
fun ResultItem(result: ChallengeResult) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val dateText = dateFormat.format(Date(result.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Result: ${result.resultValue}")
            Text(text = "Date: $dateText", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun ChallengeResultsChart(results: List<ChallengeResult>) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                setTouchEnabled(true)
                setPinchZoom(true)

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    textColor = Color.Gray.toArgb()
                    textSize = 12f
                    setDrawGridLines(false)
                    setLabelRotationAngle(-45f)
                    granularity = 1f
                }

                axisLeft.apply {
                    setDrawGridLines(true)
                    gridColor = Color.LightGray.toArgb()
                    textColor = Color.Gray.toArgb()
                    textSize = 12f

                    axisMinimum = if (results.size == 1) 0f else results.minOf { it.resultValue.toFloat() } * 0.9f
                }

                axisRight.isEnabled = false
                legend.isEnabled = false
            }
        },
        update = { chart ->
            val sortedResults = results.sortedBy { it.timestamp }

            val entries = sortedResults.mapIndexed { index, result ->
                Entry(index.toFloat(), result.resultValue.toFloat())
            }

            val dataSet = LineDataSet(entries, "Results").apply {
                color = Color.DarkGray.toArgb()
                valueTextColor = Color.Black.toArgb()
                setCircleColor(Color.DarkGray.toArgb())
                setDrawValues(false)
                lineWidth = 2f
                circleRadius = 5f
                setDrawFilled(true)
                fillDrawable = ContextCompat.getDrawable(chart.context, R.drawable.chart_gradient)
            }

            chart.xAxis.valueFormatter = IndexAxisValueFormatter(sortedResults.map { it.timestamp.toFormattedDate() })

            chart.data = LineData(dataSet)
            chart.invalidate()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp)
    )
}

fun Long.toFormattedDate(): String {
    val sdf = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
    return sdf.format(Date(this))
}




