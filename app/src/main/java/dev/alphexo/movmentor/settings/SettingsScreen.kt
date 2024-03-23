package dev.alphexo.movmentor.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.alphexo.movmentor.R
import dev.alphexo.movmentor.train.endpoints.Auth
import dev.alphexo.movmentor.train.endpoints.AuthCPToken
import dev.alphexo.movmentor.train.endpoints.URLs
import dev.alphexo.movmentor.ui.theme.Typography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
@Preview(showBackground = true, showSystemUi = true)
fun SettingsScreen() {
    val itemsCP = listOf(
        Pair("Production", URLs.CP.PRODUCTION),
        Pair("Staging", URLs.CP.STAGING)
    )

    val itemsInfra = listOf(
        Pair("Production", URLs.Infra.PRODUCTION),
    )

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingsItem(label = "Trains", title = "Comboios de Portugal") {
                SettingsDropdown(
                    title = "API Endpoint",
                    default = itemsCP[0],
                    items = itemsCP
                )

                HorizontalDivider(
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )

                SettingsFieldButton(
                    title = "CP Authorization Token"
                )
            }

            SettingsItem(label = "Trains", title = "Infra de Portugal") {
                SettingsDropdown(
                    title = "API Endpoint",
                    default = itemsInfra[0],
                    items = itemsInfra
                )
            }
        }
    }
}


@Composable
fun SettingsItem(label: String, title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ElevatedSuggestionChip(
                    onClick = { /* Do Nothing */ },
                    label = { Text(text = label) }
                )

                Text(
                    text = title,
                    style = Typography.titleLarge
                )
            }

            Spacer(Modifier.size(4.dp))

            content()
        }
    }
}


@Composable
fun SettingsDropdown(
    title: String,
    default: Pair<String, String>,
    items: List<Pair<String, String>>
) {
    val context = LocalContext.current
    var selected by remember { mutableStateOf(default) }
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column {
            Text(
                text = title,
                style = Typography.titleMedium
            )

            FilterChip(
                selected = expanded,
                label = {
                    Text(text = selected.first)
                },
                onClick = { expanded = !expanded }
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach {
                DropdownMenuItem(
                    text = { Text(it.first) },
                    onClick = {
                        //Change the selected API Endpoint URL
                        URLs.CP.SELECTED = it.second

                        // Save it on UI & close dropdown
                        selected = it
                        expanded = false

                        Toast.makeText(context, "Selected '${it.first}'", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun SettingsFieldButton(title: String) {
    val coroutineScope = rememberCoroutineScope()
    var authToken by remember { mutableStateOf(AuthCPToken ?: "No value...") }
    var fetchError by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = authToken,
            onValueChange = {},
            label = { Text(text = title) },
            readOnly = true,
            singleLine = true,
            isError = fetchError,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(8.dp))
        FilledIconButton(
            modifier = Modifier.size(32.dp),
            onClick = {
                coroutineScope.launch(Dispatchers.IO) {
                    val newToken = Auth.getToken()

                    // Switch back to Main for UI updates
                    withContext(Dispatchers.Main) {
                        authToken = newToken ?: "Error while fetching!"
                        fetchError = newToken.isNullOrEmpty()
                    }
                }
            }) {
            Icon(
                imageVector = Icons.Rounded.Refresh,
                contentDescription = stringResource(R.string.refresh_auth_token),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}