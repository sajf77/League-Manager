package com.sajad.leaguemanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sajad.leaguemanager.ui.components.TeamLogo
import com.sajad.leaguemanager.ui.theme.*
import com.sajad.leaguemanager.viewmodel.LeagueViewModel

@Composable
fun StandingsScreen(viewModel: LeagueViewModel, modifier: Modifier = Modifier) {
    val league by viewModel.activeLeague.collectAsState()
    val currentLeague = league ?: return

    val latestRoundIndex = remember(currentLeague) {
        val idx = currentLeague.rounds.indexOfLast { round ->
            round.matches.withIndex().any { (matchIdx, _) ->
                val key = "r${round.round}-m$matchIdx"
                val s = currentLeague.scores[key]
                s != null && s.home != null && s.away != null
            }
        }
        if (idx >= 0) idx else 0
    }

    val standings = remember(league, latestRoundIndex) { viewModel.computeStandingsWithMovement(latestRoundIndex, league) }
    val maxForm = remember(standings) { standings.maxOfOrNull { it.form.size } ?: 0 }
    val formColWidth = (maxForm * 22).dp
    val formColMinWidth = 52.dp

    var showTeamFixtures by remember { mutableStateOf(false) }
    var selectedSlot by remember { mutableIntStateOf(1) }

    Column(modifier = modifier.padding(horizontal = 10.dp)) {
        Spacer(modifier = Modifier.height(10.dp))

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(modifier = Modifier.padding(10.dp)) {
                    Text("#", modifier = Modifier.width(28.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Team", modifier = Modifier.width(160.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("P", modifier = Modifier.width(28.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("G:A", modifier = Modifier.width(48.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("+/-", modifier = Modifier.width(36.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Pts", modifier = Modifier.width(36.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("W", modifier = Modifier.width(28.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("D", modifier = Modifier.width(28.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("L", modifier = Modifier.width(28.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Form", modifier = Modifier.widthIn(min = maxOf(formColWidth, formColMinWidth)), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                standings.forEachIndexed { idx, row ->
                    val rank = idx + 1
                    val slot = row.slot

                    Row(
                        modifier = Modifier
                            .clickable { selectedSlot = slot; showTeamFixtures = true }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.width(28.dp), horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("$rank", style = MaterialTheme.typography.bodyMedium)
                            when (row.movement) {
                                "up" -> Text("\u25b2", color = RankUp, fontSize = 12.sp)
                                "down" -> Text("\u25bc", color = RankDown, fontSize = 12.sp)
                            }
                        }
                        Row(modifier = Modifier.width(160.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            TeamLogo(logoPath = row.team.logo, contentDescription = row.team.name, size = 24.dp)
                            Text(row.team.name, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, maxLines = 2, overflow = TextOverflow.Clip)
                        }
                        Text("${row.p}", modifier = Modifier.width(28.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium)
                        Text("${row.gf}:${row.ga}", modifier = Modifier.width(48.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium)
                        Text(if (row.gd > 0) "+${row.gd}" else "${row.gd}", modifier = Modifier.width(36.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                        Text("${row.pts}", modifier = Modifier.width(36.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelLarge)
                        Text("${row.w}", modifier = Modifier.width(28.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium)
                        Text("${row.d}", modifier = Modifier.width(28.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium)
                        Text("${row.l}", modifier = Modifier.width(28.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium)
                        Row(
                            modifier = Modifier.widthIn(min = maxOf(formColWidth, formColMinWidth)),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            row.form.forEach { r ->
                                val bg = when (r) {
                                    "W" -> WinGreen
                                    "D" -> DrawYellow
                                    else -> LossRed
                                }
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 1.dp)
                                        .size(20.dp)
                                        .background(bg, RoundedCornerShape(3.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(r, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showTeamFixtures) {
        TeamFixturesSheet(
            team = viewModel.teamByNumber(selectedSlot),
            fixtures = viewModel.getTeamFixtures(selectedSlot),
            onDismiss = { showTeamFixtures = false }
        )
    }
}
