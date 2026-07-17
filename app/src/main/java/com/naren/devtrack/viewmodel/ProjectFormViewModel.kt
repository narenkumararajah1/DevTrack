package com.naren.devtrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naren.devtrack.data.repository.AuthRepository
import com.naren.devtrack.data.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProjectFormUiState(
    val name: String = "",
    val description: String = "",
    val completionPercentage: Int = 0,
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSaved: Boolean = false
)

class ProjectFormViewModel @JvmOverloads constructor(
    private val authRepository: AuthRepository = AuthRepository(),
    private val projectRepository: ProjectRepository = ProjectRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectFormUiState())
    val uiState: StateFlow<ProjectFormUiState> = _uiState.asStateFlow()

    private var loadedProjectId: String? = null

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(name = name, errorMessage = null)
    }

    fun onDescriptionChange(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun onCompletionChange(percentage: Int) {
        _uiState.value = _uiState.value.copy(completionPercentage = percentage)
    }

    fun loadProject(projectId: String) {
        if (loadedProjectId == projectId) return
        val uid = authRepository.currentUser?.uid
        if (uid == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "Not signed in")
            return
        }
        loadedProjectId = projectId
        _uiState.value = _uiState.value.copy(isEditMode = true, isLoading = true)
        viewModelScope.launch {
            val result = projectRepository.getProject(uid, projectId)
            _uiState.value = result.fold(
                onSuccess = { project ->
                    if (project != null) {
                        _uiState.value.copy(
                            isLoading = false,
                            name = project.name,
                            description = project.description,
                            completionPercentage = project.completionPercentage
                        )
                    } else {
                        _uiState.value.copy(isLoading = false, errorMessage = "Project not found")
                    }
                },
                onFailure = { error ->
                    _uiState.value.copy(isLoading = false, errorMessage = error.message ?: "Failed to load project")
                }
            )
        }
    }

    fun save() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Project name is required")
            return
        }
        val uid = authRepository.currentUser?.uid
        if (uid == null) {
            _uiState.value = state.copy(errorMessage = "Not signed in")
            return
        }

        _uiState.value = state.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val projectId = loadedProjectId
            val result = if (projectId != null) {
                projectRepository.updateProject(
                    uid,
                    projectId,
                    state.name.trim(),
                    state.description.trim(),
                    state.completionPercentage
                )
            } else {
                projectRepository.createProject(uid, state.name.trim(), state.description.trim())
            }
            _uiState.value = result.fold(
                onSuccess = { _uiState.value.copy(isLoading = false, isSaved = true) },
                onFailure = { error ->
                    _uiState.value.copy(isLoading = false, errorMessage = error.message ?: "Failed to save project")
                }
            )
        }
    }
}
