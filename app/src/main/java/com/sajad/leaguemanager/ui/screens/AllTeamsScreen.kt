package com.sajad.leaguemanager.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sajad.leaguemanager.data.TeamData
import com.sajad.leaguemanager.data.TeamDatabase
import com.sajad.leaguemanager.data.TeamOverrides
import com.sajad.leaguemanager.ui.components.TeamLogo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTeamsScreen(onBack: () -> Unit) {
    var selectedLeague by remember { mutableStateOf<String?>(null) }
    var editingTeam by remember { mutableStateOf<TeamData?>(null) }
    var editName by remember { mutableStateOf("") }
    var editShort by remember { mutableStateOf("") }

    BackHandler(enabled = editingTeam != null) { editingTeam = null }
    BackHandler(enabled = selectedLeague != null && editingTeam == null) { selectedLeague = null }
    BackHandler(enabled = selectedLeague == null && editingTeam == null) { onBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedLeague ?: "All Teams", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = { if (editingTeam != null) editingTeam = null else if (selectedLeague != null) selectedLeague = null else onBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        val league = selectedLeague
        if (league == null) {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(TeamDatabase.leagueNames.size) { i ->
                    val name = TeamDatabase.leagueNames[i]
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { selectedLeague = name },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            TeamLogo(logoPath = TeamDatabase.leagueLogos[name] ?: "", contentDescription = name, size = 36.dp)
                            Text(name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                            Text("${TeamDatabase.teamsByLeague[name]?.size ?: 0} teams", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        } else {
            val teams = TeamDatabase.teamsByLeague[league].orEmpty()
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(teams.size) { i ->
                    val (_, team) = teams[i]
                    val resolved = TeamOverrides.resolve(team)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            TeamLogo(logoPath = resolved.logo, contentDescription = resolved.name, size = 36.dp)
                            Text(resolved.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                            Surface(shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.surfaceVariant) {
                                Text(resolved.short, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = {
                                editingTeam = resolved
                                editName = resolved.name
                                editShort = resolved.short
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }

    editingTeam?.let { team ->
        AlertDialog(
            onDismissRequest = { editingTeam = null },
            title = { Text("Edit Team") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full name") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editShort,
                        onValueChange = { editShort = it },
                        label = { Text("Short name") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    TeamOverrides.set(team.logo, editName.trim(), editShort.trim())
                    editingTeam = null
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = {
                    TeamOverrides.reset(team.logo)
                    editingTeam = null
                }) { Text("Reset") }
                TextButton(onClick = { editingTeam = null }) { Text("Cancel") }
            }
        )
    }
}