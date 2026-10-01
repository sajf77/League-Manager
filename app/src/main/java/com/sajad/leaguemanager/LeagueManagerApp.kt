package com.sajad.leaguemanager

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sajad.leaguemanager.ui.components.PreloadTeamLogos
import com.sajad.leaguemanager.ui.screens.*
import com.sajad.leaguemanager.viewmodel.LeagueViewModel
import kotlinx.coroutines.launch

fun android.content.Context.findActivity(): android.app.Activity? {
    var context = this
    while (context is android.content.ContextWrapper) {
        if (context is android.app.Activity) return context
        context = context.baseContext
    }
    return null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueManagerApp(viewModel: LeagueViewModel) {
    PreloadTeamLogos()
    val activeLeague by viewModel.activeLeague.collectAsState()
    val theme by viewModel.theme.collectAsState()
    var showWizard by remember { mutableStateOf(false) }
    var drawerOpen by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var showLeagueDetail by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showEditTeams by remember { mutableStateOf(false) }
    var showAllTeams by remember { mutableStateOf(false) }
    var showExitConfirm by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val drawerWidth = screenWidth * 0.75f

    BackHandler(enabled = drawerOpen) { drawerOpen = false }
    BackHandler(enabled = showSettings) { showSettings = false }
    BackHandler(enabled = showEditTeams) { showEditTeams = false }
    BackHandler(enabled = showWizard) { showWizard = false }
    BackHandler(enabled = showAllTeams) { showAllTeams = false }
    BackHandler(enabled = showLeagueDetail) { showLeagueDetail = false }
    BackHandler(enabled = !showLeagueDetail && !showWizard && !showSettings && !showEditTeams && !showAllTeams && !drawerOpen) {
        showExitConfirm = true
    }

    when {
        showAllTeams -> {
            AllTeamsScreen(onBack = { showAllTeams = false })
        }
        showEditTeams && activeLeague != null -> {
            EditTeamAssignmentScreen(
                initialAssignment = activeLeague!!.teamAssignment,
                onSave = { newAssignment ->
                    val league = activeLeague!!
                    viewModel.updateLeague(league.id, league.name, league.numTeams, newAssignment, league.roundsText)
                    showEditTeams = false
                },
                onBack = { showEditTeams = false }
            )
        }
        showWizard -> {
            WizardScreen(
                viewModel = viewModel,
                onFinished = { showWizard = false }
            )
        }
        showLeagueDetail && activeLeague != null -> {
            LeagueDetailScreen(
                viewModel = viewModel,
                onBack = { showLeagueDetail = false },
                onSettings = { showSettings = true }
            )
        }
        else -> {
            Box(modifier = Modifier.fillMaxSize()) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text("League Manager", style = MaterialTheme.typography.titleMedium)
                            },
                            actions = {
                                IconButton(onClick = { scope.launch { drawerOpen = true } }) {
                                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        )
                    }
                ) { padding ->
                    HomeScreen(
                        viewModel = viewModel,
                        onLeagueSelected = { showLeagueDetail = true },
                        onAddNew = { showWizard = true },
                        modifier = Modifier.padding(padding)
                    )
                }

                AnimatedVisibility(
                    visible = drawerOpen,
                    enter = fadeIn(tween(80)),
                    exit = fadeOut(tween(80))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f))
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { drawerOpen = false }
                    )
                }

                AnimatedVisibility(
                    visible = drawerOpen,
                    enter = slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(80)),
                    exit = slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(80)),
                    modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(drawerWidth),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 8.dp
                    ) {
                        SidebarContent(
                            viewModel = viewModel,
                            onCloseDrawer = { drawerOpen = false },
                            onAllTeams = { drawerOpen = false; showAllTeams = true }
                        )
                    }
                }
            }
        }
    }

    if (showSettings) {
        SettingsSheet(
            viewModel = viewModel,
            onDismiss = { showSettings = false },
            onEditTeams = { showEditTeams = true }
        )
    }

    if (showExitConfirm) {
        AlertDialog(
            onDismissRequest = { showExitConfirm = false },
            title = { Text("Exit") },
            text = { Text("Are you sure you want to exit?") },
            confirmButton = {
                TextButton(onClick = { context.findActivity()?.finish() }) { Text("Exit") }
            },
            dismissButton = {
                TextButton(onClick = { showExitConfirm = false }) { Text("Cancel") }
            }
        )
    }
}
