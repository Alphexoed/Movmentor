package dev.alphexo.movmentor.bus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.alphexo.movmentor.ui.theme.Typography

@Composable
fun BusScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            style = Typography.headlineMedium, text = "Bus Page"
        )
        Text(
            style = Typography.bodyLarge, text = "This is not implemented yet"
        )
    }
}