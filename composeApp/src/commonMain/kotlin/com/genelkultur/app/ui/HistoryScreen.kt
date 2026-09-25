package com.genelkultur.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.genelkultur.app.data.Fact
import kotlinx.datetime.LocalDate

private val turkceAylarKisa = listOf(
    "Oca", "Şub", "Mar", "Nis", "May", "Haz",
    "Tem", "Ağu", "Eyl", "Eki", "Kas", "Ara"
)

@Composable
fun HistoryScreen(
    facts: List<Fact>,
    onBack: () -> Unit,
    onOpenFact: (Fact) -> Unit
) {
    Scaffold { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 4.dp, top = 4.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Geri")
                }
                Spacer(Modifier.width(4.dp))
                Text("Arşiv", style = MaterialTheme.typography.headlineSmall)
            }

            if (facts.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Henüz geçmiş bilgi yok",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                return@Scaffold
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(facts, key = { it.id }) { fact ->
                    ArchiveRow(fact = fact, onClick = { onOpenFact(fact) })
                }
            }
        }
    }
}

@Composable
private fun ArchiveRow(fact: Fact, onClick: () -> Unit) {
    val accent = accentColorFor(fact.id)
    val date = LocalDate.fromEpochDays(fact.shownDateEpochDay.toInt())
    val dateLabel = "${date.dayOfMonth} ${turkceAylarKisa[date.monthNumber - 1]}"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.width(52.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = dateLabel,
                style = MaterialTheme.typography.labelMedium,
                color = accent
            )
        }
        Spacer(Modifier.width(4.dp))
        Box(
            Modifier
                .width(3.dp)
                .height(52.dp)
                .background(accent, RoundedCornerShape(2.dp))
        )
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = fact.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = fact.shortText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
    }
}
