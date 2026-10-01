package com.sajad.leaguemanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sajad.leaguemanager.data.TeamData
import com.sajad.leaguemanager.ui.components.TeamLogo
import com.sajad.leaguemanager.ui.theme.DrawYellow
import com.sajad.leaguemanager.viewmodel.H2HResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun H2HSheet(homeTeam: TeamData, awayTeam: TeamData, result: H2HResult, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)) {
            // Summary
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
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
                            Text(homeTeam.short, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Text("Draws", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Text(awayTeam.short, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Text("${result.homeWins}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        }
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Text("${result.draws}", style = MaterialTheme.typography.labelLarge, color = DrawYellow)
                        }
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Text("${result.awayWins}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.heightIn(max = 400.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(result.meetings) { meeting ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Column(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                            Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = if (meeting.leagueName == null) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                ) {
                                    Text(
                                        meeting.leagueName ?: "This League",
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
                                    Text(meeting.homeTeam.short, style = MaterialTheme.typography.bodyMedium)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    TeamLogo(logoPath = meeting.homeTeam.logo, contentDescription = meeting.homeTeam.name, size = 20.dp)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(meeting.score, modifier = Modifier.width(50.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                                    TeamLogo(logoPath = meeting.awayTeam.logo, contentDescription = meeting.awayTeam.name, size = 20.dp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(meeting.awayTeam.short, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
