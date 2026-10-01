package com.sajad.leaguemanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sajad.leaguemanager.data.TeamData
import com.sajad.leaguemanager.viewmodel.LeagueViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueDetailScreen(viewModel: LeagueViewModel, onBack: () -> Unit, onSettings: () -> Unit) {
    val league by viewModel.activeLeague.collectAsState()
    val currentLeague = league ?: return
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Matches", "Standings", "Teams")

    var showMatchDetail by remember { mutableStateOf(false) }
    var matchDetailTeams by remember { mutableStateOf<Pair<TeamData, TeamData>?>(null) }
    var matchDetailRound by remember { mutableIntStateOf(1) }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Top bar with back arrow
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = onSettings) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        // League logo and name centered
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.EmojiEvents,
                contentDescription = currentLeague.name,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = currentLeague.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tab bar
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Tab content
        when (selectedTab) {
            0 -> FixturesScreen(
                viewModel = viewModel,
                modifier = Modifier.weight(1f),
                onMatchClick = { home, away, round ->
                    matchDetailTeams = home to away
                    matchDetailRound = round
                    showMatchDetail = true
                }
            )
            1 -> StandingsScreen(viewModel = viewModel, modifier = Modifier.weight(1f))
            2 -> TeamsScreen(viewModel = viewModel, modifier = Modifier.weight(1f))
        }
    }

    val teams = matchDetailTeams
    if (showMatchDetail && teams != null) {
        MatchDetailScreen(
            viewModel = viewModel,
            homeTeam = teams.first,
            awayTeam = teams.second,
            roundNum = matchDetailRound,
            onBack = { showMatchDetail = false }
        )
    }
}
