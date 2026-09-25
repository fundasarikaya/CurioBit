package com.genelkultur.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.genelkultur.app.data.Fact
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

private val turkceAylar = listOf(
    "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
    "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"
)

private val turkceGunler = listOf(
    "Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi", "Pazar"
)

@Composable
fun HomeScreen(
    todaysFact: Fact?,
    archivePreview: List<Fact>,
    onOpenTodaysFact: () -> Unit,
    onOpenFact: (Fact) -> Unit,
    onOpenHistory: () -> Unit,
    isRefreshing: Boolean,
    refreshMessage: String?,
    onRefresh: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val dateLabel = "${today.dayOfMonth} ${turkceAylar[today.monthNumber - 1]} ${today.year}"
    val weekday = turkceGunler[today.dayOfWeek.ordinal]

    val pullState = rememberPullToRefreshState()

    Scaffold(containerColor = colors.background) { padding ->
        // Sayfayı aşağı çekmek de "Başka bir bilgi" butonuyla aynı işi yapar.
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            state = pullState,
            modifier = Modifier.fillMaxSize().padding(padding),
            indicator = {
                PullToRefreshDefaults.Indicator(
                    state = pullState,
                    isRefreshing = isRefreshing,
                    containerColor = colors.surface,
                    color = colors.primary,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(Modifier.height(16.dp))

                // Künye üstü şerit
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    SmallCaps("Sayı ${today.dayOfYear}")
                    SmallCaps("Günlük bilgi gazetesi")
                    SmallCaps("Fiyatı: merak")
                }

                // Künye (masthead)
                Text(
                    text = "Genel Kültür",
                    style = MaterialTheme.typography.displayLarge,
                    color = colors.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                )
                DoubleRule()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bottomRule(colors.onBackground)
                        .padding(vertical = 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SmallCaps(weekday, color = colors.onBackground)
                    SmallCaps(dateLabel, color = colors.onBackground)
                    SmallCaps("1–2 bilgi / gün", color = colors.onBackground)
                }

                Text(
                    text = "Her gün, dünya tarihinden küçük bir kırıntı. Bildirime dokun, detayını ve kaynağını gör.",
                    style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 16.dp)
                )

                if (todaysFact != null) {
                    var visible by remember(todaysFact.id) { mutableStateOf(false) }
                    LaunchedEffect(todaysFact.id) { visible = true }
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(420)) + slideInVertically(tween(420)) { it / 6 }
                    ) {
                        LeadStory(fact = todaysFact, onOpen = onOpenTodaysFact)
                    }
                    AnotherFactButton(isRefreshing = isRefreshing, onClick = onRefresh)
                } else if (!isRefreshing) {
                    AnotherFactButton(isRefreshing = false, onClick = onRefresh, label = "BİLGİYİ YÜKLE")
                }

                if (refreshMessage != null) {
                    Text(
                        text = refreshMessage,
                        style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    )
                }

                if (archivePreview.isNotEmpty()) {
                    ArchiveTeasers(facts = archivePreview.take(2), onOpenFact = onOpenFact)
                }

                ArchiveLink(onClick = onOpenHistory)
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

/** Manşetin altında, kullanıcı isterse yeni bir bilgi getiren ikincil buton. */
@Composable
private fun AnotherFactButton(
    isRefreshing: Boolean,
    onClick: () -> Unit,
    label: String = "BAŞKA BİR BİLGİ"
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .heightIn(min = 44.dp)
            .clickable(enabled = !isRefreshing, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Filled.Refresh,
            contentDescription = null,
            tint = colors.onBackground,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = if (isRefreshing) "GETİRİLİYOR…" else label,
            style = MaterialTheme.typography.labelMedium,
            color = colors.onBackground
        )
    }
}

@Composable
private fun SmallCaps(
    text: String,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Text(text = text.trUppercase(), style = MaterialTheme.typography.labelSmall, color = color)
}

private fun Modifier.bottomRule(color: androidx.compose.ui.graphics.Color) = drawBehind {
    val y = size.height - 0.5.dp.toPx()
    drawLine(color, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.dp.toPx())
}

@Composable
private fun LeadStory(fact: Fact, onOpen: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val (year, headline) = splitYear(fact.shortText)

    Column(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BUGÜN TARİHTE",
                style = MaterialTheme.typography.labelMedium,
                color = colors.onPrimary,
                modifier = Modifier.background(colors.primary).padding(horizontal = 8.dp, vertical = 4.dp)
            )
            if (year != null) {
                Text(text = year, style = MaterialTheme.typography.titleLarge, color = colors.primary)
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(text = headline, style = MaterialTheme.typography.headlineSmall, color = colors.onBackground)
        Spacer(Modifier.height(12.dp))
        Box(Modifier.fillMaxWidth().height(1.dp).background(colors.outline))

        if (fact.longText.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = fact.longText,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onBackground,
                textAlign = TextAlign.Start,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(colors.primary)
                .clickable(onClick = onOpen),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("DEVAMINI OKU", style = MaterialTheme.typography.labelLarge, color = colors.onPrimary)
            Spacer(Modifier.width(10.dp))
            Icon(Icons.Filled.ArrowForward, contentDescription = null, tint = colors.onPrimary, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun ArchiveTeasers(facts: List<Fact>, onOpenFact: (Fact) -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(Modifier.fillMaxWidth().padding(top = 22.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("ARŞİVDEN", style = MaterialTheme.typography.labelMedium, color = colors.onBackground)
            Spacer(Modifier.width(10.dp))
            Box(Modifier.weight(1f).height(1.dp).background(colors.onBackground))
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            facts.forEachIndexed { index, fact ->
                if (index > 0) {
                    Box(Modifier.padding(horizontal = 12.dp).width(1.dp).fillMaxHeight().background(colors.outline))
                }
                val (year, text) = splitYear(fact.shortText)
                Column(
                    Modifier
                        .weight(1f)
                        .clickable { onOpenFact(fact) }
                        .padding(vertical = 4.dp)
                ) {
                    if (year != null) {
                        Text(year, style = MaterialTheme.typography.labelMedium, color = colors.primary)
                        Spacer(Modifier.height(4.dp))
                    }
                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp, lineHeight = 19.sp),
                        color = colors.onBackground,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (facts.size == 1) Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
private fun ArchiveLink(onClick: () -> Unit) {
    val ink = MaterialTheme.colorScheme.onBackground
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.dp)
            .heightIn(min = 44.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Tüm arşivi gör",
            style = MaterialTheme.typography.titleMedium.copy(textDecoration = TextDecoration.Underline),
            color = ink
        )
        Spacer(Modifier.width(8.dp))
        Icon(Icons.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp), tint = ink)
    }
}
