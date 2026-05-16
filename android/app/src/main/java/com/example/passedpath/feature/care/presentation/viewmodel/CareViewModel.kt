package com.example.passedpath.feature.care.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.passedpath.app.AppContainer
import com.example.passedpath.feature.care.domain.usecase.GetCareDependentsUseCase
import com.example.passedpath.feature.care.presentation.mapper.toCareDependentUserUiState
import com.example.passedpath.feature.care.presentation.state.CareUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CareViewModel(
    private val getCareDependentsUseCase: GetCareDependentsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(CareUiState())
    val uiState: StateFlow<CareUiState> = _uiState.asStateFlow()

    init {
        refreshDependents()
    }

    fun refreshDependents() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(isLoading = true, errorMessage = null)
            }

            runCatching {
                getCareDependentsUseCase()
            }.onSuccess { dependentUserList ->
                val dependents = dependentUserList.dependentUsers
                    .map { dependent -> dependent.toCareDependentUserUiState() }
                _uiState.update { state ->
                    state.copy(
                        dependents = dependents,
                        selectedDependentUserId = state.selectedDependentUserId
                            ?.takeIf { selectedId ->
                                dependents.any { dependent ->
                                    dependent.dependentUserId == selectedId
                                }
                            },
                        isLoading = false,
                        hasLoaded = true,
                        errorMessage = null
                    )
                }
            }.onFailure {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        hasLoaded = true,
                        errorMessage = CareDependentsLoadErrorMessage
                    )
                }
            }
        }
    }

    fun selectDependent(dependentUserId: Long?) {
        _uiState.update { state ->
            state.copy(
                selectedDependentUserId = dependentUserId?.takeIf { selectedId ->
                    state.dependents.any { dependent ->
                        dependent.dependentUserId == selectedId
                    }
                }
            )
        }
    }

    private companion object {
        const val CareDependentsLoadErrorMessage = "Failed to load care dependents"
    }
}

class CareViewModelFactory(
    private val appContainer: AppContainer
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CareViewModel::class.java)) {
            return CareViewModel(
                getCareDependentsUseCase = appContainer.getCareDependentsUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
