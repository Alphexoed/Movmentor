package dev.alphexo.movmentor.train.models


import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.alphexo.movmentor.train.models.data.ServiceType
import dev.alphexo.movmentor.ui.theme.Typography


@Composable
fun ServiceIconModel(service: ServiceType) {
    IconButton(
        onClick = {},
        modifier = Modifier.size(32.dp),
        colors = IconButtonDefaults.iconButtonColors(containerColor = service.color)
    ) {
        Text(
            text = service.code,
            color = MaterialTheme.colorScheme.onSecondary,
            style = Typography.bodySmall
        )
    }
}