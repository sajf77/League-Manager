package com.sajad.leaguemanager.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.sajad.leaguemanager.data.TeamDatabase
import com.sajad.leaguemanager.data.TeamOverrides
import com.sajad.leaguemanager.ui.components.TeamLogo
import com.sajad.leaguemanager.viewmodel.LeagueViewModel

@Composable
fun SettingsSheet(viewModel: LeagueViewModel, onDismiss: () -> Unit, onEditTeams: () -> Unit = {}) {
    val league by viewModel.activeLeague.collectAsState()
    val currentLeague = league ?: return

    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showNumTeamsDialog by remember { mutableStateOf(false) }
    var showRoundsDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize().zIndex(10f),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text("League Settings", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Rename
            Card(modifier = Modifier.fillMaxWidth().clickable { showRenameDialog = true }, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Edit, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Change league name", style = MaterialTheme.typography.bodyMedium)
                        Text("Currently: ${currentLeague.name}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Change teams
            Card(modifier = Modifier.fillMaxWidth().clickable { onEditTeams() }, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.People, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Change assigned teams", style = MaterialTheme.typography.bodyMedium)
                        Text("Reassign teams to slots", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Teams
            Card(modifier = Modifier.fillMaxWidth().clickable { showNumTeamsDialog = true }, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Tag, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Change number of teams", style = MaterialTheme.typography.bodyMedium)
                        Text("Currently: ${currentLeague.numTeams} teams", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Rounds
            Card(modifier = Modifier.fillMaxWidth().clickable { showRoundsDialog = true }, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FormatListNumbered, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Change rounds config", style = MaterialTheme.typography.bodyMedium)
                        Text("${currentLeague.rounds.size} rounds configured", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Delete
            Card(modifier = Modifier.fillMaxWidth().clickable { showDeleteDialog = true }, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Delete league", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                        Text("This cannot be undone", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }

    if (showRenameDialog) {
        var newName by remember { mutableStateOf(currentLeague.name) }
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Rename League") },
            text = {
                OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("League name") }, singleLine = true)
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newName.isNotBlank()) {
                        viewModel.updateLeague(currentLeague.id, newName, currentLeague.numTeams, currentLeague.teamAssignment, currentLeague.roundsText)
                        showRenameDialog = false
                    }
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showRenameDialog = false }) { Text("Cancel") } }
        )
    }

    if (showNumTeamsDialog) {
        var newNum by remember { mutableStateOf(currentLeague.numTeams.toString()) }
        val num = newNum.toIntOrNull()
        AlertDialog(
            onDismissRequest = { showNumTeamsDialog = false },
            title = { Text("Number of Teams") },
            text = {
                OutlinedTextField(
                    value = newNum,
                    onValueChange = { newNum = it.filter { c -> c.isDigit() } },
                    label = { Text("Number of teams") },
                    singleLine = true,
                    isError = num == null || num < 2 || num > 36
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (num != null && num >= 2 && num <= 36) {
                        val newAssignment = if (num > currentLeague.numTeams) {
                            currentLeague.teamAssignment + List(num - currentLeague.numTeams) { -1 }
                        } else {
                            currentLeague.teamAssignment.take(num)
                        }
                        viewModel.updateLeague(currentLeague.id, currentLeague.name, num, newAssignment, currentLeague.roundsText)
                        showNumTeamsDialog = false
                    }
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showNumTeamsDialog = false }) { Text("Cancel") } }
        )
    }

    if (showRoundsDialog) {
        var roundsText by remember { mutableStateOf(currentLeague.roundsText) }
        AlertDialog(
            onDismissRequest = { showRoundsDialog = false },
            title = { Text("Rounds Config") },
            text = {
                OutlinedTextField(
                    value = roundsText,
                    onValueChange = { roundsText = it },
                    label = { Text("Each line = one round. Pair numbers, e.g. 1-2 3-4") },
                    modifier = Modifier.height(200.dp)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val rounds = com.sajad.leaguemanager.data.parseRoundsFromText(roundsText)
                    if (rounds.isNotEmpty()) {
                        viewModel.updateLeague(currentLeague.id, currentLeague.name, currentLeague.numTeams, currentLeague.teamAssignment, roundsText)
                        showRoundsDialog = false
                    }
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showRoundsDialog = false }) { Text("Cancel") } }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete League") },
            text = { Text("Delete \"${currentLeague.name}\"? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteLeague(currentLeague); showDeleteDialog = false; onDismiss() }) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTeamAssignmentScreen(
    initialAssignment: List<Int>,
    onSave: (List<Int>) -> Unit,
    onBack: () -> Unit
) {
    var assignment by remember { mutableStateOf(initialAssignment) }
    var selectedSlot by remember { mutableIntStateOf(-1) }
    var showDiscardDialog by remember { mutableStateOf(false) }

    val isDirty = assignment != initialAssignment

    BackHandler(enabled = selectedSlot in assignment.indices) { selectedSlot = -1 }
    BackHandler(enabled = showDiscardDialog) { showDiscardDialog = false }
    BackHandler(enabled = isDirty) { showDiscardDialog = true }
    val assignedCount = assignment.count { it >= 0 }
    val hasDuplicates = assignment.filter { it >= 0 }.distinct().size != assignedCount
    val canSave = isDirty && assignedCount == assignment.size && !hasDuplicates
    val statusText = if (hasDuplicates) "Duplicate teams - fix before saving"
    else "$assignedCount of ${assignment.size} teams assigned"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Teams") },
                navigationIcon = {
                    IconButton(onClick = { if (isDirty) showDiscardDialog = true else onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { onSave(assignment) }, enabled = canSave) { Text("Save") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                Text(
                    statusText,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (hasDuplicates) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            itemsIndexed(assignment) { idx, teamIdx ->
                val team = if (teamIdx >= 0) TeamDatabase.allTeams.getOrNull(teamIdx)?.let { TeamOverrides.resolve(it) } else null
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { selectedSlot = idx },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(start = 14.dp, top = 6.dp, bottom = 6.dp, end = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Surface(modifier = Modifier.size(32.dp), shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.surfaceVariant) {
                            Box(contentAlignment = Alignment.Center) { Text("${idx + 1}", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium) }
                        }
                        if (team != null) {
                            TeamLogo(logoPath = team.logo, contentDescription = team.name, size = 32.dp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(team.name, style = MaterialTheme.typography.bodyMedium)
                                Text(team.league, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(team.short, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            IconButton(onClick = { assignment = assignment.toMutableList().also { it[idx] = -1 } }) {
                                Icon(Icons.Default.Close, contentDescription = "Unassign", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        } else {
                            Text("Tap to assign", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.primary)
                        }
                        Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }

    TeamPickerSheet(
        visible = selectedSlot in assignment.indices,
        currentSelection = if (selectedSlot in assignment.indices) assignment[selectedSlot] else -1,
        usedIndices = if (selectedSlot in assignment.indices) assignment.filterIndexed { idx, v -> idx != selectedSlot && v >= 0 } else emptyList(),
        title = if (selectedSlot in assignment.indices) "Assign Team #${selectedSlot + 1}" else "Pick a team",
        onTeamSelected = { teamIdx ->
            assignment = assignment.toMutableList().also { it[selectedSlot] = teamIdx }
            selectedSlot = -1
        },
        onClearSelection = if (selectedSlot in assignment.indices && assignment[selectedSlot] >= 0) {
            {
                assignment = assignment.toMutableList().also { it[selectedSlot] = -1 }
                selectedSlot = -1
            }
        } else null,
        onDismiss = { selectedSlot = -1 }
    )

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Discard changes?") },
            text = { Text("You have unsaved team changes.") },
            confirmButton = { TextButton(onClick = { showDiscardDialog = false; onBack() }) { Text("Discard") } },
            dismissButton = { TextButton(onClick = { showDiscardDialog = false }) { Text("Keep Editing") } }
        )
    }
}
