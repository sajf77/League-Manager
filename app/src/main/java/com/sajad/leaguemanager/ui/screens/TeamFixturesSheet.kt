package com.sajad.leaguemanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sajad.leaguemanager.data.TeamData
import com.sajad.leaguemanager.ui.components.TeamLogo
import com.sajad.leaguemanager.ui.theme.WinGreen
import com.sajad.leaguemanager.ui.theme.DrawYellow
import com.sajad.leaguemanager.ui.theme.LossRed
import com.sajad.leaguemanager.viewmodel.TeamFixture

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamFixturesSheet(team: TeamData, fixtures: List<TeamFixture>, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                TeamLogo(logoPath = team.logo, contentDescription = team.name, size = 24.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(team.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = onDismiss) { Text("X") }
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (fixtures.isEmpty()) {
                Text("No fixtures found for this team.", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(40.dp))
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 500.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(fixtures) { fixture ->
                        val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
                        val resultLetter = when (fixture.result) {
                            "win" -> "W"
                            "loss" -> "L"
                            "draw" -> "D"
                            else -> null
                        }
                        val resultColor = when (fixture.result) {
                            "win" -> WinGreen
                            "loss" -> LossRed
                            "draw" -> DrawYellow
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFD8DDE3))) {
                            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("R${fixture.roundNum}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                TeamLogo(logoPath = fixture.opponent.logo, contentDescription = fixture.opponent.name, size = 24.dp)
                                Text(fixture.opponent.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                                Surface(shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.surface) {
                                    Text(
                                        if (fixture.isHome) "H" else "A",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (fixture.isHome) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(fixture.scoreText.ifEmpty { "vs" }, style = MaterialTheme.typography.labelLarge, color = if (isDark) Color.White else Color.Black)
                                if (resultLetter != null) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .background(resultColor, RoundedCornerShape(3.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(resultLetter, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
