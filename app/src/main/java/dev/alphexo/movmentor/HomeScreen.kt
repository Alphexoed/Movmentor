package dev.alphexo.movmentor

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.alphexo.movmentor.train.endpoints.URLs
import dev.alphexo.movmentor.ui.theme.Typography

@Composable
fun HomeScreen() {
    //? Set default values for API URLs
    if (URLs.CP.SELECTED.isNullOrEmpty()) {
        Log.v(
            "API:CP-Modify",
            "Default CP API is null, changed to Production"
        )
        URLs.CP.SELECTED = URLs.CP.PRODUCTION
    }
    if (URLs.Infra.SELECTED.isNullOrEmpty()) {
        Log.v(
            "API:Infra-Modify",
            "Default Infra API is null, changed to Production"
        )
        URLs.Infra.SELECTED = URLs.Infra.PRODUCTION
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            style = Typography.headlineMedium, text = "Home sweet home"
        )
        Text(
            style = Typography.bodyLarge, text = "This is a placeholder for now"
        )
    }
}