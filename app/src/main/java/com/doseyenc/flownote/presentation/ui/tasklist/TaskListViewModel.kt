package com.doseyenc.flownote.presentation.ui.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doseyenc.flownote.domain.model.FilterType
import com.doseyenc.flownote.domain.model.Task
import com.doseyenc.flownote.domain.usecase.DeleteTaskUseCase
import com.doseyenc.flownote.domain.usecase.FilterTasksUseCase
import com.doseyenc.flownote.domain.usecase.GetTasksUseCase
import com.doseyenc.flownote.domain.usecase.SearchTasksUseCase
import com.doseyenc.flownote.domain.usecase.UpdateTaskUseCase
import com.doseyenc.flownote.presentation.viewstate.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val searchTasksUseCase: SearchTasksUseCase,
    private val filterTasksUseCase: FilterTasksUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterType = MutableStateFlow<FilterType>(FilterType.ALL)
    val filterType: StateFlow<FilterType> = _filterType.asStateFlow()

    private val _viewState = MutableStateFlow<ViewState<List<Task>>>(ViewState.Loading)
    val viewState: StateFlow<ViewState<List<Task>>> = _viewState.asStateFlow()

    init {
        observeTasks()
    }

    private fun observeTasks() {
        viewModelScope.launch {
            combine(
                searchQuery,
                filterType
            ) { query, filter ->
                Pair(query, filter)
            }
                .flatMapLatest { (query, filter) ->
                    if (query.isNotBlank()) {
                        searchTasksUseCase(query)
                    } else {
                        filterTasksUseCase(filter)
                    }
                }
                .catch { exception ->
                    _viewState.value = ViewState.Error(
                        message = exception.message ?: "Unknown error",
                        throwable = exception
                    )
                }
                .collect { tasks ->
                    _viewState.value = if (tasks.isEmpty()) {
                        ViewState.Empty
                    } else {
                        ViewState.Success(tasks)
                    }
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onFilterChanged(filterType: FilterType) {
        _filterType.value = filterType
    }

    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) {
        viewModelScope.launch {
            val updatedTask = task.copy(isCompleted = isChecked)
            val result = updateTaskUseCase(updatedTask)
            result.fold(
                onSuccess = { /* Task updated successfully */ },
                onFailure = { exception ->
                    _viewState.value = ViewState.Error(
                        message = exception.message ?: "Failed to update task",
                        throwable = exception
                    )
                }
            )
        }
    }

    fun onDeleteTask(task: Task) {
        viewModelScope.launch {
            val result = deleteTaskUseCase(task)
            result.fold(
                onSuccess = { /* Task deleted successfully */ },
                onFailure = { exception ->
                    _viewState.value = ViewState.Error(
                        message = exception.message ?: "Failed to delete task",
                        throwable = exception
                    )
                }
            )
        }
    }

    // TODO: Navigate to detail
    fun onTaskClicked(task: Task) {
    }
}
