package com.naren.devtrack.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.naren.devtrack.navigation.Screen
import com.naren.devtrack.ui.components.DevTrackBottomBar
import com.naren.devtrack.ui.components.ProjectListItem
import com.naren.devtrack.viewmodel.ProjectListViewModel

@Composable
fun ProjectListScreen(
    onNavigate: (String) -> Unit,
    onNavigateToCreateProject: () -> Unit,
    onNavigateToEditProject: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProjectListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            DevTrackBottomBar(selectedRoute = Screen.ProjectList.route, onNavigate = onNavigate)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreateProject) {
                Text(text = "+", style = MaterialTheme.typography.headlineSmall)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Projects", style = MaterialTheme.typography.headlineSmall)
                TextButton(onClick = viewModel::toggleShowArchived) {
                    Text(if (uiState.showArchived) "Show Active" else "Show Archived")
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.projects.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (uiState.showArchived) "No archived projects" else "No projects yet",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.projects, key = { it.id }) { project ->
                        ProjectListItem(
                            project = project,
                            onClick = { onNavigateToEditProject(project.id) },
                            onArchiveToggle = { viewModel.setArchived(project.id, !project.isArchived) },
                            onDelete = { viewModel.requestDelete(project.id) }
                        )
                    }
                }
            }
        }

        uiState.pendingDeleteProjectId?.let {
            AlertDialog(
                onDismissRequest = viewModel::cancelDelete,
                title = { Text("Delete Project?") },
                text = { Text("This action cannot be undone.") },
                confirmButton = {
                    TextButton(onClick = viewModel::confirmDelete) { Text("Delete") }
                },
                dismissButton = {
                    TextButton(onClick = viewModel::cancelDelete) { Text("Cancel") }
                }
            )
        }
    }
}
