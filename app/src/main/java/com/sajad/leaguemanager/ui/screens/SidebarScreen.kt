package com.sajad.leaguemanager.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sajad.leaguemanager.viewmodel.LeagueViewModel

@Composable
fun SidebarContent(viewModel: LeagueViewModel, onCloseDrawer: () -> Unit, onAllTeams: () -> Unit) {
    val leagues by viewModel.allLeagues.collectAsState()
    val activeId by viewModel.activeLeagueId.collectAsState()
    val theme by viewModel.theme.collectAsState()
    val fontSize by viewModel.fontSize.collectAsState()
    var showNewLeague by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        // Header
        Column(modifier = Modifier.padding(20.dp)) {
            Text("League Manager", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Text("Champions Edition", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = MaterialTheme.colorScheme.outline)

        // Leagues list
        Text("My Leagues", modifier = Modifier.padding(14.dp, 10.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (leagues.isEmpty()) {
            Text("No leagues yet.", modifier = Modifier.padding(16.dp, 10.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            leagues.forEach { league ->
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { viewModel.switchLeague(league.id); onCloseDrawer() }.padding(14.dp, 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.EmojiEvents, null, tint = if (league.id == activeId) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(league.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = if (league.id == activeId) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                    Text("${league.numTeams} teams", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = MaterialTheme.colorScheme.outline)

        // All Teams
        Row(
            modifier = Modifier.fillMaxWidth().clickable { onAllTeams() }.padding(14.dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(Icons.Default.Groups, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("All Teams", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
        }

        HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = MaterialTheme.colorScheme.outline)

        // Font size picker
        Text("Font Size", modifier = Modifier.padding(14.dp, 10.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(modifier = Modifier.padding(horizontal = 14.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("small" to "12", "default" to "14", "large" to "16", "xlarge" to "18").forEach { (size, label) ->
                val isActive = fontSize == size
                FilterChip(selected = isActive, onClick = { viewModel.setFontSize(size) }, label = { Text(label) })
            }
        }

        HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = MaterialTheme.colorScheme.outline)

        // Theme toggle
        Row(
            modifier = Modifier.fillMaxWidth().clickable { viewModel.setTheme(if (theme == "dark") "light" else "dark") }.padding(14.dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(if (theme == "dark") Icons.Default.DarkMode else Icons.Default.LightMode, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Dark Mode", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            Switch(checked = theme == "dark", onCheckedChange = { viewModel.setTheme(if (it) "dark" else "light") })
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}
