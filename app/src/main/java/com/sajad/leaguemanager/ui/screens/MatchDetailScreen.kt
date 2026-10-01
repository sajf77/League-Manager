package com.sajad.leaguemanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sajad.leaguemanager.data.TeamData
import com.sajad.leaguemanager.ui.components.TeamLogo
import com.sajad.leaguemanager.ui.theme.DrawYellow
import com.sajad.leaguemanager.viewmodel.LeagueViewModel

@Composable
fun MatchDetailScreen(
    viewModel: LeagueViewModel,
    homeTeam: TeamData,
    awayTeam: TeamData,
    roundNum: Int,
    onBack: () -> Unit
) {
    val league by viewModel.activeLeague.collectAsState()
    val currentLeague = league ?: return
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    val h2hResult = remember(homeTeam, awayTeam) {
        viewModel.getH2H(homeTeam, awayTeam)
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize().padding(top = 43.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(modifier = Modifier.width(48.dp), onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentLeague.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Round $roundNum",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    val matchRound = currentLeague.rounds.find { it.round == roundNum }
                    val matchIdx = matchRound?.matches?.indexOfFirst {
                        val tH = viewModel.teamByNumber(it.first)
                        val tA = viewModel.teamByNumber(it.second)
                        tH.name == homeTeam.name && tA.name == awayTeam.name
                    }
                    val score = if (matchIdx != null && matchIdx >= 0) {
                        currentLeague.scores["r${roundNum}-m$matchIdx"]
                    } else null
                    val hasResult = score?.home != null && score.away != null

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            TeamLogo(logoPath = homeTeam.logo, contentDescription = homeTeam.name, size = 52.dp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = homeTeam.name, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                        }
                        if (hasResult) {
                            Text(
                                text = "${score!!.home} - ${score.away}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color.Black
                            )
                        } else {
                            Text(
                                text = "vs",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            TeamLogo(logoPath = awayTeam.logo, contentDescription = awayTeam.name, size = 52.dp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = awayTeam.name, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "H2H",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                    TeamLogo(logoPath = homeTeam.logo, contentDescription = homeTeam.name, size = 24.dp)
                                }
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {}
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                    TeamLogo(logoPath = awayTeam.logo, contentDescription = awayTeam.name, size = 24.dp)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                    Text(text = homeTeam.short, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                    Text(text = "Draws", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                    Text(text = awayTeam.short, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                    Text(text = "${h2hResult.homeWins}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                }
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                    Text(text = "${h2hResult.draws}", style = MaterialTheme.typography.labelLarge, color = DrawYellow)
                                }
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                    Text(text = "${h2hResult.awayWins}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }

                items(h2hResult.meetings) { meeting ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = if (meeting.leagueName == null) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.padding(bottom = 6.dp)
                            ) {
                                Text(
                                    text = meeting.leagueName ?: "This League",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (meeting.leagueName == null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = meeting.homeTeam.short, style = MaterialTheme.typography.bodyMedium)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    TeamLogo(logoPath = meeting.homeTeam.logo, contentDescription = meeting.homeTeam.name, size = 20.dp)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = meeting.score, modifier = Modifier.width(50.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                                    TeamLogo(logoPath = meeting.awayTeam.logo, contentDescription = meeting.awayTeam.name, size = 20.dp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = meeting.awayTeam.short, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}
