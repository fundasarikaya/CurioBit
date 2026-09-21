package com.genelkultur.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    todaysFactShortText: String?,
    onOpenTodaysFact: () -> Unit,
    onOpenHistory: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Genel Kültür") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Icon(
                imageVector = Icons.Filled.Public,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Her gün, dünya tarihinden küçük bir kırıntı",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Günde 1-2 kez telefonuna kısa bir genel kültür bilgisi gönderiyoruz. " +
                    "Bildirime dokun, detayını ve kaynağını gör. Aynı bilgiyi iki kez göstermiyoruz.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))

            if (todaysFactShortText != null) {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Bugünün bilgisi", style = MaterialTheme.typography.labelLarge)
                        Spacer(Modifier.height(8.dp))
                        Text(todaysFactShortText, style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = onOpenTodaysFact) {
                            Text("Detayı gör")
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            OutlinedButton(onClick = onOpenHistory) {
                Icon(Icons.Filled.History, contentDescription = null)
                Spacer(Modifier.height(0.dp))
                Text("  Son 7 günün bilgileri")
            }
        }
    }
}
