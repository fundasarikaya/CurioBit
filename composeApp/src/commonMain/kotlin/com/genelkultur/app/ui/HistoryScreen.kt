package com.genelkultur.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.genelkultur.app.data.Fact
import kotlinx.datetime.LocalDate

@Composable
fun HistoryScreen(
    facts: List<Fact>,
    onBack: () -> Unit,
    onOpenFact: (Fact) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Son 7 Gün") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { padding ->
        if (facts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Henüz geçmiş bilgi yok", style = MaterialTheme.typography.bodyMedium)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(facts, key = { it.id }) { fact ->
                Card(
                    modifier = Modifier.fillMaxSize().clickable { onOpenFact(fact) }
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            LocalDate.fromEpochDays(fact.shownDateEpochDay.toInt()).toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(fact.title, style = MaterialTheme.typography.titleMedium)
                        Text(fact.shortText, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
