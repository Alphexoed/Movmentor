package dev.alphexo.movmentor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.alphexo.movmentor.ui.components.NavDrawer
import dev.alphexo.movmentor.ui.theme.MovmentorTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovmentorTheme {
                NavDrawer()
            }
        }
    }
}
