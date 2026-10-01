package com.sajad.leaguemanager.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sajad.leaguemanager.ui.components.TeamLogo
import com.sajad.leaguemanager.viewmodel.LeagueViewModel

@Composable
fun TeamsScreen(viewModel: LeagueViewModel, modifier: Modifier = Modifier) {
    val league by viewModel.activeLeague.collectAsState()
    val currentLeague = league ?: return

    var showTeamFixtures by remember { mutableStateOf(false) }
    var selectedSlot by remember { mutableIntStateOf(1) }

    Column(modifier = modifier.padding(horizontal = 10.dp)) {
        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(currentLeague.numTeams) { i ->
                val team = viewModel.teamByNumber(i + 1)
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { selectedSlot = i + 1; showTeamFixtures = true },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TeamLogo(logoPath = team.logo, contentDescription = team.name, size = 36.dp)
                        Text(team.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                        Surface(shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.surfaceVariant) {
                            Text(team.short, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
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
