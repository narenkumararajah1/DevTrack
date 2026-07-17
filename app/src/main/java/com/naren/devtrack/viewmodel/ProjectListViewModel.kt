package com.naren.devtrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naren.devtrack.data.repository.AuthRepository
import com.naren.devtrack.data.repository.ProjectRepository
import com.naren.devtrack.domain.model.Project
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProjectListUiState(
    val isLoading: Boolean = true,
    val projects: List<Project> = emptyList(),
    val showArchived: Boolean = false,
    val pendingDeleteProjectId: String? = null,
    val errorMessage: String? = null
)

class ProjectListViewModel @JvmOverloads constructor(
    private val authRepository: AuthRepository = AuthRepository(),
    private val projectRepository: ProjectRepository = ProjectRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectListUiState())
    val uiState: StateFlow<ProjectListUiState> = _uiState.asStateFlow()

    init {
        loadProjects()
    }

    fun toggleShowArchived() {
        _uiState.value = _uiState.value.copy(showArchived = !_uiState.value.showArchived)
        loadProjects()
    }

    fun refresh() {
        loadProjects()
    }

    fun setArchived(projectId: String, archived: Boolean) {
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            projectRepository.setArchived(uid, projectId, archived)
            loadProjects()
        }
    }

    fun requestDelete(projectId: String) {
        _uiState.value = _uiState.value.copy(pendingDeleteProjectId = projectId)
    }

    fun cancelDelete() {
        _uiState.value = _uiState.value.copy(pendingDeleteProjectId = null)
    }

    fun confirmDelete() {
        val projectId = _uiState.value.pendingDeleteProjectId ?: return
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            projectRepository.deleteProject(uid, projectId)
            _uiState.value = _uiState.value.copy(pendingDeleteProjectId = null)
            loadProjects()
        }
    }

    private fun loadProjects() {
        val uid = authRepository.currentUser?.uid
        if (uid == null) {
            _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Not signed in")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            val result = projectRepository.getProjects(uid, archived = _uiState.value.showArchived)
            _uiState.value = result.fold(
                onSuccess = { projects -> _uiState.value.copy(isLoading = false, projects = projects) },
                onFailure = { error ->
                    _uiState.value.copy(isLoading = false, errorMessage = error.message ?: "Failed to load projects")
                }
            )
        }
    }
}
