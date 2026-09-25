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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.genelkultur.app.data.Fact
import com.genelkultur.app.data.Reaction
import kotlinx.datetime.LocalDate

private val turkceAylarKisa = listOf(
    "Oca", "Şub", "Mar", "Nis", "May", "Haz",
    "Tem", "Ağu", "Eyl", "Eki", "Kas", "Ara"
)

private enum class ArchiveTab { RECENT, FAVORITES }

@Composable
fun HistoryScreen(
    facts: List<Fact>,
    favoriteFacts: List<Fact>,
    onBack: () -> Unit,
    onOpenFact: (Fact) -> Unit
) {
    var tab by remember { mutableStateOf(ArchiveTab.RECENT) }
    val visibleFacts = if (tab == ArchiveTab.RECENT) facts else favoriteFacts

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

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ArchiveTabChip(
                    label = "Son 7 Gün",
                    selected = tab == ArchiveTab.RECENT,
                    onClick = { tab = ArchiveTab.RECENT }
                )
                ArchiveTabChip(
                    label = "Favoriler",
                    selected = tab == ArchiveTab.FAVORITES,
                    onClick = { tab = ArchiveTab.FAVORITES }
                )
            }

            if (visibleFacts.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (tab == ArchiveTab.RECENT) "Henüz geçmiş bilgi yok" else "Henüz favori bilgi yok",
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
                items(visibleFacts, key = { it.id }) { fact ->
                    ArchiveRow(fact = fact, onClick = { onOpenFact(fact) })
                }
            }
        }
    }
}

@Composable
private fun ArchiveTabChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val background = if (selected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.surface
    val content = if (selected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = Modifier
            .background(background, RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = content)
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = fact.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 2,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (fact.reaction == Reaction.LIKE) {
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = "Favori",
                        tint = accent,
                        modifier = Modifier.width(16.dp)
                    )
                }
            }
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
