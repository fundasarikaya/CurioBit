package com.genelkultur.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.genelkultur.app.data.Fact

@Composable
fun DetailScreen(
    fact: Fact?,
    onBack: () -> Unit,
    onOpenSource: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detay") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { padding ->
        if (fact == null) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 32.dp))
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(fact.title, style = MaterialTheme.typography.headlineSmall)
            androidx.compose.foundation.layout.Spacer(Modifier.padding(4.dp))
            Divider(modifier = Modifier.padding(vertical = 12.dp))
            Text(fact.fullText, style = MaterialTheme.typography.bodyLarge)
            androidx.compose.foundation.layout.Spacer(Modifier.padding(12.dp))
            TextButton(onClick = { onOpenSource(fact.sourceUrl) }) {
                Icon(Icons.Filled.OpenInNew, contentDescription = null)
                Text("  Wikipedia'da aç")
            }
        }
    }
}
