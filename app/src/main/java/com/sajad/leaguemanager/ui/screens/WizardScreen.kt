package com.sajad.leaguemanager.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sajad.leaguemanager.data.TeamDatabase
import com.sajad.leaguemanager.data.TeamOverrides
import com.sajad.leaguemanager.data.parseRoundsFromText
import com.sajad.leaguemanager.ui.components.TeamLogo
import com.sajad.leaguemanager.viewmodel.LeagueViewModel

@Composable
fun WizardScreen(viewModel: LeagueViewModel, onFinished: () -> Unit = {}) {
    var leagueName by remember { mutableStateOf("") }
    var numTeams by remember { mutableStateOf("") }
    var teamAssignment by remember { mutableStateOf(listOf<Int>()) }
    var roundsText by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf("") }
    var teamsError by remember { mutableStateOf("") }
    var assignmentError by remember { mutableStateOf("") }
    var roundsError by remember { mutableStateOf("") }
    var showTeamPicker by remember { mutableStateOf(false) }
    var teamPickerSlot by remember { mutableIntStateOf(0) }

    BackHandler(enabled = showTeamPicker) { showTeamPicker = false }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text("League Manager", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Text("Champions Edition", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(22.dp)) {
                Text("Name your league", style = MaterialTheme.typography.titleMedium)
                Text("e.g. Friday Night League", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = leagueName, onValueChange = { leagueName = it; nameError = "" }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("League name") }, singleLine = true, isError = nameError.isNotEmpty())
                if (nameError.isNotEmpty()) Text(nameError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(14.dp))
                Text("How many teams?", style = MaterialTheme.typography.titleMedium)
                Text("Choose how many clubs will take part.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = numTeams,
                    onValueChange = {
                        numTeams = it; teamsError = ""
                        val n = it.toIntOrNull()
                        if (n != null && n >= 2 && n <= 36) {
                            teamAssignment = if (teamAssignment.size == n) teamAssignment else List(n) { -1 }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g. 18") },
                    singleLine = true,
                    isError = teamsError.isNotEmpty()
                )
                if (teamsError.isNotEmpty()) Text(teamsError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(22.dp)) {
                Text("Assign your teams", style = MaterialTheme.typography.titleMedium)
                Text("Tap each slot to pick a club.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(12.dp))
                Column(modifier = Modifier.heightIn(max = 400.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    teamAssignment.forEachIndexed { i, assignedIdx ->
                        val team = if (assignedIdx >= 0) TeamDatabase.allTeams.getOrNull(assignedIdx)?.let { TeamOverrides.resolve(it) } else null
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { teamPickerSlot = i; showTeamPicker = true },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Surface(modifier = Modifier.size(32.dp), shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.surfaceVariant) {
                                    Box(contentAlignment = Alignment.Center) { Text("${i + 1}", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium) }
                                }
                                if (team != null) {
                                    TeamLogo(logoPath = team.logo, contentDescription = team.name, size = 32.dp)
                                    Text(team.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                                    Text(team.short, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                } else {
                                    Text("Tap to assign", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
                if (assignmentError.isNotEmpty()) { Spacer(modifier = Modifier.height(4.dp)); Text(assignmentError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(22.dp)) {
                Text("Configure rounds", style = MaterialTheme.typography.titleMedium)
                Text("Each line is one round. Pair team numbers, e.g. 1-2 3-4 5-6", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = roundsText, onValueChange = { roundsText = it; roundsError = "" }, modifier = Modifier.fillMaxWidth().height(200.dp), placeholder = { Text("1-2 3-4 5-6\n2-3 4-5 6-7") }, textStyle = MaterialTheme.typography.bodyMedium)
                if (roundsError.isNotEmpty()) { Spacer(modifier = Modifier.height(4.dp)); Text(roundsError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        val n = numTeams.toIntOrNull()
                        if (leagueName.isBlank()) { nameError = "Give your league a name."; return@Button }
                        if (n == null || n < 2 || n > 36) { teamsError = "Enter a number between 2 and 36."; return@Button }
                        if (teamAssignment.any { it == -1 }) { assignmentError = "Please choose a team for every number."; return@Button }
                        if (teamAssignment.distinct().size != teamAssignment.size) { assignmentError = "Each team can only be assigned once."; return@Button }
                        val rounds = parseRoundsFromText(roundsText)
                        if (rounds.isEmpty()) { roundsError = "Add at least one round."; return@Button }
                        val invalid = rounds.flatMap { it.matches }.flatMap { listOf(it.first, it.second) }.firstOrNull { it < 1 || it > n }
                        if (invalid != null) { roundsError = "Team number $invalid is invalid. Use 1-$n."; return@Button }
                        viewModel.createLeague(leagueName, n, teamAssignment, roundsText)
                        onFinished()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Create League", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    TeamPickerSheet(
        visible = showTeamPicker,
        currentSelection = if (showTeamPicker) teamAssignment.getOrElse(teamPickerSlot) { -1 } else -1,
        usedIndices = if (showTeamPicker) teamAssignment.filterIndexed { idx, v -> idx != teamPickerSlot && v != -1 } else emptyList(),
        onTeamSelected = { teamAssignment = teamAssignment.toMutableList().apply { this[teamPickerSlot] = it }; showTeamPicker = false },
        onDismiss = { showTeamPicker = false }
    )
}