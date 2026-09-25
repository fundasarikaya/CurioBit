package com.genelkultur.app.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    val colors = MaterialTheme.colorScheme

    Scaffold(containerColor = colors.background) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Column(Modifier.padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Geri", tint = colors.onBackground)
                    }
                    Spacer(Modifier.width(4.dp))
                    Text("Arşiv", style = MaterialTheme.typography.headlineLarge, color = colors.onBackground)
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "SON 7 GÜN · ${facts.size} BİLGİ",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onSurfaceVariant
                    )
                }
                DoubleRule()
                Box(Modifier.fillMaxWidth()) {
                    Box(
                        Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(colors.outline)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        ArchiveTabButton(
                            label = "SON 7 GÜN",
                            selected = tab == ArchiveTab.RECENT,
                            onClick = { tab = ArchiveTab.RECENT }
                        )
                        ArchiveTabButton(
                            label = "FAVORİLER · ${favoriteFacts.size}",
                            selected = tab == ArchiveTab.FAVORITES,
                            onClick = { tab = ArchiveTab.FAVORITES }
                        )
                    }
                }
            }

            AnimatedContent(
                targetState = tab,
                transitionSpec = {
                    fadeIn(tween(220)) togetherWith fadeOut(tween(140))
                },
                label = "archiveTab"
            ) { currentTab ->
                val tabFacts = if (currentTab == ArchiveTab.RECENT) facts else favoriteFacts
                if (tabFacts.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (currentTab == ArchiveTab.RECENT) "Henüz geçmiş bilgi yok" else "Henüz favori bilgi yok",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp)
                    ) {
                        itemsIndexed(tabFacts, key = { _, fact -> fact.id }) { index, fact ->
                            if (index > 0) {
                                Box(Modifier.fillMaxWidth().height(1.dp).background(colors.outline))
                            }
                            ArchiveRow(fact = fact, onClick = { onOpenFact(fact) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArchiveTabButton(label: String, selected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val content by animateColorAsState(
        targetValue = if (selected) colors.onBackground else colors.onSurfaceVariant,
        animationSpec = tween(220)
    )
    val underline by animateColorAsState(
        targetValue = if (selected) colors.primary else Color.Transparent,
        animationSpec = tween(220)
    )
    Column(
        modifier = Modifier
            .width(IntrinsicSize.Max)
            .clickable(onClick = onClick)
    ) {
        Box(Modifier.height(41.dp), contentAlignment = Alignment.Center) {
            Text(text = label, style = MaterialTheme.typography.labelLarge, color = content)
        }
        Box(Modifier.fillMaxWidth().height(3.dp).background(underline))
    }
}

@Composable
private fun ArchiveRow(fact: Fact, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val date = LocalDate.fromEpochDays(fact.shownDateEpochDay.toInt())
    val dateLabel = "${date.dayOfMonth} ${turkceAylarKisa[date.monthNumber - 1]}".trUppercase()
    val (year, text) = splitYear(fact.shortText)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(Modifier.width(62.dp)) {
            Text(
                text = year ?: "—",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp, lineHeight = 24.sp),
                color = colors.primary
            )
            Spacer(Modifier.height(2.dp))
            Text(text = dateLabel, style = MaterialTheme.typography.labelSmall, color = colors.onSurfaceVariant)
        }
        Spacer(Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = colors.onBackground,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        if (fact.reaction == Reaction.LIKE) {
            Spacer(Modifier.width(10.dp))
            Icon(
                Icons.Filled.Favorite,
                contentDescription = "Favori",
                tint = colors.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
