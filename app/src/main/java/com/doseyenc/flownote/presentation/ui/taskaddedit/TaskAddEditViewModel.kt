package com.doseyenc.flownote.presentation.ui.taskaddedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doseyenc.flownote.domain.model.Task
import com.doseyenc.flownote.domain.usecase.AddTaskUseCase
import com.doseyenc.flownote.domain.usecase.GetTaskByIdUseCase
import com.doseyenc.flownote.domain.usecase.UpdateTaskUseCase
import com.doseyenc.flownote.presentation.viewstate.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskAddEditViewModel @Inject constructor(
    private val addTaskUseCase: AddTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val taskId: Long = savedStateHandle.get<Long>("taskId") ?: -1L

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _titleError = MutableStateFlow(false)
    val titleError: StateFlow<Boolean> = _titleError.asStateFlow()

    private val _viewState = MutableStateFlow<ViewState<Unit>>(ViewState.Empty)
    val viewState: StateFlow<ViewState<Unit>> = _viewState.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    val isEditMode: Boolean
        get() = taskId != -1L

    init {
        if (taskId != -1L) {
            loadTask(taskId)
        }
    }

    private fun loadTask(id: Long) {
        viewModelScope.launch {
            _viewState.value = ViewState.Loading
            getTaskByIdUseCase(id).collectLatest { task ->
                if (task == null) {
                    _viewState.value = ViewState.Error("Task not found")
                } else {
                    _title.value = task.title
                    _description.value = task.description
                    _viewState.value = ViewState.Success(Unit)
                }
            }
        }
    }

    fun onTitleChanged(value: String) {
        _title.value = value
        if (_titleError.value) {
            _titleError.value = false
        }
    }

    fun onDescriptionChanged(value: String) {
        _description.value = value
    }

    fun onSaveClicked() {
        val currentTitle = title.value
        val currentDescription = description.value

        if (currentTitle.isBlank()) {
            _titleError.value = true
            return
        }

        viewModelScope.launch {
            _isSaving.value = true
            _viewState.value = ViewState.Loading

            val task = Task(
                id = if (taskId != -1L) taskId else 0L,
                title = currentTitle,
                description = currentDescription
            )

            val result = if (taskId == -1L) {
                addTaskUseCase(task)
            } else {
                updateTaskUseCase(task)
            }

            result.fold(
                onSuccess = {
                    _viewState.value = ViewState.Success(Unit)
                    _isSaving.value = false
                },
                onFailure = { exception ->
                    _viewState.value = ViewState.Error(
                        message = exception.message ?: "Failed to save task",
                        throwable = exception
                    )
                    _isSaving.value = false
                }
            )
        }
    }
}

