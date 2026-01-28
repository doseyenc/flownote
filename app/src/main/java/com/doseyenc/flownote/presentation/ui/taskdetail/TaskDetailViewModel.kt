package com.doseyenc.flownote.presentation.ui.taskdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doseyenc.flownote.domain.model.Task
import com.doseyenc.flownote.domain.usecase.DeleteTaskUseCase
import com.doseyenc.flownote.domain.usecase.GetTaskByIdUseCase
import com.doseyenc.flownote.presentation.viewstate.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val taskId: Long = savedStateHandle.get<Long>("taskId") ?: -1L

    private val _viewState = MutableStateFlow<ViewState<Task>>(ViewState.Loading)
    val viewState: StateFlow<ViewState<Task>> = _viewState.asStateFlow()

    private val _events = MutableSharedFlow<TaskDetailEvent>()
    val events: SharedFlow<TaskDetailEvent> = _events.asSharedFlow()

    init {
        loadTask()
    }

    private fun loadTask() {
        if (taskId == -1L) {
            _viewState.value = ViewState.Error("Invalid task ID")
            return
        }

        viewModelScope.launch {
            _viewState.value = ViewState.Loading
            getTaskByIdUseCase(taskId).collectLatest { task ->
                if (task == null) {
                    _viewState.value = ViewState.Error("Task not found")
                } else {
                    _viewState.value = ViewState.Success(task)
                }
            }
        }
    }

    fun onEditClicked() {
        if (taskId == -1L) return
        viewModelScope.launch {
            _events.emit(TaskDetailEvent.Edit(taskId))
        }
    }

    fun onDeleteClicked() {
        viewModelScope.launch {
            val currentTask = (viewState.value as? ViewState.Success)?.data ?: return@launch

            val result = deleteTaskUseCase(currentTask)

            result.fold(
                onSuccess = {
                    _events.emit(TaskDetailEvent.Deleted)
                },
                onFailure = { exception ->
                    _viewState.value = ViewState.Error(
                        message = exception.message ?: "Failed to delete task",
                        throwable = exception
                    )
                }
            )
        }
    }

    fun getTaskId(): Long = taskId
}
