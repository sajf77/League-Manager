package com.sajad.leaguemanager.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sajad.leaguemanager.data.TeamData
import com.sajad.leaguemanager.ui.components.TeamLogo
import com.sajad.leaguemanager.viewmodel.LeagueViewModel

private data class MatchData(val home: TeamData, val away: TeamData, val key: String, val hasResult: Boolean, val homeScore: Int?, val awayScore: Int?, val roundNum: Int)

@Composable
fun FixturesScreen(viewModel: LeagueViewModel, modifier: Modifier = Modifier, onMatchClick: (TeamData, TeamData, Int) -> Unit = { _, _, _ -> }) {
    val league by viewModel.activeLeague.collectAsState()
    val roundIndex by viewModel.currentRoundIndex.collectAsState()
    var showRoundPicker by remember { mutableStateOf(false) }
    var showScorePicker by remember { mutableStateOf(false) }
    var selectedKey by remember { mutableStateOf("") }
    var showConfirmClear by remember { mutableStateOf(false) }

    val currentLeague = league ?: return
    val currentRound = currentLeague.rounds.getOrNull(roundIndex) ?: return

    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    val matchData = remember(league, roundIndex) {
        currentRound.matches.mapIndexed { matchIdx, pair ->
            val home = viewModel.teamByNumber(pair.first)
            val away = viewModel.teamByNumber(pair.second)
            val key = "r${currentRound.round}-m$matchIdx"
            val saved = currentLeague.scores[key]
            val hasResult = saved?.home != null && saved?.away != null
            MatchData(home, away, key, hasResult, saved?.home, saved?.away, currentRound.round)
        }
    }

    val hasResults = remember(matchData) { matchData.any { it.hasResult } }

    Column(modifier = modifier.padding(horizontal = 10.dp)) {
        Spacer(modifier = Modifier.height(8.dp))

        // Round navigation
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Row(modifier = Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.navigateRound(-1) }) { Icon(Icons.Default.ChevronLeft, "Previous", tint = MaterialTheme.colorScheme.primary) }
                TextButton(onClick = { showRoundPicker = true }, modifier = Modifier.weight(1f)) {
                    Text("Round ${currentRound.round}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Icon(Icons.Default.ArrowDropDown, null, tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = { viewModel.navigateRound(1) }) { Icon(Icons.Default.ChevronRight, "Next", tint = MaterialTheme.colorScheme.primary) }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Match list - single row per match
        LazyColumn(modifier = Modifier.padding(horizontal = 2.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(matchData.size, key = { matchData[it].key }) { idx ->
                val m = matchData[idx]
                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        onMatchClick(m.home, m.away, m.roundNum)
                    },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Home team name
                        Text(
                            m.home.name,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.End
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Home logo
                        TeamLogo(logoPath = m.home.logo, contentDescription = m.home.name, size = 24.dp)

                        Spacer(modifier = Modifier.width(0.dp))

                        // Score - clickable
                        Column(
                            modifier = Modifier
                                  .width(75.dp)
                                .clip(MaterialTheme.shapes.small)
                                .clickable {
                                    selectedKey = m.key
                                    showScorePicker = true
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (m.hasResult) {
                                Text(
                                    "${m.homeScore} - ${m.awayScore}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (isDark) Color.White else Color.Black
                                )
                            } else {
                                Text(
                                    "vs",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(0.dp))

                        // Away logo
                        TeamLogo(logoPath = m.away.logo, contentDescription = m.away.name, size = 24.dp)

                        Spacer(modifier = Modifier.width(8.dp))

                        // Away team name
                        Text(
                            m.away.name,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (hasResults) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                OutlinedButton(onClick = { showConfirmClear = true }, colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                    Text("Clear Round Results")
                }
            }
        }
    }

    if (showRoundPicker) {
        RoundPickerSheet(
            rounds = currentLeague.rounds,
            currentIndex = roundIndex,
            onRoundSelected = { viewModel.goToRound(it); showRoundPicker = false },
            onDismiss = { showRoundPicker = false }
        )
    }

    if (showScorePicker) {
        ScorePickerSheet(
            league = currentLeague,
            matchKey = selectedKey,
            onScoreSet = { home, away -> viewModel.setScore(selectedKey, home, away); showScorePicker = false },
            onClear = { viewModel.clearScore(selectedKey); showScorePicker = false },
            onDismiss = { showScorePicker = false }
        )
    }

    if (showConfirmClear) {
        AlertDialog(
            onDismissRequest = { showConfirmClear = false },
            title = { Text("Clear Round") },
            text = { Text("Clear all results in Round ${currentRound.round}?") },
            confirmButton = { TextButton(onClick = { viewModel.clearRoundResults(); showConfirmClear = false }) { Text("Confirm") } },
            dismissButton = { TextButton(onClick = { showConfirmClear = false }) { Text("Cancel") } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundPickerSheet(rounds: List<com.sajad.leaguemanager.data.Round>, currentIndex: Int, onRoundSelected: (Int) -> Unit, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)) {
            Text("Jump to round", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                itemsIndexed(rounds, key = { idx, _ -> idx }) { idx, round ->
                    val isSelected = idx == currentIndex
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp).clickable { onRoundSelected(idx) },
                        shape = MaterialTheme.shapes.small,
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            "Round ${round.round}",
                            modifier = Modifier.padding(13.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
