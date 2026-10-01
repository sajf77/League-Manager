package com.sajad.leaguemanager.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.sajad.leaguemanager.data.TeamData
import com.sajad.leaguemanager.data.TeamDatabase
import com.sajad.leaguemanager.data.TeamOverrides
import com.sajad.leaguemanager.ui.components.TeamLogo

private sealed class PickerRow {
    data class Header(val name: String) : PickerRow()
    data class Team(val index: Int, val team: TeamData) : PickerRow()
}

@Composable
fun TeamPickerSheet(
    visible: Boolean,
    currentSelection: Int,
    usedIndices: List<Int>,
    title: String = "Pick a team",
    onTeamSelected: (Int) -> Unit,
    onClearSelection: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    if (!visible) return

    var searchQuery by remember { mutableStateOf("") }
    var selectedLeagueFilter by remember { mutableStateOf<String?>(null) }
    val usedSet = remember(usedIndices) { usedIndices.toSet() }

    val filteredList = remember(searchQuery, selectedLeagueFilter) {
        val base = if (selectedLeagueFilter == null) TeamDatabase.indexedTeams
        else TeamDatabase.teamsByLeague[selectedLeagueFilter] ?: emptyList()
        val resolved = base.map { (idx, team) -> idx to TeamOverrides.resolve(team) }
        if (searchQuery.isBlank()) resolved
        else resolved.filter { (_, team) ->
            team.name.contains(searchQuery, ignoreCase = true) ||
                    team.short.contains(searchQuery, ignoreCase = true)
        }
    }

    val filteredLeagues = remember(filteredList) { filteredList.map { it.second.league }.distinct() }

    val rows = remember(filteredList) {
        val byLeague = filteredList.groupBy { it.second.league }
        val list = mutableListOf<PickerRow>()
        for (league in TeamDatabase.leagueNames) {
            val teams = byLeague[league] ?: continue
            list.add(PickerRow.Header(league))
            teams.sortedBy { it.second.name }.forEach { (realIdx, team) ->
                list.add(PickerRow.Team(realIdx, team))
            }
        }
        list
    }

    Surface(
        modifier = Modifier.fillMaxSize().zIndex(10f),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(title, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search team...") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TeamDatabase.leagueNames.filter { it in filteredLeagues }.forEach { league ->
                    FilterChip(
                        selected = selectedLeagueFilter == league,
                        onClick = { selectedLeagueFilter = if (selectedLeagueFilter == league) null else league },
                        label = { Text(league) },
                        leadingIcon = {
                            val logo = TeamDatabase.leagueLogos[league]
                            if (logo != null) TeamLogo(logoPath = logo, contentDescription = null, size = 16.dp)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))

            if (onClearSelection != null && currentSelection >= 0) {
                OutlinedButton(onClick = onClearSelection, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Remove assignment")
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                rows.forEach { row ->
                    when (row) {
                        is PickerRow.Header -> {
                            val logo = TeamDatabase.leagueLogos[row.name]
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)) {
                                if (logo != null) TeamLogo(logoPath = logo, contentDescription = null, size = 16.dp)
                                Text(row.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        is PickerRow.Team -> {
                            val isUsed = row.index in usedSet
                            val isSelected = row.index == currentSelection
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .then(if (!isUsed) Modifier.clickable { onTeamSelected(row.index) } else Modifier),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TeamLogo(logoPath = row.team.logo, contentDescription = null, size = 28.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(row.team.name, style = MaterialTheme.typography.bodyMedium, color = if (isUsed && !isSelected) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurface, maxLines = 1)
                                }
                                Text(row.team.short, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                if (isSelected) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
