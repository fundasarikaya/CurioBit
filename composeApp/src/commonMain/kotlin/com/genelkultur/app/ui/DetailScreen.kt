package com.genelkultur.app.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.genelkultur.app.data.Fact
import com.genelkultur.app.data.Reaction

@Composable
fun DetailScreen(
    fact: Fact?,
    onBack: () -> Unit,
    onOpenSource: (String) -> Unit,
    onReact: (Reaction) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Scaffold(containerColor = colors.background) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(Modifier.padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Geri", tint = colors.onBackground)
                    }
                    Text(
                        text = "Genel Kültür",
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(48.dp))
                }
                DoubleRule()
            }

            if (fact == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = colors.primary)
                }
                return@Scaffold
            }

            val (year, headline) = splitYear(fact.fullText)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("TARİHTE BUGÜN", style = MaterialTheme.typography.labelMedium, color = colors.primary)
                    if (year != null) {
                        Text(year, style = MaterialTheme.typography.labelMedium, color = colors.primary)
                    }
                }

                Text(
                    text = headline,
                    style = MaterialTheme.typography.headlineMedium,
                    color = colors.onBackground
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "KAYNAK: VİKİPEDİ",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onSurfaceVariant,
                    maxLines = 1
                )
                Spacer(Modifier.height(12.dp))
                Box(Modifier.fillMaxWidth().height(1.dp).background(colors.outline))

                if (fact.longText.isNotBlank() && fact.longText != fact.fullText) {
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = withInitial(fact.longText, colors.primary, displayFamily()),
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.onBackground,
                        textAlign = TextAlign.Start
                    )
                }

                Spacer(Modifier.height(28.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .border(1.5.dp, colors.onBackground)
                        .clickable { onOpenSource(fact.sourceUrl) },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.OpenInNew, contentDescription = null, tint = colors.onBackground, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("TAM MAKALEYİ VİKİPEDİ'DE AÇ", style = MaterialTheme.typography.labelMedium, color = colors.onBackground)
                }

                Spacer(Modifier.height(16.dp))

                ReactionButtons(
                    reaction = fact.reaction,
                    onReact = onReact
                )

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

/** Gazete girişi gibi ilk harfi büyük ve vurgu renginde yazar. */
private fun withInitial(text: String, color: Color, family: FontFamily) = buildAnnotatedString {
    if (text.isEmpty()) return@buildAnnotatedString
    withStyle(SpanStyle(fontSize = 34.sp, fontWeight = FontWeight.Bold, color = color, fontFamily = family)) {
        append(text.first())
    }
    append(text.drop(1))
}

@Composable
private fun ReactionButtons(
    reaction: Reaction,
    onReact: (Reaction) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ReactionStamp(
            icon = Icons.Filled.ThumbUp,
            label = "BEĞENDİM",
            contentDescription = "Beğen",
            selected = reaction == Reaction.LIKE,
            accent = colors.primary,
            modifier = Modifier.weight(1f).rotate(-1.2f),
            onClick = { onReact(if (reaction == Reaction.LIKE) Reaction.NONE else Reaction.LIKE) }
        )
        ReactionStamp(
            icon = Icons.Filled.ThumbDown,
            label = "BEĞENMEDİM",
            contentDescription = "Beğenme",
            selected = reaction == Reaction.DISLIKE,
            accent = colors.onSurfaceVariant,
            modifier = Modifier.weight(1f).rotate(0.8f),
            onClick = { onReact(if (reaction == Reaction.DISLIKE) Reaction.NONE else Reaction.DISLIKE) }
        )
    }
}

/**
 * Keskin köşeli, çift çerçeveli "imza atılıyormuş" hissi veren tepki butonu: seçilince
 * ince bir çizgi soldan sağa çizilir, ardından dolgu rengi bu çizgiyi yakalayıp
 * butonun tamamını doldurur.
 */
@Composable
private fun ReactionStamp(
    icon: ImageVector,
    label: String,
    contentDescription: String,
    selected: Boolean,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val sweep = remember { Animatable(if (selected) 1f else 0f) }
    var filled by remember { mutableStateOf(selected) }
    var isFirstComposition by remember { mutableStateOf(true) }

    LaunchedEffect(selected) {
        if (isFirstComposition) {
            // İlk açılışta (ör. daha önce beğenilmiş bir kayda Arşiv'den girildiğinde)
            // animasyonu oynatmadan doğrudan doğru duruma geç.
            isFirstComposition = false
            filled = selected
            sweep.snapTo(if (selected) 1f else 0f)
            return@LaunchedEffect
        }
        if (selected) {
            filled = false
            sweep.snapTo(0f)
            sweep.animateTo(1f, tween(durationMillis = 320, easing = FastOutSlowInEasing))
            filled = true
        } else {
            sweep.snapTo(0f)
            filled = false
        }
    }

    val bgColor by animateColorAsState(
        targetValue = if (filled) accent else Color.Transparent,
        animationSpec = tween(220)
    )
    val contentColor by animateColorAsState(
        targetValue = if (filled) MaterialTheme.colorScheme.background else accent,
        animationSpec = tween(220)
    )

    // Damga görünümü: dış kalın çerçeve, içte ince ikinci çerçeve.
    Box(
        modifier = modifier
            .background(bgColor)
            .border(width = 2.dp, color = accent)
            .clickable(onClick = onClick)
            .padding(3.dp)
            .border(width = 1.dp, color = if (filled) contentColor else accent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 11.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = contentDescription, tint = contentColor, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor
            )
        }

        // İmza çizgisi: dolgu tamamlanana kadar soldan sağa çizilir.
        if (!filled) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(sweep.value)
                    .height(2.dp)
                    .align(Alignment.BottomStart)
                    .background(accent)
            )
        }
    }
}
