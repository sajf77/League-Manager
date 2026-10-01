package com.sajad.leaguemanager.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sajad.leaguemanager.data.League
import com.sajad.leaguemanager.data.MAX_GOALS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScorePickerSheet(league: League, matchKey: String, onScoreSet: (Int, Int) -> Unit, onClear: () -> Unit, onDismiss: () -> Unit) {
    var step by remember { mutableStateOf("home") }
    var homeScore by remember { mutableIntStateOf(0) }

    val matchResult = Regex("r(\\d+)-m(\\d+)").find(matchKey) ?: return
    val roundNum = matchResult.groupValues[1].toInt()
    val matchIdx = matchResult.groupValues[2].toInt()
    val round = league.rounds.find { it.round == roundNum } ?: return
    val pair = round.matches.getOrNull(matchIdx) ?: return

    val saved = league.scores[matchKey]
    val hasResult = saved?.home != null && saved?.away != null

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)) {
            Text(
                if (step == "home") "Home" else "Away",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(14.dp))

            LazyVerticalGrid(columns = GridCells.Fixed(5), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                itemsIndexed((0..MAX_GOALS).toList()) { _, num ->
                    Surface(
                        modifier = Modifier.height(52.dp).clickable {
                            if (step == "home") { homeScore = num; step = "away" }
                            else { onScoreSet(homeScore, num) }
                        },
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("$num", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            if (hasResult) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(onClick = onClear, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                    Text("Clear Result")
                }
            }
        }
    }
}
