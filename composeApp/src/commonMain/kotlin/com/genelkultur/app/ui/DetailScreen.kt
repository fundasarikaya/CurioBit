package com.genelkultur.app.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
    val accent = fact?.let { accentColorFor(it.id) } ?: MaterialTheme.colorScheme.primary

    Scaffold { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 4.dp, top = 4.dp, end = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Geri")
                }
            }

            if (fact == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = accent)
                }
                return@Scaffold
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(8.dp)
                            .background(accent, shape = androidx.compose.foundation.shape.CircleShape)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "TARİHTE BUGÜN",
                        style = MaterialTheme.typography.labelLarge,
                        color = accent
                    )
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text = fact.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(Modifier.height(18.dp))
                Box(
                    Modifier
                        .width(48.dp)
                        .height(3.dp)
                        .background(accent, RoundedCornerShape(2.dp))
                )
                Spacer(Modifier.height(18.dp))

                Text(
                    text = fact.fullText,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (fact.longText.isNotBlank() && fact.longText != fact.fullText) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = fact.longText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(28.dp))

                OutlinedButton(
                    onClick = { onOpenSource(fact.sourceUrl) },
                    shape = RoundedCornerShape(50),
                    border = androidx.compose.foundation.BorderStroke(1.dp, accent)
                ) {
                    Icon(Icons.Filled.OpenInNew, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Tam makaleyi Wikipedia'da aç", color = accent)
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
