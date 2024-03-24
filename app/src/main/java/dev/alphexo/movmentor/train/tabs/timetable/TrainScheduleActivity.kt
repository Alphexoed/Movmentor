package dev.alphexo.movmentor.train.tabs.timetable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import dev.alphexo.movmentor.ui.theme.MovmentorTheme


class TrainScheduleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val trainNumber: String =
            intent.getStringExtra("trainNumber") ?: "null"

        enableEdgeToEdge()
        setContent {
            MovmentorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(modifier = Modifier.padding(innerPadding)) {
                        Text(
                            text = "Selected train number: $trainNumber",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        }
    }
}