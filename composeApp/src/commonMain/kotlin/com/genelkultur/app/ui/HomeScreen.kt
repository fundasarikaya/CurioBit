package com.genelkultur.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.genelkultur.app.data.Fact
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

private val turkceAylar = listOf(
    "OCAK", "ŞUBAT", "MART", "NİSAN", "MAYIS", "HAZİRAN",
    "TEMMUZ", "AĞUSTOS", "EYLÜL", "EKİM", "KASIM", "ARALIK"
)

@Composable
fun HomeScreen(
    todaysFact: Fact?,
    onOpenTodaysFact: () -> Unit,
    onOpenHistory: () -> Unit
) {
    val accent = todaysFact?.let { accentColorFor(it.id) } ?: MaterialTheme.colorScheme.primary
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val dateLabel = "${today.dayOfMonth} ${turkceAylar[today.monthNumber - 1]} ${today.year}"

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(28.dp))

            // Gazete manşeti (masthead)
            Text(
                text = "GENEL KÜLTÜR",
                style = MaterialTheme.typography.headlineLarge.copy(letterSpacing = 1.sp),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(10.dp))
            MastheadRules()
            Spacer(Modifier.height(10.dp))
            Text(
                text = dateLabel,
                style = MaterialTheme.typography.labelLarge,
                color = accent
            )

            Spacer(Modifier.height(28.dp))

            Text(
                text = "Her gün, dünya tarihinden küçük bir kırıntı",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Günde 1-2 kez telefonuna kısa bir genel kültür bilgisi gönderiyoruz. " +
                    "Bildirime dokun, detayını ve kaynağını gör. Aynı bilgiyi iki kez göstermiyoruz.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(28.dp))

            if (todaysFact != null) {
                var visible by remember(todaysFact.id) { mutableStateOf(false) }
                LaunchedEffect(todaysFact.id) { visible = true }
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(420)) + slideInVertically(tween(420)) { it / 6 }
                ) {
                    Column {
                        TodaysFactCard(
                            fact = todaysFact,
                            accent = accent,
                            onOpen = onOpenTodaysFact
                        )
                        Spacer(Modifier.height(28.dp))
                    }
                }
            } else {
                Spacer(Modifier.height(8.dp))
            }

            ArchiveLink(onClick = onOpenHistory)

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun MastheadRules() {
    Column(
        modifier = Modifier.width(220.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(3.dp))
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
private fun TodaysFactCard(
    fact: Fact,
    accent: Color,
    onOpen: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
    ) {
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Box(
                Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(accent)
            )
            Column(Modifier.padding(22.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.MenuBook,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "BUGÜN TARİHTE",
                        style = MaterialTheme.typography.labelLarge,
                        color = accent
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = fact.shortText,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(18.dp))
                Button(
                    onClick = onOpen,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accent,
                        contentColor = Color.White
                    )
                ) {
                    Text("Devamını oku", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun ArchiveLink(onClick: () -> Unit) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        androidx.compose.material3.TextButton(onClick = onClick) {
            Text(
                text = "Tüm arşivi gör",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
